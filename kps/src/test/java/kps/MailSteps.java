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
import kps.server.CustomerRoute;
import kps.util.MailPriority;
import kps.util.RouteNotFoundException;
import kps.util.XMLFormatException;

public class MailSteps {

    BusinessFigures figures = new BusinessFigures();
    KPSServer server = new KPSServer("/dev/null", figures);
    int weight;
    int measure;
    int volume;
    MailPriority mailPriority;
    String fromCity;
    String fromCountry;
    String toCity;
    String toCountry;
    double cost;

    @Given("^an initial map$")
    public void an_initial_map() throws Throwable {
        server.readInitialLog("data/data.xml");
    }

    @Given("^a parcel that weighs (\\d+)kg$")
    public void a_parcel_that_weighs_kg(int weight) throws Throwable {
        this.weight = weight;
    }

    @Given("^a parcel with volume (\\d+)m3$")
    public void a_parcel_with_volume_m(int volume) {
        this.volume = volume;
    }

    @Given("^a parcel that measures (\\d+) cc$")
    public void a_parcel_that_measures_cc(int measure) throws Throwable {
        this.measure = measure;
    }

    @Given("^I send the parcel from \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_send_the_parcel_from(String fromCity, String fromCountry) throws Throwable {
        this.fromCity = fromCity;
        this.fromCountry = fromCountry;
    }

    @Given("^I send the parcel to \"([^\"]*)\" \"([^\"]*)\"$")
    public void i_send_the_parcel_to(String toCity, String toCountry) throws Throwable {
        this.toCity = toCity;
        this.toCountry = toCountry;
    }

    @Given("^I send the parcel by domestic standard mail$")
    public void i_send_the_parcel_by_domestic_standard_mail() throws Throwable {
        this.mailPriority = MailPriority.DOMESTIC_STANDARD;
    }

    @Given("^I send the parcel by domestic air mail$")
    public void i_send_the_parcel_by_domestic_air_mail() throws Throwable {
        this.mailPriority = MailPriority.DOMESTIC_AIR;
    }

    @Given("^I send the parcel by international standard mail$")
    public void i_send_the_parcel_by_international_standard_mail() throws Throwable {
        this.mailPriority = MailPriority.INTERNATIONAL_STANDARD;
    }

    @Given("^I send the parcel by international air mail$")
    public void i_send_the_parcel_by_international_air_mail() throws Throwable {
        this.mailPriority = MailPriority.INTERNATIONAL_AIR;
    }

    // this is wrong don't copy
    // @Then("^the cost is \\$(\\d+)$")
    // public void theCostIs$(int expectedCost) throws Throwable {
    // Destination to = new Destination("Palmerston North", "New Zealand");
    // Destination from = new Destination("Wellington", "New Zealand");
    // Mail mail = new Mail(to, from, MailPriority.DOMESTIC_STANDARD, 1.0, 1.0);
    // TransportRoute route = server.getTransportMap().calculateRoute(mail).get(0);
    // Assert.assertTrue(expectedCost == route.calculateCost(mail.weight,
    // mail.volume));
    // }

    @Then("^the cost is \\$(\\d+)$")
    public void theCostIs(int expectedCost) throws Throwable {
        // System.out.println(this.toCity);
        // System.out.println(this.toCountry);
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail mail = new Mail(to, from, mailPriority, weight, volume);
        TransportRoute route = server.getTransportMap().calculateRoute(mail).get(0);
        // System.out.println(route.calculateCost(mail.weight, mail.volume));
        // Assert.assertTrue(expectedCost == route.calculateCost(mail.weight,
        // mail.volume));
        Assert.assertEquals(expectedCost, route.calculateCost(mail.weight, mail.volume), 0.0);
    }

    @Then("^the cost is more than domestic standard mail$")
    public void costMoreThanDomesticStandardDelivery() throws Throwable {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail stanMail = new Mail(to, from, MailPriority.DOMESTIC_STANDARD, weight, volume);
        Mail airMail = new Mail(to, from, MailPriority.DOMESTIC_AIR, weight, volume);
        TransportRoute stanRoute = server.getTransportMap().calculateRoute(stanMail).get(0);
        TransportRoute airRoute = server.getTransportMap().calculateRoute(airMail).get(0);
        double airCost = airRoute.calculateCost(airMail.weight, airMail.volume);
        double stanCost = stanRoute.calculateCost(stanMail.weight, stanMail.volume);
        Assert.assertTrue("airCost: " + airCost + " should be greater than stanCost: " + stanCost, airCost > stanCost);
    }

