package kps.util;

import java.io.Serializable;
import java.util.Date;

public class DateRange implements Serializable {
	private Date from;
	private Date to;
	
	public DateRange(Date from, Date to) {
		if (to.compareTo(from) == -1) {
			throw new IllegalArgumentException("'from' must be before 'to'");
		}
		this.from = from;
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}
	
	public Date getTo() {
		return to;
	}
	
	public boolean containsInclusive(Date date) {
		if (date.equals(from) || date.equals(to))
			return true;
		
		return date.compareTo(from) != -1 && date.compareTo(to) != 1;
	}
}
