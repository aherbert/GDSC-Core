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
package gdsc.core.utils;

import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

import gdsc.test.TestSettings;
import gdsc.test.TestSettings.LogLevel;

@SuppressWarnings({ "javadoc" })
public class ConvexHullTest
{
	@Test
	public void canComputeConvexHullFromSquare()
	{
		final float[] x = new float[] { 0, 10, 10, 0 };
		final float[] y = new float[] { 0, 0, 10, 10 };
		final ConvexHull hull = ConvexHull.create(x, y);
		check(x, y, hull);
	}

	@Test
	public void canComputeConvexHullFromSquareWithInternalPoint()
	{
		final float[] x = new float[] { 0, 0, 10, 10, 5 };
		final float[] y = new float[] { 0, 10, 10, 0, 5 };
		final float[] ex = new float[] { 0, 10, 10, 0 };
		final float[] ey = new float[] { 0, 0, 10, 10 };
		final ConvexHull hull = ConvexHull.create(x, y);
		check(ex, ey, hull);
	}

	@Test
	public void canComputeConvexHullFromSquareWithInternalPoint2()
	{
		final float[] x = new float[] { 0, 0, 5, 10, 10 };
		final float[] y = new float[] { 0, 10, 5, 10, 0 };
		final float[] ex = new float[] { 0, 10, 10, 0 };
		final float[] ey = new float[] { 0, 0, 10, 10 };
		final ConvexHull hull = ConvexHull.create(x, y);
		check(ex, ey, hull);
	}

	private static void check(float[] ex, float[] ey, ConvexHull hull)
	{
		if (ex == null)
		{
			Assert.assertTrue(hull == null);
			return;
		}
		final int n = ex.length;

		Assert.assertEquals(n, hull.x.length);

		//for (int i = 0; i < ex.length; i++)
		//{
		//	TestSettings.info("[%d] %f==%f (%f), %f==%f (%f)\n", i, ex[i], hull.x[i],
		//			hull.x[i] - ex[i], ey[i], hull.y[i], hull.y[i] - ey[i]);
		//}

		for (int i = 0; i < n; i++)
		{
			Assert.assertEquals(ex[i], hull.x[i], 0);
			Assert.assertEquals(ey[i], hull.y[i], 0);
		}
	}

	@Test
	public void canComputeConvexHullFromOrigin00()
	{
		final RandomGenerator r = TestSettings.getRandomGenerator();
		for (final int size : new int[] { 10 })
			for (final float w : new float[] { 10, 5 })
				for (final float h : new float[] { 10, 5 })
					compute(r, size, 0, 0, w, h);
	}

	@Test
	public void canComputeConvexHullFromOriginXY()
	{
		final RandomGenerator r = TestSettings.getRandomGenerator();
		for (final int size : new int[] { 10 })
			for (final float ox : new float[] { -5, 5 })
				for (final float oy : new float[] { -5, 5 })
					for (final float w : new float[] { 10, 5 })
						for (final float h : new float[] { 10, 5 })
							compute(r, size, ox, oy, w, h);
	}

	private static void compute(RandomGenerator r, int size, float ox, float oy, float w, float h)
	{
		final float[][] data = createData(r, size, ox, oy, w, h);
		final ConvexHull hull = ConvexHull.create(data[0], data[1]);

		// Simple check of the bounds
		try
		{
			Assert.assertNotNull(hull);
			final Rectangle2D.Double bounds = hull.getFloatBounds();
			Assert.assertTrue("xmin " + ox + " " + bounds.getX(), ox <= bounds.getX());
			Assert.assertTrue("ymin " + oy + " " + bounds.getY(), oy <= bounds.getY());
			Assert.assertTrue("xmax " + (ox + w) + " " + bounds.getMaxX(), ox + w >= bounds.getMaxX());
			Assert.assertTrue("ymax " + (ox + h) + " " + bounds.getMaxY(), oy + h >= bounds.getMaxY());
		}
		catch (final AssertionError e)
		{
			// Debug
			if (TestSettings.allow(LogLevel.DEBUG))
			{
				for (int i = 0; i < size; i++)
					TestSettings.info("[%d] %f,%f\n", i, data[0][i], data[1][i]);
				if (hull != null)
					for (int i = 0; i < hull.x.length; i++)
						TestSettings.info("H[%d] %f,%f\n", i, hull.x[i], hull.y[i]);
			}
			throw e;
		}
	}

