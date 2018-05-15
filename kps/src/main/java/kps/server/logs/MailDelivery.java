package kps.server.logs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kps.server.CustomerRoute;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

import java.time.DayOfWeek;
import java.util.Date;

/**
 * A type of LogItem which defines a piece of mail
 * being entered into the system to be delivered.
 *
 * Use LogItem.parse(String) to generate this from input.
 */
@ParametersAreNonnullByDefault
public class MailDelivery extends LogItem {
    public Destination origin;
    public Destination destination;
    public double weight;
    public double volume;
    public MailPriority priority;
    public DayOfWeek day;
    private double revenue;
    private double expenditure;
    private double days;

    /**
     * Private constructor for a MailDelivery log item. To generate
     * from input, use LogItem.parse(String).
     *
     * @param origin Where the mail originates from.
     * @param destination Where the mail must reach.
     * @param weight How heavy the mail is.
     * @param volume The volume of the mail.
     * @param priority Whether the mail is domestic or international, and whether
     *                 it is restricted to air transport or can be transported in any way.
     * @param date The date on which this log item was entered into the system.
     */
    public MailDelivery(Destination origin, Destination destination, double weight,
                         double volume, MailPriority priority, DayOfWeek day) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.volume = volume;
        this.priority = priority;
        this.day = day;
    }

    /**
     * Generates a MailDelivery from a single XML element.
     *
     * @param input The XML element to parse as a MailDelivery.
     * @return The MailDelivery that was parsed.
     * @throws XMLFormatException If the format of the Element is incorrect for a MailDelivery.
     */
    protected static MailDelivery parse(Element input)
            throws XMLFormatException {
    	Destination origin = new Destination(
    			XMLUtil.getSubElementContent(input, "from"));
        Destination destination = new Destination(
        		XMLUtil.getSubElementContent(input, "to"));
        double weight = XMLUtil.getSubElementContentDouble(input, "weight");
        double volume = XMLUtil.getSubElementContentDouble(input, "volume");
        MailPriority priority = LogItem.getMailPriorityFromLogItem(input);
        DayOfWeek day = LogItem.getDayOfWeekFromLogItem(input);
        return new MailDelivery(origin, destination, weight, volume, priority, day);
    }

    @Override
    public String toString() {
        return
            "Mail delivery from "
            +origin
            +" to "
            +destination;
    }

    @Override
    public Element toXML(Document doc)
            throws XMLFormatException {
        return XMLUtil.buildSubElement(doc, "mail",
                new String[] { "from", "to", "weight", "volume", "priority", "day"},
                new String[] { origin.toString(), destination.toString(), Double.toString(weight),
                               Double.toString(volume), LogItem.mailPriorityToString(priority),
                               day.toString()});
    }

    @Override
    public void apply(KPSServer kps)
            throws RouteNotFoundException {
    	revenue = kps.getTransportMap().getCustomerPrice(this);
		expenditure = kps.getTransportMap().getTransportPrice(this);
		CustomerRoute customerRoute = kps.getTransportMap().getCustomerRoute(this);
		days = kps.getTransportMap().getDurationOfTravel(this) / 24;
        kps.getBusinessFigures().sendMail(revenue, expenditure, this.weight, this.volume, days, customerRoute);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		long temp;
		temp = Double.doubleToLongBits(volume);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailDelivery other = (MailDelivery) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (priority != other.priority)
			return false;
		if (Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}
}
