package kps;
import cucumber.api.java.en.*;
import kps.server.BusinessFigures;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.server.Mail;
import kps.server.TransportRoute;
import kps.util.MailPriority;

import org.junit.Assert;

import cucumber.api.PendingException;

public class KPSMail {

	BusinessFigures figures = new BusinessFigures();
	KPSServer server = new KPSServer("/dev/null", figures);
	int weight;
	int measure;
	String fromCity;
	String fromCountry;
	String toCity;
	String toCountry;
	double cost;

//	// cannot be same as any other step
//	@Given("^an initial map$")
//	public void anInitialMap() throws Throwable {
//	    server.readInitialLog("data/data.xml");
//	}

	
	
}
