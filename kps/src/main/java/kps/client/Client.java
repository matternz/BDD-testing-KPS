package kps.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.DayOfWeek;

import javax.xml.transform.stream.StreamResult;

import kps.client.ui.UI;
import org.w3c.dom.Document;

import kps.server.Destination;
import kps.server.KPSServer;
import kps.server.UserRecord;
import kps.server.logs.*;
import kps.util.MailPriority;
import kps.util.RouteType;
import kps.util.XMLFormatException;
import kps.util.XMLUtil;

public class Client {
	private ClientNotifiable ui;
	private ClientThread thread;
	private ObjectOutputStream objOut;
	private Socket socket;
	private String hostname;
	private int port;

	public Client(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}


	/**
	 * Connect the Client to the host and port specified in the Client's
	 * construction
	 * @param background -- true if call should not block, false if call should
	 *                      block
	 * @throws UnknownHostException -- if the socket library cannot resolve
	 *                                 the hostname
	 * @throws IOException
	 */
	public void connect(boolean background)
			throws UnknownHostException, IOException {
		socket = new Socket(hostname, port);
		objOut = new ObjectOutputStream(socket.getOutputStream());
		this.thread = new ClientThread(socket, ui);
		if (background) {
			thread.start();
		} else {
			thread.run();
		}
	}

	private void sendObject(Object packet)
			throws IOException {
		if (objOut == null)
			throw new IOException("Unable to send to server. Please ensure the client is connected (null object output stream)");
		objOut.writeObject(packet);
	}

	/**
	 * Send a single LogItem object across the wire to the sever.
	 * Requires that the client has been successfully connected through
	 * connect()
	 * @param item -- the item to be sent
	 * @throws XMLFormatException -- if an XML parser error occurs
	 * @throws IOException -- if an I/O exception occurs on the socket or its stream
	 */
	public void sendLogItem(LogItem item)
			throws XMLFormatException, IOException {
		if (socket == null || !socket.isConnected()) {
			throw new IllegalStateException("Socket must be connected to send a log item");
		}
		LogItem[] itemAsArray = {item};
		Document document = LogItem.toXML(itemAsArray);
		StringWriter writer = new StringWriter();
		StreamResult sr = new StreamResult(writer);
		XMLUtil.writeToStreamResult(document, sr);
		this.sendObject(new Packet(Packet.Type.LOG_ITEM, writer.getBuffer().toString()));
	}

	/**
	 * Send a request to the server for a list of transport routes
	 * @throws IOException
	 */
	public void requestTransportRoutes()
			throws IOException {
		sendObject(new Packet(Packet.Type.TRANSPORT_ROUTES_REQUEST, null));
	}

	/**
	 * Send a request to the server for a list of customer routes
	 * @throws IOException
	 */
	public void requestCustomerRoutes()
			throws IOException {
		sendObject(new Packet(Packet.Type.CUSTOMER_ROUTES_REQUEST, null));
	}

	/**
	 * Send a request to the server for a list of business figures
	 * @throws IOException
	 */
	public void requestBusinessFigures()
			throws IOException {
		sendObject(new Packet(Packet.Type.BUSINESS_FIGURES_REQUEST, null));
	}

	/**
	 * Send a request to the server for a list of customer routes
	 * @throws IOException
	 */
	public void requestAuthentication(UserRecord user)
			throws IOException {
		sendObject(new Packet(Packet.Type.AUTHENTICATION_REQUEST, user));
	}

	/**
	 * Send a request to the server for all logs
	 * @throws IOException
	 */
	public void requestAllLogs()
			throws IOException {
		sendObject(new Packet(Packet.Type.ALL_LOGS_REQUEST, null));
	}

	/**
	 * Send a request to the server for consideration of logs
	 * only between a date range (inclusive)
	 * @throws IOException
	 */
	public void requestLogsRange(Integer range)
			throws IOException {
		sendObject(new Packet(Packet.Type.LOG_RANGE_REQUEST, range));
	}

	/**
	 * Send a request to the server to stop considering any previously
	 * requested date range for logs, and to serve the public/shared
	 * log instead
	 * @throws IOException
	 */
	public void requestLogRangeStop()
			throws IOException {
		sendObject(new Packet(Packet.Type.OK_STOP_THIS_MADNESS, null));
	}

	public void sendMailDelivery(String dayString, String to, String from,
								 String weightString, String volumeString, String priorityString) {

		Destination origin = null;
		Destination destination = null;
		try {
			origin = new Destination(from);
			destination = new Destination(to);
		} catch (IllegalArgumentException e) {
			ui.postInformationMessage("Invalid origin or destination");
			return;
		}

		DayOfWeek day = DayOfWeek.valueOf(dayString.toUpperCase());
		double weight;
		double volume;
		try {
			weight = Double.parseDouble(weightString);
			volume = Double.parseDouble(volumeString);
		} catch (NumberFormatException e){
			ui.postInformationMessage("Invalid weight or volume");
			return;
		}
		MailPriority priority = MailPriority.fromString(priorityString);
		MailDelivery mailDelivery = new MailDelivery(origin,destination,weight,volume,priority,day);
		try {
			sendLogItem(mailDelivery);
		} catch (XMLFormatException e) {
			ui.postInformationMessage("Mail delivery is invalid, reason: "+ e.getMessage());
		} catch (IOException e) {
			ui.postInformationMessage("Mail delivery could not be completed. Reason: " + e.getMessage());
		}
	}

