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

import java.util.Arrays;
import java.util.Comparator;

/**
 * Provides sorting functionality.
 */
public class Sort {
  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sort(int[] indices, final int[] values) {
    return sort(indices, values, false);
  }

  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sort(int[] indices, final int[] values, boolean sortValues) {
    // Convert data for sorting
    final int[][] data = new int[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      data[i][1] = indices[i];
    }

    Arrays.sort(data, new Comparator<int[]>() {
      @Override
      public int compare(int[] o1, int[] o2) {
        // Largest first
        if (o2[0] < o1[0]) {
          return -1;
        }
        if (o2[0] > o1[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      indices[i] = data[i][1];
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sort(int[] indices, final float[] values) {
    return sort(indices, values, false);
  }

  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sort(int[] indices, final float[] values, boolean sortValues) {
    // Convert data for sorting
    final float[][] data = new float[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      // data[i][1] = indices[i];
      // This is required to handle integers that do not fit in a float.
      // Speed test shows it is also faster than the cast.
      data[i][1] = Float.intBitsToFloat(indices[i]);
    }

    Arrays.sort(data, new Comparator<float[]>() {
      @Override
      public int compare(float[] o1, float[] o2) {
        // Largest first
        if (o2[0] < o1[0]) {
          return -1;
        }
        if (o2[0] > o1[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      // indices[i] = (int) data[i][1];
      indices[i] = Float.floatToRawIntBits(data[i][1]);
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sort(int[] indices, final double[] values) {
    return sort(indices, values, false);
  }

  /**
   * Sorts the indices in descending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sort(int[] indices, final double[] values, boolean sortValues) {
    // Convert data for sorting
    final double[][] data = new double[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      data[i][1] = indices[i];
    }

    Arrays.sort(data, new Comparator<double[]>() {
      @Override
      public int compare(double[] o1, double[] o2) {
        // Largest first
        if (o2[0] < o1[0]) {
          return -1;
        }
        if (o2[0] > o1[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      indices[i] = (int) data[i][1];
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final int[] values) {
    return sortAscending(indices, values, false);
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final int[] values, boolean sortValues) {
    // Convert data for sorting
    final int[][] data = new int[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      data[i][1] = indices[i];
    }

    Arrays.sort(data, new Comparator<int[]>() {
      @Override
      public int compare(int[] o1, int[] o2) {
        // Smallest first
        if (o1[0] < o2[0]) {
          return -1;
        }
        if (o1[0] > o2[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      indices[i] = data[i][1];
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final float[] values) {
    return sortAscending(indices, values, false);
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final float[] values, boolean sortValues) {
    // Convert data for sorting
    final float[][] data = new float[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      // data[i][1] = indices[i];
      // This is required to handle integers that do not fit in a float.
      // Speed test shows it is also faster than the cast.
      data[i][1] = Float.intBitsToFloat(indices[i]);
    }

    Arrays.sort(data, new Comparator<float[]>() {
      @Override
      public int compare(float[] o1, float[] o2) {
        // Smallest first
        if (o1[0] < o2[0]) {
          return -1;
        }
        if (o1[0] > o2[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      // indices[i] = (int) data[i][1];
      indices[i] = Float.floatToRawIntBits(data[i][1]);
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final double[] values) {
    return sortAscending(indices, values, false);
  }

  /**
   * Sorts the indices in ascending order of their values.
   *
   * @param indices the indices
   * @param values the values
   * @param sortValues the sort values
   * @return The indices
   */
  public static int[] sortAscending(int[] indices, final double[] values, boolean sortValues) {
    // Convert data for sorting
    final double[][] data = new double[indices.length][2];
    for (int i = indices.length; i-- > 0;) {
      data[i][0] = values[indices[i]];
      data[i][1] = indices[i];
    }

    Arrays.sort(data, new Comparator<double[]>() {
      @Override
      public int compare(double[] o1, double[] o2) {
        // Smallest first
        if (o1[0] < o2[0]) {
          return -1;
        }
        if (o1[0] > o2[0]) {
          return 1;
        }
        return 0;
      }
    });

    // Copy back
    for (int i = indices.length; i-- > 0;) {
      indices[i] = (int) data[i][1];
    }
    if (sortValues) {
      for (int i = indices.length; i-- > 0;) {
        values[i] = data[i][0];
      }
    }

    return indices;
  }

  /**
   * Sorts array 1 using the values in array 2.
   *
   * @param values1 the values 1
   * @param values2 the values 2
   * @param ascending the ascending
   */
  public static void sortArrays(int[] values1, final double[] values2, boolean ascending) {
    // Extract indices
    final int[] indices = new int[values1.length];
    for (int i = values1.length; i-- > 0;) {
      indices[i] = i;
    }

    if (ascending) {
      sortAscending(indices, values2, false);
    } else {
      sort(indices, values2, false);
    }

    // Copy back
    final int[] v1 = Arrays.copyOf(values1, values1.length);
    final double[] v2 = Arrays.copyOf(values2, values2.length);

    for (int i = values1.length; i-- > 0;) {
      values1[i] = v1[indices[i]];
      values2[i] = v2[indices[i]];
    }
  }

  /**
   * Sorts array 1 using the values in array 2.
   *
   * @param values1 the values 1
   * @param values2 the values 2
   * @param ascending the ascending
   */
  public static void sortArrays(int[] values1, final float[] values2, boolean ascending) {
    // Extract indices
    final int[] indices = new int[values1.length];
    for (int i = values1.length; i-- > 0;) {
      indices[i] = i;
    }

    if (ascending) {
      sortAscending(indices, values2, false);
    } else {
      sort(indices, values2, false);
    }

    // Copy back
    final int[] v1 = Arrays.copyOf(values1, values1.length);
    final float[] v2 = Arrays.copyOf(values2, values2.length);

    for (int i = values1.length; i-- > 0;) {
      values1[i] = v1[indices[i]];
      values2[i] = v2[indices[i]];
    }
  }

  /**
   * Sorts array 1 using the values in array 2.
   *
   * @param values1 the values 1
   * @param values2 the values 2
   * @param ascending the ascending
   */
  public static void sortArrays(int[] values1, final int[] values2, boolean ascending) {
    // Extract indices
    final int[] indices = new int[values1.length];
    for (int i = values1.length; i-- > 0;) {
      indices[i] = i;
    }

    if (ascending) {
      sortAscending(indices, values2, false);
    } else {
      sort(indices, values2, false);
    }

    // Copy back
    final int[] v1 = Arrays.copyOf(values1, values1.length);
    final int[] v2 = Arrays.copyOf(values2, values2.length);

    for (int i = values1.length; i-- > 0;) {
      values1[i] = v1[indices[i]];
      values2[i] = v2[indices[i]];
    }
  }

  /**
   * Sorts array 1 using the values in array 2.
   *
   * @param values1 the values 1
   * @param values2 the values 2
   * @param ascending the ascending
   */
  public static void sortArrays(float[] values1, final float[] values2, boolean ascending) {
    // Extract indices
    final int[] indices = new int[values1.length];
    for (int i = values1.length; i-- > 0;) {
      indices[i] = i;
    }

    if (ascending) {
      sortAscending(indices, values2, false);
    } else {
      sort(indices, values2, false);
    }

    // Copy back
    final float[] v1 = Arrays.copyOf(values1, values1.length);
    final float[] v2 = Arrays.copyOf(values2, values2.length);

    for (int i = values1.length; i-- > 0;) {
      values1[i] = v1[indices[i]];
      values2[i] = v2[indices[i]];
    }
  }

  /**
   * Sorts array 1 using the values in array 2.
   *
   * @param values1 the values 1
   * @param values2 the values 2
   * @param ascending the ascending
   */
  public static void sortArrays(double[] values1, final double[] values2, boolean ascending) {
    // Extract indices
    final int[] indices = new int[values1.length];
    for (int i = values1.length; i-- > 0;) {
      indices[i] = i;
    }

    if (ascending) {
      sortAscending(indices, values2, false);
    } else {
      sort(indices, values2, false);
    }

    // Copy back
    final double[] v1 = Arrays.copyOf(values1, values1.length);
    final double[] v2 = Arrays.copyOf(values2, values2.length);

    for (int i = values1.length; i-- > 0;) {
      values1[i] = v1[indices[i]];
      values2[i] = v2[indices[i]];
    }
  }
}
