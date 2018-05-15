package kps.tests;

import org.junit.Assert;
import org.junit.Test;

import kps.util.DateRange;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DateRangeTests {
	private Date dateWrapper(int y, int m, int d) {
		Calendar calen = Calendar.getInstance();
		calen.clear();
		calen.set(Calendar.YEAR, y);
		calen.set(Calendar.MONTH, m);
		calen.set(Calendar.DAY_OF_MONTH, d);
		return calen.getTime();
	}
	
	@Test
	public void testInRange() {
		DateRange dateRange = new DateRange(dateWrapper(1999,12,31), dateWrapper(2010,12,31));
		Date[] candidates = {
				dateWrapper(2005,01,12),
				dateWrapper(2001,01,12),
				dateWrapper(2010,12,01),
				dateWrapper(2000,01,01),
		};
		for (Date d : candidates) {
			Assert.assertTrue(dateRange.containsInclusive(d));
		}
	}

	@Test
	public void testOutOfRange() {
		DateRange dateRange = new DateRange(dateWrapper(1999,12,31), dateWrapper(2010,12,31));
		Date[] candidates = {
				dateWrapper(1985,04,12),
				dateWrapper(2101,01,31),
				dateWrapper(2011,01,01),
				dateWrapper(1999,12,29),
		};
		for (Date d : candidates) {
			Assert.assertFalse(dateRange.containsInclusive(d));
		}
	}

	@Test
	public void testBoundary() {
		DateRange dateRange = new DateRange(dateWrapper(1999,12,31), dateWrapper(2010,12,31));
		Date[] candidates = {
				dateWrapper(1999,12,31),
				dateWrapper(2010,12,31),
		};
		for (Date d : candidates) {
			Assert.assertTrue(dateRange.containsInclusive(d));
		}
	}
	
	@Test
	public void testValidConstructor() {
		DateRange dateRange = new DateRange(dateWrapper(1999,12,31), dateWrapper(2010,12,31));
		Assert.assertEquals(dateRange.getFrom(), dateWrapper(1999,12,31));
		Assert.assertEquals(dateRange.getTo(), dateWrapper(2010,12,31));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testBackwardRange() {
		DateRange dateRange = new DateRange(dateWrapper(2010,12,31), dateWrapper(2010,12,30));
	}
	
	@Test
	public void testZeroRange() {
		DateRange dateRange = new DateRange(dateWrapper(2010,12,31), dateWrapper(2010,12,31));
	}
}
