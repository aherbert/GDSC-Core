package gdsc.core.math.interpolation;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * This is an extension of the 
 * org.apache.commons.math3.analysis.interpolation.TricubicFunction
 * 
 * Modifications have been made to allow computation of gradients and computation
 * with pre-computated x,y,z powers using single/floating precision.
 * 
 * The code is released under the original Apache licence: 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * 3D-spline function.
 */
public abstract class CustomTricubicFunction implements TrivariateFunction
{
	/**
	 * Pre-compute gradient coefficients for partial derivatives.
	 *
	 * @param order
	 *            the order (<=2)
	 */
	abstract void precomputeGradientCoefficients(int order);

	/**
	 * Convert this instance to single precision.
	 *
	 * @return the custom tricubic function
	 */
	abstract public CustomTricubicFunction toSinglePrecision();

	/**
	 * Convert this instance to double precision.
	 *
	 * @return the custom tricubic function
	 */
	abstract public CustomTricubicFunction toDoublePrecision();

	/**
	 * Gets the index in the table for the specified position.
	 *
	 * @param i
	 *            the x index
	 * @param j
	 *            the x index
	 * @param k
	 *            the z index
	 * @return the index
	 */
	public static int getIndex(int i, int j, int k)
	{
		return i + 4 * (j + 4 * k);
	}

	/**
	 * Get the interpolated value
	 * 
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the interpolated value.
	 * @throws OutOfRangeException
	 *             if {@code x}, {@code y} or
	 *             {@code z} are not in the interval {@code [0, 1]}.
	 */
	public double value(double x, double y, double z) throws OutOfRangeException
	{
		if (x < 0 || x > 1)
		{
			throw new OutOfRangeException(x, 0, 1);
		}
		if (y < 0 || y > 1)
		{
			throw new OutOfRangeException(y, 0, 1);
		}
		if (z < 0 || z > 1)
		{
			throw new OutOfRangeException(z, 0, 1);
		}

		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = { /* 1, optimised out */ x, x2, x3 };

		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = { /* 1, optimised out */ y, y2, y3 };

		final double z2 = z * z;
		final double z3 = z2 * z;
		final double[] pZ = { /* 1, optimised out */ z, z2, z3 };

		return value0(pX, pY, pZ);
	}

	/**
	 * Get the interpolated value
	 * 
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the interpolated value.
	 */
	public double value(CubicSplinePosition x, CubicSplinePosition y, CubicSplinePosition z)
	{
		return value0(x.p, y.p, z.p);
	}

	/**
	 * Get the interpolated value
	 * 
	 * @param pX
	 *            x-coordinate powers of the interpolation point.
	 * @param pY
	 *            y-coordinate powers of the interpolation point.
	 * @param pZ
	 *            z-coordinate powers of the interpolation point.
	 * @return the interpolated value.
	 */
	abstract protected double value0(final double[] pX, final double[] pY, final double[] pZ);

	/**
	 * Compute the power table.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the power table.
	 * @throws OutOfRangeException
	 *             if {@code x}, {@code y} or
	 *             {@code z} are not in the interval {@code [0, 1]}.
	 */
	public static double[] computePowerTable(double x, double y, double z) throws OutOfRangeException
	{
		if (x < 0 || x > 1)
		{
			throw new OutOfRangeException(x, 0, 1);
		}
		if (y < 0 || y > 1)
		{
			throw new OutOfRangeException(y, 0, 1);
		}
		if (z < 0 || z > 1)
		{
			throw new OutOfRangeException(z, 0, 1);
		}

		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = { /* 1, optimised out */ x, x2, x3 };

		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = { /* 1, optimised out */ y, y2, y3 };

		final double z2 = z * z;
		final double z3 = z2 * z;
		final double[] pZ = { /* 1, optimised out */ z, z2, z3 };

		return computePowerTable(pX, pY, pZ);
	}

	/**
	 * Compute the power table.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the power table.
	 */
	public static double[] computePowerTable(CubicSplinePosition x, CubicSplinePosition y, CubicSplinePosition z)
	{
		return computePowerTable(x.p, y.p, z.p);
	}

