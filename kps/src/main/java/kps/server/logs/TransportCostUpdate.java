package kps.server.logs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import kps.server.Destination;
import kps.server.KPSServer;
import kps.util.RouteNotFoundException;
import kps.util.RouteType;
import kps.util.XMLFormatException;
import kps.util.XMLUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.DayOfWeek;
import java.util.Date;

/**
 * A type of LogItem which defines the cost of a transport cost
 * being updated.
 *
 * Use LogItem.parse(String) to generate this from input.
 */
@ParametersAreNonnullByDefault
public class TransportCostUpdate extends LogItem {
    public final Destination origin;
    public final Destination destination;
    public final String firmName;
    public final RouteType type;
    public final double newWeightToCost;
    public final double newVolumeToCost;
    public final DayOfWeek day;
    public final double frequency;
    public final double duration;
    public final double maxVolume;
    public final double maxWeight;




    /**
     * Private constructor for a TransportCostUpdate log item. To generate
     * from input, use LogItem.parse(String).
     *
     * @param origin Where the transport route originates.
     * @param destination Where the transport route goes to.
     * @param firmName The name of the company that owns the transport route.
     * @param type Whether the route is considered an air route or a standard route.
     * @param date The date on which the log item was added.
     * @param newWeightToCost How much the route costs per unit of weight.
     * @param newVolumeToCost How much the route costs per unit of volume.
     * @param day The day of week when this transport departs for its destination.
     * @param frequency How many hours between subsequent departures.
     * @param duration How many hours it takes between departure and arrival at the destination.
     */
    public TransportCostUpdate(Destination origin, Destination destination, String firmName,
                               RouteType type, double newWeightToCost, double newVolumeToCost,
                               DayOfWeek day, double frequency, double duration, double maxVolume, double maxWeight) {
        this.origin = origin;
        this.destination = destination;
        this.firmName = firmName;
        this.type = type;
        this.newWeightToCost = newWeightToCost;
        this.newVolumeToCost = newVolumeToCost;
        this.day = day;
        this.frequency = frequency;
        this.duration = duration;
        this.maxVolume = maxVolume;
        this.maxWeight = maxWeight;
    }

    /**
     * Generates a TransportCostUpdate from a single XML element.
     *
     * @param input The XML element to parse as a TransportCostUpdate.
     * @return The TransportCostUpdate that was parsed.
     * @throws XMLFormatException If the format of the Element is incorrect
     *                            for a TransportCostUpdate.
     */
    protected static TransportCostUpdate parse(Element input)
            throws XMLFormatException {
    	Destination origin = new Destination(
    			XMLUtil.getSubElementContent(input, "from"));
        Destination destination = new Destination(
        		XMLUtil.getSubElementContent(input, "to"));
        String firmName = XMLUtil.getSubElementContent(input, "company");
        RouteType type = LogItem.getRouteTypeFromLogItem(input);
        double newWeightToCost = XMLUtil.getSubElementContentDouble(input, "weightcost");
        double newVolumeToCost = XMLUtil.getSubElementContentDouble(input, "volumecost");
        DayOfWeek day = DayOfWeek.valueOf(
                XMLUtil.getSubElementContent(input, "day").toUpperCase());
        double frequency = XMLUtil.getSubElementContentDouble(input, "frequency");
        double duration = XMLUtil.getSubElementContentDouble(input, "duration");

        // add the max weight and volume please

        double maxWeight = XMLUtil.getSubElementContentDouble(input, "maxWeight");
        double maxVolume = XMLUtil.getSubElementContentDouble(input, "maxVolume");


        return new TransportCostUpdate(origin, destination, firmName, type, newWeightToCost,
                                       newVolumeToCost, day, frequency, duration, maxVolume, maxWeight);
    }

    @Override
    public String toString() {
        return
            "Service by "
            +type.toString().toLowerCase()
            +" from "
            +firmName
            +" from "
            +origin
            +" to "
            +destination
            +"now costs us "
            +newWeightToCost
            +" by weight and "
            +newVolumeToCost
            +" by volume. It starts on a "
            +day
            +", repeats every "
            +frequency
            +" hours\nand takes "
            +duration
            +" to complete";
    }

    @Override
    public Element toXML(Document doc)
            throws XMLFormatException {
        return XMLUtil.buildSubElement(doc, "cost",
                new String[] { "from", "to", "company", "type", "weightcost", "volumecost",
                		       "maxWeight", "maxVolume", "day", "frequency", "duration" },
                new String[] { origin.toString(), destination.toString(), firmName, LogItem.routeTypeToString(type),
                               Double.toString(newWeightToCost), Double.toString(newVolumeToCost),
                               Double.toString(maxWeight), Double.toString(maxVolume),
                               day.toString(), Double.toString(frequency),
                               Double.toString(duration)});
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
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((firmName == null) ? 0 : firmName.hashCode());
		temp = Double.doubleToLongBits(frequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(newVolumeToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(newWeightToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TransportCostUpdate other = (TransportCostUpdate) obj;
		if (day != other.day)
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration))
			return false;
		if (firmName == null) {
			if (other.firmName != null)
				return false;
		} else if (!firmName.equals(other.firmName))
			return false;
		if (Double.doubleToLongBits(frequency) != Double.doubleToLongBits(other.frequency))
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
		if (type != other.type)
			return false;
		return true;
	}
}
