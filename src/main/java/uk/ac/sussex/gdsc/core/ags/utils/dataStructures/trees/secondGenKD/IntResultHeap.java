/*
 * Copyright 2009 Rednaxela
 *
 * Modifications to the code have been made by Alex Herbert for a smaller 
 * memory footprint and optimised 2D processing for use with image data
 * as part of the Genome Damage and Stability Centre ImageJ Core Package.
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 *
 *    2. This notice may not be removed or altered from any source
 *    distribution.
 */
package uk.ac.sussex.gdsc.core.ags.utils.dataStructures.trees.secondGenKD;

import java.util.Arrays;

/**
 * Class for tracking up to 'size' closest values.
 */
public class IntResultHeap
{
	/** The data. */
	final int[] data;

	/** The distance. */
	final double[] distance;

	/** The size. */
	private final int size;

	/** The values. */
	int values;

	/** The removed data (see {@link #removeLargest()}). */
	public int removedData;

	/** The removed distince (see {@link #removeLargest()}). */
	public double removedDist;

	/**
	 * Instantiates a new int result heap.
	 *
	 * @param size
	 *            the size
	 */
	public IntResultHeap(int size)
	{
		this.data = new int[size];
		this.distance = new double[size];
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
	public void addValue(double dist, int value)
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
			throw new IllegalStateException();

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
				final int pData = data[p];
				final double pDist = distance[p];
				data[p] = data[c];
				distance[p] = distance[c];
				data[c] = pData;
				distance[c] = pDist;
				c = p;
			}
			else
				break;
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
				c++;
			if (distance[p] < distance[c])
			{
				// Swap the points
				final int pData = data[p];
				final double pDist = distance[p];
				data[p] = data[c];
				distance[p] = distance[c];
				data[c] = pData;
				distance[c] = pDist;
			}
			else
				break;
		}
	}

	/**
	 * Gets the max dist.
	 *
	 * @return the max dist
	 */
	public double getMaxDist()
	{
		if (values < size)
			return Float.POSITIVE_INFINITY;
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
	public double[] getDistance()
	{
		return Arrays.copyOf(distance, values);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public int[] getData()
	{
		return Arrays.copyOf(data, values);
	}
}
