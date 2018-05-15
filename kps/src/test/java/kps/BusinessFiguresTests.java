package kps.tests;

import org.junit.Assert;
import org.junit.Test;

import kps.server.BusinessFigures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BusinessFiguresTests {
	@Test
	public void testInitialState() {
		BusinessFigures kps = new BusinessFigures();
		Assert.assertTrue(0 == kps.getRevenue());
		Assert.assertTrue(0 == kps.getExpenditure());
		Assert.assertTrue(0 == kps.getMailCount());
		Assert.assertTrue(0 == kps.getTotalVolume());
		Assert.assertTrue(0 == kps.getTotalWeight());
		Assert.assertTrue(0 == kps.getAverageDeliveryDays());
	}

	@Test
	public void testNegativeRevenue() {
		BusinessFigures kps = new BusinessFigures();
		try {
			kps.sendMail(-1000,1,1,1,1, null);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(0 == kps.getRevenue());
			Assert.assertTrue(0 == kps.getExpenditure());
			Assert.assertTrue(0 == kps.getMailCount());
			Assert.assertTrue(0 == kps.getTotalVolume());
			Assert.assertTrue(0 == kps.getTotalWeight());
			Assert.assertTrue(0 == kps.getAverageDeliveryDays());
			/* pass */
			return;
		}
		Assert.fail("Exception not caught");
	}

	@Test
	public void testNegativeExpenditure() {
		BusinessFigures kps = new BusinessFigures();
		try {
			kps.sendMail(1,-1,1,1,1, null);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(0 == kps.getRevenue());
			Assert.assertTrue(0 == kps.getExpenditure());
			Assert.assertTrue(0 == kps.getMailCount());
			Assert.assertTrue(0 == kps.getTotalVolume());
			Assert.assertTrue(0 == kps.getTotalWeight());
			Assert.assertTrue(0 == kps.getAverageDeliveryDays());
			/* pass */
			return;
		}
		Assert.fail("Exception not caught");
	}

	@Test
	public void testRevenue() {
		BusinessFigures kps = new BusinessFigures();

		kps.sendMail(200, 0, 0, 0, 0, null);
		Assert.assertTrue(1 == kps.getMailCount());
		kps.sendMail(315.25, 0, 0, 0, 0, null);

		/* assert that the change was recorded */
		Assert.assertEquals(515.25, kps.getRevenue(), 0.01);

		/* assert that no splash damage was made */
		Assert.assertTrue(0 == kps.getExpenditure());
		Assert.assertTrue(2 == kps.getMailCount());
		Assert.assertTrue(0 == kps.getTotalVolume());
		Assert.assertTrue(0 == kps.getTotalWeight());
		Assert.assertTrue(0 == kps.getAverageDeliveryDays());
	}

	@Test
	public void testExpenditure() {
		BusinessFigures kps = new BusinessFigures();

		kps.sendMail(0, 123.45, 0, 0, 0, null);
		Assert.assertTrue(1 == kps.getMailCount());
		kps.sendMail(0, 50, 0, 0, 0, null);

		/* assert that the change was recorded */
		Assert.assertTrue(173.45 == kps.getExpenditure());

		/* assert that no splash damage was made */
		Assert.assertTrue(0 == kps.getRevenue());
		Assert.assertTrue(2 == kps.getMailCount());
		Assert.assertTrue(0 == kps.getTotalVolume());
		Assert.assertTrue(0 == kps.getTotalWeight());
		Assert.assertTrue(0 == kps.getAverageDeliveryDays());
	}

}
