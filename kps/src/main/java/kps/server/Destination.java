package kps.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;
@ParametersAreNonnullByDefault
public class Destination implements Serializable {
	private static final long serialVersionUID = 1L;
	public final String city;
	public final String country;
	private Set<TransportRoute> possibleRoutes = new HashSet<TransportRoute>();
	
	public Destination(String city_country) 
			throws IllegalArgumentException {
		super();
		String[] split = city_country.split(",");
		if (split.length != 2) {
			throw new IllegalArgumentException("Expected 2 segments in destination, got "+
					split.length);
		}
		this.city = split[0].trim();
		this.country = split[1].trim();
	}
	
	public Destination(String city, String country) {
		super();
		this.city = city.trim();
		this.country = country.trim();
	}
	
	public Set<TransportRoute> getPossibleRoutes(){
		Set<TransportRoute> routes = new HashSet<TransportRoute>();
		for(TransportRoute t: this.possibleRoutes){
			routes.add(t);
		}
		return routes;
	}
	
	public boolean addPossibleRoute(TransportRoute t){
		 return this.possibleRoutes.add(t);
	}
	
	public boolean removePossibleRoute(TransportRoute t){
		return this.possibleRoutes.remove(t);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.toLowerCase().hashCode());
		result = prime * result + ((country == null) ? 0 : country.toLowerCase().hashCode());
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
		Destination other = (Destination) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.toLowerCase().equals(other.city.toLowerCase()))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.toLowerCase().equals(other.country.toLowerCase()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return city+", "+country;
	}

	public String getCity() {
		return this.city;
	}
	
	public String getCountry() {
		return this.country;
	}
}
