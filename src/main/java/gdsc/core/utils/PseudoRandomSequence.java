package gdsc.core.utils;

/*----------------------------------------------------------------------------- 
 * GDSC Plugins for ImageJ
 * 
 * Copyright (C) 2017 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Contains a set of random numbers that are reused in sequence
 */
public class PseudoRandomSequence implements Cloneable
{
	protected final double[] sequence;

	private int position = 0;

	/**
	 * Instantiates a new pseudo random sequence. The input sequence is cloned.
	 *
	 * @param sequence
	 *            the sequence (must contains numbers in the interval 0 to 1)
	 * @throw {@link IllegalArgumentException} if the sequence is not positive in length and contains numbers outside
	 *        the interval 0 to 1.
	 */
	public PseudoRandomSequence(double[] sequence)
	{
		if (sequence == null || sequence.length < 1)
			throw new IllegalArgumentException("Sequence must have a positive length");
		this.sequence = sequence.clone();
	}

	/**
	 * Instantiates a new pseudo random sequence of the given size.
	 *
	 * @param size
	 *            the size
	 * @param source
	 *            the random source
	 * @param scale
	 *            the scale
	 * @throw {@link IllegalArgumentException} if the size is not positive
	 * @throw {@link NullPointerException} if the generator is null
	 */
	public PseudoRandomSequence(int size, RandomGenerator source, double scale)
	{
		if (size < 1)
			throw new IllegalArgumentException("Sequence must have a positive length");
		if (source == null)
			throw new NullPointerException("Source generator must not be null");
		sequence = new double[size];
		while (size-- > 0)
		{
			sequence[size] = source.nextDouble() * scale;
		}
	}

	/**
	 * Sets the seed for the sequence.
	 *
	 * @param seed the new seed
	 */
	public void setSeed(long seed)
	{
		position = (int) (Math.abs(seed) % sequence.length);
	}

	/**
	 * Get the next double in the sequence.
	 *
	 * @return the double
	 */
	public double nextDouble()
	{
		double d = sequence[position++];
		if (position == sequence.length)
			position = 0;
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PseudoRandomSequence clone()
	{
		try
		{
			return (PseudoRandomSequence) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// This should not happen
			return new PseudoRandomSequence(sequence);
		}
	}

	/**
	 * Gets the sequence of random numbers.
	 *
	 * @return the sequence
	 */
	public double[] getSequence()
	{
		return sequence.clone();
	}
}