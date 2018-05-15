package kps.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

/**
 * A collection of utilities for XML in the KPS system.
 */
public class XMLUtil {
    private XMLUtil(){}

    /**
     * Builds an XML document from an input string.
     *
     * @param input The string to build a document from.
     * @return The document that was built.
     * @throws XMLFormatException If the string can't be read from, or a parsing error occurs.
     */
    public static Document buildXMLDocument(String input)
            throws XMLFormatException {
        // Code nabbed from:
        // https://stackoverflow.com/questions/33262/how-do-i-load-an-org-w3c-dom-document-from-xml-in-a-string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(input)));
        } catch (ParserConfigurationException e) {
            throw new XMLFormatException("Parser incorrectly configured when" +
                                         " attempting to build XML document. " + e);
        } catch (SAXException e) {
            throw new XMLFormatException(e);
        } catch (IOException e) {
            throw new XMLFormatException("Failed to read from string when building" +
                                         " document. " + e);
        }
    }

    /**
     * Finds the string content of an element within the given element, with
     * the given name.
     *
     * @param element The parent element.
     * @param name The name of the subelement to get the content of.
     * @return The content of the subelement.
     * @throws XMLFormatException If there isn't exactly one subelement by that name.
     */
    public static String getSubElementContent(Element element, String name)
            throws XMLFormatException {
        NodeList nl = element.getElementsByTagName(name);
        if (nl.getLength() != 1) {
            throw new XMLFormatException(nl.getLength()+" subelements by the name " + name +
                                         " when 1 was expected.");
        }

        Node subNode = nl.item(0);
        return subNode.getTextContent();
    }

    /**
     * Wrapper around XMLUtil.getSubElementContent which also parses the result
     * as a double.
     *
     * param element The parent element.
     * @param name The name of the subelement to get the content of.
     * @return The content of the subelement, parsed as a double.
     * @throws IllegalArgumentException If there isn't exactly one subelement by that name,
     *                                  or if it couldn't be parsed as a double.
     */
    public static double getSubElementContentDouble(Element element, String name)
            throws XMLFormatException {
        String elementStringContent = getSubElementContent(element, name);

        try {
            return Double.parseDouble(elementStringContent);
        } catch(NumberFormatException e) {
            throw new XMLFormatException(e);
        }
    }

    /**
     * Builds an element with sub elements.
     *
     * @param doc The document to build the element and sub elements in.
     * @param elemName The name of the element.
     * @param subElemNames The names of all sub-elements.
     * @param subElemContent The string content of all sub-elements.
     * @return The element that was built.
     * @throws IllegalArgumentException If subElemNames and subElemContent aren't the same length.
     */
    public static Element buildSubElement(Document doc, String elemName,
                                          String[] subElemNames, String[] subElemContent)
            throws IllegalArgumentException {
        if (subElemNames.length != subElemContent.length) {
            throw new IllegalArgumentException("subElemNames.length != subElemContent.length");
        }

        Element elem = doc.createElement(elemName);
        for (int i = 0; i < subElemNames.length; i++) {
            Element subElem = doc.createElement(subElemNames[i]);
            subElem.setTextContent(subElemContent[i]);
            elem.appendChild(subElem);
        }

        return elem;
    }

    /**
     * Attempts to write the given XML document to a file with the given name.
     *
     * @throws IOException If the file can't be written to.
     */
    public static void writeToFile(Document doc, String filename)
            throws IOException {
        writeToStreamResult(doc, new StreamResult(new FileWriter(filename)));
    }

    /**
     * Attempts to write the given XML document to a given StreamResult.
     *
     * @throws IOException If the stream cannot be written to.
     */
	public static void writeToStreamResult(Document doc, StreamResult stream)
            throws IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IOException("Failed to write document to file: " + e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        try {
            transformer.transform(source, stream);
        } catch(TransformerException e) {
            throw new IOException("Failed to write document to stream: " + e);
        }
    }
}
