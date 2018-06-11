package gdsc.core.filters;

import java.awt.Rectangle;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import gdsc.core.TestSettings;
import gdsc.core.test.BaseTimingTask;
import gdsc.core.test.TimingService;
import gdsc.core.utils.Random;
import gdsc.core.utils.Statistics;
import ij.process.FloatProcessor;
import ij.process.ImageStatistics;

public class AreaStatisticsTest
{
	boolean[] rolling = new boolean[] { true, false };
	int[] boxSizes = new int[] { 15, 9, 5, 3, 2, 1 };
	int maxx = 200, maxy = 300;

	@Test
	public void canComputeGlobalStatistics()
	{
		float[] data = createData(new Well19937c());
		Statistics s = new Statistics(data);
		AreaStatistics a = new AreaStatistics(data, maxx, maxy);
		for (boolean r : rolling)
		{
			a.setRollingSums(r);
			double[] o = a.getStatistics(0, 0, maxy);
			Assert.assertEquals(s.getN(), o[AreaStatistics.N], 0);
			Assert.assertEquals(s.getSum(), o[AreaStatistics.SUM], 1e-6);
			Assert.assertEquals(s.getStandardDeviation(), o[AreaStatistics.SD], 1e-6);

			o = a.getStatistics(new Rectangle(maxx, maxy));
			Assert.assertEquals(s.getN(), o[AreaStatistics.N], 0);
			Assert.assertEquals(s.getSum(), o[AreaStatistics.SUM], 1e-6);
			Assert.assertEquals(s.getStandardDeviation(), o[AreaStatistics.SD], 1e-6);
		}
	}

