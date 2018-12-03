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

package uk.ac.sussex.gdsc.core.utils;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Contains a set of random numbers that are reused in sequence.
 *
 * <p>The sequence is updated to contain numbers in the interval 0 (inclusive) to 1 (exclusive).
 * This class thus violates the contract of
 * {@link org.apache.commons.math3.random.RandomGenerator#nextDouble()}. It allows faster generation
 * of random integers and shuffling of lists.
 */
public class TurboRandomGenerator extends PseudoRandomGenerator {

  /**
   * The largest number that will be generated by {@link #nextDouble()}.
   */
  public static final double MAX_VALUE = Math.nextDown(1.0);

  /**
   * Copy constructor.
   */
  private TurboRandomGenerator(TurboRandomGenerator source) {
    super(source);
  }

  /**
   * Instantiates a new turbo random generator. The input sequence may be modified: any 1 is set to
   * 1-ulp.
   *
   * @param sequence the sequence (must contains numbers in the interval 0 to 1)
   * @throws IllegalArgumentException if the sequence is not positive in length and contains numbers
   *         outside the interval 0 to 1.
   */
  public TurboRandomGenerator(double[] sequence) {
    super(sequence);
    ensureSequenceIsBelow1();
  }

  /**
   * Instantiates a new turbo random generator. The input sequence may be modified: any 1 is set to
   * 1-ulp.
   *
   * @param sequence the sequence (must contains numbers in the interval 0 to 1)
   * @param length the length
   * @throws IllegalArgumentException if the sequence is not positive in length and contains numbers
   *         outside the interval 0 to 1.
   */
  public TurboRandomGenerator(double[] sequence, int length) {
    super(sequence, length);
    ensureSequenceIsBelow1();
  }

  /**
   * Instantiates a new pseudo random generator of the given size.
   *
   * @param size the size
   * @param source the random source
   * @throws IllegalArgumentException if the size is not positive
   * @throws NullPointerException if the generator is null
   */
  public TurboRandomGenerator(int size, RandomGenerator source) {
    super(size, source);
    ensureSequenceIsBelow1();
  }

  /**
   * Instantiates a new pseudo random generator of the given size.
   *
   * @param size the size
   * @param source the random source
   * @throws IllegalArgumentException if the size is not positive
   * @throws NullPointerException if the generator is null
   */
  public TurboRandomGenerator(int size, UniformRandomProvider source) {
    super(size, source);
    ensureSequenceIsBelow1();
  }

  /**
   * Update the sequence to ensure every value is below 1.
   */
  private void ensureSequenceIsBelow1() {
    for (int i = length; i-- > 0;) {
      if (sequence[i] >= MAX_VALUE) {
        sequence[i] = MAX_VALUE;
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>WARNING: This class violates the contract of
   * {@link org.apache.commons.math3.random.RandomGenerator#nextDouble()} by generating numbers in
   * the interval 0 (inclusive) to 1 (exclusive).
   *
   * @see uk.ac.sussex.gdsc.core.utils.PseudoRandomGenerator#nextDouble()
   */
  @Override
  public double nextDouble() {
    // This method is only for JavaDoc about the violation of the nextDouble contract.
    // Use the parent implementation.
    return super.nextDouble();
  }

  @Override
  public TurboRandomGenerator copy() {
    return new TurboRandomGenerator(this);
  }

  @Override
  public int nextInt(int n) {
    if (n <= 0) {
      throw new NotStrictlyPositiveException(n);
    }
    return nextIntFast(n);
  }

  /**
   * Returns a pseudorandom, uniformly distributed {@code int} value between 0 (inclusive) and the
   * specified value (exclusive), drawn from this random number generator's sequence.
   *
   * <p>The default implementation returns:
   *
   * <pre>
   * <code>(int) (nextDouble() * n</code>
   * </pre>
   *
   * <p>Warning: No check is made that n is positive so use with caution.
   *
   * @param n the bound on the random number to be returned. Must be positive.
   * @return a pseudorandom, uniformly distributed {@code int} value between 0 (inclusive) and n
   *         (exclusive).
   */
  @Override
  public int nextIntFast(int n) {
    return (int) (nextDouble() * n);
  }
}
