package kps.server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import kps.server.logs.CustomerCostUpdate;
import kps.server.logs.MailDelivery;
import kps.server.logs.TransportCostUpdate;
import kps.server.logs.TransportDiscontinue;
import kps.util.MailPriority;
import kps.util.RouteType;
import kps.util.RouteNotFoundException;

@ParametersAreNonnullByDefault
public class TransportMap {

	private Set<Destination> destinations = new HashSet<Destination>(); // routes in the system WITH possible routes attached
	private Set<TransportRoute> transportRoutes = new HashSet<TransportRoute>();
	private Set<TransportFirm> transportFirms = new HashSet<TransportFirm>();
	public static final String domesticCountry = "new zealand";
	public Set<CustomerRoute> customerRoutes = new HashSet<CustomerRoute>();
	
	
	/**
	 * Constructs an empty map
	 */
	public TransportMap(){
		super();
	}
	
	//<<---------------------------------- MAP ALTERATION BELOW ---------------------------------->>


	/**
	 * Applies the given TransportCostUpdate to the system. Will remove a matching route if found.
	 *
	 * @param u TransportCostUpdate to be applied to the system
	 * @return true if match was removed, false if no match found
	 */
	public boolean apply(TransportCostUpdate u){
		// find matching transportRoute if exists
		TransportRoute newRoute = new TransportRoute(u.destination, u.origin, u.type, u.newWeightToCost, u.newVolumeToCost,
				u.maxWeight, u.maxVolume, u.duration, u.frequency, u.day, new TransportFirm(u.firmName));;
		TransportRoute toRemove = getTransportRoute(u);
		// add new route
		transportRoutes.add(newRoute);
		// add destinations to the destinations if has yet to be encountered
		if(getDestination(u.destination) == null){
			destinations.add(u.destination);
		}
		if(getDestination(u.origin) == null){
			destinations.add(u.origin);
		}
		//add links to new route
		for(Destination d: destinations){
			if(d.equals(newRoute.from)){
				d.addPossibleRoute(newRoute);
			}
		}
		
		// if match was found remove original from routes and also remove links
		if(toRemove != null){
			/* FIXME remove source and destination if unreferenced */
			transportRoutes.remove(toRemove);
			for(Destination d: destinations){
				if(d.equals(toRemove.from)){
					d.removePossibleRoute(toRemove);
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Applies the given TransportDiscontinue to the system.
	 *
	 * @param u TransportDiscontinue to be applied to the system
	 * @return true if match was removed, false if no match found
	 */
	public boolean apply(TransportDiscontinue u){
		TransportRoute toRemove = getTransportRoute(u);
		
		// if match was found remove it
		if(toRemove != null){
			transportRoutes.remove(toRemove);
			for(Destination d: destinations){
				if(d.equals(toRemove.from)){
					d.removePossibleRoute(toRemove);
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Applies the given CustomerCostUpdate to the system. 
	 *
	 * @param u CustomerCostUpdate to be applied to the system
	 * @return true update was successful, false if none found
	 */
	public void apply(CustomerCostUpdate u){
		CustomerRoute match = getCustomerRoute(u);
		if(match!=null) {
			match.weightToCost = u.newWeightToCost;
			match.volumeToCost = u.newVolumeToCost;
		} else {
			customerRoutes.add(new CustomerRoute(u.destination, u.origin, u.priority, u.newWeightToCost, u.newVolumeToCost));
		}
	}

	//<<---------------------------------- MAP ALTERATION ABOVE ---------------------------------->>

	//<<---------------------------------- GETTERS BELOW ---------------------------------->>


	/**
	 * Finds the destination with the given country and city names
	 *
	 * @param city - name of city
	 * @param country - name of country
	 * @return destination if found, null if none found
	 */
	public Destination getDestination(String city, String country){
		for(Destination d: destinations){
			if(d.getCountry().toLowerCase().equals(country.toLowerCase()) && d.getCity().toLowerCase().equals(city.toLowerCase())){
				return d;
			}
		}
		return null;
	}
	
	/**
	 * Finds the destination in the map with the same city and country and the one given
	 *
	 * @param destination to match against map's destinations
	 * @return map's destination if found
	 */
	public Destination getDestination(Destination o){
		for(Destination d: destinations){
			if(d.getCountry().toLowerCase().equals(o.getCountry().toLowerCase()) && d.getCity().toLowerCase().equals(o.getCity().toLowerCase())){
				return d;
			}
		}
		return null;
	}

	/**
	 * Return transports routes with the given to and from destinations.
	 *
	 * @param to - to destination
	 * @param from - from destination
	 * @return transport route if found, null if none found
	 */
	public Set<TransportRoute> getRoutes(Destination to, Destination from){

		Set<TransportRoute> routes = new HashSet<TransportRoute>();
		for(TransportRoute t: transportRoutes){
			if(t.getTo().equals(to) && t.getFrom().equals(from)){
				routes.add(t);
			}
		}
		return null;
	}

	/**
	 * Returns the matching route if the to and from destination, firm name and type of route matches with the given update. 
	 * @param u
	 * @return
	 */
	public TransportRoute getTransportRoute(TransportCostUpdate u){
		for(TransportRoute t: transportRoutes){
			boolean matched = true;
			if(!(t.to.equals(u.destination))) matched = false;
			if(!(t.from.equals(u.origin))) matched = false;
			if(!(t.owner.name.equals(u.firmName))) matched = false;
			if(!(t.type.equals(u.type))) matched = false;
			if(matched == true){
				return t;
			}
		}
		return null;
	}
	
	public TransportRoute getTransportRoute(TransportDiscontinue u){
		for(TransportRoute t: transportRoutes){
			boolean matched = true;
			if(!(t.to.equals(u.destination))) matched = false;
			if(!(t.from.equals(u.origin))) matched = false;
			if(!(t.owner.name.equals(u.firmName))) matched = false;
			if(!(t.type.equals(u.type))) matched = false;
			if(matched == true){
				return t;
			}
		}
		return null;
	}
	
	public CustomerRoute getCustomerRoute(CustomerCostUpdate u){
		for(CustomerRoute c: customerRoutes){
			boolean matched = true;
			if(!(c.to.equals(u.destination))) matched = false;
			if(!(c.from.equals(u.origin))) matched = false;
			if(!(c.priority.equals(u.priority))) matched = false;
			if(matched == true){
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Gets the customer route that applies to the given mail
	 * 
	 * @param m Mail to be sent
	 * @return customerRoute Returns the customer route that would apply to this mail
	 */
	public CustomerRoute getCustomerRoute(MailDelivery md){
		Mail m = new Mail(md.destination, md.origin, md.priority, md.weight, md.volume);
		for(CustomerRoute c: customerRoutes){
			boolean matched = true;
			//check destination matches
			if(c.to.city.equals("")){ // city isnt specified just check if country the same
				if(!(c.to.country.toLowerCase().equals(m.to.country.toLowerCase()))) matched = false;
			} else { // city is specified must check whole destination for match
				if(!(c.to.equals(m.to))) matched = false;
			}
			//check origin matches
			if(c.from.city.equals("")){ // city isnt specified just check if country the same
				if(!(c.from.country.toLowerCase().equals(m.from.country.toLowerCase()))) matched = false;
			} else { // city is specified must check whole destination for match
				if(!(c.from.equals(m.from))) matched = false;
			}
			if(!(c.priority.equals(m.priority))) matched = false;
			if(matched) return c;
		}
		return null;
	}
	
	/**
	 * Calculates the price the customer will pay for sending the given mail
	 * 
	 * @param m Mail to be sent
	 * @return price Price for customer, -1 if price cannot be found for given specifications
	 */
	public double getCustomerPrice(MailDelivery md){
		Mail m = new Mail(md.destination, md.origin, md.priority, md.weight, md.volume);
		for(CustomerRoute c: customerRoutes){
			boolean matched = true;
			//check destination matches
			if(c.to.city.equals("")){ // city isnt specified just check if country the same
				if(!(c.to.country.toLowerCase().equals(m.to.country.toLowerCase()))) matched = false;
			} else { // city is specified must check whole destination for match
				if(!(c.to.equals(m.to))) matched = false;
			}
			//check origin matches
			if(c.from.city.equals("")){ // city isnt specified just check if country the same
				if(!(c.from.country.toLowerCase().equals(m.from.country.toLowerCase()))) matched = false;
			} else { // city is specified must check whole destination for match
				if(!(c.from.equals(m.from))) matched = false;
			}
			if(!(c.priority.equals(m.priority))) matched = false;
			if(!matched) continue;
			double volumeCost = m.volume*c.volumeToCost;
			double weightCost = m.weight*c.weightToCost;
			if(volumeCost>weightCost) return volumeCost;
			return weightCost;
		}
		return -1.0;
	}
	
	/**
	 * Calculates the price the that the transport firms will charge sending the given mail
	 * 
	 * @param m MailDelivery log item
	 * @return price Price for customer, -1 if price cannot be found for given specifications
	 */
	public double getTransportPrice(MailDelivery md)
			throws RouteNotFoundException {
		Mail m = new Mail(md.destination, md.origin, md.priority, md.weight, md.volume);
		List<TransportRoute> routes = calculateRoute(m);
		double total = 0.0;
		for(TransportRoute r: routes){
			double volumeCost = m.volume*r.volumeToCost;
			double weightCost = m.weight*r.weightToCost;
			if(volumeCost>weightCost) total = total + volumeCost;
			else {total = total+weightCost;}
		}
		return total;
	}
	
	/**
	 * Calculates the number of hours that mail will take to get to its final destination
	 * 
	 * @param m MailDelivary log
	 * @return hours Hours mail will take, -1 if no path exists. 
	 */
	public double getDurationOfTravel(MailDelivery md)
			throws RouteNotFoundException {
		Mail m = new Mail(md.destination, md.origin, md.priority, md.weight, md.volume);
		List<TransportRoute> routes = calculateRoute(m);
		if(routes == null) return -1;
		//duration is calculated in hours from Monday 00:00, startTime will be used to offset the time calculated from the ending result.  
		double startTime = (md.day.getValue()-1)*24; 
		double currentTime = startTime;
		double weekLength = 168;
		for(TransportRoute r: routes){
			double earliestPosTime = (r.day.getValue()-1)*24;
			double weekCutOff = earliestPosTime + weekLength;
			while(earliestPosTime < currentTime){	
				earliestPosTime = earliestPosTime + r.frequency; // keep increasing the earliestPosTime by frequency until we find match
				if(earliestPosTime > weekCutOff){ // if by increasing the earliest pos time we reach a new week
					earliestPosTime  = weekCutOff + (r.day.getValue()-1)*24; // make the earliest pos time start a new week cycle. 
					weekCutOff = weekCutOff + weekLength; // increase the week cut off time. 
				}
			}
			double endTime = earliestPosTime + r.duration; // time of arrival at destination is the earliest possible time + duration of trip
			currentTime = endTime; // set current time to the end time of that route. 
		}
		return (currentTime - startTime);
	}
	
	

	public Set<TransportRoute> getTransportRoutes() {
		return this.transportRoutes;
	}

	public Set<CustomerRoute> getCustomerRoutes() {
		return this.customerRoutes;
	}

	public Set<Destination> getDestinations() {
		return this.destinations;
	}


	//<<---------------------------------- GETTERS ABOVE ---------------------------------->>

	//<<---------------------------------- SEARCH ALGORITHM BELOW ---------------------------------->>


	public List<TransportRoute> calculateRoute(Mail mail)
			throws RouteNotFoundException {
		Destination to = getDestination(mail.to);
		Destination from = getDestination(mail.from);
		
		//due to parallel edges create a list of approved edges , basically a edge is not approved if it is parallel and not the lowest cost option.
		Set<TransportRoute> alteredRoutes = this.removeParallelEdges(transportRoutes, mail);
		//System.out.println(alteredRoutes.size());
		// visited destinations
		Set<SearchNode> visited = new HashSet<SearchNode>();
		// nodes left to evaluate
		Set<SearchNode> fringe = new HashSet<SearchNode>();
		fringe.add(new SearchNode(from, null, 0, null));

		while(!fringe.isEmpty()){
			SearchNode curNode = getLowestCostSearchNode(fringe);
			fringe.remove(curNode);
			if(!visited.contains(curNode)){
				visited.add(curNode);
				// destination found exit loop
				if(curNode.node.equals(to)){
					break;
				}
				for(TransportRoute t: curNode.node.getPossibleRoutes()){
					if(alteredRoutes.contains(t) && getSearchNodeFromDestination(visited, t.to) == null){ // if route is approved and unvisited
						double costToDestination = curNode.pathLength + t.calculateCost(mail.weight, mail.volume);
						fringe.add(new SearchNode(getDestination(t.to), curNode, costToDestination, t));
					}
				}
			}
		}

		if(getSearchNodeFromDestination(visited, to) != null){
			// go backwards from final destination to find path
			List<TransportRoute> routesToDestination = new ArrayList<TransportRoute>();
			SearchNode curNode = getSearchNodeFromDestination(visited, to);
			while(curNode.pathFrom != null){
				routesToDestination.add(curNode.route);
				curNode = curNode.pathFrom;
			}
			Collections.reverse(routesToDestination);
			return routesToDestination;
		}
		throw new RouteNotFoundException("No route from "+from+" to "+to);
	}

	public SearchNode getSearchNodeFromDestination(Set<SearchNode> nodes, Destination d){
		for(SearchNode s: nodes){
			if(s.node.equals(d)) return s;
		}
		return null;
	}

	public Set<TransportRoute> removeParallelEdges (Set<TransportRoute> routes, Mail mail){
		// keep track of routes to remove
		Set<TransportRoute> toRemove = new HashSet<TransportRoute>();
		// find the comparator related to the priority
		TransportComparator comparator = this.getTransportComparator(mail);

		// flag all invalid routes
		for(TransportRoute t: routes){
			if(!(comparator.checkValid(t))) toRemove.add(t);
		}
		//System.out.println(toRemove.size());
		// CODE GOOD UP UNTIL HERE
		
		// flag all routes that do not have suitable weight limits
		for(TransportRoute t: routes){
			if(t.maxWeight < mail.weight && t.maxVolume < mail.volume) toRemove.add(t);
			//System.out.println(t.maxWeight + "|" + mail.weight);
		}
		
		//System.out.println(toRemove.size());
		// flag all routes to go to and from the same destination but is not of highest priority.
		
		for(TransportRoute t1: routes){
			if(!toRemove.contains(t1)){ // make sure we not checking any routes that weve already flagged
				for(TransportRoute t2: routes){
					if(checkParallelRoute(t1,t2) && t1!=t2){ // check that the two destinations are parallel and that they are not exactly the same
						if(comparator.compare(t1, t2) > 1){
							toRemove.add(t2);
						} else {
							toRemove.add(t1);
						}
					}
				}

			}
		}
		
		//Strategy for every destination pairing d1->d2 and d2->d1 treated specially. flag all but the top route. 
		/**
		for(Destination d1: destinations){ //d1->d2
			for(Destination d2: destinations){
				if(d1==d2) continue;
				Set<TransportRoute> routesBetween = new HashSet<TransportRoute>();
				for(TransportRoute t: d1.getPossibleRoutes()){ // find routes from 1 destination to the other the destinations and add them to temp set
					if(t.getTo().equals(d2)) routesBetween.add(t);
				}
				TransportRoute bestRoute = null;
				boolean first = true;
				for(TransportRoute t: routesBetween){ // find best route between the destinations 
					if(first){
						bestRoute = t; 
						first = false; 
						continue;
					}
					if(comparator.compare(t, bestRoute)>1){
						bestRoute = t;
					}
				}
				for(TransportRoute t: routesBetween){ // flag all but best route
					if(!(t.equals(bestRoute))){
						toRemove.add(t);
					}
				}
			}
		}
		*/
		//System.out.println(routes.size());
		//System.out.println(toRemove.size());
		// return a new set without the flagged routes
		Set<TransportRoute> alteredRoutes = new HashSet<TransportRoute>();
		for(TransportRoute t: routes){
			if(!(toRemove.contains(t))){
				alteredRoutes.add(t);
			}
		}
		return alteredRoutes;
	}

	/**
	 * Searches the given set of search nodes for the one with the lowest cost
	 * @param nodes - set of nodes
	 * @return
	 */
	public SearchNode getLowestCostSearchNode(Set<SearchNode> nodes){
		double minCost = Double.MAX_VALUE;
		SearchNode minNode = null;
		for(SearchNode s: nodes){
			if(s.pathLength < minCost){
				minCost = s.pathLength;
				minNode = s;
			}
		}
		return minNode;
	}

	/**
	 * Searches the given set of routes for the lowest cost option given the mail.
	 * @param routes - set of routes
	 * @param m - mail to use
	 * @return
	 */
	public TransportRoute getLowestCostRoute(Set<TransportRoute> routes, Mail m){
		double minCost = Double.MAX_VALUE;
		TransportRoute minRoute = null;
		for(TransportRoute t: routes){
			if(t.calculateCost(m.weight, m.volume)<minCost){
				minCost = t.calculateCost(m.weight, m.volume);
				minRoute = t;
			}
		}
		return minRoute;
	}
	/**
	 * Returns whether or not the two routes are parallel which means same to and from destinations
	 *
	 * @param t1 - first route to compare
	 * @param t2 - second route to compare
	 * @return true if parallel, false otherwise
	 */
	public boolean checkParallelRoute(TransportRoute t1, TransportRoute t2){
		return t1.getTo().equals(t2.getTo()) && t1.getFrom().equals(t2.getType());
	}

	/**
	 * A node used for path finding algorithms.
	 *
	 * @author Jageun
	 *
	 */
	public class SearchNode {

		public Destination node;
		public double pathLength;
		public SearchNode pathFrom;
		public TransportRoute route;

		public SearchNode(Destination node, SearchNode pathFrom, double pathLength, TransportRoute route) {
			this.node = node;
			this.pathLength = pathLength;
			this.pathFrom = pathFrom;
			this.route = route;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((node == null) ? 0 : node.hashCode());
			result = prime * result + ((pathFrom == null) ? 0 : pathFrom.hashCode());
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
			SearchNode other = (SearchNode) obj;
			if (node == null) {
				if (other.node != null)
					return false;
			} else if (!node.equals(other.node))
				return false;
			if (pathFrom == null) {
				if (other.pathFrom != null)
					return false;
			} else if (!pathFrom.equals(other.pathFrom))
				return false;
			return true;
		}
	}


	//<<---------------------------------- SEARCH ALGORITHM ABOVE ---------------------------------->>

	//<<---------------------------------- TRANSPORT COMPARATORS BELOW ---------------------------------->>


	/**
	 * Returns the correct comparator for sorting routes for the given mail.
	 *
	 * @param m - mail
	 * @return TransportComparator for given mail's priority, null if none can be found
	 */

	public TransportComparator getTransportComparator(Mail m){
		if(m.priority == MailPriority.INTERNATIONAL_AIR){
			return new InternationalAirComparator(m);
		} else if(m.priority == MailPriority.INTERNATIONAL_STANDARD){
			return new InternationalStandardComparator(m);
		} else if(m.priority == MailPriority.DOMESTIC_AIR){
			return new DomesticAirComparator(m);
		} else if(m.priority == MailPriority.DOMESTIC_STANDARD){
			return new DomesticStandardComparator(m);
		}
		return null;
	}

	/**
	 * A TransportRoute comparator for use in sorting and graph manipulation.
	 *
	 * @author Jageun
	 *
	 */
	public abstract class TransportComparator {
		Mail mail;

		/**
		 * Creates transport comparator for given mail.
		 * @param m
		 */
		public TransportComparator(Mail m){
			mail = m;
		}
		/**
		 * Evaluates whether or not transport route is valid
		 *
		 * @return true if route is valid, false otherwise
		 */
		public abstract boolean checkValid(TransportRoute t);

		/**
		 * Compares two transport routes priority
		 *
		 * @param t1 - first route
		 * @param t2 - second route
		 * @return 1 if t1>t2, -1 if t1<t2, 0 if t1=t2
		 */
		public abstract int compare(TransportRoute t1, TransportRoute t2);
	}

	/**
	 * International Air Priority
	 * International routes allowed but must be by air, comparisons are by cost.
	 *
	 * @author Jageun
	 */
	public class InternationalAirComparator extends TransportComparator{

		public InternationalAirComparator(Mail m){
			super(m);
		}

		@Override
		public boolean checkValid(TransportRoute t) {
			if(t.getType() == RouteType.AIR){
				return true;
			}
			return false;
		}

		@Override
		public int compare(TransportRoute t1, TransportRoute t2) {
			if(t1.calculateCost(mail.weight, mail.volume) < t2.calculateCost(mail.weight, mail.volume)){
				return 1;
			} else if(t1.calculateCost(mail.weight, mail.volume) > t2.calculateCost(mail.weight, mail.volume)){
				return -1;
			} else {
				return 0;
			}

		}

	}

	/**
	 * International Standard Priority
	 * International routes allowed, any type of route is available to use but prioritize LAND and SEA over air, secondary comparisons with cost.
	 *
	 * @author Jageun
	 */
	public class InternationalStandardComparator extends TransportComparator{

		public InternationalStandardComparator(Mail m){
			super(m);
		}

		@Override
		public boolean checkValid(TransportRoute t) {
			return true;
		}

		@Override
		public int compare(TransportRoute t1, TransportRoute t2) {

			if((t1.getType() == RouteType.SEA || t1.getType() == RouteType.LAND) && t2.getType() == RouteType.AIR){
				//t1 has higher priority over t2 due to type.
				return 1;
			} else if ((t2.getType() == RouteType.SEA || t2.getType() == RouteType.LAND) && t1.getType() == RouteType.AIR){
				//t2 has higher priority over t1 due to type
				return -1;
			}
			// both t1 and t2 are prioritised types/ same type, compare by cost.
			if(t1.calculateCost(mail.weight, mail.volume) < t2.calculateCost(mail.weight, mail.volume)){
				return 1;
			} else if(t1.calculateCost(mail.weight, mail.volume) > t2.calculateCost(mail.weight, mail.volume)){
				return -1;
			} else {
				return 0;
			}

		}

	}

	/**
	 * Domestic Air Priority
	 * Only domestic air routes allowed, comparisons are with cost.
	 *
	 * @author Jageun
	 */
	public class DomesticAirComparator extends TransportComparator{

		public DomesticAirComparator(Mail m){
			super(m);
		}

		@Override
		public boolean checkValid(TransportRoute t) {
			//check if domestic route and air
			if(!(t.getTo().getCountry().toLowerCase().equals(TransportMap.domesticCountry.toLowerCase()))){
				return false;
			} else if(!(t.getFrom().getCountry().toLowerCase().equals(TransportMap.domesticCountry.toLowerCase()))){
				return false;
			} else if(!(t.getType().equals(RouteType.AIR))){
				return false;
			} else {
				return true;
			}
		}

		@Override
		public int compare(TransportRoute t1, TransportRoute t2) {
			if(t1.calculateCost(mail.weight, mail.volume) < t2.calculateCost(mail.weight, mail.volume)){
				return 1;
			} else if(t1.calculateCost(mail.weight, mail.volume) > t2.calculateCost(mail.weight, mail.volume)){
				return -1;
			} else {
				return 0;
			}

		}

	}

	/**
	 * Domestic Standard Priority
	 * Domestic routes only, any type of route is available to use but prioritize LAND and SEA over air, secondary comparisons with cost.
	 *
	 * @author Jageun
	 */
	public class DomesticStandardComparator extends TransportComparator{

		public DomesticStandardComparator(Mail m){
			super(m);
		}

		@Override
		public boolean checkValid(TransportRoute t) {
			if(!(t.getTo().getCountry().toLowerCase().equals(TransportMap.domesticCountry.toLowerCase()))){
				return false;
			} else if(!(t.getFrom().getCountry().toLowerCase().equals(TransportMap.domesticCountry.toLowerCase()))){
				return false;
			} else {
				return true;
			}
		}

		@Override
		public int compare(TransportRoute t1, TransportRoute t2) {

			if((t1.getType() == RouteType.SEA || t1.getType() == RouteType.LAND) && t2.getType() == RouteType.AIR){
				//t1 has higher priority over t2 due to type.
				return 1;
			} else if ((t2.getType() == RouteType.SEA || t2.getType() == RouteType.LAND) && t1.getType() == RouteType.AIR){
				//t2 has higher priority over t1 due to type
				return -1;
			}
			// both t1 and t2 are prioritised types/ same type, compare by cost.
			if(t1.calculateCost(mail.weight, mail.volume) < t2.calculateCost(mail.weight, mail.volume)){
				return 1;
			} else if(t1.calculateCost(mail.weight, mail.volume) > t2.calculateCost(mail.weight, mail.volume)){
				return -1;
			} else {
				return 0;
			}

		}

	}
	//<<---------------------------------- TRANSPORT COMPARATORS ABOVE ---------------------------------->>
}