	/**
	 * Compute the power table.
	 *
	 * @param pX
	 *            x-coordinate powers of the interpolation point.
	 * @param pY
	 *            y-coordinate powers of the interpolation point.
	 * @param pZ
	 *            z-coordinate powers of the interpolation point.
	 * @return the power table.
	 */
	private static double[] computePowerTable(final double[] pX, final double[] pY, final double[] pZ)
	{
		double[] table = new double[64];

		table[0] = 1;
		table[1] = pX[0];
		table[2] = pX[1];
		table[3] = pX[2];
		table[4] = pY[0];
		table[5] = pX[0] * pY[0];
		table[6] = pX[1] * pY[0];
		table[7] = pX[2] * pY[0];
		table[8] = pY[1];
		table[9] = pX[0] * pY[1];
		table[10] = pX[1] * pY[1];
		table[11] = pX[2] * pY[1];
		table[12] = pY[2];
		table[13] = pX[0] * pY[2];
		table[14] = pX[1] * pY[2];
		table[15] = pX[2] * pY[2];
		table[16] = pZ[0];
		table[17] = pX[0] * pZ[0];
		table[18] = pX[1] * pZ[0];
		table[19] = pX[2] * pZ[0];
		table[20] = pY[0] * pZ[0];
		table[21] = pX[0] * table[20];
		table[22] = pX[1] * table[20];
		table[23] = pX[2] * table[20];
		table[24] = pY[1] * pZ[0];
		table[25] = pX[0] * table[24];
		table[26] = pX[1] * table[24];
		table[27] = pX[2] * table[24];
		table[28] = pY[2] * pZ[0];
		table[29] = pX[0] * table[28];
		table[30] = pX[1] * table[28];
		table[31] = pX[2] * table[28];
		table[32] = pZ[1];
		table[33] = pX[0] * pZ[1];
		table[34] = pX[1] * pZ[1];
		table[35] = pX[2] * pZ[1];
		table[36] = pY[0] * pZ[1];
		table[37] = pX[0] * table[36];
		table[38] = pX[1] * table[36];
		table[39] = pX[2] * table[36];
		table[40] = pY[1] * pZ[1];
		table[41] = pX[0] * table[40];
		table[42] = pX[1] * table[40];
		table[43] = pX[2] * table[40];
		table[44] = pY[2] * pZ[1];
		table[45] = pX[0] * table[44];
		table[46] = pX[1] * table[44];
		table[47] = pX[2] * table[44];
		table[48] = pZ[2];
		table[49] = pX[0] * pZ[2];
		table[50] = pX[1] * pZ[2];
		table[51] = pX[2] * pZ[2];
		table[52] = pY[0] * pZ[2];
		table[53] = pX[0] * table[52];
		table[54] = pX[1] * table[52];
		table[55] = pX[2] * table[52];
		table[56] = pY[1] * pZ[2];
		table[57] = pX[0] * table[56];
		table[58] = pX[1] * table[56];
		table[59] = pX[2] * table[56];
		table[60] = pY[2] * pZ[2];
		table[61] = pX[0] * table[60];
		table[62] = pX[1] * table[60];
		table[63] = pX[2] * table[60];

		return table;
	}

	/**
	 * Compute the power table.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the power table.
	 * @throws OutOfRangeException
	 *             if {@code x}, {@code y} or
	 *             {@code z} are not in the interval {@code [0, 1]}.
	 */
	public static float[] computeFloatPowerTable(double x, double y, double z) throws OutOfRangeException
	{
		// Compute as a double for precision
		return toFloat(computePowerTable(x, y, z));
	}

	/**
	 * Compute the power table.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @return the power table.
	 */
	public static float[] computeFloatPowerTable(CubicSplinePosition x, CubicSplinePosition y, CubicSplinePosition z)
	{
		// Compute as a double for precision
		return toFloat(computePowerTable(x, y, z));
	}

	/**
	 * Convert a length 64 array to a float
	 *
	 * @param d
	 *            the array
	 * @return the float array
	 */
	protected static float[] toFloat(double[] d)
	{
		final float[] f = new float[64];
		for (int i = 0; i < 64; i++)
			f[i] = (float) d[i];
		return f;
	}

	/**
	 * Get the value using a pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @return the interpolated value.
	 */
	abstract public double value(double[] table);

	/**
	 * Get the value using a pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @return the interpolated value.
	 */
	abstract public double value(float[] table);

	/**
	 * Compute the value and partial first-order derivatives
	 * <p>
	 * WARNING: The gradients will be unscaled.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 * @throws OutOfRangeException
	 *             if {@code x}, {@code y} or
	 *             {@code z} are not in the interval {@code [0, 1]}.
	 */
	public double value(double x, double y, double z, double[] df_da) throws OutOfRangeException
	{
		if (x < 0 || x > 1)
		{
			throw new OutOfRangeException(x, 0, 1);
		}
		if (y < 0 || y > 1)
		{
			throw new OutOfRangeException(y, 0, 1);
		}
		if (z < 0 || z > 1)
		{
			throw new OutOfRangeException(z, 0, 1);
		}

		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = { /* 1, optimised out */ x, x2, x3 };

		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = { /* 1, optimised out */ y, y2, y3 };

		final double z2 = z * z;
		final double z3 = z2 * z;
		final double[] pZ = { /* 1, optimised out */ z, z2, z3 };

		return value1(pX, pY, pZ, df_da);
	}

