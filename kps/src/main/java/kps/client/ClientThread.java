package kps.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Set;

import kps.server.BusinessFigures;
import kps.server.CustomerRoute;
import kps.server.Destination;
import kps.server.TransportRoute;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;
import kps.util.XMLFormatException;

public class ClientThread extends Thread {
	private ClientNotifiable ui;
	private Socket socket;
	private ObjectInputStream objIn;

	public ClientThread(Socket socket, ClientNotifiable recipient)
			throws IOException {
		this.socket = socket;
		this.ui = recipient;
		this.objIn = new ObjectInputStream(socket.getInputStream());
	}

	@SuppressWarnings("unchecked") /* Trust me, I know what I'm doing. I think. */
	public void run() {
		boolean running = true;
		while (!socket.isClosed() && running) {
			try {
				Object rawObj = objIn.readObject();
				Packet packet = (Packet)rawObj;
				Object payload = packet.getPayload();
				System.err.println("Got a packet");
				/* disgusting; cannot switch on getClass() */
				switch (packet.getType()) {
				case INFORMATION_MESSAGE:
					ui.postInformationMessage((String)packet.getPayload());
					break;
				case BUSINESS_FIGURES:
					ui.postBusinessFigures((BusinessFigures)payload);
					break;
				case CUSTOMER_ROUTES:
					ui.postCustomerRoutes((Set<CustomerRoute>)payload);
					break;
				case TRANSPORT_ROUTES:
					ui.postTransportRoutes((Set<TransportRoute>)payload);
					break;
				case DESTINATIONS:
					ui.postDestinations((Set<Destination>)payload);
					break;
				case ROLE:
					ui.postRole((Role)payload);
					break;
				case LOG_ITEM_MULTIPLE:
					String xml = (String)packet.getPayload();
					/* parse XML string into an array of LogItem */
					LogItem[] logItems = LogItem.parse(xml);
					ui.postLogItems(logItems);
					break;
				case OK_STOP_THIS_MADNESS:
					ui.postLogRangeStop();
					break;
				default:
					throw new Error("Invalid packet type");
				}
			} catch (ClassCastException | XMLFormatException | ClassNotFoundException | IOException e) {
				e.printStackTrace();
				ui.postInformationMessage("Client error: "+e.getMessage());
				running = false;
			}
		}
	}
}
