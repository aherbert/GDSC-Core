package gdsc.core.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

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
 * Simple class to calculate statistics of data
 */
public class Maths
{
	public static double min(double... data)
	{
		if (data == null || data.length == 0)
			return Double.NaN;
		return minDefault(Double.POSITIVE_INFINITY, data);
	}

	public static double minDefault(double min, double... data)
	{
		if (data == null || data.length == 0)
			return min;
		for (double d : data)
			if (min > d)
				min = d;
		return min;
	}

	public static double max(double... data)
	{
		if (data == null || data.length == 0)
			return Double.NaN;
		return maxDefault(Double.NEGATIVE_INFINITY, data);
	}

	public static double maxDefault(double max, double... data)
	{
		if (data == null || data.length == 0)
			return max;
		for (double d : data)
			if (max < d)
				max = d;
		return max;
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param data
	 * @return [min, max]
	 */
	public static double[] limits(double... data)
	{
		if (data == null || data.length == 0)
			return noDoubleLimits();
		return limits(null, data);
	}

	private static double[] noDoubleLimits()
	{
		return new double[] { Double.NaN, Double.NaN };
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param limits
	 *            The current [min, max]
	 * @param data
	 * @return [min, max]
	 */
	public static double[] limits(double[] limits, double... data)
	{
		if (data == null || data.length == 0)
			return (limits == null || limits.length < 2) ? noDoubleLimits() : limits;
		if (limits == null || limits.length < 2)
			limits = new double[] { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
		double min = limits[0];
		double max = limits[1];
		for (double d : data)
		{
			if (min > d)
				min = d;
			if (max < d)
				max = d;
		}
		limits[0] = min;
		limits[1] = max;
		return limits;
	}

	public static float min(float... data)
	{
		if (data == null || data.length == 0)
			return Float.NaN;
		return minDefault(Float.POSITIVE_INFINITY, data);
	}

	public static float minDefault(float min, float... data)
	{
		if (data == null || data.length == 0)
			return min;
		for (float d : data)
			if (min > d)
				min = d;
		return min;
	}

	public static float max(float... data)
	{
		if (data == null || data.length == 0)
			return Float.NaN;
		return maxDefault(Float.NEGATIVE_INFINITY, data);
	}

	public static float maxDefault(float max, float... data)
	{
		if (data == null || data.length == 0)
			return max;
		for (float d : data)
			if (max < d)
				max = d;
		return max;
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param data
	 * @return [min, max]
	 */
	public static float[] limits(float... data)
	{
		if (data == null || data.length == 0)
			return noFloatLimits();
		return limits(null, data);
	}

	private static float[] noFloatLimits()
	{
		return new float[] { Float.NaN, Float.NaN };
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param limits
	 *            The current [min, max]
	 * @param data
	 * @return [min, max]
	 */
	public static float[] limits(float[] limits, float... data)
	{
		if (data == null || data.length == 0)
			return (limits == null || limits.length < 2) ? noFloatLimits() : limits;
		if (limits == null || limits.length < 2)
			limits = new float[] { Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY };
		float min = limits[0];
		float max = limits[1];
		for (float d : data)
		{
			if (min > d)
				min = d;
			if (max < d)
				max = d;
		}
		limits[0] = min;
		limits[1] = max;
		return limits;
	}

	public static int min(int... data)
	{
		return minDefault(Integer.MAX_VALUE, data);
	}

	public static int minDefault(int min, int... data)
	{
		if (data == null || data.length == 0)
			return min;
		for (int d : data)
			if (min > d)
				min = d;
		return min;
	}

	public static int max(int... data)
	{
		return maxDefault(Integer.MIN_VALUE, data);
	}

	public static int maxDefault(int max, int... data)
	{
		if (data == null || data.length == 0)
			return max;
		for (int d : data)
			if (max < d)
				max = d;
		return max;
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param data
	 * @return [min, max]
	 */
	public static int[] limits(int... data)
	{
		if (data == null || data.length == 0)
			return noIntegerLimits();
		return limits(null, data);
	}

	private static int[] noIntegerLimits()
	{
		return new int[] { 0, 0 };
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param limits
	 *            The current [min, max]
	 * @param data
	 * @return [min, max]
	 */
	public static int[] limits(int[] limits, int... data)
	{
		if (data == null || data.length == 0)
			return (limits == null || limits.length < 2) ? noIntegerLimits() : limits;
		if (limits == null || limits.length < 2)
			limits = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };
		int min = limits[0];
		int max = limits[1];
		for (int d : data)
		{
			if (min > d)
				min = d;
			if (max < d)
				max = d;
		}
		limits[0] = min;
		limits[1] = max;
		return limits;
	}

	public static short min(short... data)
	{
		return minDefault(Short.MAX_VALUE, data);
	}

	public static short minDefault(short min, short... data)
	{
		if (data == null || data.length == 0)
			return min;
		for (short d : data)
			if (min > d)
				min = d;
		return min;
	}

	public static short max(short... data)
	{
		return maxDefault(Short.MIN_VALUE, data);
	}

	public static short maxDefault(short max, short... data)
	{
		if (data == null || data.length == 0)
			return max;
		for (short d : data)
			if (max < d)
				max = d;
		return max;
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param data
	 * @return [min, max]
	 */
	public static short[] limits(short... data)
	{
		if (data == null || data.length == 0)
			return noShortLimits();
		return limits(null, data);
	}

	private static short[] noShortLimits()
	{
		return new short[] { 0, 0 };
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param limits
	 *            The current [min, max]
	 * @param data
	 * @return [min, max]
	 */
	public static short[] limits(short[] limits, short... data)
	{
		if (data == null || data.length == 0)
			return (limits == null || limits.length < 2) ? noShortLimits() : limits;
		if (limits == null || limits.length < 2)
			limits = new short[] { Short.MAX_VALUE, Short.MIN_VALUE };
		short min = limits[0];
		short max = limits[1];
		for (short d : data)
		{
			if (min > d)
				min = d;
			if (max < d)
				max = d;
		}
		limits[0] = min;
		limits[1] = max;
		return limits;
	}

	public static long min(long... data)
	{
		return minDefault(Integer.MAX_VALUE, data);
	}

	public static long minDefault(long min, long... data)
	{
		if (data == null || data.length == 0)
			return min;
		for (long d : data)
			if (min > d)
				min = d;
		return min;
	}

	public static long max(long... data)
	{
		return maxDefault(Integer.MIN_VALUE, data);
	}

	public static long maxDefault(long max, long... data)
	{
		if (data == null || data.length == 0)
			return max;
		for (long d : data)
			if (max < d)
				max = d;
		return max;
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param data
	 * @return [min, max]
	 */
	public static long[] limits(long... data)
	{
		if (data == null || data.length == 0)
			return noLongLimits();
		return limits(null, data);
	}

	private static long[] noLongLimits()
	{
		return new long[] { 0, 0 };
	}

	/**
	 * Compute the min and max of the data
	 * 
	 * @param limits
	 *            The current [min, max]
	 * @param data
	 * @return [min, max]
	 */
	public static long[] limits(long[] limits, long... data)
	{
		if (data == null || data.length == 0)
			return (limits == null || limits.length < 2) ? noLongLimits() : limits;
		if (limits == null || limits.length < 2)
			limits = new long[] { Long.MAX_VALUE, Long.MIN_VALUE };
		long min = limits[0];
		long max = limits[1];
		for (long d : data)
		{
			if (min > d)
				min = d;
			if (max < d)
				max = d;
		}
		limits[0] = min;
		limits[1] = max;
		return limits;
	}

	/**
	 * Calculate a cumulative histogram of the input values. The data is sorted and the first value in the returned
	 * values array will be the lowest value. NaN are ignored.
	 * 
	 * @param values
	 * @param normalise
	 *            Normalise so the total is 1
	 * @return Histogram values and cumulative total
	 */
	public static double[][] cumulativeHistogram(double[] values, boolean normalise)
	{
		if (values == null || values.length == 0)
			return new double[2][0];

		values = Arrays.copyOf(values, values.length);
		Arrays.sort(values);

		// Arrays.sort() put the NaN values higher than all others. If this is the first value then stop
		if (Double.isNaN(values[0]))
			return new double[2][0];

		double[] sum = new double[values.length];
		double lastValue = values[0];
		int position = 0, count = 0;
		for (int i = 0; i < values.length; i++)
		{
			// Arrays.sort() put the NaN values higher than all others so this should occur at the end
			if (Double.isNaN(values[i]))
				break;

			// When a new value is reached, store the cumulative total for the previous value
			if (lastValue != values[i])
			{
				values[position] = lastValue;
				sum[position] = count;
				lastValue = values[i];
				position++;
			}
			count++;
		}

		// Record the final value
		values[position] = lastValue;
		sum[position] = count;
		position++;

		// Truncate if necessary
		if (position < values.length)
		{
			values = Arrays.copyOf(values, position);
			sum = Arrays.copyOf(sum, position);
		}

		// Normalise
		if (normalise)
		{
			for (int i = 0; i < sum.length; i++)
			{
				sum[i] /= count;
			}
		}

		return new double[][] { values, sum };
	}

	/**
	 * Get the bias-corrected Akaike Information Criterion (AICc)
	 * 
	 * @param sumOfSquaredResiduals
	 *            the sum of squared residuals from the nonlinear least-squares fit
	 * @param n
	 *            The number of data points
	 * @param p
	 *            The number of fitted parameters
	 * @return The corrected Akaike Information Criterion
	 * @see <a href="https://en.wikipedia.org/wiki/Akaike_information_criterion#Comparison_with_least_squares">https://
	 *      en.wikipedia.org/wiki/Akaike_information_criterion#Comparison_with_least_squares</a>
	 */
	public static double getAkaikeInformationCriterionFromResiduals(double sumOfSquaredResiduals, int n, int p)
	{
		return getAkaikeInformationCriterion(getLogLikelihood(sumOfSquaredResiduals, n), n, p);
	}

	/**
	 * Gets the log likelihood for a least squares estimate. This assumes that the residuals are distributed according
	 * to independent identical normal distributions (with zero mean). This is approximately the case for weighted-least
	 * squares fitting of Poisson distributed data (with the weight equal to the Poisson mean of each measurement).
	 *
	 * @param sumOfSquaredResiduals
	 *            the sum of squared residuals from the nonlinear least-squares fit
	 * @param n
	 *            The number of data points
	 * @return the log likelihood
	 * @see <a href="https://en.wikipedia.org/wiki/Akaike_information_criterion#Comparison_with_least_squares">https://
	 *      en.wikipedia.org/wiki/Akaike_information_criterion#Comparison_with_least_squares</a>
	 */
	public static double getLogLikelihood(double sumOfSquaredResiduals, int n)
	{
		//final double logLikelihood = 0.5 * (-n * Math.log(2 * Math.PI) - n * Math.log(sumOfSquaredResiduals/n) - n);
		//final double logLikelihood = 0.5 * (-n * (Math.log(2 * Math.PI) + Math.log(sumOfSquaredResiduals/n) + 1));
		// Math.log(2 * Math.PI) = 1.837877066
		final double logLikelihood = 0.5 * (-n * (1.837877066 + Math.log(sumOfSquaredResiduals / n) + 1.0));
		return logLikelihood;
	}

	/**
	 * Get the bias-corrected Akaike Information Criterion (AICc)
	 * 
	 * @param logLikelihood
	 *            the log-likelihood of the fit (from Maximum likelihood estimation)
	 * @param n
	 *            The number of data points
	 * @param p
	 *            The number of fitted parameters
	 * @return The corrected Akaike Information Criterion
	 * @see <a href="http://en.wikipedia.org/wiki/Akaike_information_criterion#AICc">http://en.wikipedia.org/wiki/
	 *      Akaike_information_criterion#AICc</a>
	 */
	public static double getAkaikeInformationCriterion(double logLikelihood, int n, int p)
	{
		// Note: The true bias corrected AIC is derived from the 2nd, 3rd and 4th derivatives of the 
		// negative log-likelihood function. This is complex and so is not implemented.
		// See: 
		// http://www.math.sci.hiroshima-u.ac.jp/stat/TR/TR11/TR11-06.pdf
		// http://www.sciencedirect.com/science/article/pii/S0024379512000821#

		// This paper explains that the AIC or BIC are much better than the Adjusted coefficient of determination
		// for model selection:
		// http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2892436/

		//double aic = 2.0 * p - 2.0 * logLikelihood;

		// The Bias Corrected Akaike Information Criterion (AICc)
		// http://en.wikipedia.org/wiki/Akaike_information_criterion#AICc
		// Assumes a univariate linear model.
		//aic = aic + (2.0 * p * (p + 1)) / (n - p - 1);

		// Optimised 
		final double ic = 2.0 * (p - logLikelihood) + (2.0 * p * (p + 1)) / (n - p - 1);
		return ic;
	}

	/**
	 * Get the Bayesian Information Criterion (BIC), which gives a higher penalty on the number of parameters that the
	 * AICc
	 * 
	 * @param sumOfSquaredResiduals
	 *            the sum of squared residuals from the nonlinear least-squares fit
	 * @param n
	 *            The number of data points
	 * @param p
	 *            The number of fitted parameters
	 * @return The Bayesian Information Criterion
	 * @see <a href="http://en.wikipedia.org/wiki/Bayesian_information_criterion">http://en.wikipedia.org/wiki/
	 *      Bayesian_information_criterion</a>
	 */
	public static double getBayesianInformationCriterionFromResiduals(double sumOfSquaredResiduals, int n, int p)
	{
		return getBayesianInformationCriterion(getLogLikelihood(sumOfSquaredResiduals, n), n, p);
	}

	/**
	 * Get the Bayesian Information Criterion (BIC), which gives a higher penalty on the number of parameters that the
	 * AICc
	 * 
	 * @param logLikelihood
	 *            the log-likelihood of the fit (from Maximum likelihood estimation)
	 * @param n
	 *            The number of data points
	 * @param p
	 *            The number of fitted parameters
	 * @return The Bayesian Information Criterion
	 * @see <a href="http://en.wikipedia.org/wiki/Bayesian_information_criterion">http://en.wikipedia.org/wiki/
	 *      Bayesian_information_criterion</a>
	 */
	public static double getBayesianInformationCriterion(double logLikelihood, int n, int p)
	{
		// Bayesian Information Criterion (BIC), which gives a higher penalty on the number of parameters
		// http://en.wikipedia.org/wiki/Bayesian_information_criterion
		final double ic = p * Math.log(n) - 2.0 * logLikelihood;
		return ic;
	}

	/**
	 * @param residualSS
	 *            The sum of squared residuals from the model
	 * @param totalSS
	 *            the sum of the squared differences from the mean of the dependent variable (total sum of
	 *            squares)
	 * @param n
	 *            The number of data points
	 * @param p
	 *            The number of fitted parameters
	 * @return The adjusted coefficient of determination
	 */
	public static double getAdjustedCoefficientOfDetermination(double residualSS, double totalSS, int n, int p)
	{
		return 1 - (residualSS / totalSS) * ((n - 1) / (n - p - 1));
	}

	/**
	 * Gets the total sum of squares.
	 *
	 * @param y
	 *            the y
	 * @return the total sum of squares
	 */
	public static double getTotalSumOfSquares(double[] y)
	{
		double sx = 0, ssx = 0;
		for (int i = y.length; i-- > 0;)
		{
			sx += y[i];
			ssx += y[i] * y[i];
		}
		final double sumOfSquares = ssx - (sx * sx) / (y.length);
		return sumOfSquares;
	}

	public static double sum(double... data)
	{
		if (data == null)
			return 0;
		double sum = 0;
		for (double d : data)
			sum += d;
		return sum;
	}

	public static double sum(float... data)
	{
		if (data == null)
			return 0;
		double sum = 0;
		for (float d : data)
			sum += d;
		return sum;
	}

	public static long sum(long... data)
	{
		if (data == null)
			return 0;
		long sum = 0;
		for (long d : data)
			sum += d;
		return sum;
	}

	public static long sum(int... data)
	{
		if (data == null)
			return 0;
		long sum = 0;
		for (int d : data)
			sum += d;
		return sum;
	}

	/**
	 * Round the double to the specified significant digits
	 * 
	 * @param d
	 *            The double
	 * @param significantDigits
	 *            The number of significan digits
	 * @return A string containing the rounded double
	 */
	public static String rounded(double d, int significantDigits)
	{
		if (Double.isInfinite(d) || Double.isNaN(d))
			return "" + d;
		BigDecimal bd = new BigDecimal(d);
		bd = bd.round(new MathContext(significantDigits));
		return "" + bd.doubleValue();
	}

	/**
	 * Round the double to 4 significant digits
	 * 
	 * @param d
	 *            The double
	 * @return A string containing the rounded double
	 */
	public static String rounded(double d)
	{
		return rounded(d, 4);
	}

	/**
	 * Round the double to the specified significant digits
	 * 
	 * @param d
	 *            The double
	 * @param significantDigits
	 *            The number of significant digits
	 * @return The rounded double
	 */
	public static double round(double d, int significantDigits)
	{
		if (Double.isInfinite(d) || Double.isNaN(d))
			return d;
		BigDecimal bd = new BigDecimal(d);
		bd = bd.round(new MathContext(significantDigits));
		return bd.doubleValue();
	}

	/**
	 * Round the double to 4 significant digits
	 * 
	 * @param d
	 *            The double
	 * @return The rounded double
	 */
	public static double round(double d)
	{
		return round(d, 4);
	}

	/**
	 * Round to the nearest factor.
	 *
	 * @param value
	 *            the value
	 * @param factor
	 *            the factor
	 * @return the rounded value
	 */
	public static double round(double value, double factor)
	{
		return Math.round(value / factor) * factor;
	}

	/**
	 * Round down to the nearest factor.
	 *
	 * @param value
	 *            the value
	 * @param factor
	 *            the factor
	 * @return the rounded value
	 */
	public static double floor(double value, double factor)
	{
		return Math.floor(value / factor) * factor;
	}

	/**
	 * Round up to the nearest factor.
	 *
	 * @param value
	 *            the value
	 * @param factor
	 *            the factor
	 * @return the rounded value
	 */
	public static double ceil(double value, double factor)
	{
		return Math.ceil(value / factor) * factor;
	}

	/**
	 * Interpolate between the two points. Create a straight line and then look up the y-value for x. x may be outside
	 * the bounds.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @param x
	 *            the x
	 * @return the y value
	 */
	public static double interpolateY(double x1, double y1, double x2, double y2, double x)
	{
		// y = mx + c
		final double m = (y2 - y1) / (x2 - x1);
		// c = y - mx
		final double c = y1 - m * x1;
		return m * x + c;
	}

	/**
	 * Interpolate between the two points. Create a straight line and then look up the x-value for y. y may be outside
	 * the bounds.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @param y
	 *            the y
	 * @return the x value
	 */
	public static double interpolateX(double x1, double y1, double x2, double y2, double y)
	{
		// y = mx + c
		final double m = (y2 - y1) / (x2 - x1);
		// c = y - mx
		final double c = y1 - m * x1;
		// x = (y-c) / m
		return (y - c) / m;
	}

	/**
	 * Returns {@code true} if the argument is a finite floating-point
	 * value; returns {@code false} otherwise (for NaN and infinity
	 * arguments).
	 * <p>
	 * Taken from Double in the standard java implementation. This method was added in 1.8.
	 *
	 * @param d
	 *            the {@code double} value to be tested
	 * @return {@code true} if the argument is a finite
	 *         floating-point value, {@code false} otherwise.
	 */
	public static boolean isFinite(double d)
	{
		return Math.abs(d) <= java.lang.Double.MAX_VALUE;
	}

	/**
	 * Compute the euclidian distance between two 2D points.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return the distance
	 */
	public static double distance(double x1, double y1, double x2, double y2)
	{
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Compute the squared euclidian distance between two 2D points.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return the squared distance
	 */
	public static double distance2(double x1, double y1, double x2, double y2)
	{
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return (dx * dx + dy * dy);
	}

	/**
	 * Compute the euclidian distance between two 2D points.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return the distance
	 */
	public static float distance(float x1, float y1, float x2, float y2)
	{
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Compute the squared euclidian distance between two 2D points.
	 *
	 * @param x1
	 *            the x 1
	 * @param y1
	 *            the y 1
	 * @param x2
	 *            the x 2
	 * @param y2
	 *            the y 2
	 * @return the squared distance
	 */
	public static float distance2(float x1, float y1, float x2, float y2)
	{
		final float dx = x1 - x2;
		final float dy = y1 - y2;
		return (dx * dx + dy * dy);
	}

	/**
	 * Return value clipped to within the given bounds.
	 *
	 * @param lower
	 *            the lower limit
	 * @param upper
	 *            the upper limit
	 * @param value
	 *            the value
	 * @return the clipped value
	 */
	public static double clip(double lower, double upper, double value)
	{
		if (value < lower)
			return lower;
		if (value > upper)
			return upper;
		return value;
	}

	/**
	 * Return value clipped to within the given bounds.
	 *
	 * @param lower
	 *            the lower limit
	 * @param upper
	 *            the upper limit
	 * @param value
	 *            the value
	 * @return the clipped value
	 */
	public static float clip(float lower, float upper, float value)
	{
		if (value < lower)
			return lower;
		if (value > upper)
			return upper;
		return value;
	}

	/**
	 * Return value clipped to within the given bounds.
	 *
	 * @param lower
	 *            the lower limit
	 * @param upper
	 *            the upper limit
	 * @param value
	 *            the value
	 * @return the clipped value
	 */
	public static int clip(int lower, int upper, int value)
	{
		if (value < lower)
			return lower;
		if (value > upper)
			return upper;
		return value;
	}

	/**
	 * Get the argument to the power 2.
	 *
	 * @param d
	 *            the d
	 * @return d^2
	 */
	public static double pow2(double d)
	{
		return d * d;
	}

	/**
	 * Get the argument to the power 3.
	 *
	 * @param d
	 *            the d
	 * @return d^3
	 */
	public static double pow3(double d)
	{
		return d * d * d;
	}

	/**
	 * Get the argument to the power 4.
	 *
	 * @param d
	 *            the d
	 * @return d^4
	 */
	public static double pow4(double d)
	{
		return pow2(pow2(d));
	}
}