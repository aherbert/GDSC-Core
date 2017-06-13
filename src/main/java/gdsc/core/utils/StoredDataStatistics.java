package gdsc.core.utils;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2016 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * Calculate the mean and standard deviation of data. Stores the data for later retrieval.
 */
public class StoredDataStatistics extends Statistics implements Iterable<Double>, DoubleData
{
	private double[] values = new double[0];
	private DescriptiveStatistics stats = null;

	public StoredDataStatistics()
	{
	}

	public StoredDataStatistics(int capacity)
	{
		values = new double[capacity];
	}

	public StoredDataStatistics(float[] data)
	{
		add(data);
	}

	public StoredDataStatistics(double[] data)
	{
		add(data);
	}

	public StoredDataStatistics(int[] data)
	{
		add(data);
	}

	/**
	 * Add the data
	 * 
	 * @param data
	 */
	public void add(float[] data)
	{
		if (data == null)
			return;
		checkCapacity(data.length);
		for (int i = 0; i < data.length; i++)
		{
			final double value = data[i];
			values[n++] = value;
			s += value;
			ss += value * value;
		}
	}

	/**
	 * Ensure that the specified number of elements can be added to the array.
	 * <p>
	 * This is not synchronized. However any class using the safeAdd() methods in different threads should be using the
	 * same synchronized method to add data thus this method will be within synchronized code.
	 * 
	 * @param length
	 */
	private void checkCapacity(int length)
	{
		stats = null;
		final int minCapacity = n + length;
		final int oldCapacity = values.length;
		if (minCapacity > oldCapacity)
		{
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			double[] newValues = new double[newCapacity];
			System.arraycopy(values, 0, newValues, 0, n);
			values = newValues;
		}
	}

	/**
	 * Add the data
	 * 
	 * @param data
	 */
	public void add(double[] data)
	{
		if (data == null)
			return;
		checkCapacity(data.length);
		for (int i = 0; i < data.length; i++)
		{
			final double value = data[i];
			values[n++] = value;
			s += value;
			ss += value * value;
		}
	}

	/**
	 * Add the data
	 * 
	 * @param data
	 */
	public void add(int[] data)
	{
		if (data == null)
			return;
		checkCapacity(data.length);
		for (int i = 0; i < data.length; i++)
		{
			final double value = data[i];
			values[n++] = value;
			s += value;
			ss += value * value;
		}
	}

	/**
	 * Add the value
	 * 
	 * @param value
	 */
	public void add(final double value)
	{
		checkCapacity(1);
		values[n++] = value;
		s += value;
		ss += value * value;
	}

	/**
	 * Add the value n times
	 * 
	 * @param n
	 *            The number of times
	 * @param value
	 *            The value
	 */
	public void add(int n, double value)
	{
		checkCapacity(n);
		for (int i = 0; i < n; i++)
			values[this.n++] = value;
		s += n * value;
		ss += n * value * value;
	}

	/**
	 * Add the data. Synchronized for thread safety. (Multiple threads must all use the same safeAdd method to ensure
	 * thread safety.)
	 * 
	 * @param data
	 */
	synchronized public void safeAdd(float[] data)
	{
		if (data == null)
			return;
		checkCapacity(data.length);
		for (int i = 0; i < data.length; i++)
		{
			final double value = data[i];
			values[n++] = value;
			s += value;
			ss += value * value;
		}
	}

	/**
	 * Add the data. Synchronized for thread safety. (Multiple threads must all use the same safeAdd method to ensure
	 * thread safety.)
	 * 
	 * @param data
	 */
	synchronized public void safeAdd(double[] data)
	{
		if (data == null)
			return;
		checkCapacity(data.length);
		for (int i = 0; i < data.length; i++)
		{
			final double value = data[i];
			values[n++] = value;
			s += value;
			ss += value * value;
		}
	}

	/**
	 * Add the value. Synchronized for thread safety. (Multiple threads must all use the same safeAdd method to ensure
	 * thread safety.)
	 * 
	 * @param value
	 */
	synchronized public void safeAdd(final double value)
	{
		checkCapacity(1);
		values[n++] = value;
		s += value;
		ss += value * value;
	}

	/**
	 * @return A copy of the values added
	 */
	public double[] getValues()
	{
		return Arrays.copyOf(values, n);
	}

	/**
	 * Gets the value.
	 *
	 * @param i
	 *            the index
	 * @return the value
	 */
	public double getValue(int i)
	{
		return values[i];
	}

	/**
	 * @return A copy of the values added
	 */
	public float[] getFloatValues()
	{
		float[] data = new float[n];
		for (int i = 0; i < n; i++)
			data[i] = (float) values[i];
		return data;
	}

	/**
	 * @return object used to compute descriptive statistics. The object is cached
	 * @see {@link org.apache.commons.math3.stat.descriptive.DescriptiveStatistics }
	 */
	public DescriptiveStatistics getStatistics()
	{
		if (stats == null)
			stats = new DescriptiveStatistics(values);
		return stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.utils.Statistics#add(gdsc.core.utils.Statistics)
	 */
	@Override
	public void add(Statistics statistics)
	{
		if (statistics instanceof StoredDataStatistics)
		{
			StoredDataStatistics extra = (StoredDataStatistics) statistics;
			if (extra.n > 0)
			{
				checkCapacity(extra.n);
				System.arraycopy(extra.values, 0, values, n, extra.n);
			}
		}
		super.add(statistics);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.utils.Statistics#safeAdd(gdsc.core.utils.Statistics)
	 */
	@Override
	synchronized public void safeAdd(Statistics statistics)
	{
		this.add(statistics);
	}

	/**
	 * @return The median
	 */
	public double getMedian()
	{
		// Check for negatives
		for (double d : values)
		{
			if (d < 0)
			{
				if (n == 0)
					return Double.NaN;
				if (n == 1)
					return values[0];

				double[] data = getValues();
				Arrays.sort(data);
				return (data[(data.length - 1) / 2] + data[data.length / 2]) * 0.5;
			}
		}

		// This does not work when the array contains negative data due to the 
		// implementation of the library using partially sorted data
		return getStatistics().getPercentile(50);
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 *
	 * @return a list iterator over the elements in this list (in proper
	 *         sequence)
	 */
	public Iterator<Double> iterator()
	{
		return new Itr();
	}

	/**
	 * Copied from ArrayList and removed unrequired code
	 */
	private class Itr implements Iterator<Double>
	{
		int cursor; // index of next element to return

		public boolean hasNext()
		{
			return cursor != n;
		}

		public Double next()
		{
			// Simple implementation. Will throw index-out-of-bounds eventually
			return StoredDataStatistics.this.values[cursor++];

			// Copied from ArrayList and removed unrequired code
			//int i = cursor;
			//if (i >= n)
			//	throw new NoSuchElementException();
			//final double[] elementData = StoredDataStatistics.this.values;
			//if (i >= elementData.length)
			//	throw new ConcurrentModificationException();
			//cursor = i + 1;
			//return elementData[i];
		}

		public void remove()
		{
			throw new UnsupportedOperationException("remove");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.utils.DoubleData#size()
	 */
	public int size()
	{
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.utils.DoubleData#values()
	 */
	public double[] values()
	{
		return getValues();
	}
}