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

package uk.ac.sussex.gdsc.core.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import uk.ac.sussex.gdsc.core.utils.rng.RandomUtils;
import uk.ac.sussex.gdsc.test.junit5.RandomSeed;
import uk.ac.sussex.gdsc.test.junit5.SeededTest;
import uk.ac.sussex.gdsc.test.junit5.SpeedTag;
import uk.ac.sussex.gdsc.test.rng.RngUtils;
import uk.ac.sussex.gdsc.test.utils.TestComplexity;
import uk.ac.sussex.gdsc.test.utils.TestLogUtils;
import uk.ac.sussex.gdsc.test.utils.TestSettings;
import uk.ac.sussex.gdsc.test.utils.functions.FunctionUtils;

@SuppressWarnings({"javadoc"})
class ClusteringEngineTest {
  private static Logger logger;

  @BeforeAll
  public static void beforeAll() {
    logger = Logger.getLogger(ClusteringEngineTest.class.getName());
  }

  @AfterAll
  public static void afterAll() {
    logger = null;
  }

  // Store the closest pair of clusters
  int ii;
  int jj;

  @SeededTest
  void canClusterClusterPointsAtDifferentDensitiesUsingCentroidLinkage(RandomSeed seed) {
    final UniformRandomProvider rg = RngUtils.create(seed.getSeed());
    for (final double radius : new double[] {5, 10, 20}) {
      for (final int size : new int[] {1000, 500, 300, 100}) {
        testClusting(rg, ClusteringAlgorithm.CENTROID_LINKAGE, radius, 100, size);
      }
    }
  }

  @SeededTest
  public void
      canClusterClusterPointsAtDifferentDensitiesUsingPairwiseWithoutNeighbours(RandomSeed seed) {
    final UniformRandomProvider rg = RngUtils.create(seed.getSeed());
    for (final double radius : new double[] {5, 10, 20}) {
      for (final int size : new int[] {1000, 500, 300, 100}) {
        testClusting(rg, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius, 100, size);
      }
    }
  }

  @SpeedTag
  @SeededTest
  void pairwiseWithoutNeighboursIsFasterThanCentroidLinkageAtLowDensities(RandomSeed seed) {
    Assumptions.assumeTrue(TestSettings.allow(TestComplexity.MEDIUM));

    final UniformRandomProvider rg = RngUtils.create(seed.getSeed());
    final int repeats = 10;
    final double radius = 50;
    final Object[] points = new Object[repeats];
    for (int i = 0; i < repeats; i++) {
      points[i] = createClusters(rg, 20, 1000, 2, radius / 2);
    }

    final long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
    final long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius);

