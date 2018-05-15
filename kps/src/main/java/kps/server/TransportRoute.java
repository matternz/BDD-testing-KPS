package kps.server;

import java.io.Serializable;
import java.time.DayOfWeek;

import javax.annotation.ParametersAreNonnullByDefault;

import kps.util.RouteType;
@ParametersAreNonnullByDefault
public class TransportRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	public final TransportFirm owner;
	public final Destination to; 
	public final Destination from; // no guarantee this destination will have possible routes. Must refer to the destinations set in the map 
	public final RouteType type;
	public final double weightToCost;
	public final double volumeToCost;
	public final double maxWeight;
	public final double maxVolume;
	public final double duration;
	public final double frequency;
	public final DayOfWeek day;
	
	public TransportRoute(Destination to, Destination from, RouteType type, double weightToCost,
			double volumeToCost, double maxWeight, double maxVolume, double duration, double frequency, DayOfWeek day, TransportFirm owner) {
		super();
		this.to = to;
		this.from = from;
		this.type = type;
		this.weightToCost = weightToCost;
		this.volumeToCost = volumeToCost;
		this.maxWeight = maxWeight;
		this.maxVolume = maxVolume;
		this.duration = duration;
		this.frequency = frequency;
		this.day = day;
		this.owner = owner;
	}
	
	public double calculateCost(double weight, double volume){
		double volumeCost = volume*volumeToCost;
		double weightCost = weight*weightToCost;
		if(volumeCost>weightCost) return volumeCost;
		return weightCost;
	}

	public Destination getFrom() {
		return this.from;
	}

	public Destination getTo() {
		return this.to;
	}

	public TransportFirm getOwner() {
		return this.owner;
	}

	public RouteType getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(frequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		temp = Double.doubleToLongBits(maxVolume);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(maxWeight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TransportRoute other = (TransportRoute) obj;
		if (day != other.day)
			return false;
		if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration))
			return false;
		if (Double.doubleToLongBits(frequency) != Double.doubleToLongBits(other.frequency))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (Double.doubleToLongBits(maxVolume) != Double.doubleToLongBits(other.maxVolume))
			return false;
		if (Double.doubleToLongBits(maxWeight) != Double.doubleToLongBits(other.maxWeight))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (type != other.type)
			return false;
		if (Double.doubleToLongBits(volumeToCost) != Double.doubleToLongBits(other.volumeToCost))
			return false;
		if (Double.doubleToLongBits(weightToCost) != Double.doubleToLongBits(other.weightToCost))
			return false;
		return true;
	}
	
	
}
