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
 * Copyright (C) 2011 - 2020 Alex Herbert
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

package uk.ac.sussex.gdsc.core.trees;

import java.util.function.BiPredicate;

/**
 * Provide implementations for equality computation for {@code double[]} arrays.
 *
 * @since 2.0
 */
public enum DoubleArrayPredicates implements BiPredicate<double[], double[]> {
  /**
   * Compute array equality in N-dimensions using the {@code ==} operator, thus {@code -0.0} is
   * equal to {@code 0.0}.
   */
  EQUALS_ND {
    @Override
    public boolean test(double[] p1, double[] p2) {
      for (int i = 0; i < p1.length; i++) {
        if (p1[i] == p2[i]) {
          continue;
        }
        return false;
      }
      return true;
    }
  },
  /**
   * Compute array equality in 3-dimensions using the {@code ==} operator, thus {@code -0.0} is
   * equal to {@code 0.0}.
   */
  EQUALS_3D {
    @Override
    public boolean test(double[] p1, double[] p2) {
      return p1[0] == p2[0] && p1[1] == p2[1] && p1[2] == p2[2];
    }
  },
  /**
   * Compute array equality in 2-dimensions using the {@code ==} operator, thus {@code -0.0} is
   * equal to {@code 0.0}.
   */
  EQUALS_2D {
    @Override
    public boolean test(double[] p1, double[] p2) {
      return p1[0] == p2[0] && p1[1] == p2[1];
    }
  };

  /**
   * Return a predicate to compute array equality using the {@code ==} operator, thus {@code -0.0}
   * is equal to {@code 0.0}.
   *
   * @param dimensions the dimensions
   * @return the distance function
   */
  public static BiPredicate<double[], double[]> equals(int dimensions) {
    if (dimensions == 2) {
      return EQUALS_2D;
    }
    if (dimensions == 3) {
      return EQUALS_3D;
    }
    return EQUALS_ND;
  }
}
