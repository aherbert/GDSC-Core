package gdsc.core.clustering.optics;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.special.Erf;

import ags.utils.dataStructures.trees.secondGenKD.FloatIntKdTree2D;
import ags.utils.dataStructures.trees.secondGenKD.IntNeighbourStore;
import ags.utils.dataStructures.trees.secondGenKD.Status;
import gdsc.core.utils.TurboList;

/*----------------------------------------------------------------------------- 
 * GDSC ImageJ Software
 * 
 * Copyright (C) 2017 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * LoOP: Local Outlier Probabilities
 * <p>
 * Distance/density based algorithm similar to Local Outlier Factor (LOF) to detect outliers, but with
 * statistical methods to achieve better result stability and scaling results to the range [0:1].
 * <p>
 * Reference:
 * <p>
 * Hans-Peter Kriegel, Peer Kröger, Erich Schubert, Arthur Zimek:<br />
 * LoOP: Local Outlier Probabilities< br /> In Proceedings of the 18th
 * International Conference on Information and Knowledge Management (CIKM), Hong
 * Kong, China, 2009
 * </p>
 * <p>
 * This implementation is a port of the version in the ELKI framework: https://elki-project.github.io/.
 */
public class LoOP
{
	private int nThreads = -1;
	private final FloatIntKdTree2D.SqrEuclid2D tree;
	private final float[][] points;

	/**
	 * Instantiates a new LoOP class.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public LoOP(float[] x, float[] y)
	{
		points = new float[x.length][];
		tree = new FloatIntKdTree2D.SqrEuclid2D();
		for (int i = 0; i < x.length; i++)
			tree.addPoint(points[i] = new float[] { x[i], y[i] }, i);
	}

	/**
	 * Instantiates a new LoOP class.
	 *
	 * @param points
	 *            the points
	 */
	public LoOP(float[][] points)
	{
		this.points = points;
		tree = new FloatIntKdTree2D.SqrEuclid2D();
		for (int i = 0; i < points.length; i++)
			tree.addPoint(points[i], i);
	}

	/**
	 * Get the number of points.
	 *
	 * @return the number of points
	 */
	public int size()
	{
		return points.length;
	}

	private class KNNStore implements IntNeighbourStore
	{
		final int[][] neighbours;
		int i;
		int n;
		int[] list;

		// Sum-of-squared distances
		double ss;

		public KNNStore(int[][] neighbours)
		{
			this.neighbours = neighbours;
		}

		public void add(double distance, int neighbour)
		{
			if (i == neighbour)
				// Ignore self
				return;
			ss += distance;
			list[n++] = neighbour;
		}

		public void reset(int i)
		{
			this.i = i;
			ss = 0;
			n = 0;
			list = neighbours[i];
		}
	}

	private class KNNWorker implements Runnable
	{
		final int k;
		final double[] pd;
		final int from;
		final int to;
		final KNNStore store;

		KNNWorker(int[][] neighbours, int k, double[] pd, int from, int to)
		{
			this.k = k;
			this.pd = pd;
			this.from = from;
			this.to = to;
			store = new KNNStore(neighbours);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			// Note: The k-nearest neighbour search will include the actual 
			// point so increment by 1
			final int k1 = k + 1;
			final Status[] status = new Status[tree.getNumberOfNodes()];
			for (int i = from; i < to; i++)
			{
				store.reset(i);
				tree.nearestNeighbor(points[i], k1, store, status);
				pd[i] = Math.sqrt(store.ss / k);
			}
		}
	}

	private class PLOFWorker implements Runnable
	{
		final int[][] neighbours;
		final int k;
		final double[] pd;
		final double[] plofs;
		final int from;
		final int to;
		double nplof = 0;

		PLOFWorker(int[][] neighbours, int k, double[] pd, double[] plofs, int from, int to)
		{
			this.neighbours = neighbours;
			this.k = k;
			this.pd = pd;
			this.plofs = plofs;
			this.from = from;
			this.to = to;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			for (int i = from; i < to; i++)
			{
				double sum = 0;
				final int[] list = neighbours[i];
				for (int j = k; j-- > 0;)
				{
					sum += pd[list[j]];
				}
				double plof = max(pd[i] * k / sum, 1.0);
				if (Double.isNaN(plof) || Double.isInfinite(plof))
				{
					plof = 1.0;
				}
				else
				{
					nplof += (plof - 1.0) * (plof - 1.0);
				}
				plofs[i] = plof;
			}
		}
	}

