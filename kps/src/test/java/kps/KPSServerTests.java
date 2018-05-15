package kps.tests;

import org.junit.Assert;
import org.junit.Test;

import kps.client.Client;
import kps.client.ClientNotifiable;
import kps.server.BusinessFigures;
import kps.server.CustomerRoute;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.server.TransportRoute;
import kps.server.UserRecord;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;
import kps.util.XMLFormatException;

import java.io.IOException;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class KPSServerTests {

	class TestNotifiable implements ClientNotifiable {
		private int sema;
		private double expectedExpenditure;
		private double expectedRevenue;
		private String expectedString;

		public void setExpectedRevenue(double rev) {
			this.expectedRevenue = rev;
			checkSemaUp();
		}

		public void setExpectedExpenditure(double exp) {
			this.expectedExpenditure = exp;
			checkSemaUp();
		}

		public void setExpectedString(String msg) {
			this.expectedString = msg;
			checkSemaUp();
		}

		public synchronized void checkSemaUp() {
			sema++;
		}

		public synchronized void checkSemaDown() {
			sema--;
		}

		@Override
		public void postLogRangeStop(){}
		@Override
		public void postLogItems(LogItem[] logItems){}
		@Override
		public void postRole(Role role){}
		@Override
		public void postTransportRoutes(Set<TransportRoute> transportRoutes){}
		@Override
		public void postDestinations(Set<Destination> transportRoutes){}
		@Override
		public void postCustomerRoutes(Set<CustomerRoute> customerRoutes){}
		@Override
		public void postBusinessFigures(BusinessFigures businessFigures){
			Assert.assertEquals(expectedExpenditure, businessFigures.getExpenditure(), 0.01);
			Assert.assertEquals(expectedRevenue, businessFigures.getRevenue(), 0.01);
			checkSemaDown();
			checkSemaDown();
		}
		@Override
		public void postInformationMessage(String message){
			if (expectedString == null)
				return;
			Assert.assertEquals(expectedString, message);
			checkSemaDown();
		}

		public synchronized boolean isDone() {
			return sema == 0;
		}
	};

	/**
	 * Basic sanity check to check that no funky impromptu jazz
	 * happens on a simple log parse
	 */
	@Test
	public void testSuccessfulParse()
			throws IOException {
		BusinessFigures figures = new BusinessFigures();
		KPSServer server = new KPSServer("tmp.xml", figures);
		try {
			server.readInitialLog("testdata/testserverinput.xml");
		} catch (XMLFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Basic sanity check to ensure business figure changes
	 * persist across the network
	 */
	@Test(timeout=5000)
	public void testBusinessFiguresOnNetwork()
			throws IOException {
		int revenue = 2465423;
		int expenditure = 786654;
		String username = "test";
		String password = "test";

		TestNotifiable testNotifiable = new TestNotifiable();
		/* + 600 because the logs has a 100 weight mail sale costing us 6 per weight */
		testNotifiable.setExpectedExpenditure(expenditure + 600);
		
		/* + 1000 because logs have mail 100 weight earning us 10 per weight */
		testNotifiable.setExpectedRevenue(revenue + 1000);

		BusinessFigures figures = new BusinessFigures();
		KPSServer server = new KPSServer("tmp.xml", figures);

		try {
			server.readInitialLog("testdata/testserverinput.xml");
		} catch (XMLFormatException e) {
			e.printStackTrace();
			Assert.fail();
		}
		/* Rome route should exist */
		Assert.assertTrue(
				server.getTransportMap().getDestination("Singapore City", "Singapore")
				!= null);
		/* Sydney route should not exist */
		Assert.assertTrue(
				server.getTransportMap().getDestination("RandomCity", "Australia")
				== null);

		figures.sendMail(revenue, expenditure, 0, 0, 0, null);

		server.addUser(new UserRecord(username, password, Role.MANAGER));

		new Thread(()->{
			try {
				server.run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}).start();
		while (!server.isRunning());

		Client client = new Client("localhost", KPSServer.PORT);
		client.setNotificationRecipient(testNotifiable);

		client.connect(true);
		client.requestAuthentication(new UserRecord(username, password, null));
		client.requestBusinessFigures();
		while(!testNotifiable.isDone());
		server.stop();
	}

	/**
	 * Basic check of server->client text message
	 */
	@Test(timeout=5000)
	public void testNetworkMessage()
			throws IOException {
		TestNotifiable testNotifiable = new TestNotifiable();
		testNotifiable.setExpectedString("Authentication failed");

		String username = "test";
		String password = "somecrappypassword";

		BusinessFigures figures = new BusinessFigures();
		KPSServer server = new KPSServer("tmp.xml", figures);

		server.addUser(new UserRecord(username, password, Role.MANAGER));

		new Thread(()->{
			try {
				server.run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}).start();
		while (!server.isRunning());

		Client client = new Client("localhost", KPSServer.PORT);
		client.setNotificationRecipient(testNotifiable);

		client.connect(true);
		client.requestAuthentication(new UserRecord(username, password+"makeItInvalid", null));
		//client.requestBusinessFigures();
		while(!testNotifiable.isDone());
		server.stop();
	}
}
