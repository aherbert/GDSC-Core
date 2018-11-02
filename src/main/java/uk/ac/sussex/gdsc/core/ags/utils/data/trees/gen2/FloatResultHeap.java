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

package uk.ac.sussex.gdsc.core.ags.utils.data.trees.gen2;

import java.util.Arrays;

/**
 * Class for tracking up to 'capacity' closest size.
 *
 * @param <T> the generic type
 */
public class FloatResultHeap<T> {
  /** The data. */
  final Object[] data;

  /** The distance. */
  final float[] distance;

  /** The capacity. */
  private final int capacity;

  /** The size. */
  int size;

  /**
   * The removed data.
   */
  private Object removedData;

  /**
   * The removed distance.
   */
  private float removedDistance;

  /**
   * Instantiates a new float result heap.
   *
   * @param capacity the capacity
   */
  public FloatResultHeap(int capacity) {
    this.data = new Object[capacity];
    this.distance = new float[capacity];
    this.capacity = capacity;
    this.size = 0;
  }

  /**
   * Adds the value.
   *
   * @param dist the dist
   * @param value the value
   */
  public void addValue(float dist, Object value) {
    // If there is still room in the heap
    if (size < capacity) {
      // Insert new value at the end
      data[size] = value;
      distance[size] = dist;
      upHeapify(size);
      size++;
    } else if (dist < distance[0]) {
      // If there is no room left in the heap, and the new entry is lower
      // than the max entry replace the max entry with the new entry
      data[0] = value;
      distance[0] = dist;
      downHeapify(0);
    }
  }

  /**
   * Removes the largest.
   */
  public void removeLargest() {
    if (size == 0) {
      throw new IllegalStateException();
    }

    removedData = data[0];
    removedDistance = distance[0];
    size--;
    data[0] = data[size];
    distance[0] = distance[size];
    downHeapify(0);
  }

  /**
   * Up heapify.
   *
   * @param index the index
   */
  private void upHeapify(int index) {
    while (index > 0) {
      final int p = (index - 1) >>> 1;
      if (distance[index] > distance[p]) {
        final Object pData = data[p];
        final float pDist = distance[p];
        data[p] = data[index];
        distance[p] = distance[index];
        data[index] = pData;
        distance[index] = pDist;
        index = p;
      } else {
        break;
      }
    }
  }

  /**
   * Down heapify.
   *
   * @param index the index
   */
  private void downHeapify(int index) {
    for (int c = index * 2 + 1; c < size; index = c, c = index * 2 + 1) {
      if (c + 1 < size && distance[c] < distance[c + 1]) {
        c++;
      }
      if (distance[index] < distance[c]) {
        // Swap the points
        final Object pData = data[index];
        final float pDist = distance[index];
        data[index] = data[c];
        distance[index] = distance[c];
        data[c] = pData;
        distance[c] = pDist;
      } else {
        break;
      }
    }
  }

  /**
   * Gets the max dist.
   *
   * @return the max dist
   */
  public float getMaxDist() {
    if (size < capacity) {
      return Float.POSITIVE_INFINITY;
    }
    return distance[0];
  }

  /**
   * Gets the size.
   *
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * Gets the capacity.
   *
   * @return the capacity
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * Gets the distance.
   *
   * @return the distance
   */
  public float[] getDistance() {
    return Arrays.copyOf(distance, size);
  }

  /**
   * Gets the data.
   *
   * @return the data
   */
  public Object[] getData() {
    return Arrays.copyOf(data, size);
  }

  /**
   * Gets the data.
   *
   * @param array the array
   * @return the data
   */
  @SuppressWarnings("unchecked")
  public T[] getData(T[] array) {
    if (array.length < size) {
      // Make a new array of a's runtime type, but my contents:
      return (T[]) Arrays.copyOf(data, size, array.getClass());
    }
    System.arraycopy(data, 0, array, 0, size);
    if (array.length > size) {
      array[size] = null;
    }
    return array;
  }

  /**
   * Gets the removed data.
   *
   * @return the removed data
   * @see #removeLargest()
   */
  @SuppressWarnings("unchecked")
  public T getRemovedData() {
    return (T) removedData;
  }

  /**
   * Gets the removed distance.
   *
   * @return the removed distance
   * @see #removeLargest()
   */
  public float getRemovedDistance() {
    return removedDistance;
  }
}
