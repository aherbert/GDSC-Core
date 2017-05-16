package gdsc.core.utils;

import gdsc.core.utils.DoubleEquality;
import gdsc.core.utils.FloatEquality;
import gdsc.core.TestSettings;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class EqualityTest
{
	int MAX_ITER = 2000000;

	@Test
	public void canComputeEquality()
	{
		float maxRelativeError = 1e-2f;
		float maxAbsoluteError = 1e-16f;
		FloatEquality equality = new FloatEquality(maxRelativeError, maxAbsoluteError, 3);

		for (int i = 0; i < 100; i++)
		{
			float f = (float) (i / 10000.0);
			Assert.assertTrue("not equal " + f, equality.almostEqualRelativeOrAbsolute(f, f));
			Assert.assertTrue("not equal " + f,
					equality.almostEqualRelativeOrAbsolute(f, f * (1.00f + maxRelativeError - 1e-3f)));
			if (i > 0)
				Assert.assertFalse("equal " + f,
						equality.almostEqualRelativeOrAbsolute(f, f * (1.0f + 2.0f * maxRelativeError)));
		}

		intBits(100f);
		intBits(10f);
		intBits(1f);
		intBits(1e-1f);
		intBits(1e-2f);
		intBits(1e-3f);
		intBits(1e-4f);
		intBits(1e-5f);
		intBits(1e-6f);
		intBits(1e-7f);
		intBits(1e-8f);
		intBits(1e-9f);
		intBits(1e-10f);
		intBits(1e-11f);
		intBits(1e-12f);
		intBits(1e-13f);
		intBits(1e-14f);
		intBits(1e-15f);
		intBits(1e-16f);
		intBits(1e-26f);
		intBits(1e-36f);

		for (int i = 0; i < 18; i++)
			log("sig = %d -> %d : %d\n", i, FloatEquality.getUlps(i), DoubleEquality.getUlps(i));

		// Simple tests
		Assert.assertEquals(1, DoubleEquality.complement(0, Double.MIN_VALUE));
		Assert.assertEquals(1, DoubleEquality.complement(0, -Double.MIN_VALUE));
		Assert.assertEquals(2, DoubleEquality.complement(-Double.MIN_VALUE, Double.MIN_VALUE));

		// Check the complement is correct around a change of sign
		test(-Double.MAX_VALUE, Double.MAX_VALUE);
		test(-1e10, 1e40);
		test(-1e2, 1e2);
		test(-10, 10);
		test(-1, 1);
		test(-1e-1, 1e-1);
		test(-1e-2, 1e-2);
		test(1e-2, 1e-4);
		test(1e-2, 2e-2);
		test(1.0001, 1.0002);
	}

	private void test(double lower, double upper)
	{
		if (lower > upper)
		{
			double tmp = lower;
			lower = upper;
			upper = tmp;
		}
		long h = DoubleEquality.complement(0, upper);
		long l = DoubleEquality.complement(0, lower);
		long d = (lower > 0) ? h - l : h + l;
		if (d < 0)
			d = Long.MAX_VALUE;
		log("%g - %g = %d\n", upper, lower, d);
		Assert.assertEquals(d, DoubleEquality.complement(lower, upper));
	}

	/**
	 * Used to check what the int difference between float actually is
	 * 
	 * @param f
	 * @param f2
	 */
	private void intBits(float f)
	{
		float f3 = f + f * 1e-2f;
		float f4 = f - f * 1e-2f;
		System.out.printf("%g -> %g = %d : %d (%g : %g)\n", f, f3, FloatEquality.complement(f3, f),
				DoubleEquality.complement(f3, f), FloatEquality.relativeError(f, f3),
				DoubleEquality.relativeError(f, f3));
		System.out.printf("%g -> %g = %d : %d (%g : %g)\n", f, f4, FloatEquality.complement(f4, f),
				DoubleEquality.complement(f4, f), FloatEquality.relativeError(f, f4),
				DoubleEquality.relativeError(f, f4));
	}

	@Test
	public void floatRelativeIsSlowerThanFloatComplement()
	{
		org.junit.Assume.assumeTrue(TestSettings.RUN_SPEED_TESTS);

		float maxRelativeError = 1e-2f;
		float maxAbsoluteError = 1e-16f;
		int significantDigits = 3;
		FloatEquality equality = new FloatEquality(maxRelativeError, maxAbsoluteError, significantDigits);

		// Create data
		Random rand = new Random(30051977);
		float[] data = new float[MAX_ITER];
		float[] data2 = new float[data.length];

		for (int i = 0; i < data.length; i++)
		{
			data[i] = 1e-10f * ((rand.nextFloat() > 0.5) ? 1.001f : 1.1f);
			data2[i] *= (rand.nextFloat() > 0.5) ? 1.001f : 1.1f;
		}

		relative(equality, data, data2);
		complement(equality, data, data2);

		long start1 = System.nanoTime();
		relative(equality, data, data2);
		start1 = System.nanoTime() - start1;

		long start2 = System.nanoTime();
		complement(equality, data, data2);
		start2 = System.nanoTime() - start2;

		log("floatRelative = %d : floatComplement = %d : %fx\n", start1, start2, (1.0 * start1) / start2);
		if (TestSettings.ASSERT_SPEED_TESTS)
			Assert.assertTrue(start2 < start1);
	}

	@Test
	public void doubleRelativeIsSlowerThanFloatComplement()
	{
		org.junit.Assume.assumeTrue(TestSettings.RUN_SPEED_TESTS);

		double maxRelativeError = 1e-2;
		double maxAbsoluteError = 1e-16;
		int significantDigits = 3;
		DoubleEquality equality = new DoubleEquality(maxRelativeError, maxAbsoluteError, significantDigits);

		// Create data
		Random rand = new Random(30051977);
		double[] data = new double[MAX_ITER];
		double[] data2 = new double[data.length];

		for (int i = 0; i < data.length; i++)
		{
			data[i] = 1e-10f * ((rand.nextFloat() > 0.5) ? 1.001f : 1.1f);
			data2[i] *= (rand.nextFloat() > 0.5) ? 1.001f : 1.1f;
		}

		relative(equality, data, data2);
		complement(equality, data, data2);

		long start1 = System.nanoTime();
		relative(equality, data, data2);
		start1 = System.nanoTime() - start1;

		long start2 = System.nanoTime();
		complement(equality, data, data2);
		start2 = System.nanoTime() - start2;

		log("doubleRelative = %d : doubleComplement = %d : %fx\n", start1, start2, (1.0 * start1) / start2);
		if (TestSettings.ASSERT_SPEED_TESTS)
			Assert.assertTrue(start2 < start1);
	}

	@Test
	public void floatRelativeIsSlowerThanDoubleRelative()
	{
		org.junit.Assume.assumeTrue(TestSettings.RUN_SPEED_TESTS);

		float maxRelativeError = 1e-2f;
		float maxAbsoluteError = 1e-16f;
		int significantDigits = 3;
		FloatEquality equality = new FloatEquality(maxRelativeError, maxAbsoluteError, significantDigits);
		DoubleEquality equality2 = new DoubleEquality(maxRelativeError, maxAbsoluteError, significantDigits);

		// Create data
		Random rand = new Random(30051977);
		float[] data = new float[MAX_ITER];
		float[] data2 = new float[data.length];
		double[] data3 = new double[data.length];
		double[] data4 = new double[data.length];

		for (int i = 0; i < data.length; i++)
		{
			data[i] = 1e-10f * ((rand.nextFloat() > 0.5) ? 1.001f : 1.1f);
			data2[i] *= (rand.nextFloat() > 0.5) ? 1.001f : 1.1f;
			data3[i] = data[i];
			data4[i] = data2[i];
		}

		relative(equality, data, data2);
		relative(equality2, data3, data4);

		long start1 = System.nanoTime();
		relative(equality, data, data2);
		start1 = System.nanoTime() - start1;

		long start2 = System.nanoTime();
		relative(equality2, data3, data4);
		start2 = System.nanoTime() - start2;

		log("floatRelative = %d : doubleRelative = %d : %fx\n", start1, start2, (1.0 * start1) / start2);
		if (TestSettings.ASSERT_SPEED_TESTS)
			Assert.assertTrue(start2 < start1);
	}

	@Test
	public void floatComplementIsSlowerThanDoubleComplement()
	{
		org.junit.Assume.assumeTrue(TestSettings.RUN_SPEED_TESTS);

		float maxComplementError = 1e-2f;
		float maxAbsoluteError = 1e-16f;
		int significantDigits = 3;
		FloatEquality equality = new FloatEquality(maxComplementError, maxAbsoluteError, significantDigits);
		DoubleEquality equality2 = new DoubleEquality(maxComplementError, maxAbsoluteError, significantDigits);

		// Create data
		Random rand = new Random(30051977);
		float[] data = new float[MAX_ITER];
		float[] data2 = new float[data.length];
		double[] data3 = new double[data.length];
		double[] data4 = new double[data.length];

		for (int i = 0; i < data.length; i++)
		{
			data[i] = 1e-10f * ((rand.nextFloat() > 0.5) ? 1.001f : 1.1f);
			data2[i] *= (rand.nextFloat() > 0.5) ? 1.001f : 1.1f;
			data3[i] = data[i];
			data4[i] = data2[i];
		}

		complement(equality, data, data2);
		complement(equality2, data3, data4);

		long start1 = System.nanoTime();
		complement(equality, data, data2);
		start1 = System.nanoTime() - start1;

		long start2 = System.nanoTime();
		complement(equality2, data3, data4);
		start2 = System.nanoTime() - start2;

		log("floatComplement = %d : doubleComplement = %d : %fx\n", start1, start2, (1.0 * start1) / start2);
		if (TestSettings.ASSERT_SPEED_TESTS)
			Assert.assertTrue(start2 < start1);
	}

	private void relative(FloatEquality equality, float[] data, float[] data2)
	{
		for (int i = 0; i < data.length; i++)
			equality.almostEqualRelativeOrAbsolute(data[i], data2[i]);
	}

	private void relative(DoubleEquality equality, double[] data, double[] data2)
	{
		for (int i = 0; i < data.length; i++)
			equality.almostEqualRelativeOrAbsolute(data[i], data2[i]);
	}

	private void complement(FloatEquality equality, float[] data, float[] data2)
	{
		for (int i = 0; i < data.length; i++)
			equality.almostEqualComplement(data[i], data2[i]);
	}

	private void complement(DoubleEquality equality, double[] data, double[] data2)
	{
		for (int i = 0; i < data.length; i++)
			equality.almostEqualComplement(data[i], data2[i]);
	}

	void log(String format, Object... args)
	{
		System.out.printf(format, args);
	}
}
