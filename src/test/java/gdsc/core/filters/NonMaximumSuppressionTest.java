package gdsc.core.filters;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import gdsc.core.filters.NonMaximumSuppression;
import gdsc.core.ij.Utils;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.process.FloatProcessor;

public class NonMaximumSuppressionTest
{
	private gdsc.core.utils.Random rand;

	private boolean debug = false;

	//int[] primes = new int[] { 113, 97, 53, 29, 17, 7 };
	int[] primes = new int[] { 997, 503, 251 };
	//int[] primes = new int[] { 17 };
	int[] boxSizes = new int[] { 15, 9, 5, 3, 2, 1 };
	//int[] boxSizes = new int[] { //1, 2, 3, 5, 9, 15 };

	int ITER = 5;

	//int[] boxSizes = new int[] { 1 };

	// XXX: Copy from here...
	@Test
	public void floatBlockFindAndMaxFindReturnSameResult()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					floatCompareBlockFindToMaxFind(nms, width, height, boxSize);
	}

	private void floatCompareBlockFindToMaxFind(NonMaximumSuppression nms, int width, int height, int boxSize)
			throws ArrayComparisonFailure
	{
		// Random data
		rand = new gdsc.core.utils.Random(-30051977);
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, floatCreateData(width, height), "Random");

		// Empty data
		floatCompareBlockFindToMaxFind(nms, width, height, boxSize, new float[width * height], "Empty");

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
		nms.setNeighbourCheck(boxSize < 3);
		int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			floatCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		Assert.assertArrayEquals(String.format("%s: Indices do not match: [%dx%d] @ %d", name, width, height, boxSize),
				blockIndices, maxIndices);
	}

	private void floatCompareIndices(int width, int height, float[] data, int boxSize, int[] blockIndices,
			int[] maxIndices)
	{
		System.out.printf("float [%dx%d@%d] block = %d, max = %d\n", width, height, boxSize, blockIndices.length,
				maxIndices.length);
		int i1 = 0, i2 = 0;
		boolean match = true;
		while (i1 < blockIndices.length || i2 < maxIndices.length)
		{
			int i = (i1 < blockIndices.length) ? blockIndices[i1] : Integer.MAX_VALUE;
			int j = (i2 < maxIndices.length) ? maxIndices[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				System.out.printf("float   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				System.out.printf("float   [%d,%d] : -\n", i % width, i / width);
				i1++;
				match = false;
			}
			else if (i > j)
			{
				System.out.printf("float   - : [%d,%d]\n", j % width, j / width);
				i2++;
				match = false;
			}
		}
		if (match)
			return;
		// Show image
		showImage(width, height, data, blockIndices, "block");
		showImage(width, height, data, maxIndices, "max");
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
	public void floatBlockFindNxNAndblockFind3x3ReturnSameResult()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
			{
				float[] data = floatCreateData(width, height);

				int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
				int[] block3x3Indices = nms.blockFind3x3(data, width, height);

				Arrays.sort(blockNxNIndices);
				Arrays.sort(block3x3Indices);

				if (debug)
					floatCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

				Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d]", width, height), blockNxNIndices,
						block3x3Indices);
			}
	}

	@Test
	public void floatBlockFindIsFasterThanMaxFind()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData();
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
						System.out.printf("float maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float maxFind%d : %d => blockFind %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
					blockBoxTotal < boxTotal);
		}
		System.out.printf("float maxFind %d => blockFind %d = %.2fx\n", total, blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private ArrayList<float[]> floatCreateSpeedData()
	{
		int iter = ITER;

		ArrayList<float[]> dataSet = new ArrayList<float[]>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(floatCreateData(primes[0], primes[0]));
		return dataSet;
	}

	@Test
	public void floatBlockFindNxNInternalIsFasterThanblockFindNxNForBigBorders()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData();
		ArrayList<Long> internalTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
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
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long internalTime = internalTimes.get(index++);
					total += time;
					if (boxSize >= 5)
					{
						bigTotal += time;
						bigInternalTotal += internalTime;
					}
					internalTotal += internalTime;
					boxTotal += time;
					internalBoxTotal += internalTime;
					if (debug)
						System.out.printf("float blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width,
								height, boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal,
					internalBoxTotal, (1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		System.out.printf("float blockFind %d  (border >= 5) => blockFindInternal %d = %.2fx\n", bigTotal,
				bigInternalTotal, (1.0 * bigTotal) / bigInternalTotal);
		Assert.assertTrue(String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal),
				bigInternalTotal < bigTotal);
	}

	@Test
	public void floatBlockFind3x3MethodIsFasterThanBlockFindNxN()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData();
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
					System.out.printf("float blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height,
							time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("float blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void floatBlockFind3x3WithBufferIsFasterThanBlockFind3x3()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		ArrayList<float[]> dataSet = floatCreateSpeedData();
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
					System.out.printf("float blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width,
							height, time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("float blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		// Add margin for error
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total * 1.05);
	}

	@Test
	public void floatBlockFind3x3MethodIsFasterThanMaxFind3x3()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData();
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
					System.out.printf("float maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("float maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@Test
	public void floatAllFindBlockMethodsReturnSameResultForSize1()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				floatCompareBlockMethodsForSize1(nms, width, height);
	}

	private void floatCompareBlockMethodsForSize1(NonMaximumSuppression nms, int width, int height)
			throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);

		float[] data = floatCreateData(width, height);

		int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);
		int[] blockOptimisedIndices = nms.findBlockMaximaOptimised(data, width, height, 1);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);
		Arrays.sort(blockOptimisedIndices);

		Assert.assertArrayEquals(String.format("Block vs 2x2 do not match: [%dx%d]", width, height), blockNxNIndices,
				block2x2Indices);
		Assert.assertArrayEquals(String.format("Block vs Optimised do not match: [%dx%d]", width, height),
				blockNxNIndices, blockOptimisedIndices);
	}

	// Commented out as the block optimisation does not work
	//@Test
	public void floatFindBlockMaximaOptimisedIsFasterThanFindBlockMaxima()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet = floatCreateSpeedData();
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaOptimised(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.findBlockMaximaNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet)
						nms.findBlockMaximaOptimised(data, width, height, boxSize);
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
						nms.findBlockMaximaNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf("float findBlock [%dx%d] @ %d : %d => findBlockOptimised %d = %.2fx\n", width,
								height, boxSize, time, blockTime, (1.0 * time) / blockTime);

					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//    blockTime, time), blockTime < time);
				}
			if (debug)
				System.out.printf("float findBlock%d : %d => findBlockOptimised%d = %.2fx\n", boxSize, boxTotal,
						blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
					blockBoxTotal < boxTotal);
		}
		System.out.printf("float findBlock%d => findBlockOptimised%d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private float[] floatCreateData(int width, int height)
	{
		float[] data = new float[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		rand.shuffle(data);

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

	// XXX: Copy methods here for 'int' versions
	@Test
	public void intBlockFindAndMaxFindReturnSameResult()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					intCompareBlockFindToMaxFind(nms, width, height, boxSize);
	}

	private void intCompareBlockFindToMaxFind(NonMaximumSuppression nms, int width, int height, int boxSize)
			throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);
		int[] data = intCreateData(width, height);

		int[] blockIndices = nms.blockFindNxN(data, width, height, boxSize);
		int[] maxIndices = nms.maxFind(data, width, height, boxSize);

		Arrays.sort(blockIndices);
		Arrays.sort(maxIndices);

		if (debug)
			intCompareIndices(width, height, data, boxSize, blockIndices, maxIndices);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize),
				blockIndices, maxIndices);
	}

	private void intCompareIndices(int width, int height, int[] data, int boxSize, int[] blockIndices, int[] maxIndices)
	{
		System.out.printf("int [%dx%d@%d] block = %d, max = %d\n", width, height, boxSize, blockIndices.length,
				maxIndices.length);
		int i1 = 0, i2 = 0;
		while (i1 < blockIndices.length || i2 < maxIndices.length)
		{
			int i = (i1 < blockIndices.length) ? blockIndices[i1] : Integer.MAX_VALUE;
			int j = (i2 < maxIndices.length) ? maxIndices[i2] : Integer.MAX_VALUE;

			if (i == j)
			{
				System.out.printf("int   [%d,%d] = [%d,%d]\n", i % width, i / width, j % width, j / width);
				i1++;
				i2++;
			}
			else if (i < j)
			{
				System.out.printf("int   [%d,%d] : -\n", i % width, i / width);
				i1++;
			}
			else if (i > j)
			{
				System.out.printf("int   - : [%d,%d]\n", j % width, j / width);
				i2++;
			}
		}
	}

	@Test
	public void intBlockFindNxNAndblockFind3x3ReturnSameResult()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
			{
				int[] data = intCreateData(width, height);

				int[] blockNxNIndices = nms.blockFindNxN(data, width, height, 1);
				int[] block3x3Indices = nms.blockFind3x3(data, width, height);

				Arrays.sort(blockNxNIndices);
				Arrays.sort(block3x3Indices);

				if (debug)
					intCompareIndices(width, height, data, 1, blockNxNIndices, block3x3Indices);

				Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d]", width, height), blockNxNIndices,
						block3x3Indices);
			}
	}

	@Test
	public void intBlockFindIsFasterThanMaxFind()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
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
						System.out.printf("int maxFind [%dx%d] @ %d : %d => blockFind %d = %.2fx\n", width, height,
								boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("int maxFind%d : %d => blockFind %d = %.2fx\n", boxSize, boxTotal, blockBoxTotal,
					(1.0 * boxTotal) / blockBoxTotal);
			Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
					blockBoxTotal < boxTotal);
		}
		System.out.printf("int maxFind %d => blockFind %d = %.2fx\n", total, blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private ArrayList<int[]> intCreateSpeedData()
	{
		int iter = ITER;

		ArrayList<int[]> dataSet = new ArrayList<int[]>(iter);
		for (int i = iter; i-- > 0;)
			dataSet.add(intCreateData(primes[0], primes[0]));
		return dataSet;
	}

	@Test
	public void intBlockFindNxNInternalIsFasterThanblockFindNxNForBigBorders()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<Long> internalTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
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
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long internalTime = internalTimes.get(index++);
					total += time;
					if (boxSize >= 5)
					{
						bigTotal += time;
						bigInternalTotal += internalTime;
					}
					internalTotal += internalTime;
					boxTotal += time;
					internalBoxTotal += internalTime;
					if (debug)
						System.out.printf("int blockFind[%dx%d] @ %d : %d => blockFindInternal %d = %.2fx\n", width,
								height, boxSize, time, internalTime, (1.0 * time) / internalTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("int blockFind%d : %d => blockFindInternal %d = %.2fx\n", boxSize, boxTotal,
					internalBoxTotal, (1.0 * boxTotal) / internalBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("int blockFind %d => blockFindInternal %d = %.2fx\n", total, internalTotal,
				(1.0 * total) / internalTotal);
		System.out.printf("int blockFind (border >= 5) %d => blockFindInternal %d = %.2fx\n", bigTotal,
				bigInternalTotal, (1.0 * bigTotal) / bigInternalTotal);
		Assert.assertTrue(String.format("Internal not faster: %d > %d", bigInternalTotal, bigTotal),
				bigInternalTotal < bigTotal);
	}

	@Test
	public void intBlockFind3x3MethodIsFasterThanBlockFindNxN()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
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
					System.out.printf("int blockFindNxN [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("int blockFindNxN %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intBlockFind3x3WithBufferIsFasterThanBlockFind3x3()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();
		nms.setDataBuffer(true);

		NonMaximumSuppression nms2 = new NonMaximumSuppression();
		nms2.setDataBuffer(false);

		ArrayList<int[]> dataSet = intCreateSpeedData();
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
					System.out.printf("int blockFind3x3 [%dx%d] : %d => blockFind3x3 (buffer) %d = %.2fx\n", width,
							height, time, blockTime, (1.0 * time) / blockTime);
				// This can be close so do not allow fail on single cases
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("int blockFind3x3 %d => blockFind3x3 (buffer) %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		// Add margin for error
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total * 1.05);
	}

	@Test
	public void intBlockFind3x3MethodIsFasterThanMaxFind3x3()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
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
					System.out.printf("int maxFind3x3 [%dx%d] : %d => blockFind3x3 %d = %.2fx\n", width, height, time,
							blockTime, (1.0 * time) / blockTime);
				//Assert.assertTrue(String.format("Not faster: [%dx%d] : %d > %d", width, height, blockTime, time),
				//		blockTime < time);
			}
		System.out.printf("int maxFind3x3 %d => blockFind3x3 %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	/**
	 * Test the maximum finding algorithms for the same result
	 */
	@Test
	public void intAllFindBlockMethodsReturnSameResultForSize1()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				intCompareBlockMethodsForSize1(nms, width, height);
	}

	private void intCompareBlockMethodsForSize1(NonMaximumSuppression nms, int width, int height)
			throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);

		int[] data = intCreateData(width, height);

		int[] blockNxNIndices = nms.findBlockMaximaNxN(data, width, height, 1);
		int[] block2x2Indices = nms.findBlockMaxima2x2(data, width, height);
		int[] blockOptimisedIndices = nms.findBlockMaximaOptimised(data, width, height, 1);

		Arrays.sort(blockNxNIndices);
		Arrays.sort(block2x2Indices);
		Arrays.sort(blockOptimisedIndices);

		Assert.assertArrayEquals(String.format("Block vs 2x2 do not match: [%dx%d]", width, height), blockNxNIndices,
				block2x2Indices);
		Assert.assertArrayEquals(String.format("Block vs Optimised do not match: [%dx%d]", width, height),
				blockNxNIndices, blockOptimisedIndices);
	}

	// Commented out as the block optimisation does not work
	//@Test
	public void intFindBlockMaximaOptimisedIsFasterThanFindBlockMaxima()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaOptimised(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.findBlockMaximaNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.findBlockMaximaOptimised(data, width, height, boxSize);
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
						nms.findBlockMaximaNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"int findBlockMaximaNxN [%dx%d] @ %d : %d => findBlockMaximaOptimised %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);

					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//    blockTime, time), blockTime < time);
				}
			if (debug)
				System.out.printf("int findBlockMaximaNxN%d : %d => findBlockMaximaOptimised %d = %.2fx\n", boxSize,
						boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			Assert.assertTrue(String.format("Not faster: Block %d : %d > %d", boxSize, blockBoxTotal, boxTotal),
					blockBoxTotal < boxTotal);
		}
		System.out.printf("int findBlockMaximaNxN %d => findBlockMaximaOptimised %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	private int[] intCreateData(int width, int height)
	{
		int[] data = new int[width * height];
		for (int i = data.length; i-- > 0;)
			data[i] = i;

		rand.shuffle(data);

		return data;
	}

	@Test
	public void floatBlockFindAndIntBlockFindReturnSameResult()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					compareFloatBlockFindToIntBlockFind(nms, width, height, boxSize);
	}

	@Test
	public void floatBlockFindInternalAndIntBlockFindInternalReturnSameResult()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					compareFloatBlockFindInternalToIntBlockFindInternal(nms, width, height, boxSize);
	}

	private void compareFloatBlockFindInternalToIntBlockFindInternal(NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);
		int[] data = intCreateData(width, height);
		float[] data2 = floatClone(data);

		int[] indices = nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
		int[] indices2 = nms.blockFindNxNInternal(data2, width, height, boxSize, boxSize);

		Arrays.sort(indices);
		Arrays.sort(indices2);

		if (debug)
			floatCompareIndices(width, height, data2, boxSize, indices, indices2);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize), indices,
				indices2);
	}

	private void compareFloatBlockFindToIntBlockFind(NonMaximumSuppression nms, int width, int height, int boxSize)
			throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);
		int[] data = intCreateData(width, height);
		float[] data2 = floatClone(data);

		int[] indices = nms.blockFindNxN(data, width, height, boxSize);
		int[] indices2 = nms.blockFindNxN(data2, width, height, boxSize);

		Arrays.sort(indices);
		Arrays.sort(indices2);

		if (debug)
			floatCompareIndices(width, height, data2, boxSize, indices, indices2);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize), indices,
				indices2);
	}

	private float[] floatClone(int[] data)
	{
		float[] data2 = new float[data.length];
		for (int i = data2.length; i-- > 0;)
			data2[i] = data[i];
		return data2;
	}

	private int[] intClone(float[] data)
	{
		int[] data2 = new int[data.length];
		for (int i = data2.length; i-- > 0;)
			data2[i] = (int) data[i];
		return data2;
	}

	@Test
	public void intBlockFindNxNInternalIsFasterThanFloatBlockFindNxNInternal()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : dataSet)
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFindNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);
		nms.blockFindNxNInternal(dataSet2.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
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
					for (float[] data : dataSet2)
						nms.blockFindNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"float blockFindNxNInternal[%dx%d] @ %d : %d => int blockFindNxNInternal %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float blockFindNxNInternal%d : %d => int blockFindNxNInternal %d = %.2fx\n", boxSize,
					boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float blockFindNxNInternal %d => int blockFindNxNInternal %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Internal not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intBlockFindNxNIsFasterThanFloatBlockFindNxN()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : dataSet)
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.blockFindNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.blockFindNxN(dataSet2.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.blockFindNxN(data, width, height, boxSize);
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
					for (float[] data : dataSet2)
						nms.blockFindNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf("float blockFindNxN[%dx%d] @ %d : %d => int blockFindNxN %d = %.2fx\n", width,
								height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float blockFindNxN%d : %d => int blockFindNxN %d = %.2fx\n", boxSize, boxTotal,
					blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format(" not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float blockFindNxN %d => int blockFindNxN %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format(" not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void floatFindBlockMaximaNxNInternalAndIntFindBlockMaximaNxNInternal()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					compareFloatFindBlockMaximaNxNInternalToIntFindBlockMaximaNxNInternal(nms, width, height, boxSize);
	}

	private void compareFloatFindBlockMaximaNxNInternalToIntFindBlockMaximaNxNInternal(NonMaximumSuppression nms,
			int width, int height, int boxSize) throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);
		int[] data = intCreateData(width, height);
		float[] data2 = floatClone(data);

		int[] indices = nms.findBlockMaximaNxNInternal(data, width, height, boxSize, boxSize);
		int[] indices2 = nms.findBlockMaximaNxNInternal(data2, width, height, boxSize, boxSize);

		Arrays.sort(indices);
		Arrays.sort(indices2);

		if (debug)
			floatCompareIndices(width, height, data2, boxSize, indices, indices2);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize), indices,
				indices2);
	}

	@Test
	public void floatFindBlockMaximaNxNAndIntFindBlockMaximaNxN()
	{
		NonMaximumSuppression nms = new NonMaximumSuppression();

		for (int width : primes)
			for (int height : primes)
				for (int boxSize : boxSizes)
					compareFloatFindBlockMaximaNxNToIntFindBlockMaximaNxN(nms, width, height, boxSize);
	}

	private void compareFloatFindBlockMaximaNxNToIntFindBlockMaximaNxN(NonMaximumSuppression nms, int width, int height,
			int boxSize) throws ArrayComparisonFailure
	{
		rand = new gdsc.core.utils.Random(-30051977);
		int[] data = intCreateData(width, height);
		float[] data2 = floatClone(data);

		int[] indices = nms.findBlockMaximaNxN(data, width, height, boxSize);
		int[] indices2 = nms.findBlockMaximaNxN(data2, width, height, boxSize);

		Arrays.sort(indices);
		Arrays.sort(indices2);

		if (debug)
			floatCompareIndices(width, height, data2, boxSize, indices, indices2);

		Assert.assertArrayEquals(String.format("Indices do not match: [%dx%d] @ %d", width, height, boxSize), indices,
				indices2);
	}

	@Test
	public void intFindBlockMaximaNxNInternalIsFasterThanFloatFindBlockMaximaNxNInternal()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : dataSet)
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaNxNInternal(dataSet.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);
		nms.findBlockMaximaNxNInternal(dataSet2.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.findBlockMaximaNxNInternal(data, width, height, boxSize, boxSize);
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
					for (float[] data : dataSet2)
						nms.findBlockMaximaNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"float findBlockMaximaNxNInternal[%dx%d] @ %d : %d => int findBlockMaximaNxNInternal %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float findBlockMaximaNxNInternal%d : %d => int findBlockMaximaNxNInternal %d = %.2fx\n",
					boxSize, boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float findBlockMaximaNxNInternal %d => int findBlockMaximaNxNInternal %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Internal not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intFindBlockMaximaNxNIsFasterThanFloatFindBlockMaximaNxN()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<int[]> dataSet = intCreateSpeedData();
		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : dataSet)
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaNxN(dataSet.get(0), primes[0], primes[0], boxSizes[0]);
		nms.findBlockMaximaNxN(dataSet2.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (int[] data : dataSet)
						nms.findBlockMaximaNxN(data, width, height, boxSize);
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
					for (float[] data : dataSet2)
						nms.findBlockMaximaNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"float findBlockMaximaNxN[%dx%d] @ %d : %d => int findBlockMaximaNxN %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float findBlockMaximaNxN%d : %d => int findBlockMaximaNxN %d = %.2fx\n", boxSize,
					boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format(" not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float findBlockMaximaNxN %d => int findBlockMaximaNxN %d = %.2fx\n", total, blockTotal,
				(1.0 * total) / blockTotal);
		Assert.assertTrue(String.format(" not faster: %d > %d", blockTotal, total), blockTotal < total);
	}

	@Test
	public void intFindBlockMaximaNxNInternalIsSlowerWithConversionThanFloatFindBlockMaximaNxNInternal()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : intCreateSpeedData())
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaNxNInternal(intClone(dataSet2.get(0)), primes[0], primes[0], boxSizes[0], boxSizes[0]);
		nms.findBlockMaximaNxNInternal(dataSet2.get(0), primes[0], primes[0], boxSizes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data : dataSet2)
						nms.findBlockMaximaNxNInternal(intClone(data), width, height, boxSize, boxSize);
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
					for (float[] data : dataSet2)
						nms.findBlockMaximaNxNInternal(data, width, height, boxSize, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"float findBlockMaximaNxNInternal[%dx%d] @ %d : %d => int findBlockMaximaNxNInternalWithConversion %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf(
					"float findBlockMaximaNxNInternal%d : %d => int findBlockMaximaNxNInternalWithConversion %d = %.2fx\n",
					boxSize, boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format("Internal not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf(
				"float findBlockMaximaNxNInternal %d => int findBlockMaximaNxNInternalWithConversion %d = %.2fx\n",
				total, blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format("Internal not faster: %d > %d", blockTotal, total), blockTotal > total);
	}

	@Test
	public void intFindBlockMaximaNxNIsSlowerWithConversionThanFloatFindBlockMaximaNxN()
	{
		rand = new gdsc.core.utils.Random(-30051977);

		NonMaximumSuppression nms = new NonMaximumSuppression();

		ArrayList<float[]> dataSet2 = new ArrayList<float[]>();
		for (int[] data : intCreateSpeedData())
			dataSet2.add(floatClone(data));

		ArrayList<Long> blockTimes = new ArrayList<Long>();

		// Initialise
		nms.findBlockMaximaNxN(intClone(dataSet2.get(0)), primes[0], primes[0], boxSizes[0]);
		nms.findBlockMaximaNxN(dataSet2.get(0), primes[0], primes[0], boxSizes[0]);

		for (int boxSize : boxSizes)
			for (int width : primes)
				for (int height : primes)
				{
					long time = System.nanoTime();
					for (float[] data2 : dataSet2)
						nms.findBlockMaximaNxN(intClone(data2), width, height, boxSize);
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
					for (float[] data : dataSet2)
						nms.findBlockMaximaNxN(data, width, height, boxSize);
					time = System.nanoTime() - time;

					long blockTime = blockTimes.get(index++);
					total += time;
					blockTotal += blockTime;
					boxTotal += time;
					blockBoxTotal += blockTime;
					if (debug)
						System.out.printf(
								"float findBlockMaximaNxN[%dx%d] @ %d : %d => int findBlockMaximaNxNWithConversion %d = %.2fx\n",
								width, height, boxSize, time, blockTime, (1.0 * time) / blockTime);
					//Assert.assertTrue(String.format("Not faster: [%dx%d] @ %d : %d > %d", width, height, boxSize,
					//		blockTime, time), blockTime < time);
				}
			//if (debug)
			System.out.printf("float findBlockMaximaNxN%d : %d => int findBlockMaximaNxNWithConversion %d = %.2fx\n",
					boxSize, boxTotal, blockBoxTotal, (1.0 * boxTotal) / blockBoxTotal);
			// This is not always faster for the 15-size block so leave commented out.
			//Assert.assertTrue(String.format(" not faster: Block %d : %d > %d", boxSize,
			//		blockBoxTotal, boxTotal), blockBoxTotal < boxTotal);
		}
		System.out.printf("float findBlockMaximaNxN %d => int findBlockMaximaNxNWithConversion %d = %.2fx\n", total,
				blockTotal, (1.0 * total) / blockTotal);
		Assert.assertTrue(String.format(" not faster: %d > %d", blockTotal, total), blockTotal > total);
	}
}
