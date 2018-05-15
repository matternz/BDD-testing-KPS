package kps.server;

import java.io.Serializable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TransportFirm implements Serializable {
	private static final long serialVersionUID = 1L;
	public final String name;
	
	public TransportFirm(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransportFirm other = (TransportFirm) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
