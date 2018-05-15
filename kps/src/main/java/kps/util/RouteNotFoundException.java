package kps.util;

/**
 * An exception thrown when an appropriate route can't be found.
 */
public class RouteNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public RouteNotFoundException(String message) {
        super(message);
    }

    public RouteNotFoundException(Exception parent) {
        super(parent);
    }
}