	private static float[][] createData(RandomGenerator r, int size, float ox, float oy, float w, float h)
	{
		final float[][] data = new float[2][size];
		for (int i = 0; i < size; i++)
		{
			data[0][i] = ox + r.nextFloat() * w;
			data[1][i] = oy + r.nextFloat() * h;
		}
		return data;
	}

	@Test
	public void canCreateWithNoPoints()
	{
		final float[] x = new float[0];
		Assert.assertNull(ConvexHull.create(x, x));
	}

	@Test
	public void canCreateWithOnePoint()
	{
		final float[] x = new float[] { 1.2345f };
		final ConvexHull hull = ConvexHull.create(x, x);
		Assert.assertEquals(1, hull.size());
		Assert.assertTrue(hull.getLength() == 0);
		Assert.assertTrue(hull.getArea() == 0);
	}

	@Test
	public void canCreateWithTwoPoints()
	{
		final float[] x = new float[] { 1.5f, 2.5f };
		final ConvexHull hull = ConvexHull.create(x, x);
		Assert.assertEquals(2, hull.size());
		Assert.assertEquals(2 * Math.sqrt(2), hull.getLength(), 1e-10);
		Assert.assertTrue(hull.getArea() == 0);
	}

	@Test
	public void canCreateWithThreePoints()
	{
		final float[] x = new float[] { 1, 2, 2 };
		final float[] y = new float[] { 1, 1, 2 };
		final ConvexHull hull = ConvexHull.create(x, y);
		Assert.assertEquals(3, hull.size());
		Assert.assertEquals(2 + Math.sqrt(2), hull.getLength(), 1e-10);
		Assert.assertEquals(hull.getArea(), 0.5, 1e-10);
	}

	@Test
	public void canComputeLengthAndArea()
	{
		// Parallelogram
		float[] x = new float[] { 0, 10, 11, 1 };
		float[] y = new float[] { 0, 0, 10, 10 };
		ConvexHull hull = ConvexHull.create(x, y);
		Assert.assertEquals(2 * 10 + 2 * Math.sqrt(1 * 1 + 10 * 10), hull.getLength(), 1e-6);
		Assert.assertEquals(100, hull.getArea(), 1e-6);

		// Rotated square
		x = new float[] { 0, 10, 9, -1 };
		y = new float[] { 0, 1, 11, 10 };
		hull = ConvexHull.create(x, y);
		final double edgeLengthSquared = 1 * 1 + 10 * 10;
		Assert.assertEquals(4 * Math.sqrt(edgeLengthSquared), hull.getLength(), 1e-6);
		Assert.assertEquals(edgeLengthSquared, hull.getArea(), 1e-6);

		// Polygon circle
		final int n = 1000;
		final double r = 4;
		x = new float[n];
		y = new float[n];
		for (int i = 0; i < 1000; i++)
		{
			final double a = i * 2 * Math.PI / n;
			x[i] = (float) (Math.sin(a) * r);
			y[i] = (float) (Math.cos(a) * r);
		}
		hull = ConvexHull.create(x, y);
		Assert.assertEquals(2 * Math.PI * r, hull.getLength(), 1e-2);
		Assert.assertEquals(Math.PI * r * r, hull.getArea(), 1e-2);
	}

	@Test
	public void conComputeContains()
	{
		final float[] x = new float[] { 0, 10, 11, 1 };
		final float[] y = new float[] { 0, 0, 10, 10 };
		final ConvexHull hull = ConvexHull.create(x, y);
		// Contains does not match outer bounds on right or bottom
		Assert.assertTrue(hull.contains(x[0], y[0]));
		for (int i = 1; i < x.length; i++)
			Assert.assertFalse(hull.contains(x[i], y[i]));
		Assert.assertTrue(hull.contains(5, 5));
		Assert.assertFalse(hull.contains(-5, 5));
		Assert.assertFalse(hull.contains(5, -5));
	}
}
