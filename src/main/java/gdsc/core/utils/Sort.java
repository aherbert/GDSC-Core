package gdsc.core.utils;

import java.util.Arrays;
import java.util.Comparator;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2013 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * Provides sorting functionality
 */
public class Sort
{
	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final int[] values)
	{
		return sort(indices, values, false);
	}

	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final int[] values, boolean sortValues)
	{
		// Convert data for sorting
		int[][] data = new int[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<int[]>()
		{
			public int compare(int[] o1, int[] o2)
			{
				// Largest first
				if (o2[0] < o1[0])
					return -1;
				if (o2[0] > o1[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final float[] values)
	{
		return sort(indices, values, false);
	}

	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final float[] values, boolean sortValues)
	{
		// Convert data for sorting
		float[][] data = new float[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<float[]>()
		{
			public int compare(float[] o1, float[] o2)
			{
				// Largest first
				if (o2[0] < o1[0])
					return -1;
				if (o2[0] > o1[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = (int) data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final double[] values)
	{
		return sort(indices, values, false);
	}

	/**
	 * Sorts the indices in descending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sort(int[] indices, final double[] values, boolean sortValues)
	{
		// Convert data for sorting
		double[][] data = new double[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<double[]>()
		{
			public int compare(double[] o1, double[] o2)
			{
				// Largest first
				if (o2[0] < o1[0])
					return -1;
				if (o2[0] > o1[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = (int) data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final int[] values)
	{
		return sortAscending(indices, values, false);
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final int[] values, boolean sortValues)
	{
		// Convert data for sorting
		int[][] data = new int[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<int[]>()
		{
			public int compare(int[] o1, int[] o2)
			{
				// Smallest first
				if (o1[0] < o2[0])
					return -1;
				if (o1[0] > o2[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final float[] values)
	{
		return sortAscending(indices, values, false);
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final float[] values, boolean sortValues)
	{
		// Convert data for sorting
		float[][] data = new float[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<float[]>()
		{
			public int compare(float[] o1, float[] o2)
			{
				// Smallest first
				if (o1[0] < o2[0])
					return -1;
				if (o1[0] > o2[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = (int) data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final double[] values)
	{
		return sortAscending(indices, values, false);
	}

	/**
	 * Sorts the indices in ascending order of their values
	 * 
	 * @param indices
	 * @param values
	 * @param sortValues
	 * @return The indices
	 */
	public static int[] sortAscending(int[] indices, final double[] values, boolean sortValues)
	{
		// Convert data for sorting
		double[][] data = new double[indices.length][2];
		for (int i = indices.length; i-- > 0;)
		{
			data[i][0] = values[indices[i]];
			data[i][1] = indices[i];
		}

		Arrays.sort(data, new Comparator<double[]>()
		{
			public int compare(double[] o1, double[] o2)
			{
				// Smallest first
				if (o1[0] < o2[0])
					return -1;
				if (o1[0] > o2[0])
					return 1;
				return 0;
			}
		});

		// Copy back
		for (int i = indices.length; i-- > 0;)
		{
			indices[i] = (int) data[i][1];
		}
		if (sortValues)
		{
			for (int i = indices.length; i-- > 0;)
			{
				values[i] = data[i][0];
			}
		}

		return indices;
	}

	/**
	 * Reverse the array order
	 * 
	 * @param data
	 * @deprecated Moved to SimpleArrayUtils
	 */
	public static void reverse(int[] data)
	{
		int left = 0;
		int right = data.length - 1;

		while (left < right)
		{
			// swap the values at the left and right indices
			int temp = data[left];
			data[left] = data[right];
			data[right] = temp;

			// move the left and right index pointers in toward the center
			left++;
			right--;
		}
	}

	/**
	 * Reverse the array order
	 * 
	 * @param data
	 * @deprecated Moved to SimpleArrayUtils
	 */
	public static void reverse(float[] data)
	{
		int left = 0;
		int right = data.length - 1;

		while (left < right)
		{
			// swap the values at the left and right indices
			float temp = data[left];
			data[left] = data[right];
			data[right] = temp;

			// move the left and right index pointers in toward the center
			left++;
			right--;
		}
	}

	/**
	 * Reverse the array order
	 * 
	 * @param data
	 * @deprecated Moved to SimpleArrayUtils
	 */
	public static void reverse(double[] data)
	{
		int left = 0;
		int right = data.length - 1;

		while (left < right)
		{
			// swap the values at the left and right indices
			double temp = data[left];
			data[left] = data[right];
			data[right] = temp;

			// move the left and right index pointers in toward the center
			left++;
			right--;
		}
	}

	/**
	 * Sorts array 1 using the values in array 2.
	 * 
	 * @param values1
	 * @param values2
	 * @param ascending
	 * @return The two arrays, sorted using array 2
	 */
	public static void sortArrays(int[] values1, final double[] values2, boolean ascending)
	{
		// Extract indices
		int[] indices = new int[values1.length];
		for (int i = values1.length; i-- > 0;)
		{
			indices[i] = i;
		}

		if (ascending)
			sortAscending(indices, values2, false);
		else
			sort(indices, values2, false);

		// Copy back
		int[] v1 = Arrays.copyOf(values1, values1.length);
		double[] v2 = Arrays.copyOf(values2, values2.length);

		for (int i = values1.length; i-- > 0;)
		{
			values1[i] = v1[indices[i]];
			values2[i] = v2[indices[i]];
		}
	}

	/**
	 * Sorts array 1 using the values in array 2.
	 * 
	 * @param values1
	 * @param values2
	 * @param ascending
	 * @return The two arrays, sorted using array 2
	 */
	public static void sortArrays(int[] values1, final float[] values2, boolean ascending)
	{
		// Extract indices
		int[] indices = new int[values1.length];
		for (int i = values1.length; i-- > 0;)
		{
			indices[i] = i;
		}

		if (ascending)
			sortAscending(indices, values2, false);
		else
			sort(indices, values2, false);

		// Copy back
		int[] v1 = Arrays.copyOf(values1, values1.length);
		float[] v2 = Arrays.copyOf(values2, values2.length);

		for (int i = values1.length; i-- > 0;)
		{
			values1[i] = v1[indices[i]];
			values2[i] = v2[indices[i]];
		}
	}

	/**
	 * Sorts array 1 using the values in array 2.
	 * 
	 * @param values1
	 * @param values2
	 * @param ascending
	 * @return The two arrays, sorted using array 2
	 */
	public static void sortArrays(int[] values1, final int[] values2, boolean ascending)
	{
		// Extract indices
		int[] indices = new int[values1.length];
		for (int i = values1.length; i-- > 0;)
		{
			indices[i] = i;
		}

		if (ascending)
			sortAscending(indices, values2, false);
		else
			sort(indices, values2, false);

		// Copy back
		int[] v1 = Arrays.copyOf(values1, values1.length);
		int[] v2 = Arrays.copyOf(values2, values2.length);

		for (int i = values1.length; i-- > 0;)
		{
			values1[i] = v1[indices[i]];
			values2[i] = v2[indices[i]];
		}
	}

	/**
	 * Sorts array 1 using the values in array 2.
	 * 
	 * @param values1
	 * @param values2
	 * @param ascending
	 * @return The two arrays, sorted using array 2
	 */
	public static void sortArrays(float[] values1, final float[] values2, boolean ascending)
	{
		// Extract indices
		int[] indices = new int[values1.length];
		for (int i = values1.length; i-- > 0;)
		{
			indices[i] = i;
		}

		if (ascending)
			sortAscending(indices, values2, false);
		else
			sort(indices, values2, false);

		// Copy back
		float[] v1 = Arrays.copyOf(values1, values1.length);
		float[] v2 = Arrays.copyOf(values2, values2.length);

		for (int i = values1.length; i-- > 0;)
		{
			values1[i] = v1[indices[i]];
			values2[i] = v2[indices[i]];
		}
	}

	/**
	 * Sorts array 1 using the values in array 2.
	 * 
	 * @param values1
	 * @param values2
	 * @param ascending
	 * @return The two arrays, sorted using array 2
	 */
	public static void sortArrays(double[] values1, final double[] values2, boolean ascending)
	{
		// Extract indices
		int[] indices = new int[values1.length];
		for (int i = values1.length; i-- > 0;)
		{
			indices[i] = i;
		}

		if (ascending)
			sortAscending(indices, values2, false);
		else
			sort(indices, values2, false);

		// Copy back
		double[] v1 = Arrays.copyOf(values1, values1.length);
		double[] v2 = Arrays.copyOf(values2, values2.length);

		for (int i = values1.length; i-- > 0;)
		{
			values1[i] = v1[indices[i]];
			values2[i] = v2[indices[i]];
		}
	}
}