	//TODO write transport cost update method
	public void sendTransportCostUpdate(String companyString, String to, String from,
										String typeString, String weightCostString,
										String volumeCostString, String maxWeightString,
										String maxVolumeString, String durationString,
										String frequencyString, String dayString) {

		Destination origin = null;
		Destination destination = null;
		try {
			origin = new Destination(from);
			destination = new Destination(to);
		} catch (IllegalArgumentException e) {
			ui.postInformationMessage("Invalid origin or destination");
			return;
		}

		DayOfWeek day = DayOfWeek.valueOf(dayString.toUpperCase());
		double weightCost;
		double volumeCost;
		double maxWeight;
		double maxVolume;
		double duration;
		double frequency;
		try {
			weightCost = Double.parseDouble(weightCostString);
			volumeCost = Double.parseDouble(volumeCostString);
			maxWeight = Double.parseDouble(maxWeightString);
			maxVolume = Double.parseDouble(maxVolumeString);
			duration = Double.parseDouble(durationString);
			frequency = Double.parseDouble(frequencyString);
		} catch (NumberFormatException e){
			ui.postInformationMessage("Invalid weight or volume");
			return;
		}

		RouteType type = RouteType.valueOf(typeString.toUpperCase());

		TransportCostUpdate transportCostUpdate = new TransportCostUpdate(origin,destination,companyString,
				type,weightCost,volumeCost,day,frequency,duration,maxVolume,maxWeight);
		try {
			sendLogItem(transportCostUpdate);
		} catch (XMLFormatException e) {
			ui.postInformationMessage("Mail delivery is invalid, reason: "+ e.getMessage());
		} catch (IOException e) {
			ui.postInformationMessage("Mail delivery could not be completed. Reason: " + e.getMessage());
		}
	}

	public void sendTransportDiscontinue(String company, String to, String from,
								 String typeString) {

		Destination origin = null;
		Destination destination = null;
		try {
			origin = new Destination(from);
			destination = new Destination(to);
		} catch (IllegalArgumentException e) {
			ui.postInformationMessage("Invalid origin or destination");
			return;
		}

		RouteType type = RouteType.valueOf(typeString.toUpperCase());


		TransportDiscontinue transportDiscontinue = new TransportDiscontinue(origin,destination,company,type);

		try {
			sendLogItem(transportDiscontinue);
		} catch (XMLFormatException e) {
			ui.postInformationMessage("Mail delivery is invalid, reason: "+ e.getMessage());
		} catch (IOException e) {
			ui.postInformationMessage("Mail delivery could not be completed. Reason: " + e.getMessage());
		}
	}

	public void sendCustomerPriceUpdate(String to, String from, String priorityString,
										String weightCostString, String volumeCostString) {

		Destination origin = null;
		Destination destination = null;
		try {
			origin = new Destination(from);
			destination = new Destination(to);
		} catch (IllegalArgumentException e) {
			ui.postInformationMessage("Invalid origin or destination");
			return;
		}
		double weightCost;
		double volumeCost;

		try {
			weightCost = Double.parseDouble(weightCostString);
			volumeCost = Double.parseDouble(volumeCostString);
		} catch (NumberFormatException e){
			ui.postInformationMessage("Invalid weight or volume");
			return;
		}

		MailPriority priority = MailPriority.fromString(priorityString);

		CustomerCostUpdate customerCostUpdate = new CustomerCostUpdate(origin,destination,priority,weightCost,volumeCost);

		try {
			sendLogItem(customerCostUpdate);
		} catch (XMLFormatException e) {
			ui.postInformationMessage("Mail delivery is invalid, reason: "+ e.getMessage());
		} catch (IOException e) {
			ui.postInformationMessage("Mail delivery could not be completed. Reason: " + e.getMessage());
		}
	}

	/**
	 * Set the recipient for any notification of data from the
	 * server end such as BusinessFigures and transport routes
	 * @param recipient
	 */
	public void setNotificationRecipient(ClientNotifiable recipient) {
		this.ui = recipient;
	}

	public static void main(String[] args) {
		Client client = new Client("localhost", KPSServer.PORT);
		ClientNotifiable ui = new UI(client);
		client.setNotificationRecipient(ui);
		try {
			client.connect(false);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			ui.postInformationMessage("Client error: "+e.getMessage());
		}
	}
}
