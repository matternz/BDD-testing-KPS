package kps.util;

/**
 * An exception thrown when an invalid format (e.g. missing parameters)
 * in an XML input file is encountered.
 */
public class XMLFormatException extends Exception {
	private static final long serialVersionUID = 1L;

	public XMLFormatException(String message) {
        super(message);
    }

    public XMLFormatException(Exception parent) {
        super(parent);
    }
}
