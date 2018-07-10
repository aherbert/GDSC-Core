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
package ags.utils.dataStructures;

/**
 * The Interface MaxHeap.
 *
 * @param <T>
 *            the generic type
 */
public interface MaxHeap<T>
{
	/**
	 * Get the size.
	 *
	 * @return the size
	 */
	public int size();

	/**
	 * Offer.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void offer(double key, T value);

	/**
	 * Replace max.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void replaceMax(double key, T value);

	/**
	 * Removes the max.
	 */
	public void removeMax();

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	public T getMax();

	/**
	 * Gets the max key.
	 *
	 * @return the max key
	 */
	public double getMaxKey();
}
