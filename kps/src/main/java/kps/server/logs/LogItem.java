package kps.server.logs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import kps.server.KPSServer;
import kps.util.*;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract class over each type of log item, for example
 * a transport discontinue log item. Can be applied to a
 * KPServer to enact its effects.
 */
@ParametersAreNonnullByDefault
public abstract class LogItem {
    /**
     * Protected constructor for a LogItem;
     * Use parse(String) to generate LogItems from input.
     */
    protected LogItem() {
    }

    /**
     * Parses the input string to generate LogItems.
     * Note that this is just a wrapper around
     * parse(input, minIndex, maxIndex), between infinite
     * indices.
     *
     * @param input The XML input to parse.
     * @return An array of LogItems found in the input file.
     * @throws XMLFormatException If the input file has an invalid format.
     */
    public static LogItem[] parse(String input)
            throws XMLFormatException {
        return parse(input, 0, 0);
    }

    /**
     * Parses the input string to generate LogItems, but only
     * returns LogItems between the given indices (inclusive).
     *
     * Set minIndex to 0 to have no minIndex.
     * Set maxIndex to 0 to have no maxIndex.
     *
     * The max index is capped to the actual amount of nodes;
     * so there will be no problem if it is set too high.
     *
     * @param input The XML input to parse.
     * @return An array of LogItems found in the input file.
     * @throws XMLFormatException If the input file has an invalid format.
     */
    public static LogItem[] parse(String input, int minIndex, int maxIndex)
            throws XMLFormatException{
        Document inputDoc = XMLUtil.buildXMLDocument(input);
        // The root node should be <document>, with the child nodes being the log items
        NodeList nodes = inputDoc.getFirstChild().getChildNodes();
        List<Element> logElems = new ArrayList<Element>();

        // Filter out the ones which actually are log items
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                // This isn't a log; it's probably just delimiters or something
                continue;
            }

