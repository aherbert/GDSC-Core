package gdsc.core.clustering;

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
 * Define the clustering algorithm
 */
public enum ClusteringAlgorithm
{
	//@formatter:off
	/**
	 * Joins the closest pair of particles, one of which must not be in a cluster. Clusters are not joined and can
	 * only grow when particles are added.
	 */
	PARTICLE_SINGLE_LINKAGE{ public String getName() { return "Particle single-linkage"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of clusters iteratively
	 */
	CENTROID_LINKAGE{ public String getName() { return "Centroid-linkage"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of any single particle and another single or
	 * cluster. Clusters are not joined and can only grow when particles are added.
	 */
	PARTICLE_CENTROID_LINKAGE{ public String getName() { return "Particle centroid-linkage"; }},
	/**
	 * Join the current set of closest pairs in a greedy algorithm. This method computes the pairwise distances and
	 * joins the closest pairs without updating the centroid of each cluster, and the distances, after every join
	 * (centroids and distances are updated after each pass over the data). This can lead to errors over true
	 * hierarchical centroid-linkage clustering where centroid are computed after each link step. For example if A joins
	 * B and C joins D in a single step but the new centroid of AB is closer to C than D.
	 */
	PAIRWISE{ public String getName() { return "Pairwise"; }},
	/**
	 * A variant of Pairwise is to join the closest pairs only if the number of neighbours for each is
	 * 1. In the event that no pairs has only a single neighbour then only the closest pair is joined.
	 * <p>
	 * In dense images this will return the same results as the Closest algorithm but will be much slower. It may be
	 * faster for sparse density due to the greedy nature of the algorithm.
	 */
	PAIRWISE_WITHOUT_NEIGHBOURS{ public String getName() { return "Pairwise without neighbours"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of clusters iteratively. Clusters are
	 * compared using time and distance thresholds with priority on the closest time gap (within the distance
	 * threshold).
	 */
	CENTROID_LINKAGE_DISTANCE_PRIORITY{ public String getName() { return "Centroid-linkage (Distance priority)"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of clusters iteratively. Clusters are
	 * compared using time and distance thresholds with priority on the closest distance gap (within the time
	 * threshold).
	 */
	CENTROID_LINKAGE_TIME_PRIORITY{ public String getName() { return "Centroid-linkage (Time priority)"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of any single particle and another single or
	 * cluster. Clusters are not joined and can only grow when particles are added.
	 * <p>
	 * Clusters are compared using time and distance thresholds with priority on the closest time gap (within the
	 * distance threshold).
	 */
	PARTICLE_CENTROID_LINKAGE_DISTANCE_PRIORITY{ public String getName() { return "Particle centroid-linkage (Distance priority)"; }},
	/**
	 * Hierarchical centroid-linkage clustering by joining the closest pair of any single particle and another single or
	 * cluster. Clusters are not joined and can only grow when particles are added.
	 * <p>
	 * Clusters are compared using time and distance thresholds with priority on the closest distance gap (within the
	 * time threshold).
	 */
	PARTICLE_CENTROID_LINKAGE_TIME_PRIORITY{ public String getName() { return "Particle centroid-linkage (Time priority)"; }};
	//@formatter:on

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	abstract public String getName();
}