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
package uk.ac.sussex.gdsc.core.clustering.optics;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import uk.ac.sussex.gdsc.core.utils.ConvexHull;

/**
 * Provide space for storing cluster coordinates.
 *
 * @author Alex Herbert
 */
class ScratchSpace
{
	/** The x. */
	float[] x;
	/** The y. */
	float[] y;
	/** The number of values. */
	int n;

	/**
	 * Instantiates a new scratch space.
	 *
	 * @param capacity
	 *            the capacity
	 */
	ScratchSpace(int capacity)
	{
		x = new float[capacity];
		y = new float[capacity];
		n = 0;
	}

	/**
	 * Resize to the given capacity and reset the number of values.
	 *
	 * @param capacity
	 *            the capacity
	 */
	void resize(int capacity)
	{
		if (x.length < capacity)
		{
			x = new float[capacity];
			y = new float[capacity];
		}
		n = 0;
	}

	/**
	 * Adds the (x,y) value.
	 *
	 * @param xx
	 *            the x
	 * @param yy
	 *            the y
	 */
	void add(float xx, float yy)
	{
		x[n] = xx;
		y[n] = yy;
		n++;
	}

	/**
	 * Adds the (x,y) values.
	 *
	 * @param xx
	 *            the x
	 * @param yy
	 *            the y
	 */
	void add(float[] xx, float[] yy)
	{
		final int size = xx.length;
		System.arraycopy(xx, 0, x, n, size);
		System.arraycopy(yy, 0, y, n, size);
		n += size;
	}

	/**
	 * Adds the (x,y) value.
	 *
	 * @param xx
	 *            the x
	 * @param yy
	 *            the y
	 */
	void safeAdd(float xx, float yy)
	{
		if (x.length == n)
		{
			final int size = x.length * 2;
			x = Arrays.copyOf(x, size);
			y = Arrays.copyOf(y, size);
		}

		x[n] = xx;
		y[n] = yy;
		n++;
	}

	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	Rectangle2D getBounds()
	{
		if (n == 0)
			return null;
		float minX = x[0];
		float minY = y[0];
		float maxX = minX;
		float maxY = minY;
		for (int i = 1; i < n; i++)
		{
			if (maxX < x[i])
				maxX = x[i];
			else if (minX > x[i])
				minX = x[i];
			if (maxY < y[i])
				maxY = y[i];
			else if (minY > y[i])
				minY = y[i];
		}
		return new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * Gets the convex hull.
	 *
	 * @return the convex hull
	 */
	ConvexHull getConvexHull()
	{
		return ConvexHull.create(x, y, n);
	}
}
