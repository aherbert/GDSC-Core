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
/**
 * Copyright 2009 Rednaxela
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 *
 *    2. This notice may not be removed or altered from any source
 *    distribution.
 */

package ags.utils.dataStructures.trees.secondGenKD;

/**
 * An efficient well-optimized kd-tree
 * <p>
 * This is a basic copy of the KdTree class but limited to 2 dimensions.
 *
 * @author Alex Herbert
 */
abstract class KdTreeNode2D<T>
{
	// Static variables
	static final int bucketSize = 24;

	// All types
	final KdTreeNode2D<T> parent;

	// Leaf only
	double[][] locations;
	Object[] data;
	int locationCount;

	// Stem only
	KdTreeNode2D<T> left, right;
	int splitDimension;
	double splitValue;

	// Bounds
	double[] minLimit, maxLimit;
	boolean singularity;

	// Temporary
	Status status;

	/**
	 * Construct a RTree with 2 dimensions
	 */
	KdTreeNode2D()
	{
		// Init as leaf
		this.locations = new double[bucketSize][];
		this.data = new Object[bucketSize];
		this.locationCount = 0;
		this.singularity = true;

		// Init as root
		this.parent = null;
	}

	/**
	 * Constructor for child nodes. Internal use only.
	 */
	KdTreeNode2D(KdTreeNode2D<T> parent)
	{
		// Init as leaf
		this.locations = new double[Math.max(bucketSize, parent.locationCount)][];
		this.data = new Object[locations.length];
		this.locationCount = 0;
		this.singularity = true;

		// Init as non-root
		this.parent = parent;
	}

	/**
	 * Extends the bounds of this node do include a new location
	 */
	final void extendBounds(double[] location)
	{
		if (minLimit == null)
		{
			minLimit = new double[] { location[0], location[1] };
			maxLimit = new double[] { location[0], location[1] };
			return;
		}

		for (int i = 2; i-- > 0;)
			if (Double.isNaN(location[i]))
			{
				minLimit[i] = Double.NaN;
				maxLimit[i] = Double.NaN;
				singularity = false;
			}
			else if (minLimit[i] > location[i])
			{
				minLimit[i] = location[i];
				singularity = false;
			}
			else if (maxLimit[i] < location[i])
			{
				maxLimit[i] = location[i];
				singularity = false;
			}
	}

	/**
	 * Find the widest axis of the bounds of this node
	 */
	final int findWidestAxis()
	{
		double width = (maxLimit[0] - minLimit[0]);
		if (Double.isNaN(width))
			width = 0;

		double nwidth = (maxLimit[1] - minLimit[1]);
		if (Double.isNaN(nwidth))
			nwidth = 0;
		if (nwidth > width)
			return 1;

		return 0;
	}

	// Override in subclasses
	protected abstract double pointDist(double[] p1, double[] p2);

	protected abstract double pointRegionDist(double[] point, double[] min, double[] max);
}
