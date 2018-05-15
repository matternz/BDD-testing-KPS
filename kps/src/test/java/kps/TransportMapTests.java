package kps.tests;

import org.junit.Assert;
import org.junit.Test;

import kps.server.BusinessFigures;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.server.Mail;
import kps.server.TransportRoute;
import kps.server.UserRecord;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;
import kps.server.logs.MailDelivery;
import kps.util.MailPriority;
import kps.util.RouteNotFoundException;
import kps.util.XMLFormatException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TransportMapTests {
	BusinessFigures figures;
	KPSServer server;
	
	@Test
	public void testParseOfXML(){
		initialiseMap();
	}
	
	@Test
	public void testNumberOfPossibleRoutes(){
		initialiseMap();
		Set<TransportRoute> routes;
		//System.out.println(routes.size());
		routes = server.getTransportMap().getDestination("Palmerston North", "New Zealand").getPossibleRoutes();
		Assert.assertTrue(routes.size() == 4);
		routes = server.getTransportMap().getDestination("Auckland", "New Zealand").getPossibleRoutes();
		Assert.assertTrue(routes.size() == 12);
		routes = server.getTransportMap().getDestination("Singapore City", "Singapore").getPossibleRoutes();	
		Assert.assertTrue(routes.size() == 5);
	}
	
	@Test
	public void testValidSinglePaths()
			throws RouteNotFoundException {
		initialiseMap();
		List<TransportRoute> routes;
		TransportRoute route;
		Destination to;
		Destination from;
		Mail mail;
		
		
		//Only one path WEL --> PAL
		to = new Destination("Palmerston North", "New Zealand");
		from = new Destination("Wellington", "New Zealand");
		mail = new Mail(to, from, MailPriority.DOMESTIC_STANDARD, 1, 1);
		routes = server.getTransportMap().calculateRoute(mail);
		Assert.assertTrue(routes.size()==1);
		route = routes.get(0);
		Assert.assertTrue(route.getFrom().equals(from));
		Assert.assertTrue(route.getTo().equals(to));
		Assert.assertTrue(route.calculateCost(mail.weight, mail.volume) == 5);
	}
	
	@Test
	public void testValidPaths()
			throws RouteNotFoundException {
		initialiseMap();
		List<TransportRoute> routes;
		TransportRoute route;
		Destination to;
		Destination from;
		Mail mail;

		
		//Only one path WEL --> PAL
		to = new Destination("Singapore City", "Singapore");
		from = new Destination("Wellington", "New Zealand");
		mail = new Mail(to, from, MailPriority.INTERNATIONAL_AIR, 1, 1);
		routes = server.getTransportMap().calculateRoute(mail);
		MailDelivery md = new MailDelivery(to, from, 1, 1, MailPriority.INTERNATIONAL_AIR, DayOfWeek.MONDAY);
		Assert.assertTrue(routes.size()==2);
	}
	
	
	
	private void initialiseMap() {
		figures = new BusinessFigures();
		server = new KPSServer("tmp.xml", figures);
		
		try {
			server.readInitialLog("data/data.xml");
		} catch (XMLFormatException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	private void initialiseMap(String initialLog) {
		figures = new BusinessFigures();
		server = new KPSServer("tmp.xml", figures);
		
		try {
			server.readInitialLog(initialLog);
		} catch (XMLFormatException | IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	// -----------------------------------------------------------------------------------------
	
	@Test
	public void testValidPresentation(){
		initialiseMap("presentationALTERED.xml");
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
