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
	 * <p>
	 * Note that if the number of points to return (n) is above half the total size of the input list (m, e.g. n/m >
	 * 0.5) then it is probably faster to sort the input list and take the top n.
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
	 * <p>
	 * This class is based on ags.utils.dataStructures.trees.secondGenKD.ResultHeap
	 */
	public static class DoubleHeap
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
		 * Instantiates a new max double heap.
		 *
		 * @param n
		 *            the number to select
		 */
		public DoubleHeap(int n)
		{
			if (n < 1)
				throw new IllegalArgumentException("N must be strictly positive");
			this.queue = new double[n];
			this.n = n;
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
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				// Insert new value at the end
				queue[i] = list[i];
				bottomUpHeapify(i);
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if lower
				if (queue[0] > list[i])
				{
					queue[0] = list[i];
					bottomDownHeapify(0);
				}
				i++;
			}

			if (options == 0)
				return queue;

			return quickFinish(queue, options);
		}

		private void bottomUpHeapify(int c)
		{
			for (int p = (c - 1) / 2; c != 0 && queue[c] > queue[p]; c = p, p = (c - 1) / 2)
			{
				double pDist = queue[p];
				queue[p] = queue[c];
				queue[c] = pDist;
			}
		}

		private void bottomDownHeapify(int p)
		{
			for (int c = p * 2 + 1; c < n; p = c, c = p * 2 + 1)
			{
				if (c + 1 < n && queue[c] < queue[c + 1])
				{
					c++;
				}
				if (queue[p] < queue[c])
				{
					// Swap the points
					double pDist = queue[p];
					queue[p] = queue[c];
					queue[c] = pDist;
				}
				else
				{
					break;
				}
			}
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
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				// Insert new value at the end
				queue[i] = list[i];
				topUpHeapify(i);
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if higher
				if (queue[0] < list[i])
				{
					queue[0] = list[i];
					topDownHeapify(0);
				}
				i++;
			}

			if (options == 0)
				return queue;

			return quickFinish(queue, options | OPTION_TOP);
		}

		private void topUpHeapify(int c)
		{
			for (int p = (c - 1) / 2; c != 0 && queue[c] < queue[p]; c = p, p = (c - 1) / 2)
			{
				double pDist = queue[p];
				queue[p] = queue[c];
				queue[c] = pDist;
			}
		}

		private void topDownHeapify(int p)
		{
			for (int c = p * 2 + 1; c < n; p = c, c = p * 2 + 1)
			{
				if (c + 1 < n && queue[c] > queue[c + 1])
				{
					c++;
				}
				if (queue[p] > queue[c])
				{
					// Swap the points
					double pDist = queue[p];
					queue[p] = queue[c];
					queue[c] = pDist;
				}
				else
				{
					break;
				}
			}
		}
	}

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
	}

	private static double[] quickFinish(double[] data, int options)
	{
		data = removeNaN(data, options);
		sort(data, options);
		return data;
	}

	private static double[] bottomFinish(double[] data, int options)
	{
		if (options == 0)
			return data;
		replaceHead(data, options);
		return quickFinish(data, options);
	}

	private static double[] topFinish(double[] data, int options)
	{
		if (options == 0)
			return data;
		options |= OPTION_TOP;
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

	private static void swapHead(double[] data, int max)
	{
		double head = data[max];
		data[max] = data[0];
		data[0] = head;
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

	/**
	 * Provide partial sort of float arrays
	 * <p>
	 * This class is based on ags.utils.dataStructures.trees.secondGenKD.ResultHeap
	 */
	public static class FloatHeap
	{
		/**
		 * The number N to select
		 */
		final int n;
		/**
		 * Working storage
		 */
		private final float[] queue;

		/**
		 * Instantiates a new max float heap.
		 *
		 * @param n
		 *            the number to select
		 */
		public FloatHeap(int n)
		{
			if (n < 1)
				throw new IllegalArgumentException("N must be strictly positive");
			this.queue = new float[n];
			this.n = n;
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
		public float[] bottom(float[] list)
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
		public float[] bottom(int options, float[] list)
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
		public float[] bottom(int options, float[] list, int size)
		{
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				// Insert new value at the end
				queue[i] = list[i];
				bottomUpHeapify(i);
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if lower
				if (queue[0] > list[i])
				{
					queue[0] = list[i];
					bottomDownHeapify(0);
				}
				i++;
			}

			if (options == 0)
				return queue;

			return quickFinish(queue, options);
		}

		private void bottomUpHeapify(int c)
		{
			for (int p = (c - 1) / 2; c != 0 && queue[c] > queue[p]; c = p, p = (c - 1) / 2)
			{
				float pDist = queue[p];
				queue[p] = queue[c];
				queue[c] = pDist;
			}
		}

		private void bottomDownHeapify(int p)
		{
			for (int c = p * 2 + 1; c < n; p = c, c = p * 2 + 1)
			{
				if (c + 1 < n && queue[c] < queue[c + 1])
				{
					c++;
				}
				if (queue[p] < queue[c])
				{
					// Swap the points
					float pDist = queue[p];
					queue[p] = queue[c];
					queue[c] = pDist;
				}
				else
				{
					break;
				}
			}
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
		public float[] top(float[] list)
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
		public float[] top(int options, float[] list)
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
		public float[] top(int options, float[] list, int size)
		{
			queue[0] = list[0];

			// Fill 
			int i = 1;
			while (i < n)
			{
				// Insert new value at the end
				queue[i] = list[i];
				topUpHeapify(i);
				i++;
			}

			// Scan
			while (i < size)
			{
				// Replace if lower
				if (queue[0] > list[i])
				{
					queue[0] = list[i];
					topDownHeapify(0);
				}
				i++;
			}

			if (options == 0)
				return queue;

			return quickFinish(queue, options | OPTION_TOP);
		}

		private void topUpHeapify(int c)
		{
			for (int p = (c - 1) / 2; c != 0 && queue[c] < queue[p]; c = p, p = (c - 1) / 2)
			{
				float pDist = queue[p];
				queue[p] = queue[c];
				queue[c] = pDist;
			}
		}

		private void topDownHeapify(int p)
		{
			for (int c = p * 2 + 1; c < n; p = c, c = p * 2 + 1)
			{
				if (c + 1 < n && queue[c] > queue[c + 1])
				{
					c++;
				}
				if (queue[p] > queue[c])
				{
					// Swap the points
					float pDist = queue[p];
					queue[p] = queue[c];
					queue[c] = pDist;
				}
				else
				{
					break;
				}
			}
		}
	}

	/**
	 * Provide partial sort of float arrays
	 */
	public static class FloatSelector
	{
		/**
		 * The number N to select
		 */
		final int n;
		/**
		 * Working storage
		 */
		private final float[] queue;

		/**
		 * Create a new FloatSelector
		 * 
		 * @param n
		 *            The number N to select
		 */
		public FloatSelector(int n)
		{
			if (n < 1)
				throw new IllegalArgumentException("N must be strictly positive");
			this.n = n;
			queue = new float[n];
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
		public float[] bottom(float[] list)
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
		public float[] bottom(int options, float[] list)
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
		public float[] bottom(int options, float[] list, int size)
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
		public float[] top(float[] list)
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
		public float[] top(int options, float[] list)
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
		public float[] top(int options, float[] list, int size)
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
	}

	private static int bottomMax(float[] data)
	{
		int max = 0;
		for (int i = 1; i < data.length; i++)
			if (data[max] < data[i])
				max = i;
		return max;
	}

	private static int topMax(float[] data)
	{
		int max = 0;
		for (int i = 1; i < data.length; i++)
			if (data[max] > data[i])
				max = i;
		return max;
	}

	private static float[] quickFinish(float[] data, int options)
	{
		data = removeNaN(data, options);
		sort(data, options);
		return data;
	}

	private static float[] bottomFinish(float[] data, int options)
	{
		if (options == 0)
			return data;
		replaceHead(data, options);
		return quickFinish(data, options);
	}

	private static float[] topFinish(float[] data, int options)
	{
		if (options == 0)
			return data;
		options |= OPTION_TOP;
		replaceHead(data, options);
		return quickFinish(data, options);
	}

	private static void replaceHead(float[] data, int options)
	{
		if ((options & OPTION_HEAD_FIRST) != 0)
		{
			swapHead(data, ((options & OPTION_TOP) != 0) ? topMax(data) : bottomMax(data));
		}
	}

	private static void swapHead(float[] data, int max)
	{
		float head = data[max];
		data[max] = data[0];
		data[0] = head;
	}

	private static float[] removeNaN(float[] data, int options)
	{
		if ((options & OPTION_REMOVE_NAN) != 0)
		{
			int size = 0;
			for (int i = 0; i < data.length; i++)
			{
				if (Float.isNaN(data[i]))
					continue;
				data[size++] = data[i];
			}
			if (size == data.length)
				return data;
			if (size == 0)
				return new float[0];
			data = Arrays.copyOf(data, size);
		}
		return data;
	}

	private static void sort(float[] data, int options)
	{
		if ((options & OPTION_SORT) != 0)
		{
			Arrays.sort(data);
			if ((options & OPTION_TOP) != 0)
				Sort.reverse(data);
		}
	}

	// XXX - Copy bottom methods from here

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
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The bottom N
	 */
	public static double[] bottom(int options, double[] list, int size, int n)
	{
		if (list == null || size <= 0)
			return new double[0];
		size = Math.min(size, list.length);
		if (size <= n)
		{
			list = bottomFinish(list.clone(), options);
		}
		else if (n < 5)
		{
			list = new DoubleSelector(n).bottom(options, list, size);
		}
		else
		{
			list = new DoubleHeap(n).bottom(options, list, size);
		}
		return list;
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
	public static float[] bottom(float[] list, int n)
	{
		if (list == null)
			return new float[0];
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
	public static float[] bottom(float[] list, int size, int n)
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
	public static float[] bottom(int options, float[] list, int n)
	{
		if (list == null)
			return new float[0];
		return bottom(options, list, list.length, n);
	}

	/**
	 * Pick the bottom N from the data using ascending order, i.e. find the bottom n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The bottom N
	 */
	public static float[] bottom(int options, float[] list, int size, int n)
	{
		if (list == null || size <= 0)
			return new float[0];
		size = Math.min(size, list.length);
		if (size <= n)
		{
			list = bottomFinish(list.clone(), options);
		}
		else if (n < 5)
		{
			list = new FloatSelector(n).bottom(options, list, size);
		}
		else
		{
			list = new FloatHeap(n).bottom(options, list, size);
		}
		return list;
	}

	// XXX - Copy to here

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static double[] top(double[] list, int n)
	{
		if (list == null)
			return new double[0];
		return top(list, list.length, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 * 
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static double[] top(double[] list, int size, int n)
	{
		return top(OPTION_CLEAN, list, size, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static double[] top(int options, double[] list, int n)
	{
		if (list == null)
			return new double[0];
		return top(options, list, list.length, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static double[] top(int options, double[] list, int size, int n)
	{
		if (list == null || size <= 0)
			return new double[0];
		size = Math.min(size, list.length);
		if (size <= n)
		{
			list = topFinish(list.clone(), options);
		}
		else if (n < 5)
		{
			list = new DoubleSelector(n).top(options, list, size);
		}
		else
		{
			list = new DoubleHeap(n).top(options, list, size);
		}
		return list;
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static float[] top(float[] list, int n)
	{
		if (list == null)
			return new float[0];
		return top(list, list.length, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 * 
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static float[] top(float[] list, int size, int n)
	{
		return top(OPTION_CLEAN, list, size, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static float[] top(int options, float[] list, int n)
	{
		if (list == null)
			return new float[0];
		return top(options, list, list.length, n);
	}

	/**
	 * Pick the top N from the data using ascending order, i.e. find the top n smallest values.
	 *
	 * @param options
	 *            the options
	 * @param list
	 *            the data list
	 * @param size
	 *            The size of the list
	 * @param n
	 *            The number N to select
	 * @return The top N
	 */
	public static float[] top(int options, float[] list, int size, int n)
	{
		if (list == null || size <= 0)
			return new float[0];
		size = Math.min(size, list.length);
		if (size <= n)
		{
			list = topFinish(list.clone(), options);
		}
		else if (n < 5)
		{
			list = new FloatSelector(n).top(options, list, size);
		}
		else
		{
			list = new FloatHeap(n).top(options, list, size);
		}
		return list;
	}
}
