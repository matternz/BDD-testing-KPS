package kps.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import org.w3c.dom.Document;

import kps.client.Packet;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;
import kps.util.DateRange;
import kps.util.RouteNotFoundException;
import kps.util.XMLUtil;
import kps.util.XMLFormatException;

public class ServerThread extends Thread {
	private KPSServer shared_server;
	private KPSServer server;
	private Socket sock;
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	private UserRecord user;

	public ServerThread(KPSServer server, Socket sock)
			throws IOException {
		this.server = this.shared_server = server;
		this.sock = sock;
		objIn = new ObjectInputStream(sock.getInputStream());
		objOut = new ObjectOutputStream(sock.getOutputStream());
	}

	/**
	 * Send current business figures to the client
	 * @throws IOException
	 */
	private void sendBusinessFigures()
			throws IOException {
		objOut.writeObject(new Packet(
				Packet.Type.BUSINESS_FIGURES, server.getBusinessFigures()));
		objOut.reset();
	}

	/**
	 * Send current transport routes to the client
	 * @throws IOException
	 */
	private void sendTransportRoutes()
			throws IOException {
		objOut.writeObject(new Packet(
			Packet.Type.TRANSPORT_ROUTES, server.getTransportRoutes()));
		objOut.reset();
	}

	/**
	 * Send current customer routes to the client
	 * @throws IOException
	 */
	private void sendCustomerRoutes()
			throws IOException {
		objOut.writeObject(new Packet(
			Packet.Type.CUSTOMER_ROUTES, server.getCustomerRoutes()));
		objOut.reset();
	}

	/**
	 * Send an information message back to the client/user to display
	 * @param message
	 * @throws IOException
	 */
	private void sendInformationMessage(String message)
			throws IOException {
		objOut.writeObject(new Packet(
				Packet.Type.INFORMATION_MESSAGE, message));
		objOut.reset();
	}

	/**
	 * Send the user's role to the client
	 * @param role
	 * @throws IOException
	 */
	private void sendRole(Role role)
			throws IOException {
		objOut.writeObject(new Packet(
				Packet.Type.ROLE, role));
		objOut.reset();
	}

	private void sendLogItems(LogItem[] items)
		throws XMLFormatException, IOException {
		Document document = LogItem.toXML(items);
		StringWriter writer = new StringWriter();
		StreamResult sr = new StreamResult(writer);
		XMLUtil.writeToStreamResult(document, sr);
		objOut.writeObject(new Packet(
				Packet.Type.LOG_ITEM_MULTIPLE,
				writer.getBuffer().toString()));
		objOut.reset();
	}

	/**
	 * Eat all non-auth packets from the client, and when an auth
	 * packet does arrive, return only if the auth is valid
	 * @throws IOException
	 */
	public void waitForAuthentication()
			throws ClassNotFoundException, IOException {
		while (user == null) {
			Object rawObject = objIn.readObject();
			Packet packet = (Packet)rawObject;

			switch (packet.getType()) {
			case AUTHENTICATION_REQUEST:
				UserRecord recuser = (UserRecord)packet.getPayload();
				if (server.authenticate(recuser)) {
					/* get the actual user record from the server so we know the role */
					Role role = server.getUserRole(recuser.getUsername());
					String roleString = role == Role.CLERK ? "Clerk" : "Manager";
					sendRole(role);
					sendInformationMessage("Welcome, "+recuser.getUsername()+" ("+roleString+")");
					recuser.setRole(role);
					user = recuser;
				} else {
					sendInformationMessage("Authentication failed");
				}
				break;
			default:
				sendInformationMessage("You must first authenticate");
				break;
			}
		}
	}