	/**
	 * Compute the value and partial first-order derivatives
	 * <p>
	 * The gradients are scaled
	 * 
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	public double value(CubicSplinePosition x, CubicSplinePosition y, CubicSplinePosition z, double[] df_da)
	{
		double value = value1(x.p, y.p, z.p, df_da);
		df_da[0] = x.scaleGradient(df_da[0]);
		df_da[1] = y.scaleGradient(df_da[1]);
		df_da[2] = z.scaleGradient(df_da[2]);
		return value;
	}

	/**
	 * Compute the value and partial first-order derivatives
	 * 
	 * @param pX
	 *            x-coordinate powers of the interpolation point.
	 * @param pY
	 *            y-coordinate powers of the interpolation point.
	 * @param pZ
	 *            z-coordinate powers of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract protected double value1(final double[] pX, final double[] pY, final double[] pZ, final double[] df_da);

	/**
	 * Compute the value and partial first-order derivatives using pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract public double value(double[] table, double[] df_da);

	/**
	 * Compute the value and partial first-order derivatives using pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract public double value(float[] table, double[] df_da);

	/**
	 * Compute the value and partial first-order and second-order derivatives
	 * <p>
	 * WARNING: The gradients will be unscaled.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 * @throws OutOfRangeException
	 *             if {@code x}, {@code y} or
	 *             {@code z} are not in the interval {@code [0, 1]}.
	 */
	public double value(double x, double y, double z, double[] df_da, double[] d2f_da2) throws OutOfRangeException
	{
		if (x < 0 || x > 1)
		{
			throw new OutOfRangeException(x, 0, 1);
		}
		if (y < 0 || y > 1)
		{
			throw new OutOfRangeException(y, 0, 1);
		}
		if (z < 0 || z > 1)
		{
			throw new OutOfRangeException(z, 0, 1);
		}

		final double x2 = x * x;
		final double x3 = x2 * x;
		final double[] pX = { /* 1, optimised out */ x, x2, x3 };

		final double y2 = y * y;
		final double y3 = y2 * y;
		final double[] pY = { /* 1, optimised out */ y, y2, y3 };

		final double z2 = z * z;
		final double z3 = z2 * z;
		final double[] pZ = { /* 1, optimised out */ z, z2, z3 };

		return value2(pX, pY, pZ, df_da, d2f_da2);
	}

	/**
	 * Compute the value and partial first-order and second-order derivatives
	 * <p>
	 * The gradients are scaled.
	 *
	 * @param x
	 *            x-coordinate of the interpolation point.
	 * @param y
	 *            y-coordinate of the interpolation point.
	 * @param z
	 *            z-coordinate of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @param d2f_da2
	 *            the partial second order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	public double value(CubicSplinePosition x, CubicSplinePosition y, CubicSplinePosition z, double[] df_da,
			double[] d2f_da2)
	{
		double value = value2(x.p, y.p, z.p, df_da, d2f_da2);
		df_da[0] = x.scaleGradient(df_da[0]);
		df_da[1] = y.scaleGradient(df_da[1]);
		df_da[2] = z.scaleGradient(df_da[2]);
		d2f_da2[0] = x.scaleGradient2(d2f_da2[0]);
		d2f_da2[1] = y.scaleGradient2(d2f_da2[1]);
		d2f_da2[2] = z.scaleGradient2(d2f_da2[2]);
		return value;
	}

	/**
	 * Compute the value and partial first-order and second-order derivatives
	 * 
	 * @param pX
	 *            x-coordinate powers of the interpolation point.
	 * @param pY
	 *            y-coordinate powers of the interpolation point.
	 * @param pZ
	 *            z-coordinate powers of the interpolation point.
	 * @param df_da
	 *            the partial first order derivatives with respect to x,y,z
	 * @param d2f_da2
	 *            the partial second order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract protected double value2(final double[] pX, final double[] pY, final double[] pZ, final double[] df_da,
			double[] d2f_da2);

	/**
	 * Compute the value and partial first-order and second-order derivatives using pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @param df_da
	 *            the partial second order derivatives with respect to x,y,z
	 * @param d2f_da2
	 *            the partial second order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract public double value(double[] table, double[] df_da, double[] d2f_da2);

	/**
	 * Compute the value and partial first-order and second-order derivatives using pre-computed power table.
	 *
	 * @param table
	 *            the power table
	 * @param df_da
	 *            the partial second order derivatives with respect to x,y,z
	 * @param d2f_da2
	 *            the partial second order derivatives with respect to x,y,z
	 * @return the interpolated value.
	 */
	abstract public double value(float[] table, double[] df_da, double[] d2f_da2);
}