            logElems.add((Element)node);
        }

        // Now parse them into log items. We do it in this order
        // so we don't have to parse ones outside the required indices.
        List<LogItem> logs = new ArrayList<LogItem>();

        // 0 means there's no max index
        if (maxIndex == 0) {
            maxIndex = Integer.MAX_VALUE;
        }

        // Cap it to the amount of nodes.
        if (maxIndex >= logElems.size()) {
            maxIndex = logElems.size() - 1;
        }

        for (int i = minIndex; i <= maxIndex; i++) {
            try {
                logs.add(parseOne(logElems.get(i)));
            } catch (XMLFormatException e) {
                // We don't need to abort here, just make it obvious there's a problem.
                System.err.println("Invalid element encountered at index "
                        + i + ": " + e);
                throw new Error(e);
            }
        }

        return logs.toArray(new LogItem[0]);
    }

    /**
     * Parses an XML element into a LogItem.
     *
     * @param input The XML element to parse the LogItem from.
     * @return The LogItem that was parsed.
     * @throws XMLFormatException If the input element doesn't correspond to a LogItem.
     */
    public static LogItem parseOne(Element input)
            throws XMLFormatException {
        switch(input.getNodeName()) {
            case "mail":
                return MailDelivery.parse(input);
            case "price":
                return CustomerCostUpdate.parse(input);
            case "cost":
                return TransportCostUpdate.parse(input);
            case "discontinue":
                return TransportDiscontinue.parse(input);
            default:
                throw new XMLFormatException(input.getNodeName()+" is an invalid type of log.");
        }
    }

    /**
     * Generates a date from the given log item.
     * Preferably does this by reading the "date" element.
     * Otherwise uses the current date.
     *
     * @param logItem The log item to be read from.
     * @return The generated date.
     */
    public static Date getDateFromLogItem(Element logItem) {
        try {
            String dateS = XMLUtil.getSubElementContent(logItem, "date");
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            return format.parse(dateS);
        } catch (ParseException|XMLFormatException e) {
            // Use today's date instead.
            return new Date();
        }
    }

    /**
     * Generates a route type from the given log item.
     *
     * @param logItem The log item to be read from.
     * @return The generated route type.
     * @throws XMLFormatException If the route type can't be parsed correctly.
     */
    public static RouteType getRouteTypeFromLogItem(Element logItem)
            throws XMLFormatException {
        String routeTypeS = XMLUtil.getSubElementContent(logItem, "type");
        RouteType type = routeTypeS.equals("Air") ? RouteType.AIR :
                         routeTypeS.equals("Land") ? RouteType.LAND :
                         routeTypeS.equals("Sea") ? RouteType.SEA :
                         null;
        if (type == null) {
            throw new XMLFormatException("Route type was not one of the two accepted values.");
        }

        return type;
    }

    /**
     * Generates a mail priority from the given log item.
     *
     * @param logItem The log item to be read from.
     * @return The generated mail priority.
     * @throws XMLFormatException If the mail priority can't be parsed correctly.
     */
    public static MailPriority getMailPriorityFromLogItem(Element logItem)
            throws XMLFormatException {
        String mailPriorityS = XMLUtil.getSubElementContent(logItem, "priority");
        MailPriority priority =
          mailPriorityS.equals("International Air") ? MailPriority.INTERNATIONAL_AIR :
          mailPriorityS.equals("Domestic Air") ? MailPriority.DOMESTIC_AIR :
          mailPriorityS.equals("Domestic Standard") ? MailPriority.DOMESTIC_STANDARD :
          mailPriorityS.equals("International Standard") ? MailPriority.INTERNATIONAL_STANDARD :
          null;

        if (priority == null) {
            throw new XMLFormatException("Priority was not one of the four accepted values.");
        }

        return priority;
    }

    /**
     * Generates a DayOfWeek object from the given log item.
     *
     * @param logItem The log item to be read from.
     * @return The generated day of week.
     * @throws XMLFormatException If the day of week can't be parsed correctly.
     */
    public static DayOfWeek getDayOfWeekFromLogItem(Element logItem)
            throws XMLFormatException {
        String dayOfWeekS = XMLUtil.getSubElementContent(logItem, "day").toLowerCase();
        DayOfWeek dayOfWeek =
          dayOfWeekS.equals("monday") ? DayOfWeek.MONDAY :
          dayOfWeekS.equals("tuesday") ? DayOfWeek.TUESDAY :
          dayOfWeekS.equals("wednesday") ? DayOfWeek.WEDNESDAY :
          dayOfWeekS.equals("thursday") ? DayOfWeek.THURSDAY :
          dayOfWeekS.equals("friday") ? DayOfWeek.FRIDAY :
          dayOfWeekS.equals("saturday") ? DayOfWeek.SATURDAY :
          dayOfWeekS.equals("sunday") ? DayOfWeek.SUNDAY :
          null;

        if (dayOfWeek == null) {
            throw new XMLFormatException("Day was not one of the seven accepted values, but '"+dayOfWeekS+"'");
        }
        return dayOfWeek;
    }

    /**
     * Generates a string from a mail priority enum.
     *
     * @throws XMLFormatException If the enum wasn't one of the four expected values.
     */
    public static String mailPriorityToString(MailPriority priority)
            throws XMLFormatException {
        String ret = priority == MailPriority.INTERNATIONAL_AIR ? "International Air" :
                     priority == MailPriority.INTERNATIONAL_STANDARD ? "International Standard" :
                     priority == MailPriority.DOMESTIC_AIR ? "Domestic Air" :
                     priority == MailPriority.DOMESTIC_STANDARD ? "Domestic Standard" :
                     null;

        if (ret == null) {
            throw new XMLFormatException("Priority was not one of the expected four enum values.");
        }

        return ret;
    }

    /**
     * Generates a string from a route type enum.
     *
     * @throws XMLFormatException If the enum wasn't one of the two expected values.
     */
    public static String routeTypeToString(RouteType type)
            throws XMLFormatException {
        String ret = type == RouteType.AIR ? "Air" :
                     type == RouteType.LAND ? "Land" :
                     type == RouteType.SEA ? "Sea" :
                     null;

        if (ret == null) {
            throw new XMLFormatException("Route type was not one of the expected two enum values.");
        }

        return ret;
    }

    /**
     * Returns an element corresponding to this LogItem,
     * in XML form.
     *
     * @throws XMLFormatException If an error occurs when translating this item to XML.
     */
    public abstract Element toXML(Document doc) throws XMLFormatException;

    /**
     * Returns a Document corresponding to the given
     * LogItems, in XML form. This is equivalent to wrapping
     * the individually produced elements from toXML in a
     * document tag.
     *
     * @throws XMLFormatException If an error occurs when translating the items to XML.
     */
    public static Document toXML(LogItem[] items)
            throws XMLFormatException {
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            throw new XMLFormatException("Parser incorrectly configured when" +
                    " attempting to build XML document. " + e);
        }

        Element root = doc.createElement("document");
        doc.appendChild(root);

        for (LogItem item : items) {
            doc.getDocumentElement().appendChild(item.toXML(doc));
        }

        return doc;
    }

    /**
     * Applies the effects of this log item to the given system.
     * For example, changes the cost of the appropriate route if
     * this is a TransportCostUpdate.
     *
     * @throws RouteNotFoundException If an appropriate route isn't found (if applicable).
     */
    public abstract void apply(KPSServer kps) throws RouteNotFoundException;
}
