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
package uk.ac.sussex.gdsc.core.utils;

import org.apache.commons.math3.util.FastMath;

/**
 * Provides a rolling median on a fixed size data set. The median is maintained using a double-linked list data
 * structure.
 * <p>
 * See Juhola, et al. (1991) Comparison of algorithms for standard median filtering. Signal Processing
 */
public class MedianWindowDLL
{
	private class Data
	{
		int i; // Index
		double v; // Value
		Data s; // Smaller data
		Data g; // Greater data

		public Data(double v, int i)
		{
			this.v = v;
			this.i = i;
		}
	}

	private int t = 0;
	private final Data[] data;
	private Data median;

	/**
	 * Instantiates a new median window DLL.
	 *
	 * @param values
	 *            the values
	 * @throws IllegalArgumentException
	 *             if the input data is an even size
	 */
	public MedianWindowDLL(double[] values)
	{
		if (values == null || values.length < 1)
			throw new IllegalArgumentException("Input data must not be null or empty");
		if (values.length % 2 == 0)
			throw new IllegalArgumentException("Input data must not even in length");
		this.data = new Data[values.length];

		// Store the data and create indices for sorting
		final int[] indices = new int[values.length];

		for (int i = 0; i < values.length; i++)
		{
			indices[i] = i;
			this.data[i] = new Data(values[i], i);
		}
		Sort.sort(indices, values);

		// Create the smaller and greater pointers.
		// (The sort is in descending order)
		for (int i = 0; i < values.length; i++)
		{
			if (i > 0)
				// Set the smaller pointer to the data smaller than this
				data[indices[i]].g = data[indices[i - 1]];
			if (i < values.length - 1)
				// Set the greater pointer to the data greater than this
				data[indices[i]].s = data[indices[i + 1]];
		}

		// Set the median
		median = data[indices[indices.length / 2]];

		//debugData();
	}

	/**
	 * Debug the input sort and pointers
	 */
	@SuppressWarnings("unused")
	private void debugData()
	{
		System.out.printf("Array = %f", data[0].v);
		for (int i = 1; i < data.length; i++)
			System.out.printf(", %f", data[i].v);
		System.out.printf("\n");

		// Find the head and tail
		Data head = median;
		while (head.s != null)
			head = head.s;
		System.out.printf("G pointers = %g", head.v);
		while (head.g != null)
		{
			head = head.g;
			System.out.printf(", %g", head.v);
		}
		System.out.printf("\n");

		// Find the head and tail
		Data tail = median;
		while (tail.g != null)
			tail = tail.g;
		System.out.printf("S pointers = %g", tail.v);
		while (tail.s != null)
		{
			tail = tail.s;
			System.out.printf(", %g", tail.v);
		}
		System.out.printf("\n");

		System.out.printf("Median = %g\n", median.v);
	}

	/**
	 * @return The median
	 */
	public double getMedian()
	{
		return median.v;
	}