	private static double max(double a, double b)
	{
		return a >= b ? a : b;
	}

	private class NormWorker implements Runnable
	{
		final double[] plofs;
		final double norm;
		final int from;
		final int to;

		NormWorker(double[] plofs, double norm, int from, int to)
		{
			this.plofs = plofs;
			this.norm = norm;
			this.from = from;
			this.to = to;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			for (int i = from; i < to; i++)
			{
				plofs[i] = Erf.erf((plofs[i] - 1.0) * norm);
			}
		}
	}

	/**
	 * Run the Local Outlier Probability computation using the given number of neighbours.
	 *
	 * @param k
	 *            the number of neighbours (excluding self)
	 * @param lambda
	 *            The number of standard deviations to consider for density computation.
	 * @return the LoOP scores
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @throws ExecutionException
	 *             if the computation threw an exception
	 */
	public double[] run(int k, double lambda) throws InterruptedException, ExecutionException
	{
		final int size = size();

		// Bounds check k
		if (k < 1)
			k = 1;
		else if (k > size)
			k = size;

		// Multi-thread
		final int nThreads = getNumberOfThreads();
		final ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		final TurboList<Future<?>> futures = new TurboList<Future<?>>(nThreads);
		final int nPerThread = (int) Math.ceil((double) size / nThreads);

		// Find neighbours for each point and 
		// compute probabilistic distances
		final int[][] neighbours = new int[size][k];
		final double[] pd = new double[size];

		for (int from = 0; from < size;)
		{
			int to = Math.min(from + nPerThread, size);
			futures.add(executor.submit(new KNNWorker(neighbours, k, pd, from, to)));
			from = to;
		}
		wait(futures);

		// Compute Probabilistic Local Outlier Factors (PLOF)
		final double[] plofs = new double[size];
		final TurboList<PLOFWorker> workers = new TurboList<PLOFWorker>(nThreads);
		for (int from = 0; from < size;)
		{
			int to = Math.min(from + nPerThread, size);
			PLOFWorker w = new PLOFWorker(neighbours, k, pd, plofs, from, to);
			workers.add(w);
			futures.add(executor.submit(w));
			from = to;
		}
		wait(futures);

		// Get the final normalisation factor
		double nplof = 0;
		for (PLOFWorker w : workers)
			nplof += w.nplof;
		nplof = lambda * Math.sqrt(nplof / size);
		if (nplof <= 0)
			nplof = 1;

		// Normalise
		final double norm = 1. / (nplof * Math.sqrt(2.));
		for (int from = 0; from < size;)
		{
			int to = Math.min(from + nPerThread, size);
			futures.add(executor.submit(new NormWorker(plofs, norm, from, to)));
			from = to;
		}
		wait(futures);

		return plofs;
	}

	private void wait(TurboList<Future<?>> futures) throws InterruptedException, ExecutionException
	{
		// Wait for all to finish
		for (int t = futures.size(); t-- > 0;)
		{
			// The future .get() method will block until completed
			futures.get(t).get();
		}
		futures.clear();
	}

	/**
	 * Gets the number of threads to use for multi-threaded algorithms (FastOPTICS).
	 * <p>
	 * Note: This is initialised to the number of processors available to the JVM.
	 *
	 * @return the number of threads
	 */
	public int getNumberOfThreads()
	{
		if (nThreads == -1)
			nThreads = Runtime.getRuntime().availableProcessors();
		return nThreads;
	}

	/**
	 * Sets the number of threads to use for multi-threaded algorithms (FastOPTICS).
	 *
	 * @param nThreads
	 *            the new number of threads
	 */
	public void setNumberOfThreads(int nThreads)
	{
		if (nThreads > 0)
			this.nThreads = nThreads;
		else
			this.nThreads = 1;
	}
}