    @Then("^the cost is less than domestic air mail$")
    public void costLessThanDomesticAirDelivery() throws Throwable {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail stanMail = new Mail(to, from, MailPriority.DOMESTIC_STANDARD, weight, volume);
        Mail airMail = new Mail(to, from, MailPriority.DOMESTIC_AIR, weight, volume);
        TransportRoute stanRoute = server.getTransportMap().calculateRoute(stanMail).get(0);
        TransportRoute airRoute = server.getTransportMap().calculateRoute(airMail).get(0);
        double airCost = airRoute.calculateCost(airMail.weight, airMail.volume);
        double stanCost = stanRoute.calculateCost(stanMail.weight, stanMail.volume);
        Assert.assertTrue("airCost: " + airCost + " should be greater than stanCost: " + stanCost, airCost >  stanCost);
    }

    @Then("^the cost is more than or equal to international standard mail$")
    public void costMoreThanInternationalStandardDelivery() throws Throwable {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail stanMail = new Mail(to, from, MailPriority.INTERNATIONAL_STANDARD, weight, volume);
        Mail airMail = new Mail(to, from, MailPriority.INTERNATIONAL_AIR, weight, volume);
        TransportRoute stanRoute = server.getTransportMap().calculateRoute(stanMail).get(0);
        TransportRoute airRoute = server.getTransportMap().calculateRoute(airMail).get(0);
        double airCost = airRoute.calculateCost(airMail.weight, airMail.volume);
        double stanCost = stanRoute.calculateCost(stanMail.weight, stanMail.volume);
        Assert.assertTrue("airCost: " + airCost + " should be greater than stanCost: " + stanCost, airCost >=  stanCost);
    
    }

    @Then("^the cost is less than or equal to international air mail$")
    public void costLessThanInternationalAirDelivery() throws Throwable {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail stanMail = new Mail(to, from, MailPriority.INTERNATIONAL_STANDARD, weight, volume);
        Mail airMail = new Mail(to, from, MailPriority.INTERNATIONAL_AIR, weight, volume);
        TransportRoute stanRoute = server.getTransportMap().calculateRoute(stanMail).get(0);
        TransportRoute airRoute = server.getTransportMap().calculateRoute(airMail).get(0);
        double airCost = airRoute.calculateCost(airMail.weight, airMail.volume);
        double stanCost = stanRoute.calculateCost(stanMail.weight, stanMail.volume);
        Assert.assertTrue("airCost: " + airCost + " should be greater than stanCost: " + stanCost, airCost >=  stanCost);
    
    }

    @Then("^the route type is \"([^\"]*)\"$")
    public void theRouteTypeIs(String expectedRoute) throws Throwable {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        Mail mail = new Mail(to, from, mailPriority, weight, volume);
        TransportRoute route = server.getTransportMap().calculateRoute(mail).get(0);
        Assert.assertTrue(expectedRoute.equals(route.type.AIR.toString()));
    }

    @Then("^this should produce an error")
    public void destinationExists() {
        try {
            Destination to = new Destination(this.toCity, this.toCountry);
            Destination from = new Destination(this.fromCity, this.fromCountry);
            Mail mail = new Mail(to, from, mailPriority, weight, volume);
            TransportRoute route = server.getTransportMap().calculateRoute(mail).get(0);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof Exception);
            return;
        }
        Assert.fail();
    }

    @And("^I send a domestic standard mail")
    public void sendDomesticStandardMail() {
        Destination to = new Destination(this.toCity, this.toCountry);
        Destination from = new Destination(this.fromCity, this.fromCountry);
        CustomerRoute cr = new CustomerRoute(to, from, mailPriority, 1, 1);
        server.getBusinessFigures().sendMail(1, 1, 1, 1, 1, cr);
    }

    @Then("^the total revenue is \\$(\\d+)$")
    public void totalRevenue(int expectedRevenue) throws Throwable {
        Assert.assertEquals(expectedRevenue, server.getBusinessFigures().getRevenue(), 0.0);
    }

    @Then("^the total expenditure is \\$(\\d+)$")
    public void totalExpenditure(int expectedExpenditure) throws Throwable {
        Assert.assertEquals(expectedExpenditure, server.getBusinessFigures().getExpenditure(), 0.0);
    }

    @Then("^the total volume is (\\d+)m3$")
    public void totalVolume(int expectedVolume) throws Throwable {
        Assert.assertEquals(expectedVolume, server.getBusinessFigures().getTotalVolume(), 0.0);
    }

    @Then("^the total weight is (\\d+)kg$")
    public void totalWeight(int expectedWeight) throws Throwable {
        Assert.assertEquals(expectedWeight, server.getBusinessFigures().getTotalWeight(), 0.0);
    }

    @Then("^the total number of items is (\\d+)$")
    public void totalItems(int expectedNumItems) throws Throwable {
        Assert.assertEquals(expectedNumItems, server.getBusinessFigures().getMailCount(), 0.0);
    }

    @Then("^the average delivery days is (\\d+)$")
    public void totalDays(int expectedDays) throws Throwable {
        Assert.assertEquals(expectedDays, server.getBusinessFigures().getAverageDeliveryDays(), 0.0);
    }

}
