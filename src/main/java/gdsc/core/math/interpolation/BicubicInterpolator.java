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
package gdsc.core.math.interpolation;

/**
 * Bicubic Interpolator using the Catmull-Rom spline.
 * <p>
 * Taken from http://www.paulinternet.nl/?page=bicubic.
 */
public class BicubicInterpolator extends CubicInterpolator
{
	private double[] arr = new double[4];

	/**
	 * Gets the interpolated value.
	 *
	 * @param p
	 *            the value of the function at x=-1 to x=2 and y=-1 to y=2
	 * @param x
	 *            the x (between 0 and 1)
	 * @param y
	 *            the y (between 0 and 1)
	 * @return the value
	 */
	public double getValue(double[][] p, double x, double y)
	{
		arr[0] = getValue(p[0], y);
		arr[1] = getValue(p[1], y);
		arr[2] = getValue(p[2], y);
		arr[3] = getValue(p[3], y);
		return getValue(arr, x);
	}

	/**
	 * Gets the interpolated value.
	 *
	 * @param p
	 *            the value of the function at x=-1 to x=2 and y=-1 to y=2, length 16 packed as 4 strips of x values for
	 *            each y
	 * @param x
	 *            the x (between 0 and 1)
	 * @param y
	 *            the y (between 0 and 1)
	 * @return the value
	 */
	public double getValue(double[] p, double x, double y)
	{
		arr[0] = getValue(p, 0, x);
		arr[1] = getValue(p, 4, x);
		arr[2] = getValue(p, 8, x);
		arr[3] = getValue(p, 12, x);
		return getValue(arr, y);
	}
}
