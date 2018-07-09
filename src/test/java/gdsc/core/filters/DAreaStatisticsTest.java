/*-
 * #%L
 * Genome Damage and Stability Centre ImageJ Core Package
 * 
 * Contains code used by:
 * 
 * GDSC ImageJ Plugins - Microscopy image analysis
 * 
 * GDSC SMLM ImageJ Plugins - Single molecule localisation microscopy (SMLM)
 * %%
 * Copyright (C) 2011 - 2018 Alex Herbert
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package gdsc.core.filters;

import java.awt.Rectangle;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

import gdsc.core.utils.Random;
import gdsc.core.utils.Statistics;
import gdsc.test.BaseTimingTask;
import gdsc.test.TestAssert;
import gdsc.test.TestSettings;
import gdsc.test.TimingService;
import gdsc.test.TestSettings.LogLevel;
import ij.process.FloatProcessor;
import ij.process.ImageStatistics;

@SuppressWarnings({"javadoc"})
public class DAreaStatisticsTest
{
	boolean[] rolling = new boolean[] { true, false };
	int[] boxSizes = new int[] { 15, 9, 5, 3, 2, 1 };
	int maxx = 97, maxy = 101;

	@Test
	public void canComputeGlobalStatistics()
	{
		double[] data = createData(TestSettings.getRandomGenerator());
		Statistics s = new Statistics(data);
		DAreaStatistics a = new DAreaStatistics(data, maxx, maxy);
		for (boolean r : rolling)
		{
			a.setRollingSums(r);
			double[] o = a.getStatistics(0, 0, maxy);
			Assert.assertEquals(s.getN(), o[DAreaSum.N], 0);
			TestAssert.assertEqualsRelative(s.getSum(), o[DAreaSum.SUM], 1e-6);
			TestAssert.assertEqualsRelative(s.getStandardDeviation(), o[DAreaStatistics.SD], 1e-6);

			o = a.getStatistics(new Rectangle(maxx, maxy));
			Assert.assertEquals(s.getN(), o[DAreaSum.N], 0);
			TestAssert.assertEqualsRelative(s.getSum(), o[DAreaSum.SUM], 1e-6);
			TestAssert.assertEqualsRelative(s.getStandardDeviation(), o[DAreaStatistics.SD], 1e-6);
		}
	}

	@Test
	public void canComputeNxNRegionStatistics()
	{
		RandomGenerator r = TestSettings.getRandomGenerator();
		double[] data = createData(r);
		DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(5, maxx, r))
			for (int y : Random.sample(5, maxy, r))
				for (int n : boxSizes)
				{
					double[] e = a1.getStatistics(x, y, n);
					double[] o = a2.getStatistics(x, y, n);
					TestAssert.assertArrayEqualsRelative(e, o, 1e-6);
					//TestSettings.debug("%s vs %s\n", toString(e), toString(o));

					// Check with ImageJ
					fp.setRoi(new Rectangle(x - n, y - n, 2 * n + 1, 2 * n + 1));
					ImageStatistics s = fp.getStatistics();

					Assert.assertEquals(s.area, o[DAreaSum.N], 0);
					double sum = s.mean * s.area;
					TestAssert.assertEqualsRelative(sum, o[DAreaSum.SUM], 1e-6);
					TestAssert.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
				}
	}

	@Test
	public void canComputeNxMRegionStatistics()
	{
		RandomGenerator r = TestSettings.getRandomGenerator();
		double[] data = createData(r);
		DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(5, maxx, r))
			for (int y : Random.sample(5, maxy, r))
				for (int nx : boxSizes)
					for (int ny : boxSizes)
					{
						double[] e = a1.getStatistics(x, y, nx, ny);
						double[] o = a2.getStatistics(x, y, nx, ny);
						TestAssert.assertArrayEqualsRelative(e, o, 1e-6);
						//TestSettings.debug("%s vs %s\n", toString(e), toString(o));

						// Check with ImageJ
						fp.setRoi(new Rectangle(x - nx, y - ny, 2 * nx + 1, 2 * ny + 1));
						ImageStatistics s = fp.getStatistics();

						Assert.assertEquals(s.area, o[DAreaSum.N], 0);
						double sum = s.mean * s.area;
						TestAssert.assertEqualsRelative(sum, o[DAreaSum.SUM], 1e-6);
						TestAssert.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
					}
	}

	@Test
	public void canComputeRectangleRegionStatistics()
	{
		RandomGenerator r = TestSettings.getRandomGenerator();
		double[] data = createData(r);
		DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		int width = 10, height = 12;
		Rectangle roi = new Rectangle(width, height);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(5, maxx - width, r))
			for (int y : Random.sample(5, maxy - height, r))
			{
				roi.x = x;
				roi.y = y;
				double[] e = a1.getStatistics(roi);
				double[] o = a2.getStatistics(roi);
				TestAssert.assertArrayEqualsRelative(e, o, 1e-6);
				//TestSettings.debug("%s vs %s\n", toString(e), toString(o));

				// Check with ImageJ
				fp.setRoi(roi);
				ImageStatistics s = fp.getStatistics();

				Assert.assertEquals(s.area, o[DAreaSum.N], 0);
				TestAssert.assertEqualsRelative(s.mean * s.area, o[DAreaSum.SUM], 1e-6);
				TestAssert.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
			}
	}

	@Test
	public void canComputeStatisticsWithinClippedBounds()
	{
		double[] data = new double[] { 1, 2, 3, 4 };
		DAreaStatistics a = new DAreaStatistics(data, 2, 2);
		Statistics stats = new Statistics(data);
		int c = stats.getN();
		double u = stats.getSum();
		double s = stats.getStandardDeviation();
		for (boolean r : rolling)
		{
			a.setRollingSums(r);
			for (int n : boxSizes)
			{
				double[] o = a.getStatistics(0, 0, n);
				Assert.assertEquals(c, o[DAreaSum.N], 0);
				TestAssert.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
				TestAssert.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);

				Rectangle bounds = new Rectangle(2 * n + 1, 2 * n + 1);
				o = a.getStatistics(bounds);
				Assert.assertEquals(c, o[DAreaSum.N], 0);
				TestAssert.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
				TestAssert.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);

				bounds.x--;
				bounds.y--;
				o = a.getStatistics(bounds);
				Assert.assertEquals(c, o[DAreaSum.N], 0);
				TestAssert.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
				TestAssert.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);
			}
		}
	}

	private class MyTimingtask extends BaseTimingTask
	{
		boolean rolling;
		int n;
		double[][] data;
		int[] sample;

		public MyTimingtask(boolean rolling, int n, double[][] data, int[] sample)
		{
			super(((rolling) ? "Rolling" : "Simple") + n);
			this.rolling = rolling;
			this.n = n;
			this.data = data;
			this.sample = sample;
		}

		@Override
		public int getSize()
		{
			return data.length;
		}

		@Override
		public Object getData(int i)
		{
			return data[i];
		}

		@Override
		public Object run(Object data)
		{
			double[] d = (double[]) data;
			DAreaStatistics a = new DAreaStatistics(d, maxx, maxy);
			a.setRollingSums(rolling);
			for (int i = 0; i < sample.length; i += 2)
				a.getStatistics(sample[i], sample[i + 1], n);
			return null;
		}
	}

	@Test
	public void simpleIsfasterAtLowDensityAndNLessThan10()
	{
		// Test the speed for computing the noise around spots at a density of roughly 1 / 100 pixels.
		speedTest(1.0 / 100, false, 1, 10);
	}

	@Test
	public void simpleIsfasterAtMediumDensityAndNLessThan5()
	{
		// Test the speed for computing the noise around each 3x3 box 
		// using a region of 3x3 (n=1) to 9x9 (n=4)		
		speedTest(1.0 / 9, false, 1, 4);
	}

	@Test
	public void rollingIsfasterAtHighDensity()
	{
		// Since this is a slow test
		TestSettings.assumeSpeedTest();

		// Test for sampling half the pixels. Ignore the very small box size
		speedTest(0.5, true, 2, Integer.MAX_VALUE);
	}

	private void speedTest(double density, boolean rollingIsFaster, int minN, int maxN)
	{
		RandomGenerator r = TestSettings.getRandomGenerator();

		int k = (int) Math.round(maxx * maxy * density);
		int[] x = Random.sample(k, maxx, r);
		int[] y = Random.sample(k, maxy, r);
		int[] sample = new int[k * 2];
		for (int i = 0, j = 0; i < x.length; i++)
		{
			sample[j++] = x[i];
			sample[j++] = y[i];
		}

		double[][] data = new double[10][];
		for (int i = 0; i < data.length; i++)
			data[i] = createData(r);

		TimingService ts = new TimingService();
		for (int n : boxSizes)
		{
			if (n < minN || n > maxN)
				continue;
			ts.execute(new MyTimingtask(true, n, data, sample));
			ts.execute(new MyTimingtask(false, n, data, sample));
		}
		int size = ts.getSize();
		ts.repeat();
		if (TestSettings.allow(LogLevel.INFO))
			ts.report(size);
		// Do not let this fail the test suite
		//Assert.assertEquals(ts.get(-2).getMean() < ts.get(-1).getMean(), rollingIsFaster);
		TestSettings.logSpeedTestResult(ts.get(-2).getMean() < ts.get(-1).getMean() == rollingIsFaster,
				"DAreaStatistics Density=%g RollingIsFaster=%b N=%d:%d: rolling %s vs simple %s", density, rollingIsFaster, minN, maxN,
				ts.get(-2).getMean(), ts.get(-1).getMean());
	}

	private double[] createData(RandomGenerator r)
	{
		double[] d = new double[maxx * maxy];
		for (int i = 0; i < d.length; i++)
			d[i] = r.nextDouble();
		return d;
	}

	static String toString(double[] d)
	{
		return java.util.Arrays.toString(d);
	}
}
