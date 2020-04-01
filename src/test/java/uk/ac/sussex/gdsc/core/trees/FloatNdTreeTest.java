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

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.sussex.gdsc.core.utils.LocalList;
import uk.ac.sussex.gdsc.core.utils.MathUtils;
import uk.ac.sussex.gdsc.core.utils.PartialSort;
import uk.ac.sussex.gdsc.test.junit5.RandomSeed;
import uk.ac.sussex.gdsc.test.junit5.SeededTest;
import uk.ac.sussex.gdsc.test.rng.RngUtils;

@SuppressWarnings({"javadoc"})
public class FloatNdTreeTest {
  private static final int BUCKET_SIZE = 24;

  @Test
  public void testEmptyTree() {
    final FloatKdTree tree = KdTrees.newFloatKdTree(2);
    Assertions.assertEquals(2, tree.dimensions());
    Assertions.assertEquals(0, tree.size());
    final TDoubleArrayList distances = new TDoubleArrayList();
    final FloatDistanceFunction distanceFunction = FloatDistanceFunctions.SQUARED_EUCLIDEAN_2D;
    final double[] point = new double[2];

    final double min = tree.nearestNeighbour(point, distanceFunction, distances::add);
    Assertions.assertEquals(0, min);
    Assertions.assertTrue(distances.isEmpty());

    boolean result = tree.nearestNeighbours(point, 1, true, distanceFunction, distances::add);
    Assertions.assertFalse(result);
    Assertions.assertTrue(distances.isEmpty());

    result = tree.findNeighbours(point, 10.0, distanceFunction, distances::add);
    Assertions.assertFalse(result);
    Assertions.assertTrue(distances.isEmpty());
  }

