package kps.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;

import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;
import kps.util.RouteNotFoundException;
import kps.util.XMLFormatException;
import kps.util.XMLUtil;

public class KPSServer {
	private String filename;
	private boolean isRunning;
	private List<UserRecord> users;
	public static final int PORT = 9852;
	private List<LogItem> logs;
	private ServerSocket socket;
	private BusinessFigures businessFigures;
	private TransportMap transportMap;

	public KPSServer()
			throws IOException {
		this("logs.xml");
	}

	public KPSServer(String filename)
			throws IOException {
		this(filename, new BusinessFigures());
	}

	/**
	 *
	 * @param scanner -- scanner to read log items from
	 * @param kps -- object to store BusinessFigures in
	 */
	public KPSServer(String filename, BusinessFigures businessFigures) {
		this.businessFigures = businessFigures;
		this.transportMap = new TransportMap();
		this.logs = new ArrayList<LogItem>();
		this.filename = filename;

		/* FIXME remove dummy users */
		this.users = new ArrayList<UserRecord>();
		this.users.add(new UserRecord("manager", "manager", Role.MANAGER));
		this.users.add(new UserRecord("clerk", "clerk", Role.CLERK));
	}

	/**
	 * Ask the server if a user record matching a given one exists
	 * @param user -- user record to try and match
	 * @return true if match found, false otherwise
	 */
	public boolean authenticate(UserRecord user) {
		return users.contains(user);
	}

	/**
	 * Get a user's role from their username
	 * @param username
	 * @return the Role of the user, or null if no matching user found
	 */
	public Role getUserRole(String username) {
		for (UserRecord u : users) {
			if (u.getUsername().equals(username)) {
				return u.getRole();
			}
		}
		return null;
	}

	/**
	 * Add a user to the server's user list
	 * @param user -- user to add
	 * @return true if user didn't exist, false if it already exists
	 */
	public boolean addUser(UserRecord user) {
		return users.add(user);
	}
	/**
	 * Run the server's socket listening loop
	 * @throws IOException
	 */
	public void run()
			throws IOException {
		socket = new ServerSocket();
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(PORT));

		setRunning();
		System.out.println("Server listening on port "+PORT);
		List<ServerThread> workers = new ArrayList<ServerThread>();

		while (!socket.isClosed() && socket.isBound()) {
			try {
				Socket s = socket.accept();
				ServerThread worker = new ServerThread(this, s);
				System.out.println("Connection from "+s.getInetAddress());
				workers.add(worker);
				worker.start();
			} catch (IOException e) {
				e.printStackTrace(System.err);
				continue;
			}
		}
	}

	public void stop() {
		try {
			socket.close();
		} catch (IOException e) {
			/* FIXME throw the error away */
		}
	}

	public TransportMap getTransportMap() {
		return this.transportMap;
	}

	public BusinessFigures getBusinessFigures() {
		return this.businessFigures;
	}

	public Set<TransportRoute> getTransportRoutes() {
		return this.transportMap.getTransportRoutes();
	}

	public Set<CustomerRoute> getCustomerRoutes() {
		return this.transportMap.getCustomerRoutes();
	}

	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	public synchronized void setRunning() {
		this.isRunning = true;
	}

	public synchronized void saveToFile(String filename)
			throws IOException, XMLFormatException {
		LogItem[] arrayLogs = new LogItem[logs.size()];
		arrayLogs = logs.toArray(arrayLogs);
        XMLUtil.writeToFile(LogItem.toXML((LogItem[])arrayLogs), filename);
	}

	synchronized void applyItems(LogItem[] items)
			throws RouteNotFoundException {
		for (LogItem item : items) {
			item.apply(this);
			logs.add(item);
		}
		try {
			saveToFile(filename);
		} catch (IOException | XMLFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read an XML log of log items representing the server's
	 * initial state
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws XMLFormatException
	 */
	synchronized public void readInitialLog(String filename)
			throws FileNotFoundException, IOException, XMLFormatException {
		String content = new String(Files.readAllBytes(Paths.get(filename)));

		LogItem[] logitems = LogItem.parse(content);
		try {
			applyItems(logitems);
		} catch (RouteNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get a list of all log items representing the server's BusinessFigures'
	 * current state
	 * @return -- list of the log items
	 */
	synchronized public List<LogItem> getLogs() {
		return this.logs;
	}

	public static void main(String[] args) {
		KPSServer server;

		if (args.length != 2) {
			System.err.println(
				"Syntax: inputFile outputFile\n"+
				"  Reads initial state from inputFile and writes all existing\n"+
				"  log items, changes, and new log items to outputFile");
			return;
		}

		String initialFile = args[0];
		String saveFile = args[1];
		System.out.println("Reading initial state from "+initialFile);
		System.out.println("Writing working state to "+saveFile);
		try {
			server = new KPSServer(saveFile);
			server.readInitialLog(initialFile);
			server.run();
		} catch (IOException | XMLFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

}
