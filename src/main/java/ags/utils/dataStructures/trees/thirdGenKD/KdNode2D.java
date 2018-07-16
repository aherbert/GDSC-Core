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
package ags.utils.dataStructures.trees.thirdGenKD;

/**
 * A 2D-tree node
 *
 * @param <T>
 *            the generic type
 */
class KdNode2D<T> extends KdNode<T>
{
	/**
	 * Instantiates a new 2D-tree node
	 *
	 * @param bucketCapacity
	 *            the bucket capacity
	 */
	protected KdNode2D(int bucketCapacity)
	{
		super(bucketCapacity);
	}

	@Override
	public int getDimensions()
	{
		return 2;
	}

	@Override
	protected KdNode<T> newInstance()
	{
		return new KdNode2D<>(bucketCapacity);
	}
}