    logger.log(TestLogUtils.getTimingRecord("(Low Density) Centroid-linkage", t1,
        "PairwiseWithoutNeighbours", t2));
  }

  @SpeedTag
  @SeededTest
  void pairwiseWithoutNeighboursIsSlowerThanCentroidLinkageAtHighDensities(RandomSeed seed) {
    Assumptions.assumeTrue(TestSettings.allow(TestComplexity.MEDIUM));

    final UniformRandomProvider rg = RngUtils.create(seed.getSeed());
    final int repeats = 10;
    final double radius = 50;
    final Object[] points = new Object[repeats];
    for (int i = 0; i < repeats; i++) {
      points[i] = createClusters(rg, 500, 1000, 2, radius / 2);
    }

    final long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
    final long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius);

    logger.log(TestLogUtils.getTimingRecord("(High Density) Centroid-linkage", t1,
        "PairwiseWithoutNeighbours", t2));
    Assertions.assertTrue(t1 <= t2);
  }

  @SeededTest
  void pairwiseIsFasterThanCentroidLinkage(RandomSeed seed) {
    Assumptions.assumeTrue(TestSettings.allow(TestComplexity.MEDIUM));

    final UniformRandomProvider rg = RngUtils.create(seed.getSeed());
    final int repeats = 20;
    final Object[] points = new Object[repeats];
    for (int i = 0; i < repeats; i++) {
      points[i] = createPoints(rg, 500, 1000);
    }
    final double radius = 50;

    final long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
    final long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE, radius);

    logger.log(TestLogUtils.getTimingRecord("Centroid-linkage", t1, "Pairwise", t2));
    Assertions.assertTrue(t2 <= t1);
  }

  @SeededTest
  void canMultithreadParticleSingleLinkage(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_SINGLE_LINKAGE);
  }

  @SpeedTag
  @SeededTest
  void multithreadedParticleSingleLinkageIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_SINGLE_LINKAGE);
  }

  @SeededTest
  void canMultithreadClosest(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE);
  }

  @SeededTest
  void canMultithreadClosestParticle(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestParticleIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE);
  }

  @SeededTest
  void canMultithreadClosestDistancePriority(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE_DISTANCE_PRIORITY);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestDistancePriorityIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE_DISTANCE_PRIORITY);
  }

  @SeededTest
  void canMultithreadClosestTimePriority(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE_TIME_PRIORITY);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestTimePriorityIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.CENTROID_LINKAGE_TIME_PRIORITY);
  }

  @SeededTest
  void canMultithreadClosestParticleDistancePriority(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_DISTANCE_PRIORITY);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestParticleDistancePriorityIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_DISTANCE_PRIORITY);
  }

  @SeededTest
  void canMultithreadClosestParticleTimePriority(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_TIME_PRIORITY);
  }

  @SpeedTag
  @SeededTest
  void multithreadedClosestParticleTimePriorityIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_TIME_PRIORITY);
  }

  @SeededTest
  void canMultithreadPairwiseWithoutNeighbours(RandomSeed seed) {
    runMultithreadingAlgorithmTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS);
  }

  @SpeedTag
  @SeededTest
  void multithreadedPairwiseWithoutNeighboursIsFaster(RandomSeed seed) {
    runMultithreadingSpeedTest(RngUtils.create(seed.getSeed()),
        ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS);
  }

  private static void runMultithreadingAlgorithmTest(UniformRandomProvider rg,
      ClusteringAlgorithm algorithm) {
    final double radius = 50;
    final int time = 10;
    final ArrayList<ClusterPoint> points = createClusters(rg, 500, 1000, 2, radius / 2, time);
    final ClusteringEngine engine = new ClusteringEngine(0, algorithm);
    final List<Cluster> exp = engine.findClusters(points, radius, time);
    engine.setThreadCount(8);
    final List<Cluster> obs = engine.findClusters(points, radius, time);
    compareClusters(exp, obs);
  }

  private static void runMultithreadingSpeedTest(UniformRandomProvider rg,
      ClusteringAlgorithm algorithm) {
    Assumptions.assumeTrue(TestSettings.allow(TestComplexity.MEDIUM));
    final int cores = Runtime.getRuntime().availableProcessors();
    final int testCores = 4;
    Assumptions.assumeTrue(cores >= testCores,
        () -> String.format("Multi-threading test requires %d cores", testCores));

    final int repeats = 5;
    final double radius = 50;
    final int time = 10;
    final Object[] points = new Object[repeats];
    for (int i = 0; i < repeats; i++) {
      points[i] = createClusters(rg, 1000, 1000, 2, radius / 2, time);
    }

    final long t1 = runSpeedTest(points, algorithm, radius, time, 1);
    final long t2 = runSpeedTest(points, algorithm, radius, time, testCores);

    logger.log(TestLogUtils.getTimingRecord(algorithm.toString() + " Single", t1,
        "Multi-threaded 4-cores", t2));
    // Assertions.assertTrue(t2 <= t1);
  }

  private static long runSpeedTest(Object[] points, ClusteringAlgorithm algorithm, double radius) {
    return runSpeedTest(points, algorithm, radius, 0, 1);
  }

  @SuppressWarnings("unchecked")
  private static long runSpeedTest(Object[] points, ClusteringAlgorithm algorithm, double radius,
      int time, int threadCount) {
    final ClusteringEngine engine = new ClusteringEngine(threadCount, algorithm);

    // Initialise
    engine.findClusters((ArrayList<ClusterPoint>) points[0], radius, time);

    final long start = System.nanoTime();
    for (int i = 0; i < points.length; i++) {
      engine.findClusters((ArrayList<ClusterPoint>) points[i], radius, time);
    }
    return System.nanoTime() - start;
  }

  private void testClusting(UniformRandomProvider rg, ClusteringAlgorithm algorithm, double radius,
      int n, int size) {
    final ClusteringEngine engine = new ClusteringEngine();
    engine.setClusteringAlgorithm(algorithm);
    final ArrayList<ClusterPoint> points = createPoints(rg, n, size);

    // Report density of the clustering we are testing. Size/radius are in nm
    // TestLog.debug(logger,"Testing n=%d, Size=%d, Density=%s um^-2, Radius=%s nm", n, size,
    // MathUtils.rounded(n * 1e6 / (size * size)), MathUtils.rounded(radius));

    final List<Cluster> exp = findClusters(points, radius);
    final List<Cluster> obs = engine.findClusters(points, radius);
    compareClusters(exp, obs);
  }

  private static void compareClusters(List<Cluster> exp, List<Cluster> obs) throws AssertionError {
    Collections.sort(exp, ClusterComparator.getInstance());
    Collections.sort(obs, ClusterComparator.getInstance());

    try {
      Assertions.assertEquals(exp.size(), obs.size(), "# clusters is different");
      for (int i = 0; i < exp.size(); i++) {
        assertEqual(i, exp.get(i), obs.get(i));
      }
    } catch (final AssertionError ex) {
      print("Expected", exp);
      print("Observed", obs);
      throw ex;
    }
  }

  private static void print(String name, List<Cluster> clusters) {
    logger.info(FunctionUtils.getSupplier(name + " : size=%d", clusters.size()));
    for (int i = 0; i < clusters.size(); i++) {
      final Cluster c = clusters.get(i);
      logger.info(FunctionUtils.getSupplier("[%d] : head=%d, n=%d, cx=%g, cy=%g", i,
          c.getHeadClusterPoint().getId(), c.getSize(), c.getX(), c.getY()));
    }
  }

  private static void assertEqual(int index, Cluster cluster, Cluster cluster2) {
    Assertions.assertEquals(cluster.getSize(), cluster2.getSize(),
        () -> String.format("Cluster %d: Size is different", index));
    Assertions.assertEquals(cluster.getX(), cluster2.getX(), 1e-4,
        () -> String.format("Cluster %d: X is different", index));
    Assertions.assertEquals(cluster.getY(), cluster2.getY(), 1e-4,
        () -> String.format("Cluster %d: Y is different", index));
    // Q. Should we check each cluster member is the same ?
  }

  /**
   * Perform centroid-linkage clustering up to the given radius.
   *
   * @param points the points
   * @param radius the radius
   * @return The clusters
   */
  private ArrayList<Cluster> findClusters(ArrayList<ClusterPoint> points, double radius) {
    // Initialise all clusters with one molecule
    final ArrayList<Cluster> clusters = new ArrayList<>(points.size());
    for (int i = 0; i < points.size(); i++) {
      final ClusterPoint m = points.get(i);
      clusters.add(new Cluster(ClusterPoint.newClusterPoint(i, m.getX(), m.getY())));
    }

    // Iteratively find the closest pair
    while (findClosest(clusters, radius)) {
      clusters.get(ii).add(clusters.get(jj));
      clusters.remove(jj);
    }

    return clusters;
  }

  /**
   * Implement and all-vs-all search for the closest pair of clusters within the given radius. Set
   * the class level variables ii and jj to the indices of the closest pair.
   *
   * @param clusters the clusters
   * @param radius the radius
   * @return True if a pair was found
   */
  private boolean findClosest(ArrayList<Cluster> clusters, double radius) {
    double minD = radius * radius;
    ii = -1;
    for (int i = 0; i < clusters.size(); i++) {
      final Cluster c1 = clusters.get(i);
      for (int j = i + 1; j < clusters.size(); j++) {
        final double d2 = c1.distance2(clusters.get(j));
        if (d2 < minD) {
          ii = i;
          jj = j;
          minD = d2;
        }
      }
    }

    return ii > -1;
  }

  /**
   * Create points in a 2D distribution of size * size.
   *
   * @param rg the random generator
   * @param totalClusters the totalClusters
   * @param size the size
   * @return The points
   */
  private static ArrayList<ClusterPoint> createPoints(UniformRandomProvider rg, int totalClusters,
      int size) {
    final ArrayList<ClusterPoint> points = new ArrayList<>(totalClusters);
    while (totalClusters-- > 0) {
      points.add(ClusterPoint.newClusterPoint(totalClusters, rg.nextDouble() * size,
          rg.nextDouble() * size));
    }
    return points;
  }

  /**
   * Create clusters of clusterSize points in a 2D distribution of size * size. Clusters will be
   * spread in a radius*radius square.
   *
   * @param rg the random generator
   * @param totalClusters the totalClusters
   * @param size the size
   * @param clusterSize the clusterSize
   * @param radius the radius
   * @return The points
   */
  private static ArrayList<ClusterPoint> createClusters(UniformRandomProvider rg, int totalClusters,
      int size, int clusterSize, double radius) {
    return createClusters(rg, totalClusters, size, clusterSize, radius, null);
  }

  /**
   * Create clusters of clusterSize points in a 2D distribution of size * size. Clusters will be
   * spread in a radius*radius square. Points will be selected randomly from the given number of
   * frames.
   *
   * @param rg the random generator
   * @param totalClusters the totalClusters
   * @param size the size
   * @param clusterSize the clusterSize
   * @param radius the radius
   * @param maxTime the maxTime
   * @return The points
   */
  private static ArrayList<ClusterPoint> createClusters(UniformRandomProvider rg, int totalClusters,
      int size, int clusterSize, double radius, int maxTime) {
    final int[] time = new int[maxTime];
    for (int i = 0; i < maxTime; i++) {
      time[i] = i + 1;
    }
    return createClusters(rg, totalClusters, size, clusterSize, radius, time);
  }

  /**
   * Create clusters of clusterSize points in a 2D distribution of size * size. Clusters will be
   * spread in a radius*radius square. Points will be selected randomly from the given frames.
   *
   * @param rg the random generator
   * @param totalClusters the totalClusters
   * @param size the size
   * @param clusterSize the clusterSize
   * @param radius the radius
   * @param time the time
   * @return The points
   */
  private static ArrayList<ClusterPoint> createClusters(UniformRandomProvider rg, int totalClusters,
      int size, int clusterSize, double radius, int[] time) {
    final ArrayList<ClusterPoint> points = new ArrayList<>(totalClusters);
    int id = 0;
    if (time != null) {
      if (time.length < clusterSize) {
        throw new RuntimeException(
            "Input time array must be at least as large as the number of points");
      }
    }
    while (totalClusters-- > 0) {
      final double x = rg.nextDouble() * size;
      final double y = rg.nextDouble() * size;
      if (time != null) {
        RandomUtils.shuffle(time, rg);
        for (int i = clusterSize; i-- > 0;) {
          points.add(ClusterPoint.newTimeClusterPoint(id++, x + rg.nextDouble() * radius,
              y + rg.nextDouble() * radius, time[i], time[i]));
        }
      } else {
        for (int i = clusterSize; i-- > 0;) {
          points.add(ClusterPoint.newClusterPoint(id++, x + rg.nextDouble() * radius,
              y + rg.nextDouble() * radius));
        }
      }
    }
    return points;
  }
}
