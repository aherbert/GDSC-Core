package gdsc.core.math;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;
import org.junit.Assert;
import org.junit.Test;

import gdsc.core.math.RollingArrayMoment;
import gdsc.core.utils.Statistics;

public class ArrayMomentTest
{
	final double DELTA = 1e-8;
	
	@Test
	public void canComputeRollingMomentDouble()
	{
		canComputeMoment("Single", new double[] { Math.PI }, new RollingArrayMoment());

		RandomGenerator rand = new Well19937c();
		double[] d = new double[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextDouble();
		canComputeMoment("Uniform", d, new RollingArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextGaussian();
		canComputeMoment("Gaussian", d, new RollingArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new RollingArrayMoment());
	}

	@Test
	public void canComputeRollingMomentFloat()
	{
		canComputeMoment("Single", new float[] { (float) Math.PI }, new RollingArrayMoment());

		RandomGenerator rand = new Well19937c();
		float[] d = new float[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextFloat();
		canComputeMoment("Uniform", d, new RollingArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = (float) rand.nextGaussian();
		canComputeMoment("Gaussian", d, new RollingArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new RollingArrayMoment());
	}

	@Test
	public void canComputeRollingMomentInt()
	{
		canComputeMoment("Single", new int[] { 42 }, new RollingArrayMoment());

		RandomGenerator rand = new Well19937c();
		int[] d = new int[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextInt();
		canComputeMoment("Uniform", d, new RollingArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new RollingArrayMoment());
	}

	@Test
	public void canComputeRollingArrayMomentDouble()
	{
		RandomGenerator rand = new Well19937c();
		double[][] d = new double[3][];

		for (int i = d.length; i-- > 0;)
			d[i] = new double[] { rand.nextDouble() };
		canComputeArrayMoment("Single", d, new RollingArrayMoment());

		int n = 1000;
		for (int i = d.length; i-- > 0;)
			d[i] = uniform(rand, n);
		canComputeArrayMoment("Uniform", d, new RollingArrayMoment());
	}

	@Test
	public void canCombineRollingArrayMomentDouble()
	{
		RandomGenerator rand = new Well19937c();
		double[][] d = new double[50][];

		int n = 1000;
		for (int i = d.length; i-- > 0;)
			d[i] = uniform(rand, n);

		RollingArrayMoment r1 = new RollingArrayMoment();
		int size = 6;
		RollingArrayMoment[] r2 = new RollingArrayMoment[size];
		for (int i = 0; i < size; i++)
			r2[i] = new RollingArrayMoment();
		for (int i = 0; i < d.length; i++)
		{
			r1.add(d[i]);
			r2[i % size].add(d[i]);
		}

		double[] em1 = r1.getFirstMoment();
		double[] em2 = r1.getSecondMoment();
		double[] ev = r1.getVariance();
		double[] esd = r1.getStandardDeviation();

		for (int i = 1; i < size; i++)
			r2[0].add(r2[i]);

		double[] om1 = r2[0].getFirstMoment();
		double[] om2 = r2[0].getSecondMoment();
		double[] ov = r2[0].getVariance();
		double[] osd = r2[0].getStandardDeviation();

		assertArrayEquals("Mean", em1, om1, DELTA);
		assertArrayEquals("2nd Moment", em2, om2, DELTA);
		assertArrayEquals("Variance", ev, ov, DELTA);
		assertArrayEquals("SD", esd, osd, DELTA);
	}

	// Copy to here
	
	@Test
	public void canComputeSimpleMomentDouble()
	{
		canComputeMoment("Single", new double[] { Math.PI }, new SimpleArrayMoment());

		RandomGenerator rand = new Well19937c();
		double[] d = new double[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextDouble();
		canComputeMoment("Uniform", d, new SimpleArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextGaussian();
		canComputeMoment("Gaussian", d, new SimpleArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new SimpleArrayMoment());
	}

	@Test
	public void canComputeSimpleMomentFloat()
	{
		canComputeMoment("Single", new float[] { (float) Math.PI }, new SimpleArrayMoment());

		RandomGenerator rand = new Well19937c();
		float[] d = new float[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextFloat();
		canComputeMoment("Uniform", d, new SimpleArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = (float) rand.nextGaussian();
		canComputeMoment("Gaussian", d, new SimpleArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new SimpleArrayMoment());
	}

	@Test
	public void canComputeSimpleMomentInt()
	{
		canComputeMoment("Single", new int[] { 42 }, new SimpleArrayMoment());

		RandomGenerator rand = new Well19937c();
		int[] d = new int[1000];

		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextInt();
		canComputeMoment("Uniform", d, new SimpleArrayMoment());

		for (int i = 0; i < d.length; i++)
			d[i] = i;
		canComputeMoment("Series", d, new SimpleArrayMoment());
	}

	@Test
	public void canComputeSimpleArrayMomentDouble()
	{
		RandomGenerator rand = new Well19937c();
		double[][] d = new double[3][];

		for (int i = d.length; i-- > 0;)
			d[i] = new double[] { rand.nextDouble() };
		canComputeArrayMoment("Single", d, new SimpleArrayMoment());

		int n = 1000;
		for (int i = d.length; i-- > 0;)
			d[i] = uniform(rand, n);
		canComputeArrayMoment("Uniform", d, new SimpleArrayMoment());
	}

	@Test
	public void canCombineSimpleArrayMomentDouble()
	{
		RandomGenerator rand = new Well19937c();
		double[][] d = new double[50][];

		int n = 1000;
		for (int i = d.length; i-- > 0;)
			d[i] = uniform(rand, n);

		SimpleArrayMoment r1 = new SimpleArrayMoment();
		int size = 6;
		SimpleArrayMoment[] r2 = new SimpleArrayMoment[size];
		for (int i = 0; i < size; i++)
			r2[i] = new SimpleArrayMoment();
		for (int i = 0; i < d.length; i++)
		{
			r1.add(d[i]);
			r2[i % size].add(d[i]);
		}

		double[] em1 = r1.getFirstMoment();
		double[] em2 = r1.getSecondMoment();
		double[] ev = r1.getVariance();
		double[] esd = r1.getStandardDeviation();

		for (int i = 1; i < size; i++)
			r2[0].add(r2[i]);

		double[] om1 = r2[0].getFirstMoment();
		double[] om2 = r2[0].getSecondMoment();
		double[] ov = r2[0].getVariance();
		double[] osd = r2[0].getStandardDeviation();

		assertArrayEquals("Mean", em1, om1, DELTA);
		assertArrayEquals("2nd Moment", em2, om2, DELTA);
		assertArrayEquals("Variance", ev, ov, DELTA);
		assertArrayEquals("SD", esd, osd, DELTA);
	}
	
	private void canComputeMoment(String title, double[] d, ArrayMoment r2)
	{
		Statistics m1 = new Statistics();
		m1.add(d);
		SecondMoment m2 = new SecondMoment();
		m2.incrementAll(d);
		for (int i = 0; i < d.length; i++)
			r2.add(new double[] { d[i] });
		assertEquals(title + " Mean", m1.getMean(), r2.getFirstMoment()[0], DELTA);
		assertEquals(title + " 2nd Moment", m2.getResult(), r2.getSecondMoment()[0], DELTA);
		assertEquals(title + " Variance", m1.getVariance(), r2.getVariance()[0], DELTA);
		assertEquals(title + " SD", m1.getStandardDeviation(), r2.getStandardDeviation()[0], DELTA);
	}

	private void canComputeMoment(String title, float[] d, ArrayMoment r2)
	{
		Statistics m1 = new Statistics();
		m1.add(d);
		SecondMoment m2 = new SecondMoment();
		m2.incrementAll(toDouble(d));
		for (int i = 0; i < d.length; i++)
			r2.add(new double[] { d[i] });
		assertEquals(title + " Mean", m1.getMean(), r2.getFirstMoment()[0], DELTA);
		assertEquals(title + " 2nd Moment", m2.getResult(), r2.getSecondMoment()[0], DELTA);
		assertEquals(title + " Variance", m1.getVariance(), r2.getVariance()[0], DELTA);
		assertEquals(title + " SD", m1.getStandardDeviation(), r2.getStandardDeviation()[0], DELTA);
	}

	private double[] toDouble(float[] in)
	{
		double[] d = new double[in.length];
		for (int i = 0; i < d.length; i++)
			d[i] = in[i];
		return d;
	}
	
	private void canComputeMoment(String title, int[] d, ArrayMoment r2)
	{
		Statistics m1 = new Statistics();
		m1.add(d);
		SecondMoment m2 = new SecondMoment();
		m2.incrementAll(toDouble(d));
		for (int i = 0; i < d.length; i++)
			r2.add(new double[] { d[i] });
		assertEquals(title + " Mean", m1.getMean(), r2.getFirstMoment()[0], DELTA);
		assertEquals(title + " 2nd Moment", m2.getResult(), r2.getSecondMoment()[0], DELTA);
		assertEquals(title + " Variance", m1.getVariance(), r2.getVariance()[0], DELTA);
		assertEquals(title + " SD", m1.getStandardDeviation(), r2.getStandardDeviation()[0], DELTA);
	}

	private double[] toDouble(int[] in)
	{
		double[] d = new double[in.length];
		for (int i = 0; i < d.length; i++)
			d[i] = in[i];
		return d;
	}

	private double[] uniform(RandomGenerator rand, int n)
	{
		double[] d = new double[n];
		for (int i = 0; i < d.length; i++)
			d[i] = rand.nextDouble();
		return d;
	}

	private void canComputeArrayMoment(String title, double[][] d, ArrayMoment r2)
	{
		for (int i = 0; i < d.length; i++)
			r2.add(d[i]);
		double[] om1 = r2.getFirstMoment();
		double[] om2 = r2.getSecondMoment();
		double[] ov = r2.getVariance();
		double[] osd = r2.getStandardDeviation();

		for (int n = d[0].length; n-- > 0;)
		{
			Statistics m1 = new Statistics();
			SecondMoment m2 = new SecondMoment();
			for (int i = 0; i < d.length; i++)
			{
				m1.add(d[i][n]);
				m2.increment(d[i][n]);
			}
			assertEquals(title + " Mean", m1.getMean(), om1[n], DELTA);
			assertEquals(title + " 2nd Moment", m2.getResult(), om2[n], DELTA);
			assertEquals(title + " Variance", m1.getVariance(), ov[n], DELTA);
			assertEquals(title + " SD", m1.getStandardDeviation(), osd[n], DELTA);
		}
	}

	//@Test
	public void canComputeMomentForLargeSeries()
	{
		RandomGenerator rand = new Well19937c();

		SimpleArrayMoment m1 = new SimpleArrayMoment();
		SecondMoment m2 = new SecondMoment();
		RollingArrayMoment r2 = new RollingArrayMoment();
		
		// Test if the standard Statistics object is good enough for 
		// computing the mean and variance of sCMOS data from 60,000 frames. It seems it is.
		for (int i = 600000; i-- > 0;)
		{
			double d = 100.345 + rand.nextGaussian() * Math.PI;
			m1.add(d);
			m2.increment(d);
			r2.add(d);
		}
		System.out.printf("Mean %s vs %s, SD %s vs %s\n", Double.toString(m1.getFirstMoment()[0]),
				Double.toString(r2.getFirstMoment()[0]), Double.toString(m1.getStandardDeviation()[0]),
				Double.toString(r2.getStandardDeviation()[0]));
		assertEquals("Mean", m1.getFirstMoment()[0], r2.getFirstMoment()[0], DELTA);
		assertEquals("2nd Moment", m2.getResult(), r2.getSecondMoment()[0], 0);
	}

	private void assertEquals(String msg, double e, double o, double delta)
	{
		Assert.assertEquals(msg, e, o, Math.abs(e * delta));
	}

	private void assertArrayEquals(String msg, double[] e, double[] o, double delta)
	{
		for (int i = 0; i < e.length; i++)
			assertEquals(msg, e[i], o[i], Math.abs(e[i] * delta));
	}
}