	/**
	 * Make this thread serve data from a thread-local server
	 * instance with the first toIndex logs processed
	 * @param toIndex index of the last LogItem to process
	 * @throws IOException
	 */
	private void logRangeRequest(Integer toIndex) throws IOException {
		server = new KPSServer();
		List<LogItem> allLogs = shared_server.getLogs();

		try {
			/* +1 to make to inclusive */
			List<LogItem> logsInRange = allLogs.subList(0, toIndex+1);
			LogItem[] itemArray = new LogItem[logsInRange.size()];
			itemArray = logsInRange.toArray(itemArray);
			server.applyItems(itemArray);
		} catch (RouteNotFoundException e) {
			sendInformationMessage("Unable to load logs for that range: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Check that this thread is logged in as a manager
	 * Sends a message to the client if they are not a manager
	 * @return true if authorised as a manager, false otherwise
	 * @throws IOException
	 */
	private boolean isManager()
			throws IOException {
		/* assert that client is a manager */
		if (user.getRole() != Role.MANAGER) {
			sendInformationMessage("You must be a manager to do that (you are a "+user.getRole()+")");
			return false;
		}
		return true;
	}

	private void sendDestinations()
			throws IOException {
		/* Send client the destinations */
		objOut.writeObject(new Packet(Packet.Type.DESTINATIONS,
				server.getTransportMap().getDestinations()));
		objOut.reset();
	}

	private void stopLogRange()
			throws IOException {
		/* switch client back to the live/shared server */
		if (this.server != this.shared_server)
			this.server = shared_server;
		objOut.writeObject(new Packet(Packet.Type.OK_STOP_THIS_MADNESS, null));
		sendBusinessFigures();
		sendCustomerRoutes();
		sendTransportRoutes();
		sendDestinations();
	}

	private void sendAllLogs(KPSServer s)
			throws XMLFormatException, IOException {
		List<LogItem> newItems = s.getLogs();
		LogItem[] itemsAsArray = new LogItem[newItems.size()];
		itemsAsArray = newItems.toArray(itemsAsArray);
		sendLogItems(itemsAsArray);
		/* Send the new figures */
		sendBusinessFigures();
		sendCustomerRoutes();
		sendTransportRoutes();
	}

	@Override
	public void run() {
		boolean running = true;
		try {
			/* assert that the client authenticates first */
			waitForAuthentication();

			if (user.getRole() == Role.MANAGER) {
				sendAllLogs(server);
			}
			sendDestinations();
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}


		while (!sock.isClosed() && running) {
			try {
				Object rawObject = objIn.readObject();
				Packet packet = (Packet)rawObject;

				switch (packet.getType()) {
				case AUTHENTICATION_REQUEST:
					sendInformationMessage("Already logged in as "+user.getUsername());
					break;
				case LOG_ITEM:
					stopLogRange();
					String input = (String)packet.getPayload();
					LogItem[] items = LogItem.parse(input);
					server.applyItems(items);
					sendInformationMessage("Success!");
					/* Send the client the updated routes and figures */
					sendBusinessFigures();
					sendCustomerRoutes();
					sendTransportRoutes();
					sendDestinations();
					if (user.getRole() == Role.MANAGER) {
						sendAllLogs(server);
					}
					break;
				case BUSINESS_FIGURES_REQUEST:
					sendBusinessFigures();
					break;
				case CUSTOMER_ROUTES_REQUEST:
					sendCustomerRoutes();
					break;
				case TRANSPORT_ROUTES_REQUEST:
					sendTransportRoutes();
					break;
				case ALL_LOGS_REQUEST:
					if (!isManager())
						break;
					/* send all logs from the /shared/ server; all logs /ever/ */
					sendAllLogs(shared_server);
					break;
				case LOG_RANGE_REQUEST:
					if (!isManager())
						break;
					Integer range = (Integer)packet.getPayload();
					/* perform the request */
					logRangeRequest(range);
					/* dump the client */
					sendAllLogs(shared_server);
					break;
				case OK_STOP_THIS_MADNESS:
					stopLogRange();
					break;
				default:
					System.err.println("Unsupported packet type "+packet.getType());
					break;

				}

			/* FIXME should probably catch RouteNotFound and throw an
			 * error message at the client/user */
			} catch (NullPointerException
					| RouteNotFoundException
					| XMLFormatException
					| ClassNotFoundException
					| ClassCastException
					| IllegalArgumentException e) {
				try {
					sendInformationMessage("Server error: "+e.getMessage());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					/* error sending the error… disconnect? */
					e1.printStackTrace();
					running = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}

}
