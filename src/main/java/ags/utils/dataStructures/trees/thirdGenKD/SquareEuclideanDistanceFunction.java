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
 *
 */
public class SquareEuclideanDistanceFunction implements DistanceFunction
{

	@Override
	public double distance(double[] p1, double[] p2)
	{
		double d = 0;

		for (int i = 0; i < p1.length; i++)
		{
			final double diff = (p1[i] - p2[i]);
			d += diff * diff;
		}

		return d;
	}

	@Override
	public double distanceToRect(double[] point, double[] min, double[] max)
	{
		double d = 0;

		for (int i = 0; i < point.length; i++)
		{
			double diff = 0;
			if (point[i] > max[i])
				diff = (point[i] - max[i]);
			else if (point[i] < min[i])
				diff = (point[i] - min[i]);
			d += diff * diff;
		}

		return d;
	}
}
