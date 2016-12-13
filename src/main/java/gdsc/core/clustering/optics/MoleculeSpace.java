package gdsc.core.clustering.optics;

/*----------------------------------------------------------------------------- 
 * GDSC ImageJ Software
 * 
 * Copyright (C) 2016 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * Used in the OPTICS/DBSCAN algorithms
 */
abstract class MoleculeSpace
{
	final float generatingDistanceE;
	Molecule[] setOfObjects;
	final int size;
	// Working storage for find neighbours
	final MoleculeList neighbours;

	/**
	 * Instantiates a new molecule space.
	 *
	 * @param size
	 *            the size
	 * @param generatingDistanceE
	 *            the generating distance E
	 */
	MoleculeSpace(int size, float generatingDistanceE)
	{
		this.generatingDistanceE = generatingDistanceE;
		this.size = size;
		neighbours = new MoleculeList(size);
	}
	
	@Override
	public String toString()
	{
		return String.format("%s, e=%f", this.getClass().getSimpleName(), generatingDistanceE);
	}

	/**
	 * Generate the molecule space. Return the list of molecules that will be processed.
	 *
	 * @return the molecule list
	 */
	abstract Molecule[] generate();

	/**
	 * Reset all the molecules for fresh processing.
	 */
	void reset()
	{
		for (int i = setOfObjects.length; i-- > 0;)
			setOfObjects[i].reset();
	}

	/**
	 * Find neighbours closer than the generating distance. The neighbours are written to the working memory store.
	 * <p>
	 * If the number of points is definitely below the minimum number of points then no distances are computed (to
	 * save time).
	 * <p>
	 * The neighbours includes the actual point in the list of neighbours (where the distance would be 0).
	 *
	 * @param minPts
	 *            the min points
	 * @param object
	 *            the object
	 * @param e
	 *            the generating distance
	 */
	abstract void findNeighbours(int minPts, Molecule object, float e);

	/**
	 * Find neighbours closer than the generating distance. The neighbours are written to the working memory store.
	 * The distances are stored in the objects encountered.
	 * <p>
	 * If the number of points is definitely below the minimum number of points then no distances are computed (to
	 * save time). Objects are ranked by distance and distances for objects below the min points may not be computed (in
	 * this case they can be set to zero).
	 * <p>
	 * The neighbours includes the actual point in the list of neighbours (where the distance would be 0).
	 *
	 * @param minPts
	 *            the min points
	 * @param object
	 *            the object
	 * @param e
	 *            the generating distance
	 */
	abstract void findNeighboursAndDistances(int minPts, Molecule object, float e);
}