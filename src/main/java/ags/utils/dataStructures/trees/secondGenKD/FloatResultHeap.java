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
package ags.utils.dataStructures.trees.secondGenKD;

import java.util.Arrays;

/**
 * Class for tracking up to 'size' closest values.
 *
 * @param <T>
 *            the generic type
 */
public class FloatResultHeap<T>
{
	/** The data. */
	final Object[] data;

	/** The distance. */
	final float[] distance;

	/** The size. */
	private final int size;

	/** The values. */
	int values;
	/** The removed data (see {@link #removeLargest()}). */
	public Object removedData;
	/** The removed distance (see {@link #removeLargest()}). */
	public float removedDist;

	/**
	 * Instantiates a new float result heap.
	 *
	 * @param size
	 *            the size
	 */
	public FloatResultHeap(int size)
	{
		this.data = new Object[size];
		this.distance = new float[size];
		this.size = size;
		this.values = 0;
	}

	/**
	 * Adds the value.
	 *
	 * @param dist
	 *            the dist
	 * @param value
	 *            the value
	 */
	public void addValue(float dist, Object value)
	{
		// If there is still room in the heap
		if (values < size)
		{
			// Insert new value at the end
			data[values] = value;
			distance[values] = dist;
			upHeapify(values);
			values++;
		}
		// If there is no room left in the heap, and the new entry is lower
		// than the max entry
		else if (dist < distance[0])
		{
			// Replace the max entry with the new entry
			data[0] = value;
			distance[0] = dist;
			downHeapify(0);
		}
	}

	/**
	 * Removes the largest.
	 */
	public void removeLargest()
	{
		if (values == 0)
		{
			throw new IllegalStateException();
		}

		removedData = data[0];
		removedDist = distance[0];
		values--;
		data[0] = data[values];
		distance[0] = distance[values];
		downHeapify(0);
	}

	/**
	 * Up heapify.
	 *
	 * @param c
	 *            the c
	 */
	private void upHeapify(int c)
	{
		while (c > 0)
		{
			final int p = (c - 1) >>> 1;
			if (distance[c] > distance[p])
			{
				Object pData = data[p];
				float pDist = distance[p];
				data[p] = data[c];
				distance[p] = distance[c];
				data[c] = pData;
				distance[c] = pDist;
				c = p;
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * Down heapify.
	 *
	 * @param p
	 *            the p
	 */
	private void downHeapify(int p)
	{
		for (int c = p * 2 + 1; c < values; p = c, c = p * 2 + 1)
		{
			if (c + 1 < values && distance[c] < distance[c + 1])
			{
				c++;
			}
			if (distance[p] < distance[c])
			{
				// Swap the points
				Object pData = data[p];
				float pDist = distance[p];
				data[p] = data[c];
				distance[p] = distance[c];
				data[c] = pData;
				distance[c] = pDist;
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * Gets the max dist.
	 *
	 * @return the max dist
	 */
	public float getMaxDist()
	{
		if (values < size)
		{
			return Float.POSITIVE_INFINITY;
		}
		return distance[0];
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize()
	{
		return values;
	}

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public int getCapacity()
	{
		return size;
	}

	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public float[] getDistance()
	{
		return Arrays.copyOf(distance, values);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Object[] getData()
	{
		return Arrays.copyOf(data, values);
	}

	/**
	 * Gets the data.
	 *
	 * @param a
	 *            the a
	 * @return the data
	 */
	@SuppressWarnings("unchecked")
	public T[] getData(T[] a)
	{
		if (a.length < values)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(data, values, a.getClass());
		System.arraycopy(data, 0, a, 0, values);
		if (a.length > values)
			a[values] = null;
		return a;
	}
}
