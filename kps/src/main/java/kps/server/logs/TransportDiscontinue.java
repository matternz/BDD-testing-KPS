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
 * A type of LogItem which defines a transport route
 * being removed from the system.
 *
 * Use LogItem.parse(String) to generate this from input.
 */
@ParametersAreNonnullByDefault
public class TransportDiscontinue extends LogItem {
    public final Destination origin;
    public final Destination destination;
    public final String firmName;
    public final RouteType type;

    /**
     * Private constructor for a TransportDiscontinue log item. To generate
     * from input, use LogItem.parse(String).
     *
     * @param origin Where the transport route originates.
     * @param destination Where the transport route goes to.
     * @param firmName The name of the company that owns the transport route.
     * @param type Whether the route is considered an air route or a standard route.
     * @param date The date on which the log item was added.
     */
    public TransportDiscontinue(Destination origin, Destination destination, String firmName,
                                 RouteType type) {
        this.origin = origin;
        this.destination = destination;
        this.firmName = firmName;
        this.type = type;
    }

    /**
     * Generates a TransportDiscontinue from a single XML element.
     *
     * @param input The XML element to parse as a TransportDiscontinue.
     * @return The TransportDiscontinue that was parsed.
     * @throws XMLFormatException If the format of the Element is incorrect
     *                            for a TransportDiscontinue.
     */
    protected static TransportDiscontinue parse(Element input)
            throws XMLFormatException {
    	Destination origin = new Destination(
    			XMLUtil.getSubElementContent(input, "from"));
        Destination destination = new Destination(
        		XMLUtil.getSubElementContent(input, "to"));
        String firmName = XMLUtil.getSubElementContent(input, "company");
        RouteType type = LogItem.getRouteTypeFromLogItem(input);

        return new TransportDiscontinue(origin, destination, firmName, type);
    }

    @Override
    public String toString() {
        return
            firmName
            +" discontinued transport from "
            +origin
            +" to "
            +destination
            +" by "
            +type.toString().toLowerCase();
    }

    @Override
    public Element toXML(Document doc)
            throws XMLFormatException {
        return XMLUtil.buildSubElement(doc, "discontinue",
                new String[] { "from", "to", "company", "type" },
                new String[] { origin.toString(), destination.toString(), firmName, LogItem.routeTypeToString(type)});
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
		result = prime * result + ((firmName == null) ? 0 : firmName.hashCode());
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
		TransportDiscontinue other = (TransportDiscontinue) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (firmName == null) {
			if (other.firmName != null)
				return false;
		} else if (!firmName.equals(other.firmName))
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