  @Test
  public void testComputeKnnWithSmallTree() {
    float[][] data = new float[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1},};
    assertComputeKnn(data);
  }

  @Test
  public void testComputeKnnWithSingleTree() {
    float[][] data = new float[][] {{0, 0}};
    assertComputeKnn(data);
  }

  @Test
  public void testComputeKnnWithSingularity() {
    LocalList<float[]> list = new LocalList<>(BUCKET_SIZE * 3);
    float[] point1 = new float[2];
    // Create some data.
    // Make the first split on a singularity.
    IntStream.range(0, BUCKET_SIZE).forEach(i -> list.add(point1));
    IntStream.range(0, BUCKET_SIZE + 2).mapToObj(i -> new float[] {0, i}).forEach(list::add);
    float[][] data = list.toArray(new float[0][]);
    assertComputeKnn(data);
  }

  @SeededTest
  public void testComputeKnn(RandomSeed seed) {
    assertComputeKnn(seed, false);
  }

  @SeededTest
  public void testComputeKnnWithDuplicates(RandomSeed seed) {
    assertComputeKnn(seed, true);
  }

  private static void assertComputeKnn(RandomSeed seed, boolean allowDuplicates) {
    final UniformRandomProvider r = RngUtils.create(seed.getSeed());
    for (final int n : KdTreeTestUtils.ns) {
      final float[][] data =
          KdTreeTestUtils.createFloatData(r, KdTreeTestUtils.size, n, allowDuplicates);
      assertComputeKnn(data);
    }
  }

  private static void assertComputeKnn(float[][] data) {
    final TDoubleArrayList distances = new TDoubleArrayList();
    final FloatDistanceFunction distanceFunction = FloatDistanceFunctions.SQUARED_EUCLIDEAN_2D;
    final int n = data.length;

    // Create the KDtree
    final FloatKdTree tree = KdTrees.newFloatKdTree(2);
    Assertions.assertEquals(2, tree.dimensions());
    for (final float[] location : data) {
      tree.addPoint(location);
    }
    Assertions.assertEquals(n, tree.size());

    // Compute all-vs-all distances
    final double[][] d = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        d[i][j] =
            d[j][i] = MathUtils.distance2((double) data[i][0], data[i][1], data[j][0], data[j][1]);
      }
    }

    // For each point
    final double[] searchPoint = new double[2];
    for (int i = 0; i < n; i++) {
      searchPoint[0] = data[i][0];
      searchPoint[1] = data[i][1];
      // Get the sorted distances to neighbours
      final double[] d2 =
          PartialSort.bottom(d[i], KdTreeTestUtils.ks[KdTreeTestUtils.ks.length - 1]);

      // Check the closest
      distances.resetQuick();
      final double min = tree.nearestNeighbour(searchPoint, distanceFunction, distances::add);
      Assertions.assertEquals(min, d2[0]);
      Assertions.assertEquals(min, distances.getQuick(0));

      // Nearest neighbour handles a null consumer
      final double min2 = tree.nearestNeighbour(searchPoint, distanceFunction, null);
      Assertions.assertEquals(min, min2);

      // Nearest neighbours ignores a bad count
      distances.resetQuick();
      boolean result =
          tree.nearestNeighbours(searchPoint, -1, true, distanceFunction, distances::add);
      Assertions.assertFalse(result);
      Assertions.assertTrue(distances.isEmpty());

      // Get the knn
      for (final int k : KdTreeTestUtils.ks) {
        distances.resetQuick();
        result = tree.nearestNeighbours(searchPoint, k, true, distanceFunction, distances::add);
        Assertions.assertTrue(result);
        // Neighbours will be in reverse order
        distances.reverse();
        final double[] observed = distances.toArray();
        final double[] expected = Arrays.copyOf(d2, Math.min(n, k));

        Assertions.assertArrayEquals(expected, observed);

        // Same results can be obtained unsorted
        result = tree.nearestNeighbours(searchPoint, k, false, distanceFunction, dist -> {
          final int index = distances.indexOf(dist);
          Assertions.assertTrue(index >= 0);
        });
        Assertions.assertTrue(result);
      }

      // Get the neighbours within a range
      for (final double range : KdTreeTestUtils.ranges) {
        distances.resetQuick();
        result = tree.findNeighbours(searchPoint, range, distanceFunction, distances::add);
        // Check
        int count = 0;
        for (int j = 0; j < n; j++) {
          if (d[i][j] <= range) {
            count++;
          }
        }
        Assertions.assertEquals(count, distances.size());
        Assertions.assertEquals(result, !distances.isEmpty());
        for (int j = 0; j < distances.size(); j++) {
          Assertions.assertTrue(distances.getQuick(j) <= range);
        }
      }
    }
  }

  /**
   * Test that the distance function can be used with a singularity to exclude points.
   */
  @Test
  public void testSingularityWithDistanceAboveRange() {
    LocalList<float[]> list = new LocalList<>(BUCKET_SIZE * 3);
    float[] point1 = {0, 0};
    float[] point2 = {5, 4};
    // Create some data.
    list.add(point1);
    list.add(point2);
    // This should create two singularities with a split value roughly halfway between
    // 5 and 0 (i.e. 2.5)
    IntStream.range(1, BUCKET_SIZE).forEach(i -> {
      list.add(point1);
      list.add(point2);
    });
    float[][] data = list.toArray(new float[0][]);

    final TDoubleArrayList distances = new TDoubleArrayList();
    final FloatDistanceFunction distanceFunction = FloatDistanceFunctions.SQUARED_EUCLIDEAN_2D;

    // Create the KDtree
    final FloatKdTree tree = KdTrees.newFloatKdTree(2);
    Assertions.assertEquals(2, tree.dimensions());
    for (final float[] location : data) {
      tree.addPoint(location);
    }

    // Search with a point that is on the left side of the split value but actually closer to the
    // other point data.
    double[] p1 = {2, 4};
    distances.resetQuick();
    boolean result = tree.findNeighbours(p1, 9, distanceFunction, distances::add);
    Assertions.assertTrue(result);
    Assertions.assertEquals(BUCKET_SIZE, distances.size());
    for (double d : distances.toArray()) {
      Assertions.assertEquals(9, d);
    }

    // For nearest neighbours we use a NaN point so the distance is invalid
    double[] p2 = {2, Double.NaN};
    distances.resetQuick();
    result = tree.nearestNeighbours(p2, BUCKET_SIZE, false, distanceFunction, distances::add);
    Assertions.assertFalse(result);
    Assertions.assertTrue(distances.isEmpty());

    double d = tree.nearestNeighbour(p2, distanceFunction, distances::add);
    Assertions.assertEquals(Double.NaN, d);
    Assertions.assertTrue(distances.isEmpty());
  }

  @Test
  public void testComputeKnnWithNaN() {
    LocalList<float[]> list = new LocalList<>(BUCKET_SIZE * 2);
    float[] point1 = {0, 0};
    float[] point2 = {Float.NaN, Float.NaN};
    // Create some data.
    list.add(point1);
    list.add(point2);
    // This should split once and have to handle the NaN data
    IntStream.range(1, BUCKET_SIZE).mapToObj(i -> new float[] {0, i}).forEach(list::add);

    float[][] data = list.toArray(new float[0][]);

    final TDoubleArrayList distances = new TDoubleArrayList();
    final FloatDistanceFunction distanceFunction = FloatDistanceFunctions.SQUARED_EUCLIDEAN_2D;

    // Create the KDtree
    final FloatKdTree tree = KdTrees.newFloatKdTree(2);
    Assertions.assertEquals(2, tree.dimensions());
    for (final float[] location : data) {
      tree.addPoint(location);
    }

    // Search with a point in the tree.
    double[] p1 = {0, 4};
    distances.resetQuick();
    boolean result = tree.findNeighbours(p1, 1, distanceFunction, distances::add);
    Assertions.assertTrue(result);
    Assertions.assertEquals(3, distances.size());

    // For nearest neighbours we use a NaN point so the distance is invalid
    distances.resetQuick();
    result = tree.nearestNeighbours(p1, 3, false, distanceFunction, distances::add);
    Assertions.assertTrue(result);
    Assertions.assertEquals(3, distances.size());

    distances.resetQuick();
    double d = tree.nearestNeighbour(p1, distanceFunction, distances::add);
    Assertions.assertEquals(0.0, d);
    Assertions.assertEquals(1, distances.size());
    Assertions.assertEquals(0, distances.getQuick(0));
  }
}