	@Test
	public void canComputeNxNRegionStatistics()
	{
		RandomGenerator r = new Well19937c();
		float[] data = createData(r);
		AreaStatistics a1 = new AreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		AreaStatistics a2 = new AreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(10, maxx, r))
			for (int y : Random.sample(10, maxy, r))
				for (int n : boxSizes)
				{
					double[] e = a1.getStatistics(x, y, n);
					double[] o = a2.getStatistics(x, y, n);
					Assert.assertArrayEquals(e, o, 1e-6);
					//System.out.printf("%s vs %s\n", toString(e), toString(o));

					// Check with ImageJ
					fp.setRoi(new Rectangle(x - n, y - n, 2 * n + 1, 2 * n + 1));
					ImageStatistics s = fp.getStatistics();

					Assert.assertEquals(s.area, o[AreaStatistics.N], 0);
					double sum = s.mean * s.area;
					Assert.assertEquals(sum, o[AreaStatistics.SUM], sum * 1e-6);
					Assert.assertEquals(s.stdDev, o[AreaStatistics.SD], s.stdDev * 1e-6);
				}
	}

	@Test
	public void canComputeNxMRegionStatistics()
	{
		RandomGenerator r = new Well19937c();
		float[] data = createData(r);
		AreaStatistics a1 = new AreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		AreaStatistics a2 = new AreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(10, maxx, r))
			for (int y : Random.sample(10, maxy, r))
				for (int nx : boxSizes)
					for (int ny : boxSizes)
					{
						double[] e = a1.getStatistics(x, y, nx, ny);
						double[] o = a2.getStatistics(x, y, nx, ny);
						Assert.assertArrayEquals(e, o, 1e-6);
						//System.out.printf("%s vs %s\n", toString(e), toString(o));

						// Check with ImageJ
						fp.setRoi(new Rectangle(x - nx, y - ny, 2 * nx + 1, 2 * ny + 1));
						ImageStatistics s = fp.getStatistics();

						Assert.assertEquals(s.area, o[AreaStatistics.N], 0);
						Assert.assertEquals(s.mean * s.area, o[AreaStatistics.SUM], 1e-6);
						Assert.assertEquals(s.stdDev, o[AreaStatistics.SD], 1e-6);
					}
	}

	@Test
	public void canComputeRectangleRegionStatistics()
	{
		RandomGenerator r = new Well19937c();
		float[] data = createData(r);
		AreaStatistics a1 = new AreaStatistics(data, maxx, maxy);
		a1.setRollingSums(true);
		AreaStatistics a2 = new AreaStatistics(data, maxx, maxy);
		a2.setRollingSums(false);

		int width = 10, height = 12;
		Rectangle roi = new Rectangle(width, height);

		FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

		for (int x : Random.sample(10, maxx - width, r))
			for (int y : Random.sample(10, maxy - height, r))
			{
				roi.x = x;
				roi.y = y;
				double[] e = a1.getStatistics(roi);
				double[] o = a2.getStatistics(roi);
				Assert.assertArrayEquals(e, o, 1e-6);
				//System.out.printf("%s vs %s\n", toString(e), toString(o));

				// Check with ImageJ
				fp.setRoi(roi);
				ImageStatistics s = fp.getStatistics();

				Assert.assertEquals(s.area, o[AreaStatistics.N], 0);
				Assert.assertEquals(s.mean * s.area, o[AreaStatistics.SUM], 1e-6);
				Assert.assertEquals(s.stdDev, o[AreaStatistics.SD], 1e-6);
			}
	}

	@Test
	public void canComputeStatisticsWithinClippedBounds()
	{
		float[] data = new float[] { 1, 2, 3, 4 };
		AreaStatistics a = new AreaStatistics(data, 2, 2);
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
				Assert.assertEquals(c, o[AreaStatistics.N], 0);
				Assert.assertEquals(u, o[AreaStatistics.SUM], 1e-6);
				Assert.assertEquals(s, o[AreaStatistics.SD], 1e-6);

				Rectangle bounds = new Rectangle(2 * n + 1, 2 * n + 1);
				o = a.getStatistics(bounds);
				Assert.assertEquals(c, o[AreaStatistics.N], 0);
				Assert.assertEquals(u, o[AreaStatistics.SUM], 1e-6);
				Assert.assertEquals(s, o[AreaStatistics.SD], 1e-6);

				bounds.x--;
				bounds.y--;
				o = a.getStatistics(bounds);
				Assert.assertEquals(c, o[AreaStatistics.N], 0);
				Assert.assertEquals(u, o[AreaStatistics.SUM], 1e-6);
				Assert.assertEquals(s, o[AreaStatistics.SD], 1e-6);
			}
		}
	}

	private class MyTimingtask extends BaseTimingTask
	{
		boolean rolling;
		int n;
		float[][] data;
		int[] sample;

		public MyTimingtask(boolean rolling, int n, float[][] data, int[] sample)
		{
			super(((rolling) ? "Rolling" : "Simple") + n);
			this.rolling = rolling;
			this.n = n;
			this.data = data;
			this.sample = sample;
		}

		public int getSize()
		{
			return data.length;
		}

		public Object getData(int i)
		{
			return data[i];
		}

		public Object run(Object data)
		{
			float[] d = (float[]) data;
			AreaStatistics a = new AreaStatistics(d, maxx, maxy);
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
		Assume.assumeTrue(TestSettings.RUN_SPEED_TESTS);

		// Test for sampling half the pixels. Ignore the very small box size
		speedTest(0.5, true, 2, Integer.MAX_VALUE);
	}

	private void speedTest(double density, boolean rollingIsFaster, int minN, int maxN)
	{
		RandomGenerator r = new Well19937c();

		int k = (int) Math.round(maxx * maxy * density);
		int[] x = Random.sample(k, maxx, r);
		int[] y = Random.sample(k, maxy, r);
		int[] sample = new int[k * 2];
		for (int i = 0, j = 0; i < x.length; i++)
		{
			sample[j++] = x[i];
			sample[j++] = y[i];
		}

		float[][] data = new float[10][];
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
		ts.report(size);
		for (int i = ts.getSize(); i > 0; i -= 2)
		{
			Assert.assertEquals(ts.get(i - 2).getMean() < ts.get(i - 1).getMean(), rollingIsFaster);
		}
	}

	private float[] createData(RandomGenerator r)
	{
		float[] d = new float[maxx * maxy];
		for (int i = 0; i < d.length; i++)
			d[i] = r.nextFloat();
		return d;
	}

	static String toString(double[] d)
	{
		return java.util.Arrays.toString(d);
	}
}
