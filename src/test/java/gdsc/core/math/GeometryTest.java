package gdsc.core.math;

import org.junit.Assert;
import org.junit.Test;

public class GeometryTest
{
	@Test
	public void canComputeArea()
	{
		// Area is signed
		canComputeArea(0.5, true, 0, 0, 1, 0, 1, 1);
		canComputeArea(-0.5, true, 0, 0, 1, 1, 1, 0);
		canComputeArea(0.5, false, 0, 0, 1, 1, 1, 0);

		canComputeArea(1, true, 0, 0, 1, 0, 1, 1, 0, 1);
	}

	private void canComputeArea(double e, boolean signed, double... vertices)
	{
		double[] x = new double[vertices.length / 2];
		double[] y = new double[x.length];
		for (int i = 0, j = 0; i < vertices.length; i += 2, j++)
		{
			x[j] = vertices[i];
			y[j] = vertices[i + 1];
		}
		double o = Geometry.getArea(x, y);
		if (!signed)
			o = Math.abs(o);
		Assert.assertEquals(e, o, 1e-10);
	}

	@Test
	public void canComputeIntersection()
	{
		canComputeIntersection(null, 0, 0, 1, 0, 0, 1, 1, 1);
		canComputeIntersection(new double[] { 0.5, 0.5 }, 0, 0, 1, 1, 1, 0, 0, 1);
		canComputeIntersection(new double[] { 0, 0 }, 0, 0, 1, 1, 0, 0, 0, 1);
	}

	private void canComputeIntersection(double[] e, double x1, double y1, double x2, double y2, double x3, double y3,
			double x4, double y4)
	{
		double[] o = new double[2];
		boolean result = Geometry.getIntersection(x1, y1, x2, y2, x3, y3, x4, y4, o);
		if (e == null)
		{
			Assert.assertFalse(result);
		}
		else
		{
			Assert.assertTrue(result);
			Assert.assertArrayEquals(e, o, 1e-10);
		}
	}
}