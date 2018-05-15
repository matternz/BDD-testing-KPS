package kps;
import cucumber.api.java.en.*;
import cucumber.api.PendingException;

import org.junit.Assert;

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


public class MailSteps {

BusinessFigures figures = new BusinessFigures();
KPSServer server = new KPSServer("/dev/null", figures);
int weight;
int measure;
String fromCity;
String fromCountry;
String toCity;
String toCountry;
double cost;		

@Given("^an initial map$")
public void anInitialMap() throws Throwable {
    server.readInitialLog("data/data.xml");   
}

@Given("^a parcel that weighs (\\d+)kg$")
public void aParcelThatWeighsKg(int weight) throws Throwable {
    this.weight = weight;
}

@Given("^a parcel that measures (\\d+) cc$")
public void aParcelThatMeasuresCc(int measure) throws Throwable {
    this.measure = measure;
}

@Given("^I send the parcel from \"([^\"]*)\" \"([^\"]*)\"$")
public void iSendTheParcelFrom(String fromCity, String fromCountry) throws Throwable {
    this.fromCity = fromCity;
    this.fromCountry = fromCountry;
}

@Given("^I send the parcel to \"([^\"]*)\" \"([^\"]*)\"$")
public void iSendTheParcelTo(String toCity, String toCountry) throws Throwable {
    this.toCity = toCity;
    this.toCountry = toCountry;
}

@Given("^I send the parcel by domestic standard mail$")
public void iSendTheParcelByDomesticStandardMail() throws Throwable {
}

@Then("^the cost is \\$(\\d+)$")
public void theCostIs$(int expectedCost) throws Throwable {
Destination to = new Destination("Palmerston North", "New Zealand");
Destination from = new Destination("Wellington", "New Zealand");
Mail mail = new Mail(to, from, MailPriority.DOMESTIC_STANDARD, 1.0, 1.0);
TransportRoute route = server.getTransportMap().calculateRoute(mail).get(0);
Assert.assertTrue(expectedCost == route.calculateCost(mail.weight, mail.volume));
}



}
