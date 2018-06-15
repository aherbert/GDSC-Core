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
package gdsc.core.clustering;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

import gdsc.core.utils.Random;
import gdsc.test.TestSettings;
import gdsc.test.TestSettings.LogLevel;
import gdsc.test.TestSettings.TestComplexity;

public class ClusteringEngineTest
{
	// Store the closest pair of clusters
	int ii, jj;

	@Test
	public void canClusterClusterPointsAtDifferentDensitiesUsingClosest()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		for (double radius : new double[] { 5, 10, 20 })
		{
			for (int size : new int[] { 2000, 1000, 500, 400, 300, 200, 100 })
			{
				testClusting(rg, ClusteringAlgorithm.CENTROID_LINKAGE, radius, 100, size);
			}
		}
	}

	@Test
	public void canClusterClusterPointsAtDifferentDensitiesUsingPairwiseWithoutNeighbours()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		for (double radius : new double[] { 5, 10, 20 })
		{
			for (int size : new int[] { 2000, 1000, 500, 400, 300, 200, 100 })
			{
				testClusting(rg, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius, 100, size);
			}
		}
	}

	@Test
	public void pairwiseWithoutNeighboursIsFasterAtLowDensities()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();
		TestSettings.assume(LogLevel.WARN, TestComplexity.LOW);
		int repeats = 10;
		double radius = 50;
		Object[] points = new Object[repeats];
		for (int i = 0; i < repeats; i++)
			points[i] = createClusters(rg, 20, 1000, 2, radius / 2);

		long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
		long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius);

		LogLevel level = (t2 < t1) ? LogLevel.WARN : LogLevel.INFO;
		TestSettings.log(level, "SpeedTest (Low Density) Closest %d, PairwiseWithoutNeighbours %d = %fx faster\n", t1,
				t2, (double) t1 / t2);
	}

	@Test
	public void pairwiseWithoutNeighboursIsSlowerAtHighDensities()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();
		int repeats = 10;
		double radius = 50;
		Object[] points = new Object[repeats];
		for (int i = 0; i < repeats; i++)
			points[i] = createClusters(rg, 500, 1000, 2, radius / 2);

		long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
		long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS, radius);

		TestSettings.info("SpeedTest (High Density) Closest %d, PairwiseWithoutNeighbours %d = %fx faster\n", t1, t2,
				(double) t1 / t2);
		Assert.assertTrue(t1 < t2);
	}

	@Test
	public void pairwiseIsFaster()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();
		int repeats = 20;
		Object[] points = new Object[repeats];
		for (int i = 0; i < repeats; i++)
			points[i] = createPoints(rg, 500, 1000);
		double radius = 50;

		long t1 = runSpeedTest(points, ClusteringAlgorithm.CENTROID_LINKAGE, radius);
		long t2 = runSpeedTest(points, ClusteringAlgorithm.PAIRWISE, radius);

		TestSettings.info("SpeedTest Closest %d, Pairwise %d = %fx faster\n", t1, t2, (double) t1 / t2);
		Assert.assertTrue(t2 < t1);
	}

	@Test
	public void canMultithreadParticleSingleLinkage()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.PARTICLE_SINGLE_LINKAGE);
	}

	@Test
	public void multithreadedParticleSingleLinkageIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.PARTICLE_SINGLE_LINKAGE);
	}

	@Test
	public void canMultithreadClosest()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.CENTROID_LINKAGE);
	}

	@Test
	public void multithreadedClosestIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.CENTROID_LINKAGE);
	}

	@Test
	public void canMultithreadClosestParticle()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE);
	}

	@Test
	public void multithreadedClosestParticleIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE);
	}

	@Test
	public void canMultithreadClosestDistancePriority()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.CENTROID_LINKAGE_DISTANCE_PRIORITY);
	}

	@Test
	public void multithreadedClosestDistancePriorityIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.CENTROID_LINKAGE_DISTANCE_PRIORITY);
	}

	@Test
	public void canMultithreadClosestTimePriority()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.CENTROID_LINKAGE_TIME_PRIORITY);
	}

	@Test
	public void multithreadedClosestTimePriorityIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.CENTROID_LINKAGE_TIME_PRIORITY);
	}

	@Test
	public void canMultithreadClosestParticleDistancePriority()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_DISTANCE_PRIORITY);
	}

	@Test
	public void multithreadedClosestParticleDistancePriorityIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_DISTANCE_PRIORITY);
	}

	@Test
	public void canMultithreadClosestParticleTimePriority()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_TIME_PRIORITY);
	}

	@Test
	public void multithreadedClosestParticleTimePriorityIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PARTICLE_CENTROID_LINKAGE_TIME_PRIORITY);
	}

	@Test
	public void canMultithreadPairwiseWithoutNeighbours()
	{
		runMultithreadingAlgorithmTest(TestSettings.getRandomGenerator(),
				ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS);
	}

	@Test
	public void multithreadedPairwiseWithoutNeighboursIsFaster()
	{
		runMultithreadingSpeedTest(TestSettings.getRandomGenerator(), ClusteringAlgorithm.PAIRWISE_WITHOUT_NEIGHBOURS);
	}

	private void runMultithreadingAlgorithmTest(RandomGenerator rg, ClusteringAlgorithm algorithm)
	{
		double radius = 50;
		int time = 10;
		ArrayList<ClusterPoint> points = createClusters(rg, 500, 1000, 2, radius / 2, time);
		ClusteringEngine engine = new ClusteringEngine(0, algorithm);
		ArrayList<Cluster> exp = engine.findClusters(points, radius, time);
		engine.setThreadCount(8);
		ArrayList<Cluster> obs = engine.findClusters(points, radius, time);
		compareClusters(exp, obs);
	}

	private void runMultithreadingSpeedTest(RandomGenerator rg, ClusteringAlgorithm algorithm)
	{
		TestSettings.assumeMediumComplexity();

		int repeats = 5;
		double radius = 50;
		int time = 10;
		Object[] points = new Object[repeats];
		for (int i = 0; i < repeats; i++)
			points[i] = createClusters(rg, 1000, 1000, 2, radius / 2, time);

		long t1 = runSpeedTest(points, algorithm, radius, time, 1);
		long t2 = runSpeedTest(points, algorithm, radius, time, 8);

		TestSettings.info("Threading SpeedTest %s : Single %d, Multi-threaded %d = %fx faster\n", algorithm.toString(),
				t1, t2, (double) t1 / t2);
		Assert.assertTrue(t2 < t1);
	}

	private long runSpeedTest(Object[] points, ClusteringAlgorithm algorithm, double radius)
	{
		return runSpeedTest(points, algorithm, radius, 0, 1);
	}

	@SuppressWarnings("unchecked")
	private long runSpeedTest(Object[] points, ClusteringAlgorithm algorithm, double radius, int time, int threadCount)
	{
		ClusteringEngine engine = new ClusteringEngine(threadCount, algorithm);

		// Initialise
		engine.findClusters((ArrayList<ClusterPoint>) points[0], radius, time);

		long start = System.nanoTime();
		for (int i = 0; i < points.length; i++)
			engine.findClusters((ArrayList<ClusterPoint>) points[i], radius, time);
		return System.nanoTime() - start;
	}

	private void testClusting(RandomGenerator rg, ClusteringAlgorithm algorithm, double radius, int n, int size)
	{
		ClusteringEngine engine = new ClusteringEngine();
		engine.setClusteringAlgorithm(algorithm);
		ArrayList<ClusterPoint> points = createPoints(rg, n, size);

		// Report density of the clustering we are testing. Size/radius are in nm
		//TestSettings.debug("Testing n=%d, Size=%d, Density=%s um^-2, Radius=%s nm\n", n, size,
		//		Utils.rounded(n * 1e6 / (size * size)), Utils.rounded(radius));

		ArrayList<Cluster> exp = findClusters(points, radius);
		ArrayList<Cluster> obs = engine.findClusters(points, radius);
		compareClusters(exp, obs);
	}

	private void compareClusters(ArrayList<Cluster> exp, ArrayList<Cluster> obs) throws AssertionError
	{
		Collections.sort(exp);
		Collections.sort(obs);

		try
		{
			Assert.assertEquals("# clusters is different", exp.size(), obs.size());
			for (int i = 0; i < exp.size(); i++)
			{
				assertEqual(i, exp.get(i), obs.get(i));
			}
		}
		catch (AssertionError e)
		{
			print("Expected", exp);
			print("Observed", obs);
			throw e;
		}
	}

	private void print(String name, ArrayList<Cluster> clusters)
	{
		TestSettings.info(name + " : size=%d\n", clusters.size());
		for (int i = 0; i < clusters.size(); i++)
		{
			Cluster c = clusters.get(i);
			TestSettings.info("[%d] : head=%d, n=%d, cx=%g, cy=%g\n", i, c.head.id, c.n, c.x, c.y);
		}
	}

	private void assertEqual(int i, Cluster cluster, Cluster cluster2)
	{
		Assert.assertEquals(i + " cluster: Size is different", cluster.n, cluster2.n);
		Assert.assertEquals(i + " cluster: X is different", cluster.x, cluster2.x, 1e-4);
		Assert.assertEquals(i + " cluster: Y is different", cluster.y, cluster2.y, 1e-4);
		// Q. Should we check each cluster member is the same ?
	}

	/**
	 * Perform centroid-linkage clustering up to the given radius
	 * 
	 * @param points
	 * @param radius
	 * @return The clusters
	 */
	private ArrayList<Cluster> findClusters(ArrayList<ClusterPoint> points, double radius)
	{
		// Initialise all clusters with one molecule
		ArrayList<Cluster> clusters = new ArrayList<Cluster>(points.size());
		for (int i = 0; i < points.size(); i++)
		{
			final ClusterPoint m = points.get(i);
			clusters.add(new Cluster(ClusterPoint.newClusterPoint(i, m.x, m.y)));
		}

		// Iteratively find the closest pair
		while (findClosest(clusters, radius))
		{
			clusters.get(ii).add(clusters.get(jj));
			clusters.remove(jj);
		}

		return clusters;
	}

	/**
	 * Implement and all-vs-all search for the closest pair of clusters within the given radius. Set the class level
	 * variables ii and jj to the indices of the closest pair.
	 * 
	 * @param clusters
	 * @param radius
	 * @return True if a pair was found
	 */
	private boolean findClosest(ArrayList<Cluster> clusters, double radius)
	{
		double minD = radius * radius;
		ii = -1;
		for (int i = 0; i < clusters.size(); i++)
		{
			Cluster c1 = clusters.get(i);
			for (int j = i + 1; j < clusters.size(); j++)
			{
				final double d2 = c1.distance2(clusters.get(j));
				if (d2 < minD)
				{
					ii = i;
					jj = j;
					minD = d2;
				}
			}
		}

		return ii > -1;
	}

	/**
	 * Create n points in a 2D distribution of size * size.
	 *
	 * @param rg
	 *            the rg
	 * @param n
	 *            the n
	 * @param size
	 *            the size
	 * @return The points
	 */
	private ArrayList<ClusterPoint> createPoints(RandomGenerator rg, int n, int size)
	{
		ArrayList<ClusterPoint> points = new ArrayList<ClusterPoint>(n);
		while (n-- > 0)
			points.add(ClusterPoint.newClusterPoint(n, rg.nextDouble() * size, rg.nextDouble() * size));
		return points;
	}

	/**
	 * Create n clusters of m points in a 2D distribution of size * size. Clusters will be spread in a radius*radius
	 * square.
	 * 
	 * @param n
	 * @param size
	 * @param m
	 * @param radius
	 * @return The points
	 */
	private ArrayList<ClusterPoint> createClusters(RandomGenerator rg, int n, int size, int m, double radius)
	{
		return createClusters(rg, n, size, m, radius, null);
	}

	/**
	 * Create n clusters of m points in a 2D distribution of size * size. Clusters will be spread in a radius*radius
	 * square. Points will be selected randomly from the given number of frames.
	 * 
	 * @param n
	 * @param size
	 * @param m
	 * @param radius
	 * @param t
	 * @return The points
	 */
	private ArrayList<ClusterPoint> createClusters(RandomGenerator rg, int n, int size, int m, double radius, int t)
	{
		int[] time = new int[t];
		for (int i = 0; i < t; i++)
			time[i] = i + 1;
		return createClusters(rg, n, size, m, radius, time);
	}

	/**
	 * Create n clusters of m points in a 2D distribution of size * size. Clusters will be spread in a radius*radius
	 * square. Points will be selected randomly from the given frames.
	 * 
	 * @param n
	 * @param size
	 * @param m
	 * @param radius
	 * @param time
	 * @return The points
	 */
	private ArrayList<ClusterPoint> createClusters(RandomGenerator rg, int n, int size, int m, double radius,
			int[] time)
	{
		ArrayList<ClusterPoint> points = new ArrayList<ClusterPoint>(n);
		int id = 0;
		if (time != null)
		{
			if (time.length < m)
				throw new RuntimeException("Input time array must be at least as large as the number of points");
		}
		while (n-- > 0)
		{
			double x = rg.nextDouble() * size;
			double y = rg.nextDouble() * size;
			if (time != null)
			{
				Random.shuffle(time, rg);
				for (int i = m; i-- > 0;)
				{
					points.add(ClusterPoint.newTimeClusterPoint(id++, x + rg.nextDouble() * radius,
							y + rg.nextDouble() * radius, time[i], time[i]));
				}
			}
			else
			{
				for (int i = m; i-- > 0;)
				{
					points.add(ClusterPoint.newClusterPoint(id++, x + rg.nextDouble() * radius,
							y + rg.nextDouble() * radius));
				}
			}
		}
		return points;
	}
}
