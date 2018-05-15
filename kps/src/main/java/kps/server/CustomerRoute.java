package kps.server;

import kps.util.MailPriority;
import java.io.Serializable;

public class CustomerRoute implements Serializable {
	
	public final Destination to;
	public final Destination from;
	public final MailPriority priority;
	
	public double weightToCost;
	public double volumeToCost;

	
	public CustomerRoute(Destination to, Destination from, MailPriority priority, double weightToCost, double volumeToCost){
		super();
		this.to = to;
		this.from = from;
		this.priority = priority;
		this.weightToCost = weightToCost;
		this.volumeToCost = volumeToCost;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		long temp;
		temp = Double.doubleToLongBits(volumeToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(weightToCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CustomerRoute other = (CustomerRoute) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (priority != other.priority)
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (Double.doubleToLongBits(volumeToCost) != Double.doubleToLongBits(other.volumeToCost))
			return false;
		if (Double.doubleToLongBits(weightToCost) != Double.doubleToLongBits(other.weightToCost))
			return false;
		return true;
	}
}
