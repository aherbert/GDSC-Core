package gdsc.core.match;

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
 * <p>
 * Stores a 2D/3D point.
 * <p>
 * Overrides equals and hashCode methods using x,y,z, coordinates for equivalence. Derived classes can optionally
 * override this.
 * 
 * @see {@link java.lang.Object#equals(java.lang.Object) }
 * @see {@link java.lang.Object#hashCode() }
 */
public class BasePoint implements Coordinate
{
	protected final float x;
	protected final float y;
	protected final float z;

	public BasePoint(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BasePoint(float x, float y)
	{
		this(x, y, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aThat)
	{
		if (this == aThat)
			return true;
		if (!(aThat instanceof BasePoint))
			return false;

		//cast to native object is now safe
		BasePoint that = (BasePoint) aThat;

		return x == that.x && y == that.y && z == that.z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// Note: floatToRawIntBits does not unify all possible NaN values
		// However since the equals() will fail for NaN values we are not
		// breaking the java contract.
		return (41 * (41 * (41 + Float.floatToRawIntBits(x)) + Float.floatToRawIntBits(y)) + Float.floatToRawIntBits(z));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getX()
	 */
	public float getX()
	{
		return x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getY()
	 */
	public float getY()
	{
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getZ()
	 */
	public float getZ()
	{
		return z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getXint()
	 */
	public int getXint()
	{
		return (int) x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getYint()
	 */
	public int getYint()
	{
		return (int) y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#getZint()
	 */
	public int getZint()
	{
		return (int) z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distance(float, float, float)
	 */
	public double distance(float x, float y, float z)
	{
		return Math.sqrt(distance2(x, y, z));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distance(float, float)
	 */
	public double distance(float x, float y)
	{
		return Math.sqrt(distance2(x, y));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distance2(float, float, float)
	 */
	public double distance2(float x, float y, float z)
	{
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distance2(float, float)
	 */
	public double distance2(float x, float y)
	{
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distanceXY(gdsc.core.match.Coordinate)
	 */
	public double distanceXY(Coordinate other)
	{
		return distance(other.getX(), other.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distanceXY2(gdsc.core.match.Coordinate)
	 */
	public double distanceXY2(Coordinate other)
	{
		return distance2(other.getX(), other.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distanceXYZ(gdsc.core.match.Coordinate)
	 */
	public double distanceXYZ(Coordinate other)
	{
		return distance(other.getX(), other.getY(), other.getZ());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.match.Coordinate#distanceXYZ2(gdsc.core.match.Coordinate)
	 */
	public double distanceXYZ2(Coordinate other)
	{
		return distance2(other.getX(), other.getY(), other.getZ());
	}

	/**
	 * Shift the point by the given deltas.
	 *
	 * @param dx
	 *            the dx
	 * @param dy
	 *            the dy
	 * @param dz
	 *            the dz
	 * @return the new base point
	 */
	public BasePoint shift(float dx, float dy, float dz)
	{
		return new BasePoint(x + dx, y + dy, z + dz);
	}
}