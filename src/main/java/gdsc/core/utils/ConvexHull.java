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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;

import gdsc.core.math.Geometry;

/**
 * Contains a set of paired coordinates representing the convex hull of a set of points.
 * <p>
 * Functionality of this has been taken from ij.process.FloatPolygon.
 */
public class ConvexHull
{
	/** The x coordinates. */
	public final float[] x;

	/** The y coordinates. */
	public final float[] y;

	/**
	 * Instantiates a new convex hull.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private ConvexHull(float xbase, float ybase, float[] x, float[] y)
	{
		this.x = x;
		this.y = y;
		if (xbase != 0 || ybase != 0)
		{
			for (int i = x.length; i-- > 0;)
			{
				x[i] += xbase;
				y[i] += ybase;
			}
		}
	}

	public int size()
	{
		return x.length;
	}

	/**
	 * Create a new convex hull from the given coordinates.
	 * 
	 * @throws NullPointerException
	 *             if the inputs are null
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the yCoordinates are smaller than the xCoordinates
	 */
	public static ConvexHull create(float[] xCoordinates, float[] yCoordinates)
	{
		return create(0, 0, xCoordinates, yCoordinates, xCoordinates.length);
	}

	/**
	 * Create a new convex hull from the given coordinates.
	 *
	 * @param xCoordinates
	 *            the x coordinates
	 * @param yCoordinates
	 *            the y coordinates
	 * @param n
	 *            the number of coordinates
	 * @return the convex hull
	 * @throws NullPointerException
	 *             if the inputs are null
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the yCoordinates are smaller than the xCoordinates
	 */
	public static ConvexHull create(float[] xCoordinates, float[] yCoordinates, int n)
	{
		return create(0, 0, xCoordinates, yCoordinates, n);
	}

	/** Default value for tolerance. */
	private static final double DEFAULT_TOLERANCE = 1e-10;

	/**
	 * Create a new convex hull from the given coordinates.
	 *
	 * @param xbase
	 *            the x base coordinate (origin)
	 * @param ybase
	 *            the y base coordinate (origin)
	 * @param xCoordinates
	 *            the x coordinates
	 * @param yCoordinates
	 *            the y coordinates
	 * @param n
	 *            the number of coordinates
	 * @return the convex hull
	 * @throws NullPointerException
	 *             if the inputs are null
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the yCoordinates are smaller than the xCoordinates
	 */
	public static ConvexHull create(float xbase, float ybase, float[] xCoordinates, float[] yCoordinates, int n)
	{
		// Use Apache Math to do this
		MonotoneChain chain = new MonotoneChain(false, DEFAULT_TOLERANCE);
		TurboList<Vector2D> points = new TurboList<Vector2D>(n);
		for (int i = 0; i < n; i++)
			points.add(new Vector2D(xbase + xCoordinates[i], ybase + yCoordinates[i]));
		ConvexHull2D hull = null;
		try
		{
			hull = chain.generate(points);
		}
		catch (ConvergenceException e)
		{
		}

		if (hull == null)
			return null;

		Vector2D[] v = hull.getVertices();
		if (v.length == 0)
			return null;

		int size = v.length;
		float[] xx = new float[size];
		float[] yy = new float[size];
		int n2 = 0;
		for (int i = 0; i < size; i++)
		{
			xx[n2] = (float) v[i].getX();
			yy[n2] = (float) v[i].getY();
			n2++;
		}
		return new ConvexHull(0, 0, xx, yy);
	}

	// Below is functionality taken from ij.process.FloatPolygon
	private Rectangle bounds;
	private float minX, minY, maxX, maxY;

	/**
	 * Returns 'true' if the point (x,y) is inside this polygon. This is a Java
	 * version of the remarkably small C program by W. Randolph Franklin at
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html#The%20C%20Code
	 */
	public boolean contains(float x, float y)
	{
		int npoints = size();
		float[] xpoints = this.x;
		float[] ypoints = this.y;
		boolean inside = false;
		for (int i = 0, j = npoints - 1; i < npoints; j = i++)
		{
			if (((ypoints[i] > y) != (ypoints[j] > y)) &&
					(x < (xpoints[j] - xpoints[i]) * (y - ypoints[i]) / (ypoints[j] - ypoints[i]) + xpoints[i]))
				inside = !inside;
		}
		return inside;
	}

	public Rectangle getBounds()
	{
		int npoints = size();
		if (npoints == 0)
			return new Rectangle();
		if (bounds == null)
			calculateBounds(x, y, npoints);
		return bounds.getBounds();
	}

	public Rectangle2D.Double getFloatBounds()
	{
		int npoints = size();
		float[] xpoints = this.x;
		float[] ypoints = this.y;
		if (npoints == 0)
			return new Rectangle2D.Double();
		if (bounds == null)
			calculateBounds(xpoints, ypoints, npoints);
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	void calculateBounds(float[] xpoints, float[] ypoints, int npoints)
	{
		minX = xpoints[0];
		minY = ypoints[0];
		maxX = minX;
		maxY = minY;
		for (int i = 1; i < npoints; i++)
		{
			float x = xpoints[i];
			if (maxX < x)
				maxX = x;
			else if (minX > x)
				minX = x;
			float y = ypoints[i];
			if (maxY < y)
				maxY = y;
			else if (minY > y)
				minY = y;
		}
		int iMinX = (int) Math.floor(minX);
		int iMinY = (int) Math.floor(minY);
		bounds = new Rectangle(iMinX, iMinY, (int) (maxX - iMinX + 0.5), (int) (maxY - iMinY + 0.5));
	}

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public double getLength()
	{
		int npoints = size();
		if (npoints < 2)
			return 0;
		// Start with the closing line
		double length = distance(x[0], y[0], x[npoints - 1], y[npoints - 1]);
		for (int i = 1; i < npoints; i++)
		{
			length += distance(x[i], y[i], x[i - 1], y[i - 1]);
		}
		return length;
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
	private static double distance(double x1, double y1, double x2, double y2)
	{
		// Note: This casts up to double for increased precision
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public double getArea()
	{
		return Geometry.getArea(x, y);
	}
}