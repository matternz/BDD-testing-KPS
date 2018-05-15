package kps.client;

import java.util.Set;

import kps.server.BusinessFigures;
import kps.server.CustomerRoute;
import kps.server.Destination;
import kps.server.TransportRoute;
import kps.server.UserRecord.Role;
import kps.server.logs.LogItem;

/**
 * Interface for defining the callback protocol between
 * a Client and a user interface
 * @author David Phillips
 *
 */
public interface ClientNotifiable {
	/**
	 * Notify of a new list of transport routes
	 * @param transportRoutes
	 */
	public void postTransportRoutes(Set<TransportRoute> transportRoutes);

	/* FIXME CustomerRoute needs to be in the model! */
	//public void postCustomerRoutes(List<CustomerRoute> transportRoutes);
	/**
	 * Notify of a new list of customer routes
	 * @param transportRoutes
	 */
	public void postCustomerRoutes(Set<CustomerRoute> customerRoutes);

	/**
	 * Notify of new business figures
	 * @param businessFigures
	 */
	public void postBusinessFigures(BusinessFigures businessFigures);

	/**
	 * Notify of some misc message
	 * @param message
	 */
	public void postInformationMessage(String message);

	/**
	 * Notify of new business figures
	 * @param businessFigures
	 */
	public void postDestinations(Set<Destination> destination);

	/**
	 * Notify of new user role
	 * @param role
	 */
	public void postRole(Role role);

	/**
	 * Notify of LogItems
	 * Should be used for after a DateRange request etc
	 * @param role
	 */
	public void postLogItems(LogItem[] logItems);

	/**
	 * Notify of ended client-specific log range
	 */
	public void postLogRangeStop();
}
