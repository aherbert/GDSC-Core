package gdsc.core.utils;

import java.util.Arrays;

/*----------------------------------------------------------------------------- 
 * GDSC ImageJ Software
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
 * Provides functionality to partially sort an array
 * 
 * @author Alex Herbert
 */
public class PartialSort
{
	/**
	 * Sort the final returned data. This takes precedence over {@link #OPTION_HEAD_FIRST}.
	 */
	public final static int OPTION_SORT = 1;

	/**
	 * Remove NaN values from the returned data
	 */
	public final static int OPTION_REMOVE_NAN = 2;

	/**
	 * Return the head position at the first point in the returned data (i.e. if choosing the bottom N then array[0]
	 * will
	 * contain the Nth point. This is not compatible with {@link #OPTION_SORT}.
	 */
	public final static int OPTION_HEAD_FIRST = 4;

	/**
	 * Return a sorted array with no invalid data
	 */
	public final static int OPTION_CLEAN = OPTION_SORT | OPTION_REMOVE_NAN;

	/**
	 * Internal top direction flag
	 */
	private final static int OPTION_TOP = 8;

	/**
	 * Provide partial sort of double arrays
	 */
	public static class DoubleSelector
	{
		/**
		 * The number N to select
		 */
		final int n;
		/**
		 * Working storage
		 */
		private final double[] queue;

		/**
		 * Create a new DoubleSelector
		 * 
		 * @param n
		 *            The number N to select
		 */
		public DoubleSelector(int n)
		{
			if (n < 1)
				throw new IllegalArgumentException("N must be strictly positive");
			this.n = n;
			queue = new double[n];
		}

		/**
		 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 * 
		 * @param list
		 *            the data list
		 * @return The bottom N (passed as a reference to internal data structure)
		 */
		public double[] bottom(double[] list)
		{
			return bottom(OPTION_CLEAN, list, list.length);
		}

		/**
		 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 *
		 * @param list
		 *            the data list
		 * @param options
		 *            the options
		 * @return The bottom N (passed as a reference to internal data structure)
		 */
		public double[] bottom(int options, double[] list)
		{
			return bottom(options, list, list.length);
		}

		/**
		 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 *
		 * @param list
		 *            the data list
		 * @param size
		 *            The size of the list (must be equal or above N)
		 * @param options
		 *            the options
		 * @return The bottom N (passed as a reference to internal data structure)
		 */
		public double[] bottom(int options, double[] list, int size)
		{
			// We retain a pointer to the current highest value in the set. 
			int max = 0;
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				queue[i] = list[i];
				if (queue[max] < queue[i])
					max = i;
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if lower
				if (queue[max] > list[i])
				{
					queue[max] = list[i];
					// Find new max
					max = bottomMax(queue);
				}
				i++;
			}

			if (options == 0)
				return queue;

			if ((options & OPTION_HEAD_FIRST) != 0)
				swapHead(queue, max);

			return quickFinish(queue, options);
		}

		/**
		 * Pick the top N from the data using ascending order, i.e. find the top n largest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 * 
		 * @param list
		 *            the data list
		 * @return The top N (passed as a reference to internal data structure)
		 */
		public double[] top(double[] list)
		{
			return top(OPTION_CLEAN, list, list.length);
		}

		/**
		 * Pick the top N from the data using ascending order, i.e. find the top n largest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 *
		 * @param options
		 *            the options
		 * @param list
		 *            the data list
		 * @return The top N (passed as a reference to internal data structure)
		 */
		public double[] top(int options, double[] list)
		{
			return top(options, list, list.length);
		}

		/**
		 * Pick the top N from the data using ascending order, i.e. find the top n largest values.
		 * <p>
		 * If the input data size is smaller than N then an {@link ArrayIndexOutOfBoundsException} will occur.
		 *
		 * @param list
		 *            the data list
		 * @param size
		 *            The size of the list (must be equal or above N)
		 * @param options
		 *            the options
		 * @return The top N (passed as a reference to internal data structure)
		 */
		public double[] top(int options, double[] list, int size)
		{
			// We retain a pointer to the current highest value in the set. 
			int max = 0;
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				queue[i] = list[i];
				if (queue[max] > queue[i])
					max = i;
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if higher
				if (queue[max] < list[i])
				{
					queue[max] = list[i];
					// Find new max
					max = topMax(queue);
				}
				i++;
			}

			if (options == 0)
				return queue;

			if ((options & OPTION_HEAD_FIRST) != 0)
				swapHead(queue, max);

			return quickFinish(queue, options | OPTION_TOP);
		}

		private static int bottomMax(double[] data)
		{
			int max = 0;
			for (int i = 1; i < data.length; i++)
				if (data[max] < data[i])
					max = i;
			return max;
		}

		private static int topMax(double[] data)
		{
			int max = 0;
			for (int i = 1; i < data.length; i++)
				if (data[max] > data[i])
					max = i;
			return max;
		}

		private static double[] quickFinish(double[] data, int options)
		{
			data = removeNaN(data, options);
			sort(data, options);
			return data;
		}

		private static double[] finish(double[] data, int options)
		{
			replaceHead(data, options);
			return quickFinish(data, options);
		}

		private static void replaceHead(double[] data, int options)
		{
			if ((options & OPTION_HEAD_FIRST) != 0)
			{
				swapHead(data, ((options & OPTION_TOP) != 0) ? topMax(data) : bottomMax(data));
			}
		}

		private static void swapHead(double[] data, int max)
		{
			double head = data[max];
			data[max] = data[0];
			data[0] = head;
		}

		private static double[] removeNaN(double[] data, int options)
		{
			if ((options & OPTION_REMOVE_NAN) != 0)
			{
				int size = 0;
				for (int i = 0; i < data.length; i++)
				{
					if (Double.isNaN(data[i]))
						continue;
					data[size++] = data[i];
				}
				if (size == data.length)
					return data;
				if (size == 0)
					return new double[0];
				data = Arrays.copyOf(data, size);
			}
			return data;
		}

		private static void sort(double[] data, int options)
		{
			if ((options & OPTION_SORT) != 0)
			{
				Arrays.sort(data);
				if ((options & OPTION_TOP) != 0)
					Sort.reverse(data);
			}
		}
	}

	/**
	 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
	 *
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The bottom N
	 */
	public static double[] bottom(double[] list, int n)
	{
		if (list == null)
			return new double[0];
		return bottom(list, list.length, n);
	}

	/**
	 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
	 * 
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The bottom N
	 */
	public static double[] bottom(double[] list, int size, int n)
	{
		return bottom(OPTION_CLEAN, list, size, n);
	}

	/**
	 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The bottom N
	 */
	public static double[] bottom(int options, double[] list, int n)
	{
		if (list == null)
			return new double[0];
		return bottom(options, list, list.length, n);
	}

	/**
	 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
	 * 
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @param options
	 *            the options
	 * @return The bottom N
	 */
	public static double[] bottom(int options, double[] list, int size, int n)
	{
		if (list == null || size <= 0)
			return new double[0];
		size = Math.min(size, list.length);
		if (size < n)
		{
			list = DoubleSelector.finish(list.clone(), options);
		}
		else
		{
			list = new DoubleSelector(n).bottom(options, list, size);
		}
		return list;
	}
}