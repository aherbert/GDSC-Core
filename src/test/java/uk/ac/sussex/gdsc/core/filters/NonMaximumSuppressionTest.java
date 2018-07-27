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
package uk.ac.sussex.gdsc.core.filters;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.rng.UniformRandomProvider;
import org.junit.internal.ArrayComparisonFailure;
import org.junit.jupiter.api.Test;

import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.process.FloatProcessor;
import uk.ac.sussex.gdsc.core.ij.Utils;
import uk.ac.sussex.gdsc.core.utils.Random;
import uk.ac.sussex.gdsc.test.LogLevel;
import uk.ac.sussex.gdsc.test.TestLog;
import uk.ac.sussex.gdsc.test.TestSettings;
import uk.ac.sussex.gdsc.test.junit5.ExtraAssertions;
import uk.ac.sussex.gdsc.test.junit5.ExtraAssumptions;
import uk.ac.sussex.gdsc.test.junit5.RandomSeed;
import uk.ac.sussex.gdsc.test.junit5.SeededTest;
import uk.ac.sussex.gdsc.test.junit5.SpeedTag;

@SuppressWarnings({ "javadoc" })
public class NonMaximumSuppressionTest
{
	private final boolean debug = TestSettings.getLogLevel() >= LogLevel.DEBUG.getValue();

	//int[] primes = new int[] { 113, 97, 53, 29, 17, 7 };
	//int[] primes = new int[] { 509, 251 };
	int[] primes = new int[] { 113, 29 };
	//int[] primes = new int[] { 17 };
	//int[] smallPrimes = new int[] { 113, 97, 53, 29, 17, 7 };
	int[] smallPrimes = new int[] { 17 };
	int[] boxSizes = new int[] { 9, 5, 3, 2, 1 };
	//int[] boxSizes = new int[] { 2, 3, 5, 9, 15 };

	int ITER = 5;

	//int[] boxSizes = new int[] { 1 };

