/*
 * Copyright 2009 Rednaxela
 *
 * Modifications to the code have been made by Alex Herbert for a smaller memory footprint and
 * optimised 2D processing for use with image data as part of the Genome Damage and Stability Centre
 * ImageJ Core Package.
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the
 * authors be held liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial
 * applications, and to alter it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the
 * original software. If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 *
 * 2. This notice may not be removed or altered from any source distribution.
 */

package uk.ac.sussex.gdsc.core.ags.utils.data.trees.gen3;

/**
 * The Class KdTreeND.
 *
 * @param <T> the generic type
 */
public class KdTreeNd<T> extends KdTree<T> {
  /** The dimensions. */
  protected int dimensions;

  /**
   * Instantiates a new kd tree ND.
   *
   * @param dimensions the dimensions
   */
  public KdTreeNd(int dimensions) {
    this(dimensions, 24);
  }

  /**
   * Instantiates a new kd tree ND.
   *
   * @param dimensions the dimensions
   * @param bucketCapacity the bucket capacity
   */
  public KdTreeNd(int dimensions, int bucketCapacity) {
    super(bucketCapacity);
    this.dimensions = dimensions;
  }

  @Override
  public int getDimensions() {
    return dimensions;
  }

  @Override
  protected KdNode<T> newInstance() {
    return new KdTreeNd<>(dimensions, bucketCapacity);
  }
}