	/**
	 * Add a new value to the set.
	 *
	 * @param x
	 *            the value
	 */
	public void add(final double x)
	{
		// Replaces y by x using the latest insertion finger
		// after which the s and g chains are updated accordingly. An appro-
		// priate node is found by comparing x to the previously inserted sam
		// ple and advancing either s or g chains depending on the comparison.
		// Both links and the latest insertion finger are updated. If the median
		// is between x and y in the s (and g) chain, it is changed by moving
		// the median finger one node towards the inserted sample along the
		// s or g chain. The same scheme is followed if the sample to be
		// deleted is the median itself.

		final Data point = data[t];
		final double y = point.v;
		if (x == y)
		{
			t = ++t % data.length;
			return;
		}

		final double m = median.v;

		// Replace y by x
		point.v = x;

		// Sort the data and update the median
		if (x < y)
		{
			// Move along the s chain until sorted
			Data movePast = point;
			for (Data s = point.s; s != null && s.v > x; s = s.s)
				movePast = s;
			if (movePast != point)
				if (y == m && median != point)
				{
					// The value removed matches the median, however it
					// could have been below or above the median in the linked list.
					// The new value is lower. Check if the position is above the median.
					final boolean shift = aboveMedian(point);

					// Update the sorted list:
					// 1. Remove the point
					if (point.g != null)
						point.g.s = point.s;
					point.s.g = point.g;

					// 2. Insert into new location
					if (movePast.s != null)
						movePast.s.g = point;
					point.s = movePast.s;
					movePast.s = point;
					point.g = movePast;

					if (shift)
						median = median.s;
				}
				else
				{
					final Data aboveMedian = median.g;

					// Update the sorted list:
					// 1. Remove the point
					if (point.g != null)
						point.g.s = point.s;
					point.s.g = point.g;

					// 2. Insert into new location
					if (movePast.s != null)
						movePast.s.g = point;
					point.s = movePast.s;
					movePast.s = point;
					point.g = movePast;

					// If we moved the median then update using the unmoved node next to the median
					if (median == point)
						median = aboveMedian.s;
					else if (x < m && y > m)
						median = median.s;
				}
		}
		else
		// x > y
		{
			// Move along the g chain until sorted
			Data movePast = point;
			for (Data g = point.g; g != null && g.v < x; g = g.g)
				movePast = g;
			if (movePast != point)
				if (y == m && median != point)
				{
					// The value removed matches the median, however it
					// could have been below or above the median in the linked list.
					// The new value is higher. Check if the position is above the median.
					final boolean shift = !aboveMedian(point);

					// Update the sorted list:
					// 1. Remove the point
					if (point.s != null)
						point.s.g = point.g;
					point.g.s = point.s;

					// 2. Insert into new location
					if (movePast.g != null)
						movePast.g.s = point;
					point.g = movePast.g;
					movePast.g = point;
					point.s = movePast;

					if (shift)
						median = median.g;
				}
				else
				{
					final Data belowMedian = median.s;

					// Update the sorted list:
					// 1. Remove the point
					if (point.s != null)
						point.s.g = point.g;
					point.g.s = point.s;

					// 2. Insert into new location
					if (movePast.g != null)
						movePast.g.s = point;
					point.g = movePast.g;
					movePast.g = point;
					point.s = movePast;

					// If we moved the median then update using the unmoved node next to the median
					if (median == point)
						median = belowMedian.g;
					else if (x > m && y < m)
						median = median.g;
				}
		}

		// Update the latest insertion finger
		t = ++t % data.length;

		//System.out.printf("Add %g : Remove %g\n", x, y);
		//debugData();
	}

	private boolean aboveMedian(Data point)
	{
		for (Data p = median.g; p != null; p = p.g)
		{
			if (p == point)
				return true;
			if (p.v != median.v)
				return false;
		}
		return false;
	}

	/**
	 * Compute the median for the input data using a range of time points. The first time point added is t=0. Time
	 * points after that have a positive index. The maximum allowed index is the data length-1
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the median
	 */
	public double getMedian(int start, int end)
	{
		end = FastMath.min(data.length - 1, Math.abs(end));
		start = FastMath.max(0, Math.abs(start));

		final int length = end - start + 1;
		if (length == 0)
			return Double.NaN;

		// Find the head of the list
		Data head = median;
		while (head.s != null)
			head = head.s;

		// Create a list of the data using only the desired time points
		final Data[] list = new Data[length];

		// Extract the data into a list. This should be sorted.
		int i = 0;
		while (head != null)
		{
			final int age = (data.length + head.i - t) % data.length;
			if (age >= start && age <= end)
				list[i++] = head;
			head = head.g;
		}

		return (list[(list.length - 1) / 2].v + list[list.length / 2].v) * 0.5;
	}

	/**
	 * Compute the median for the input data using the oldest n data points.
	 *
	 * @param n
	 *            the n
	 * @return the median
	 */
	public double getMedianOldest(int n)
	{
		return getMedian(0, n - 1);
	}

	/**
	 * Compute the median for the input data using the youngest n data points.
	 *
	 * @param n
	 *            the n
	 * @return the median
	 */
	public double getMedianYoungest(int n)
	{
		final int end = data.length - 1;
		return getMedian(end - n + 1, end);
	}

	/**
	 * @return The size of the rolling window
	 */
	public int getSize()
	{
		return data.length;
	}
}