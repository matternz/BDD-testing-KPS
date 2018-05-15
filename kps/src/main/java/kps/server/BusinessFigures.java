package kps.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for keeping business figures cosy in
 *
 * @author David Phillips
 *
 */
public class BusinessFigures implements Serializable {
	private static final long serialVersionUID = 1L;
	private double totalWeight;
	private double totalVolume;
	private long mailCount;
	private double expenditure;
	private double revenue;
	private double totalDeliveryDays;
	private Map<CustomerRoute, Double> routesToProfit = new HashMap<CustomerRoute, Double>();
	private Map<CustomerRoute, Double> routesToCount = new HashMap<CustomerRoute, Double>(); 

	public BusinessFigures() {
	}

	public void assertPositive(double value, String name) {
		if (value < 0) {
			throw new IllegalArgumentException(name+" must be positive, but was "+value);
		}
	}

	public void sendMail(double revenue, double expenditure, double weight, double volume, double days, CustomerRoute cr) {
		/* zesty! */
		assertPositive(revenue, "Revenue");
		assertPositive(expenditure, "Expenditure/route cost");
		assertPositive(weight, "Mail Weight");
		assertPositive(volume, "Mail Volume");
		assertPositive(days, "Mail delivery time");
		this.revenue += revenue;
		this.expenditure += expenditure;
		this.totalWeight += weight;
		this.totalVolume += volume;
		this.mailCount++;
		totalDeliveryDays += days;
		
		if(cr == null) return;
		// keep track of routes used and profit by each route
		double profit = revenue - expenditure;
		if(routesToProfit.containsKey(cr)){
			routesToProfit.put(cr, routesToProfit.get(cr) + profit);
			routesToCount.put(cr, routesToCount.get(cr)+1.0);
		} else {
			routesToProfit.put(cr, profit);
			routesToCount.put(cr, 1.0);
		}
	}

	public double getExpenditure() {
		return this.expenditure;
	}

	public double getRevenue() {
		return this.revenue;
	}

	public double getTotalWeight() {
		return this.totalWeight;
	}

	public double getTotalVolume() {
		return this.totalVolume;
	}

	public long getMailCount() {
		return this.mailCount;
	}

	public double getAverageDeliveryDays() {
		if (mailCount == 0)
			return 0;
		return this.totalDeliveryDays / this.mailCount;
	}
	
	/**
	 * Returns all customer routes that have been used so far with the average loss per item
	 * 
	 * @return map of routes and the average loss per item send on said route
	 */
	public Map<CustomerRoute, Double> getCriticalRoutes(){
		Map<CustomerRoute, Double> x = new HashMap<CustomerRoute, Double> (); 
		for(CustomerRoute c: routesToProfit.keySet()){
			if(routesToProfit.get(c) < 0){
				x.put(c,routesToProfit.get(c)/routesToCount.get(c)); // average loss
			}
		}
		return x;
	}
}
