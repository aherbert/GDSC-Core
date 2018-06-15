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
package gdsc.core.filters;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import gdsc.core.ij.Utils;
import gdsc.core.utils.Random;
import gdsc.test.TestSettings;
import gdsc.test.TestSettings.LogLevel;
import gdsc.test.TestSettings.TestComplexity;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.process.FloatProcessor;

public class NonMaximumSuppressionTest
{
	private boolean debug = TestSettings.getLogLevel() >= LogLevel.DEBUG.getValue();

	//int[] primes = new int[] { 113, 97, 53, 29, 17, 7 };
	int[] primes = new int[] { 509, 251 };
	//int[] primes = new int[] { 17 };
	//int[] smallPrimes = new int[] { 113, 97, 53, 29, 17, 7 };
	int[] smallPrimes = new int[] { 17 };
	int[] boxSizes = new int[] { 9, 5, 3, 2, 1 };
	//int[] boxSizes = new int[] { 2, 3, 5, 9, 15 };

	int ITER = 5;

	//int[] boxSizes = new int[] { 1 };

	// XXX: Copy from here...
	@Test
	public void floatBlockFindAndMaxFindReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					floatCompareBlockFindToMaxFind(rg, nms, width, height, boxSize);
	}

	private void floatCompareBlockFindToMaxFind(RandomGenerator rg, NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreateData(rg, width, height), "Random");

		// Empty data
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, new float[width * height], "Empty");
	}

	@Test
	public void floatBlockFindReturnSameResultWithNeighbourCheck()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					floatCompareBlockFindWithNeighbourCheck(rg, nms, width, height, boxSize);
	}

	private void floatCompareBlockFindWithNeighbourCheck(RandomGenerator rg, NonMaximumSuppression nms, int width,
			int height, int boxSize) throws ArrayComparisonFailure
	{
		// Random data
		float[] data = floatCreateData(rg, width, height);
		nms.setNeighbourCheck(false);
		int[] blockIndices1 = nms.blockFindNxN(data, width, height, boxSize);
		nms.setNeighbourCheck(true);
		int[] blockIndices2 = nms.blockFindNxN(data, width, height, boxSize);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize),
				blockIndices1, blockIndices2);
	}

	@Test
	public void floatBlockFindAndMaxFindReturnSameResultOnPatternDataWithNeighbourCheck()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		for (int width : smallPrimes)
			for (int height : smallPrimes)
				for (int boxSize : boxSizes)
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
		int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			floatCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		Assert.assertArrayEquals(String.format("%s: Indices do not match: [%dx%d] @ %d", name, width, height, boxSize),
				maxIndices, blockIndices);
	}

	private void floatCompareIndices(int width, int height, float[] data, int boxSize, int[] indices1, int[] indices2)
	{
		TestSettings.info("float [%dx%d@%d] i1 = %d, i2 = %d\n", width, height, boxSize, indices1.length,
				indices2.length);
		int i1 = 0, i2 = 0;
		boolean match = true;
		while (i1 < indices1.length || i2 < indices2.length)
		{
			int i = (i1 < indices1.length) ? indices1[i1] : Integer.MAX_VALUE;
			int j = (i2 < indices2.length) ? indices2[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				TestSettings.info("float   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				TestSettings.info("float   [%d,%d] : -\n", i % width, i / width);
				i1++;
				match = false;
			}
			else if (i > j)
			{
				TestSettings.info("float   - : [%d,%d]\n", j % width, j / width);
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

	private void showImage(int width, int height, float[] data, int[] indices, String title)
	{
		ImagePlus imp = Utils.display(title, new FloatProcessor(width, height, data));
		int[] ox = new int[indices.length];
		int[] oy = new int[indices.length];
		int points = 0;
		for (int i : indices)
		{
			ox[points] = i % width;
			oy[points++] = i / width;
		}
		PointRoi roi = new PointRoi(ox, oy, points);
		imp.setRoi(roi);
		//imp.getWindow().getCanvas().setMagnification(16);
		for (int i = 7; i-- > 0;)
			imp.getWindow().getCanvas().zoomIn(0, 0);
	}

	@Test
	public void floatBlockFindNxNAndBlockFind3x3ReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				float[] data = floatCreateData(rg, width, height);

				for (boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
					int[] block3x3Indices = nms.blockFind3x3(data, width, height);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						floatCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] %b", width, height, b),
							blockNxNIndices, block3x3Indices);
				}
			}
		}
	}

	@Test
	public void floatBlockFindNxNInternalAndBlockFind3x3InternalReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				float[] data = floatCreateData(rg, width, height);

				for (boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					int[] blockNxNIndices = nms.blockFindNxNInternal(data, width, height, 1, 1);
					int[] block3x3Indices = nms.blockFind3x3Internal(data, width, height, 1);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						floatCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] %b", width, height, b),
							blockNxNIndices, block3x3Indices);
				}
			}
		}
	}

	@Test
	public void floatBlockFindIsFasterThanMaxFind()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestSettings.info("float maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("float maxFind%d : %d => blockFind %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			if (boxSize > 1) // Sometimes this fails at small sizes
				Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
						blockBoxTotal < boxTotal);
		}
		TestSettings.info("float maxFind %d => blockFind %d = %.2fx\n", total, blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void floatBlockFindWithNeighbourCheckIsFasterThanMaxFind()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestSettings.info("float maxFind [%dx%d] @ %d : %d => blockFindWithCheck %d = %.2fx\n", width,
								height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("float maxFind%d : %d => blockFindWithCheck %d = %.2fx\n", boxSize, boxTotal,
					blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			if (boxSize > 1) // Sometimes this fails at small sizes
				Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
						blockBoxTotal < boxTotal);
		}
		TestSettings.info("float maxFind %d => blockFindWithCheck %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private ArrayList<float[]> floatCreateSpeedData(RandomGenerator rg)
	{
		int iter = ITER;

		ArrayList<float[]> dataSet = new ArrayList<float[]>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(floatCreateData(rg, primes[0], primes[0]));
		return dataSet;
	}

	@Test
	public void floatBlockFindNxNInternalIsFasterThanBlockFindNxNForBigBorders()
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

		TestSettings.assume(LogLevel.WARN, TestComplexity.MEDIUM);

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> internalTimes = new ArrayList<Long>();

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					// Initialise
					nms.blockFindNxNInternal(dataSet.get(0), width, height, boxSize, boxSize);
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					internalTimes.add(time);
				}

		long total = 0, internalTotal = 0;
		long bigTotal = 0, bigInternalTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, internalBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					// Initialise
					nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long internalTime = internalTimes.get(index++);
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
						TestSettings.info("float blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width,
								height, boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("float blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal,
					internalBoxTotal, (1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("float blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		TestSettings.info("float blockFind %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigTotal,
				bigInternalTotal, (1.0 * bigTotal) / bigInternalTotal);
		TestSettings.logSpeedTestResult(bigInternalTotal < bigTotal,
				String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal));
	}

	@Test
	public void floatBlockFindInternalIsFasterWithoutNeighbourCheck()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> noCheckTimes = new ArrayList<Long>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					long noCheckTime = noCheckTimes.get(index++);
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
						TestSettings.info(
								"float blockFindInternal check [%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n",
								width, height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assert.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("float blockFindInternal check%d : %d => blockFindInternal %d = %.2fx\n", boxSize,
					checkBoxTotal, noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("float blockFindInternal check %d => blockFindInternal %d = %.2fx\n", checkTotal,
				noCheckTotal, (1.0 * checkTotal) / noCheckTotal);
		TestSettings.info("float blockFindInternal check %d  (border >= 5) => blockFindInternal %d = %.2fx\n",
				bigCheckTotal, bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
		Assert.assertTrue(String.format("Without neighbour check not faster: %d > %d", bigNoCheckTotal, bigCheckTotal),
				bigNoCheckTotal < bigCheckTotal);
	}

	@Test
	public void floatBlockFindIsFasterWithoutNeighbourCheck()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> noCheckTimes = new ArrayList<Long>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long noCheckTime = noCheckTimes.get(index++);
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
						TestSettings.info("float blockFind check [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width,
								height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assert.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("float blockFind check%d : %d => blockFind %d = %.2fx\n", boxSize, checkBoxTotal,
					noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("float blockFind check %d => blockFind %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestSettings.info("float blockFind check %d  (border >= 5) => blockFind %d = %.2fx\n", bigCheckTotal,
				bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
		Assert.assertTrue(String.format("Without neighbour check not faster: %d > %d", bigNoCheckTotal, bigCheckTotal),
				bigNoCheckTotal < bigCheckTotal);
	}

	@Test
	public void floatBlockFind3x3MethodIsFasterThanBlockFindNxN()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], 1);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				blockTimes.add(System.nanoTime() - time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms.blockFindNxN(data, width, height, 1);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("float blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height,
							time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.info("float blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void floatBlockFind3x3WithBufferIsFasterThanBlockFind3x3()
	{
		TestSettings.assume(LogLevel.WARN, TestComplexity.MEDIUM);

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms2.blockFind3x3(dataSet.get(0), primes[0], primes[0]);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms2.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("float blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width,
							height, time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.logSpeedTestResult(blockTotal < total,
				"float blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n", total, blockTotal,
				(double) total / blockTotal);
	}

	@Test
	public void floatBlockFind3x3MethodIsFasterThanMaxFind3x3()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], 1);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (float[] data : dataSet)
					nms.maxFind(data, width, height, 1);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("float maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.info("float maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@Test
	public void floatAllFindBlockMethodsReturnSameResultForSize1()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		for (int width : primes)
			for (int height : primes)
				floatCompareBlockMethodsForSize1(rg, nms, width, height);
	}

	private void floatCompareBlockMethodsForSize1(RandomGenerator rg, NonMaximumSuppression nms, int width, int height)
			throws ArrayComparisonFailure
	{
		float[] data = floatCreateData(rg, width, height);

		int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);

		Assert.assertArrayEquals(String.format("Block vs 2x2 do not match: [%dx%d]", width, height), blockNxNIndices,
				block2x2Indices);
	}

	private float[] floatCreateData(RandomGenerator rg, int width, int height)
	{
		float[] data = new float[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		Random.shuffle(data, rg);

		return data;
	}

	private float[] floatCreatePatternData(int width, int height, float a, float b, float c, float d)
	{
		float[] row1 = new float[width + 2];
		float[] row2 = new float[width + 2];
		for (int x = 0; x < width; x += 2)
		{
			row1[x] = a;
			row1[x + 1] = b;
			row2[x] = c;
			row2[x + 1] = d;
		}

		float[] data = new float[width * height];
		for (int y = 0; y < height; y++)
		{
			float[] row = (y % 2 == 0) ? row1 : row2;
			System.arraycopy(row, 0, data, y * width, width);
		}

		return data;
	}

	// XXX: Copy methods up to here for 'int' versions
	@Test
	public void intBlockFindAndMaxFindReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					intCompareBlockFindToMaxFind(rg, nms, width, height, boxSize);
	}

	private void intCompareBlockFindToMaxFind(RandomGenerator rg, NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, intCreateData(rg, width, height), "Random");

		// Empty data
		intCompareBlockFindToMaxFind(nms, width, height, boxSize, new int[width * height], "Empty");
	}

	@Test
	public void intBlockFindReturnSameResultWithNeighbourCheck()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					intCompareBlockFindWithNeighbourCheck(rg, nms, width, height, boxSize);
	}

	private void intCompareBlockFindWithNeighbourCheck(RandomGenerator rg, NonMaximumSuppression nms, int width,
			int height, int boxSize) throws ArrayComparisonFailure
	{
		// Random data
		int[] data = intCreateData(rg, width, height);
		nms.setNeighbourCheck(false);
		int[] blockIndices1 = nms.blockFindNxN(data, width, height, boxSize);
		nms.setNeighbourCheck(true);
		int[] blockIndices2 = nms.blockFindNxN(data, width, height, boxSize);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize),
				blockIndices1, blockIndices2);
	}

	@Test
	public void intBlockFindAndMaxFindReturnSameResultOnPatternDataWithNeighbourCheck()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		for (int width : smallPrimes)
			for (int height : smallPrimes)
				for (int boxSize : boxSizes)
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
		int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			intCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		Assert.assertArrayEquals(String.format("%s: Indices do not match: [%dx%d] @ %d", name, width, height, boxSize),
				maxIndices, blockIndices);
	}

	private void intCompareIndices(int width, int height, int[] data, int boxSize, int[] indices1, int[] indices2)
	{
		TestSettings.info("int [%dx%d@%d] i1 = %d, i2 = %d\n", width, height, boxSize, indices1.length,
				indices2.length);
		int i1 = 0, i2 = 0;
		boolean match = true;
		while (i1 < indices1.length || i2 < indices2.length)
		{
			int i = (i1 < indices1.length) ? indices1[i1] : Integer.MAX_VALUE;
			int j = (i2 < indices2.length) ? indices2[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				TestSettings.info("int   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				TestSettings.info("int   [%d,%d] : -\n", i % width, i / width);
				i1++;
				match = false;
			}
			else if (i > j)
			{
				TestSettings.info("int   - : [%d,%d]\n", j % width, j / width);
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

	private void showImage(int width, int height, int[] data, int[] indices, String title)
	{
		ImagePlus imp = Utils.display(title, new FloatProcessor(width, height, data));
		int[] ox = new int[indices.length];
		int[] oy = new int[indices.length];
		int points = 0;
		for (int i : indices)
		{
			ox[points] = i % width;
			oy[points++] = i / width;
		}
		PointRoi roi = new PointRoi(ox, oy, points);
		imp.setRoi(roi);
		//imp.getWindow().getCanvas().setMagnification(16);
		for (int i = 7; i-- > 0;)
			imp.getWindow().getCanvas().zoomIn(0, 0);
	}

	@Test
	public void intBlockFindNxNAndBlockFind3x3ReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				int[] data = intCreateData(rg, width, height);

				for (boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
					int[] block3x3Indices = nms.blockFind3x3(data, width, height);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						intCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] %b", width, height, b),
							blockNxNIndices, block3x3Indices);
				}
			}
		}
	}

	@Test
	public void intBlockFindNxNInternalAndBlockFind3x3InternalReturnSameResult()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
		{
			// 3x3 does not process to the edge of odd size images
			width++;

			for (int height : primes)
			{
				height++;

				int[] data = intCreateData(rg, width, height);

				for (boolean b : new boolean[] { false, true })
				{
					nms.setNeighbourCheck(b);
					int[] blockNxNIndices = nms.blockFindNxNInternal(data, width, height, 1, 1);
					int[] block3x3Indices = nms.blockFind3x3Internal(data, width, height, 1);

					Arrays.sort(blockNxNIndices);
					Arrays.sort(block3x3Indices);

					if (debug)
						intCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

					Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] %b", width, height, b),
							blockNxNIndices, block3x3Indices);
				}
			}
		}
	}

	@Test
	public void intBlockFindIsFasterThanMaxFind()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestSettings.info("int maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("int maxFind%d : %d => blockFind %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			if (boxSize > 1) // Sometimes this fails at small sizes
				Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
						blockBoxTotal < boxTotal);
		}
		TestSettings.info("int maxFind %d => blockFind %d = %.2fx\n", total, blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intBlockFindWithNeighbourCheckIsFasterThanMaxFind()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setNeighbourCheck(true);

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFind(data, width, height, boxSize);
					time = System.nanoTime() - time;
					blockTimes.add(time);
				}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, blockBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.maxFind(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						TestSettings.info("int maxFind [%dx%d] @ %d : %d => blockFindWithCheck %d = %.2fx\n", width,
								height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("int maxFind%d : %d => blockFindWithCheck %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			if (boxSize > 1) // Sometimes this fails at small sizes
				Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
						blockBoxTotal < boxTotal);
		}
		TestSettings.info("int maxFind %d => blockFindWithCheck %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private ArrayList<int[]> intCreateSpeedData(RandomGenerator rg)
	{
		int iter = ITER;

		ArrayList<int[]> dataSet = new ArrayList<int[]>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(intCreateData(rg, primes[0], primes[0]));
		return dataSet;
	}

	@Test
	public void intBlockFindNxNInternalIsFasterThanBlockFindNxNForBigBorders()
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

		TestSettings.assume(LogLevel.WARN, TestComplexity.MEDIUM);

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> internalTimes = new ArrayList<Long>();

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					// Initialise
					nms.blockFindNxNInternal(dataSet.get(0), width, height, boxSize, boxSize);
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					internalTimes.add(time);
				}

		long total = 0, internalTotal = 0;
		long bigTotal = 0, bigInternalTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long boxTotal = 0, internalBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					// Initialise
					nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long internalTime = internalTimes.get(index++);
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
						TestSettings.info("int blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width,
								height, boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("int blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal,
					internalBoxTotal, (1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("int blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		TestSettings.info("int blockFind %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigTotal,
				bigInternalTotal, (1.0 * bigTotal) / bigInternalTotal);
		TestSettings.logSpeedTestResult(bigInternalTotal < bigTotal,
				String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal));
	}

	@Test
	public void intBlockFindInternalIsFasterWithoutNeighbourCheck()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> noCheckTimes = new ArrayList<Long>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					long noCheckTime = noCheckTimes.get(index++);
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
						TestSettings.info(
								"int blockFindInternal check [%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n",
								width, height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assert.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("int blockFindInternal check%d : %d => blockFindInternal %d = %.2fx\n", boxSize,
					checkBoxTotal, noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("int blockFindInternal check %d => blockFindInternal %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestSettings.info("int blockFindInternal check %d  (border >= 5) => blockFindInternal %d = %.2fx\n",
				bigCheckTotal, bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
		Assert.assertTrue(String.format("Without neighbour check not faster: %d > %d", bigNoCheckTotal, bigCheckTotal),
				bigNoCheckTotal < bigCheckTotal);
	}

	@Test
	public void intBlockFindIsFasterWithoutNeighbourCheck()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> noCheckTimes = new ArrayList<Long>();

		// Initialise
		nms.setNeighbourCheck(false);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;
					noCheckTimes.add(time);
				}

		nms.setNeighbourCheck(true);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		long checkTotal = 0, noCheckTotal = 0;
		long bigCheckTotal = 0, bigNoCheckTotal = 0;
		int index = 0;
		for (int boxSize : boxSizes)
		{
			long checkBoxTotal = 0, noCheckBoxTotal = 0;
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long noCheckTime = noCheckTimes.get(index++);
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
						TestSettings.info("int blockFind check [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width,
								height, boxSize, time, noCheckTime, (1.0 * time) / noCheckTime);
					//Assert.assertTrue(String.format("Without neighbour check not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			TestSettings.info("int blockFind check%d : %d => blockFind %d = %.2fx\n", boxSize, checkBoxTotal,
					noCheckBoxTotal, (1.0 * checkBoxTotal) / noCheckBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Without neighbour check not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		TestSettings.info("int blockFind check %d => blockFind %d = %.2fx\n", checkTotal, noCheckTotal,
				(1.0 * checkTotal) / noCheckTotal);
		TestSettings.info("int blockFind check %d  (border >= 5) => blockFind %d = %.2fx\n", bigCheckTotal,
				bigNoCheckTotal, (1.0 * bigCheckTotal) / bigNoCheckTotal);
		Assert.assertTrue(String.format("Without neighbour check not faster: %d > %d", bigNoCheckTotal, bigCheckTotal),
				bigNoCheckTotal < bigCheckTotal);
	}

	@Test
	public void intBlockFind3x3MethodIsFasterThanBlockFindNxN()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], 1);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				blockTimes.add(System.nanoTime() - time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms.blockFindNxN(data, width, height, 1);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("int blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.info("int blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intBlockFind3x3WithBufferIsFasterThanBlockFind3x3()
	{
		TestSettings.assume(LogLevel.WARN, TestComplexity.MEDIUM);

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms2.blockFind3x3(dataSet.get(0), primes[0], primes[0]);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms2.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("int blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width,
							height, time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.logSpeedTestResult(blockTotal < total, "int blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n",
				total, blockTotal, (double) total / blockTotal);
	}

	@Test
	public void intBlockFind3x3MethodIsFasterThanMaxFind3x3()
	{
		TestSettings.assumeMediumComplexity();

		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData(rg);
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFind3x3(dataSet.get(0), primes[0], primes[0]);
		nms.maxFind(dataSet.get(0), primes[0], primes[0], 1);

		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms.blockFind3x3(data, width, height);
				time = System.nanoTime() - time;
				blockTimes.add(time);
			}

		long total = 0, blockTotal = 0;
		int index = 0;
		for (int width : primes)
			for (int height : primes)
			{
				long time = System.nanoTime();
				for (int[] data : dataSet)
					nms.maxFind(data, width, height, 1);
				time = System.nanoTime() - time;

				long blockTime = blockTimes.get(index++);
				total += time;
				blockTotal += blockTime;
				if (debug)
					TestSettings.info("int maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		TestSettings.info("int maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@Test
	public void intAllFindBlockMethodsReturnSameResultForSize1()
	{
		RandomGenerator rg = TestSettings.getRandomGenerator();

		NonMaximumSuppression nms = new NonMaximumSuppression();
		for (int width : primes)
			for (int height : primes)
				intCompareBlockMethodsForSize1(rg, nms, width, height);
	}

	private void intCompareBlockMethodsForSize1(RandomGenerator rg, NonMaximumSuppression nms, int width, int height)
			throws ArrayComparisonFailure
	{
		int[] data = intCreateData(rg, width, height);

		int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);

		Assert.assertArrayEquals(String.format("Block vs 2x2 do not match: [%dx%d]", width, height), blockNxNIndices,
				block2x2Indices);
	}

	private int[] intCreateData(RandomGenerator rg, int width, int height)
	{
		int[] data = new int[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		Random.shuffle(data, rg);

		return data;
	}

	private int[] intCreatePatternData(int width, int height, int a, int b, int c, int d)
	{
		int[] row1 = new int[width + 2];
		int[] row2 = new int[width + 2];
		for (int x = 0; x < width; x += 2)
		{
			row1[x] = a;
			row1[x + 1] = b;
			row2[x] = c;
			row2[x + 1] = d;
		}

		int[] data = new int[width * height];
		for (int y = 0; y < height; y++)
		{
			int[] row = (y % 2 == 0) ? row1 : row2;
			System.arraycopy(row, 0, data, y * width, width);
		}

		return data;
	}
}