	// XXX: Copy from here...
	@Test
	public void floatBlockFindAndMaxFindReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());
		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (final int width : primes)
			for (final int height : primes)
				for (final int boxSize : boxSizes)
					floatCompareBlockFindToMaxFind(rg, nms, width, height, boxSize);
	}

	private void floatCompareBlockFindToMaxFind(UniformRandomProvider rg, NonMaximumSuppression nms, int width,
			int height, int boxSize) throws ArrayComparisonFailure
	{
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreateData(rg, width, height), "Random");

		// Empty data
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, new float[width * height], "Empty");
	}

	@SeededTest
	public void floatBlockFindReturnSameResultWithNeighbourCheck(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());
		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (final int width : primes)
			for (final int height : primes)
				for (final int boxSize : boxSizes)
					floatCompareBlockFindWithNeighbourCheck(rg, nms, width, height, boxSize);
	}

	private static void floatCompareBlockFindWithNeighbourCheck(UniformRandomProvider rg, NonMaximumSuppression nms,
			int width, int height, int boxSize) throws ArrayComparisonFailure
	{
		// Random data
		final float[] data = floatCreateData(rg, width, height);
		nms.setNeighbourCheck(false);
		final int[] blockIndices1 = nms.blockFindNxN(data, width, height, boxSize);
		nms.setNeighbourCheck(true);
		final int[] blockIndices2 = nms.blockFindNxN(data, width, height, boxSize);

		ExtraAssertions.assertArrayEquals(blockIndices1, blockIndices2, "Indices do not match: [%dx%d] @ %d", width,
				height, boxSize);
	}

	@Test
	public void floatBlockFindAndMaxFindReturnSameResultOnPatternDataWithNeighbourCheck()
	{
		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		for (final int width : smallPrimes)
			for (final int height : smallPrimes)
				for (final int boxSize : boxSizes)
					floatCompareBlockFindToMaxFindWithPatternData(nms, width, height, boxSize);
	}

	private void floatCompareBlockFindToMaxFindWithPatternData(NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		// This fails when N=2. Pattern data is problematic given the block find algorithm processes the pixels in a different order
		// from a linear run across the yx order data. So when the pattern produces a max pixel within the range of all
		// candidates on the top row of the block, the block algorithm will output a maxima from a subsequent row. Standard
		// processing will just move further along the row (beyond the block boundary) to find the next maxima.
		if (boxSize <= 2)
			return;

		// Pattern data
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreatePatternData(width, height, 1, 0, 0, 0),
				"Pattern1000");
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreatePatternData(width, height, 1, 0, 1, 0),
				"Pattern1010");
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreatePatternData(width, height, 1, 0, 0, 1),
				"Pattern1001");
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreatePatternData(width, height, 1, 1, 1, 0),
				"Pattern1110");
	}

	private void floatCompareBlockFindToMaxFind(NonMaximumSuppression nms, int width, int height, int boxSize,
			float[] data, String name) throws ArrayComparisonFailure
	{
		final int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		final int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			floatCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		ExtraAssertions.assertArrayEquals(maxIndices, blockIndices, "%s: Indices do not match: [%dx%d] @ %d", name,
				width, height, boxSize);
	}

	private static void floatCompareIndices(int width, int height, float[] data, int boxSize, int[] indices1,
			int[] indices2)
	{
		TestLog.info("float [%dx%d@%d] i1 = %d, i2 = %d\n", width, height, boxSize, indices1.length, indices2.length);
		int i1 = 0, i2 = 0;
		boolean match = true;
		while (i1 < indices1.length || i2 < indices2.length)
		{
			final int i = (i1 < indices1.length) ? indices1[i1] : Integer.MAX_VALUE;
			final int j = (i2 < indices2.length) ? indices2[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				TestLog.info("float   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				TestLog.info("float   [%d,%d] : -\n", i % width, i / width);
				i1++;
				match = false;
			}
			else if (i > j)
			{
				TestLog.info("float   - : [%d,%d]\n", j % width, j / width);
				i2++;
				match = false;
			}
		}
		if (match)
			return;
		// Show image
		showImage(width, height, data, indices1, "i1");
		showImage(width, height, data, indices2, "i2");
	}

	private static void showImage(int width, int height, float[] data, int[] indices, String title)
	{
		final ImagePlus imp = Utils.display(title, new FloatProcessor(width, height, data));
		final int[] ox = new int[indices.length];
		final int[] oy = new int[indices.length];
		int points = 0;
		for (final int i : indices)
		{
			ox[points] = i % width;
			oy[points++] = i / width;
		}
		final PointRoi roi = new PointRoi(ox, oy, points);
		imp.setRoi(roi);
		//imp.getWindow().getCanvas().setMagnification(16);
		for (int i = 7; i-- > 0;)
			imp.getWindow().getCanvas().zoomIn(0, 0);
	}

	@SeededTest
	public void floatBlockFindNxNAndBlockFind3x3ReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				final float[] data = floatCreateData(rg, width, height);

				for (final boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					final int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
					final int[] block3x3Indices = nms.blockFind3x3(data, width, height);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						floatCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					ExtraAssertions.assertArrayEquals(blockNxNIndices, block3x3Indices,
							"Indices do not match: [%dx%d] %b", width, height, b);
				}
			}
		}
	}

	@SeededTest
	public void floatBlockFindNxNInternalAndBlockFind3x3InternalReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				final float[] data = floatCreateData(rg, width, height);

				for (final boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					final int[] blockNxNIndices = nms.blockFindNxNInternal(data, width, height, 1, 1);
					final int[] block3x3Indices = nms.blockFind3x3Internal(data, width, height, 1);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						floatCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					ExtraAssertions.assertArrayEquals(blockNxNIndices, block3x3Indices,
							"Indices do not match: [%dx%d] %b", width, height, b);
				}
			}
		}
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFindIsFasterThanMaxFind(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestLog.info("float maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(blockBoxTotal < boxTotal, "float maxFind%d : %d => blockFind %d = %.2fx\n",
					boxSize, boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			//if (boxSize > 1) // Sometimes this fails at small sizes
			//	Assertions.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
			//			blockBoxTotal < boxTotal);
		}
		TestLog.logSpeedTestResult(blockTotal < total, "float maxFind %d => blockFind %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFindWithNeighbourCheckIsFasterThanMaxFind(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestLog.info("float maxFind [%dx%d] @ %d : %d => blockFindWithCheck %d = %.2fx\n", width,
								height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(blockBoxTotal < boxTotal,
					"float maxFind%d : %d => blockFindWithCheck %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			//if (boxSize > 1) // Sometimes this fails at small sizes
			//	Assertions.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
			//			blockBoxTotal < boxTotal);
		}
		TestLog.logSpeedTestResult(blockTotal < total, "float maxFind %d => blockFindWithCheck %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	private ArrayList<float[]> floatCreateSpeedData(UniformRandomProvider rg)
	{
		final int iter = ITER;

		final ArrayList<float[]> dataSet = new ArrayList<>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(floatCreateData(rg, primes[0], primes[0]));
		return dataSet;
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFindNxNInternalIsFasterThanBlockFindNxNForBigBorders(RandomSeed seed)
	{
		// Note: This test is currently failing. The primes used to be:
		// int[] primes = new int[] { 997, 503, 251 };
		// Now with smaller primes (to increase the speed of running these tests)
		// this test fails. The time for the JVM to optimise the internal method
		// is high.
		// If all the tests are run then the similar test
		// floatBlockFindInternalIsFasterWithoutNeighbourCheck shows much faster
		// times for the internal method.
		// This test should be changed to repeat until the times converge.

		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> internalTimes = new ArrayList<>();

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					// Initialise
					nms.blockFindNxNInternal(dataSet.get(0), width, height, boxSize, boxSize);
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					internalTimes.add(time);
				}

		long total = 0, internalTotal = 0;
		long bigTotal = 0, bigInternalTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, internalBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					// Initialise
					nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long internalTime = internalTimes.get(index++);
					total += time;
					internalTotal += internalTime;
					if (boxSize >= 5)
					{
						bigTotal += time;
						bigInternalTotal += internalTime;
					}
					boxTotal += time;
					internalBoxTotal += internalTime;
					if (debug)
						TestLog.info("float blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width,
								height, boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(boxTotal < internalBoxTotal,
					"float blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal, internalBoxTotal,
					(1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("float blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		TestLog.info("float blockFind %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigTotal, bigInternalTotal,
				(1.0 * bigTotal) / bigInternalTotal);
		TestLog.logSpeedTestResult(bigInternalTotal < bigTotal,
				String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal));
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFindInternalIsFasterWithoutNeighbourCheck(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> noCheckTimes = new ArrayList<>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					final long noCheckTime = noCheckTimes.get(index++);
					checkTotal += time;
					if (boxSize >= 5)
					{
						bigCheckTotal += time;
						bigNoCheckTotal += noCheckTime;
					}
					noCheckTotal += noCheckTime;
					checkBoxTotal += time;
					noCheckBoxTotal += noCheckTime;
					if (debug)
						TestLog.info(
								"float blockFindInternal check [%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n",
								width, height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assertions.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(checkBoxTotal < noCheckBoxTotal,
					"float blockFindInternal check%d : %d => blockFindInternal %d = %.2fx\n", boxSize, checkBoxTotal,
					noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("float blockFindInternal check %d => blockFindInternal %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestLog.logSpeedTestResult(bigNoCheckTotal < bigCheckTotal,
				"float blockFindInternal check %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigCheckTotal,
				bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFindIsFasterWithoutNeighbourCheck(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> noCheckTimes = new ArrayList<>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long noCheckTime = noCheckTimes.get(index++);
					checkTotal += time;
					if (boxSize >= 5)
					{
						bigCheckTotal += time;
						bigNoCheckTotal += noCheckTime;
					}
					noCheckTotal += noCheckTime;
					checkBoxTotal += time;
					noCheckBoxTotal += noCheckTime;
					if (debug)
						TestLog.info("float blockFind check [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assertions.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(checkBoxTotal < noCheckBoxTotal,
					"float blockFind check%d : %d => blockFind %d = %.2fx\n", boxSize, checkBoxTotal, noCheckBoxTotal,
					(1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("float blockFind check %d => blockFind %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestLog.logSpeedTestStageResult(bigNoCheckTotal < bigCheckTotal,
				"float blockFind check %d  (border >= 5) => blockFind %d = %.2fx\n", bigCheckTotal, bigNoCheckTotal,
				(1.0 * bigCheckTotal) / bigNoCheckTotal);
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFind3x3MethodIsFasterThanBlockFindNxN(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], 1);

		for (final int width : primes)
			for (final int height : primes)
			{
				final long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				blockTimes.add(System.nanoTime() - time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms.blockFindNxN(data, width, height, 1);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("float blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "float blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFind3x3WithBufferIsFasterThanBlockFind3x3(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		final NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms2.blockFind3x3(dataSet.get(0), primes[0], primes[0]);

		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms2.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("float blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width, height,
							time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "float blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n",
				total, blockTotal, (double) total / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void floatBlockFind3x3MethodIsFasterThanMaxFind3x3(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], 1);

		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final float[] data : dataSet)
					nms.maxFind(data, width, height, 1);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("float maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "float maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@SeededTest
	public void floatAllFindBlockMethodsReturnSameResultForSize1(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		for (final int width : primes)
			for (final int height : primes)
				floatCompareBlockMethodsForSize1(rg, nms, width, height);
	}

	private static void floatCompareBlockMethodsForSize1(UniformRandomProvider rg, NonMaximumSuppression nms, int width,
			int height) throws ArrayComparisonFailure
	{
		final float[] data = floatCreateData(rg, width, height);

		final int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		final int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);

		ExtraAssertions.assertArrayEquals(blockNxNIndices, block2x2Indices, "Block vs 2x2 do not match: [%dx%d]", width,
				height);
	}

	private static float[] floatCreateData(UniformRandomProvider rg, int width, int height)
	{
		final float[] data = new float[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		Random.shuffle(data, rg);

		return data;
	}

	private static float[] floatCreatePatternData(int width, int height, float a, float b, float c, float d)
	{
		final float[] row1 = new float[width + 2];
		final float[] row2 = new float[width + 2];
		for (int x = 0; x < width; x += 2)
		{
			row1[x] = a;
			row1[x + 1] = b;
			row2[x] = c;
			row2[x + 1] = d;
		}

		final float[] data = new float[width * height];
		for (int y = 0; y < height; y++)
		{
			final float[] row = (y % 2 == 0) ? row1 : row2;
			System.arraycopy(row, 0, data, y * width, width);
		}

		return data;
	}

	// XXX: Copy methods up to here for 'int' versions
	@SeededTest
	public void intBlockFindAndMaxFindReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());
		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (final int width : primes)
			for (final int height : primes)
				for (final int boxSize : boxSizes)
					intCompareBlockFindToMaxFind(rg, nms, width, height, boxSize);
	}

	private void intCompareBlockFindToMaxFind(UniformRandomProvider rg, NonMaximumSuppression nms, int width,
			int height, int boxSize) throws ArrayComparisonFailure
	{
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreateData(rg, width, height), "Random");

		// Empty data
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, new int[width * height], "Empty");
	}

	@SeededTest
	public void intBlockFindReturnSameResultWithNeighbourCheck(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());
		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (final int width : primes)
			for (final int height : primes)
				for (final int boxSize : boxSizes)
					intCompareBlockFindWithNeighbourCheck(rg, nms, width, height, boxSize);
	}

	private static void intCompareBlockFindWithNeighbourCheck(UniformRandomProvider rg, NonMaximumSuppression nms,
			int width, int height, int boxSize) throws ArrayComparisonFailure
	{
		// Random data
		final int[] data = intCreateData(rg, width, height);
		nms.setNeighbourCheck(false);
		final int[] blockIndices1 = nms.blockFindNxN(data, width, height, boxSize);
		nms.setNeighbourCheck(true);
		final int[] blockIndices2 = nms.blockFindNxN(data, width, height, boxSize);

		ExtraAssertions.assertArrayEquals(blockIndices1, blockIndices2, "Indices do not match: [%dx%d] @ %d", width,
				height, boxSize);
	}

	@Test
	public void intBlockFindAndMaxFindReturnSameResultOnPatternDataWithNeighbourCheck()
	{
		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		for (final int width : smallPrimes)
			for (final int height : smallPrimes)
				for (final int boxSize : boxSizes)
					intCompareBlockFindToMaxFindWithPatternData(nms, width, height, boxSize);
	}

	private void intCompareBlockFindToMaxFindWithPatternData(NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		// This fails when N=2. Pattern data is problematic given the block find algorithm processes the pixels in a different order
		// from a linear run across the yx order data. So when the pattern produces a max pixel within the range of all
		// candidates on the top row of the block, the block algorithm will output a maxima from a subsequent row. Standard
		// processing will just move further along the row (beyond the block boundary) to find the next maxima.
		if (boxSize <= 2)
			return;

		// Pattern data
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreatePatternData(width, height, 1, 0, 0, 0),
				"Pattern1000");
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreatePatternData(width, height, 1, 0, 1, 0),
				"Pattern1010");
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreatePatternData(width, height, 1, 0, 0, 1),
				"Pattern1001");
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreatePatternData(width, height, 1, 1, 1, 0),
				"Pattern1110");
	}

	private void intCompareBlockFindToMaxFind(NonMaximumSuppression nms, int width, int height, int boxSize, int[] data,
			String name) throws ArrayComparisonFailure
	{
		final int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		final int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			intCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		ExtraAssertions.assertArrayEquals(maxIndices, blockIndices, "%s: Indices do not match: [%dx%d] @ %d", name,
				width, height, boxSize);
	}

	private static void intCompareIndices(int width, int height, int[] data, int boxSize, int[] indices1,
			int[] indices2)
	{
		TestLog.info("int [%dx%d@%d] i1 = %d, i2 = %d\n", width, height, boxSize, indices1.length, indices2.length);
		int i1 = 0, i2 = 0;
		boolean match = true;
		while (i1 < indices1.length || i2 < indices2.length)
		{
			final int i = (i1 < indices1.length) ? indices1[i1] : Integer.MAX_VALUE;
			final int j = (i2 < indices2.length) ? indices2[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				TestLog.info("int   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				TestLog.info("int   [%d,%d] : -\n", i % width, i / width);
				i1++;
				match = false;
			}
			else if (i > j)
			{
				TestLog.info("int   - : [%d,%d]\n", j % width, j / width);
				i2++;
				match = false;
			}
		}
		if (match)
			return;
		// Show image
		showImage(width, height, data, indices1, "i1");
		showImage(width, height, data, indices2, "i2");
	}

	private static void showImage(int width, int height, int[] data, int[] indices, String title)
	{
		final ImagePlus imp = Utils.display(title, new FloatProcessor(width, height, data));
		final int[] ox = new int[indices.length];
		final int[] oy = new int[indices.length];
		int points = 0;
		for (final int i : indices)
		{
			ox[points] = i % width;
			oy[points++] = i / width;
		}
		final PointRoi roi = new PointRoi(ox, oy, points);
		imp.setRoi(roi);
		//imp.getWindow().getCanvas().setMagnification(16);
		for (int i = 7; i-- > 0;)
			imp.getWindow().getCanvas().zoomIn(0, 0);
	}

	@SeededTest
	public void intBlockFindNxNAndBlockFind3x3ReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				final int[] data = intCreateData(rg, width, height);

				for (final boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					final int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
					final int[] block3x3Indices = nms.blockFind3x3(data, width, height);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						intCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					ExtraAssertions.assertArrayEquals(blockNxNIndices, block3x3Indices,
							"Indices do not match: [%dx%d] %b", width, height, b);
				}
			}
		}
	}

	@SeededTest
	public void intBlockFindNxNInternalAndBlockFind3x3InternalReturnSameResult(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				final int[] data = intCreateData(rg, width, height);

				for (final boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					final int[] blockNxNIndices = nms.blockFindNxNInternal(data, width, height, 1, 1);
					final int[] block3x3Indices = nms.blockFind3x3Internal(data, width, height, 1);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						intCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					ExtraAssertions.assertArrayEquals(blockNxNIndices, block3x3Indices,
							"Indices do not match: [%dx%d] %b", width, height, b);
				}
			}
		}
	}

	@SpeedTag
	@SeededTest
	public void intBlockFindIsFasterThanMaxFind(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestLog.info("int maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height, boxSize,
								time, blockTime, (1.0 * time) / blockTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(blockBoxTotal < boxTotal, "int maxFind%d : %d => blockFind %d = %.2fx\n",
					boxSize, boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			//if (boxSize > 1) // Sometimes this fails at small sizes
			//	Assertions.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
			//			blockBoxTotal < boxTotal);
		}
		TestLog.logSpeedTestResult(blockTotal < total, "int maxFind %d => blockFind %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void intBlockFindWithNeighbourCheckIsFasterThanMaxFind(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestLog.info("int maxFind [%dx%d] @ %d : %d => blockFindWithCheck %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(blockBoxTotal < boxTotal,
					"int maxFind%d : %d => blockFindWithCheck %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			//if (boxSize > 1) // Sometimes this fails at small sizes
			//	Assertions.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
			//			blockBoxTotal < boxTotal);
		}
		TestLog.logSpeedTestResult(blockTotal < total, "int maxFind %d => blockFindWithCheck %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	private ArrayList<int[]> intCreateSpeedData(UniformRandomProvider rg)
	{
		final int iter = ITER;

		final ArrayList<int[]> dataSet = new ArrayList<>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(intCreateData(rg, primes[0], primes[0]));
		return dataSet;
	}

	@SpeedTag
	@SeededTest
	public void intBlockFindNxNInternalIsFasterThanBlockFindNxNForBigBorders(RandomSeed seed)
	{
		// Note: This test is currently failing. The primes used to be:
		// int[] primes = new int[] { 997, 503, 251 };
		// Now with smaller primes (to increase the speed of running these tests)
		// this test fails. The time for the JVM to optimise the internal method
		// is high.
		// If all the tests are run then the similar test
		// intBlockFindInternalIsFasterWithoutNeighbourCheck shows much faster
		// times for the internal method.
		// This test should be changed to repeat until the times converge.

		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> internalTimes = new ArrayList<>();

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					// Initialise
					nms.blockFindNxNInternal(dataSet.get(0), width, height, boxSize, boxSize);
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					internalTimes.add(time);
				}

		long total = 0, internalTotal = 0;
		long bigTotal = 0, bigInternalTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long boxTotal = 0, internalBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					// Initialise
					nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long internalTime = internalTimes.get(index++);
					total += time;
					internalTotal += internalTime;
					if (boxSize >= 5)
					{
						bigTotal += time;
						bigInternalTotal += internalTime;
					}
					boxTotal += time;
					internalBoxTotal += internalTime;
					if (debug)
						TestLog.info("int blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width, height,
								boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assertions.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(boxTotal < internalBoxTotal,
					"int blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal, internalBoxTotal,
					(1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("int blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		TestLog.info("int blockFind %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigTotal, bigInternalTotal,
				(1.0 * bigTotal) / bigInternalTotal);
		TestLog.logSpeedTestResult(bigInternalTotal < bigTotal,
				String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal));
	}

	@SpeedTag
	@SeededTest
	public void intBlockFindInternalIsFasterWithoutNeighbourCheck(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> noCheckTimes = new ArrayList<>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					final long noCheckTime = noCheckTimes.get(index++);
					checkTotal += time;
					if (boxSize >= 5)
					{
						bigCheckTotal += time;
						bigNoCheckTotal += noCheckTime;
					}
					noCheckTotal += noCheckTime;
					checkBoxTotal += time;
					noCheckBoxTotal += noCheckTime;
					if (debug)
						TestLog.info("int blockFindInternal check [%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n",
								width, height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assertions.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(checkBoxTotal < noCheckBoxTotal,
					"int blockFindInternal check%d : %d => blockFindInternal %d = %.2fx\n", boxSize, checkBoxTotal,
					noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("int blockFindInternal check %d => blockFindInternal %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestLog.logSpeedTestResult(bigNoCheckTotal < bigCheckTotal,
				"int blockFindInternal check %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigCheckTotal,
				bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
	}

	@SpeedTag
	@SeededTest
	public void intBlockFindIsFasterWithoutNeighbourCheck(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> noCheckTimes = new ArrayList<>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (final int boxSize : boxSizes)
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (final int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (final int width : primes)
				for (final int height : primes)
				{
					long time = System.nanoTime();
					for (final int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					final long noCheckTime = noCheckTimes.get(index++);
					checkTotal += time;
					if (boxSize >= 5)
					{
						bigCheckTotal += time;
						bigNoCheckTotal += noCheckTime;
					}
					noCheckTotal += noCheckTime;
					checkBoxTotal += time;
					noCheckBoxTotal += noCheckTime;
					if (debug)
						TestLog.info("int blockFind check [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assertions.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestLog.logSpeedTestStageResult(checkBoxTotal < noCheckBoxTotal,
					"int blockFind check%d : %d => blockFind %d = %.2fx\n", boxSize, checkBoxTotal, noCheckBoxTotal,
					(1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assertions.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestLog.info("int blockFind check %d => blockFind %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestLog.logSpeedTestStageResult(bigNoCheckTotal < bigCheckTotal,
				"int blockFind check %d  (border >= 5) => blockFind %d = %.2fx\n", bigCheckTotal, bigNoCheckTotal,
				(1.0 * bigCheckTotal) / bigNoCheckTotal);
	}

	@SpeedTag
	@SeededTest
	public void intBlockFind3x3MethodIsFasterThanBlockFindNxN(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], 1);

		for (final int width : primes)
			for (final int height : primes)
			{
				final long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				blockTimes.add(System.nanoTime() - time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms.blockFindNxN(data, width, height, 1);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("int blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "int blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void intBlockFind3x3WithBufferIsFasterThanBlockFind3x3(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		final NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms2.blockFind3x3(dataSet.get(0), primes[0], primes[0]);

		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms2.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("int blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width, height,
							time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "int blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n",
				total, blockTotal, (double) total / blockTotal);
	}

	@SpeedTag
	@SeededTest
	public void intBlockFind3x3MethodIsFasterThanMaxFind3x3(RandomSeed seed)
	{
		ExtraAssumptions.assumeSpeedTest();

		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();

		final ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		final ArrayList<Long> blockTimes = new ArrayList<>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], 1);

		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (final int width : primes)
			for (final int height : primes)
			{
				long time = System.nanoTime();
				for (final int[] data : dataSet)
					nms.maxFind(data, width, height, 1);
				time = System.nanoTime() - time;

				final long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestLog.info("int maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assertions.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestLog.logSpeedTestResult(blockTotal < total, "int maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@SeededTest
	public void intAllFindBlockMethodsReturnSameResultForSize1(RandomSeed seed)
	{
		final UniformRandomProvider rg = TestSettings.getRandomGenerator(seed.getSeed());

		final NonMaximumSuppression nms = new NonMaximumSuppression();
		for (final int width : primes)
			for (final int height : primes)
				intCompareBlockMethodsForSize1(rg, nms, width, height);
	}

	private static void intCompareBlockMethodsForSize1(UniformRandomProvider rg, NonMaximumSuppression nms, int width,
			int height) throws ArrayComparisonFailure
	{
		final int[] data = intCreateData(rg, width, height);

		final int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		final int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);

		ExtraAssertions.assertArrayEquals(blockNxNIndices, block2x2Indices, "Block vs 2x2 do not match: [%dx%d]", width,
				height);
	}

	private static int[] intCreateData(UniformRandomProvider rg, int width, int height)
	{
		final int[] data = new int[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		Random.shuffle(data, rg);

		return data;
	}

	private static int[] intCreatePatternData(int width, int height, int a, int b, int c, int d)
	{
		final int[] row1 = new int[width + 2];
		final int[] row2 = new int[width + 2];
		for (int x = 0; x < width; x += 2)
		{
			row1[x] = a;
			row1[x + 1] = b;
			row2[x] = c;
			row2[x + 1] = d;
		}

		final int[] data = new int[width * height];
		for (int y = 0; y < height; y++)
		{
			final int[] row = (y % 2 == 0) ? row1 : row2;
			System.arraycopy(row, 0, data, y * width, width);
		}

		return data;
	}
}
