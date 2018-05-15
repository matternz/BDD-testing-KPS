package kps.server.logs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

import java.time.DayOfWeek;
import java.util.Date;

/**
 * A type of LogItem which defines the cost to a customer
 * being changed between an origin and a destination.
 *
 * Use LogItem.parse(String) to generate this from input.
 */
@ParametersAreNonnullByDefault
public class CustomerCostUpdate extends LogItem {
    public final Destination origin;
    public final Destination destination;
    public final MailPriority priority;
    public final double newWeightToCost;
    public final double newVolumeToCost;

    /**
     * Private constructor for a CustomerCostUpdate log item. To generate
     * from input, use LogItem.parse(String).
     *
     * @param origin Where the customer route originates.
     * @param destination Where the customer route goes to.
     * @param priority The priority of the route.
     * @param newWeightToCost How much the route costs per unit of weight.
     * @param newVolumeToCost How much the route costs per unit of volume.
     * @param date The date on which the log item was added.
     */
    public CustomerCostUpdate(Destination origin, Destination destination, MailPriority priority,
                               double newWeightToCost, double newVolumeToCost) {
        this.origin = origin;
        this.destination = destination;
        this.priority = priority;
        this.newWeightToCost = newWeightToCost;
        this.newVolumeToCost = newVolumeToCost;
    }

    /**
     * Generates a CustomerCostUpdate from a single XML element.
     *
     * @param input The XML element to parse as a CustomerCostUpdate.
     * @return The CustomerCostUpdate that was parsed.
     * @throws XMLFormatException If the format of the Element is incorrect
     *                            for a CustomerCostUpdate.
     */
    protected static CustomerCostUpdate parse(Element input)
            throws XMLFormatException {
    	Destination origin = new Destination(
    			XMLUtil.getSubElementContent(input, "from"));
        Destination destination = new Destination(
        		XMLUtil.getSubElementContent(input, "to"));
        MailPriority priority = LogItem.getMailPriorityFromLogItem(input);
        double newWeightToCost = XMLUtil.getSubElementContentDouble(input, "weightcost");
        double newVolumeToCost = XMLUtil.getSubElementContentDouble(input, "volumecost");
        //DayOfWeek day = LogItem.getDayOfWeekFromLogItem(input);

        return new CustomerCostUpdate(origin, destination, priority,
                                      newWeightToCost, newVolumeToCost);
    }

    @Override
    public String toString() {
        return
            "Now charging customers "
            +newWeightToCost
            +" by weight and "
            +newVolumeToCost
            +" by volume from "
            +origin
            +" to "
            +destination;
    }


    @Override
    public Element toXML(Document doc)
            throws XMLFormatException {
        return XMLUtil.buildSubElement(doc, "price",
                new String[] { "from", "to", "priority", "weightcost", "volumecost"},
                new String[] { origin.toString(), destination.toString(), LogItem.mailPriorityToString(priority),
                               Double.toString(newWeightToCost), Double.toString(newVolumeToCost)});
    }

    @Override
    public void apply(KPSServer kps)
            throws RouteNotFoundException {
        kps.getTransportMap().apply(this);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		long temp;
		temp = Double.doubleToLongBits(newVolumeToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(newWeightToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
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
		CustomerCostUpdate other = (CustomerCostUpdate) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (Double.doubleToLongBits(newVolumeToCost) != Double.doubleToLongBits(other.newVolumeToCost))
			return false;
		if (Double.doubleToLongBits(newWeightToCost) != Double.doubleToLongBits(other.newWeightToCost))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}
}
