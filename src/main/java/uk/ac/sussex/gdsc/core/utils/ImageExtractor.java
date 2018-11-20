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

package uk.ac.sussex.gdsc.core.utils;

import java.awt.Rectangle;
import java.util.Arrays;

/**
 * Contains methods for extracting parts of an image.
 */
public class ImageExtractor {
  private final float[] data;

  /** The width of the image. */
  public final int width;

  /** The height of the image. */
  public final int height;

  /**
   * Constructor.
   *
   * @param data The image data
   * @param width The image width
   * @param height The image height
   */
  ImageExtractor(float[] data, int width, int height) {
    this.data = data;
    this.width = width;
    this.height = height;
  }

  /**
   * Create a new image extractor wrapping the provided data.
   *
   * @param data the data
   * @param width the width
   * @param height the height
   * @return the image extractor
   */
  public static ImageExtractor wrap(float[] data, int width, int height) {
    return new ImageExtractor(data, width, height);
  }

  /**
   * Extract a region from the image.
   *
   * @param regionBounds The region to extract
   * @return The image region (with dimensions specified in the dimensions array)
   */
  public float[] crop(Rectangle regionBounds) {
    return crop(regionBounds, (float[]) null);
  }

  /**
   * Extract a region from the image. The output array can be truncated using the
   * {@link #truncate(float[], int)} method.
   *
   * @param regionBounds The region to extract
   * @param region A reusable buffer for the region
   * @return The image region (with dimensions specified in the dimensions array)
   */
  public float[] crop(Rectangle regionBounds, float[] region) {
    final float[] buffer = allocate(region, regionBounds.width * regionBounds.height);

    int offset1 = 0;
    for (int ys = regionBounds.y; ys < regionBounds.y + regionBounds.height; ys++) {
      int offset2 = ys * width + regionBounds.x;
      for (int xs = 0; xs < regionBounds.width; xs++) {
        buffer[offset1++] = data[offset2++];
      }
    }

    return buffer;
  }

  /**
   * Extract a region from the image. The output array can be truncated using the
   * {@link #truncate(double[], int)} method.
   *
   * @param regionBounds The region to extract
   * @param region A reusable buffer for the region
   * @return The image region (with dimensions specified in the dimensions array)
   */
  public double[] crop(Rectangle regionBounds, double[] region) {
    final double[] buffer = allocate(region, regionBounds.width * regionBounds.height);

    int offset1 = 0;
    for (int ys = regionBounds.y; ys < regionBounds.y + regionBounds.height; ys++) {
      int offset2 = ys * width + regionBounds.x;
      for (int xs = 0; xs < regionBounds.width; xs++) {
        buffer[offset1++] = data[offset2++];
      }
    }

    return buffer;
  }

  /**
   * Extract a region from the image.
   *
   * @param regionBounds The region to extract
   * @return The image region (with dimensions specified in the dimensions array)
   */
  public double[] cropToDouble(Rectangle regionBounds) {
    return crop(regionBounds, (double[]) null);
  }

  private static float[] allocate(float[] buffer, int size) {
    if (buffer == null || buffer.length < size) {
      return new float[size];
    }
    return buffer;
  }

  private static double[] allocate(double[] buffer, int size) {
    if (buffer == null || buffer.length < size) {
      return new double[size];
    }
    return buffer;
  }

  /**
   * Truncate the input data to the given length. Does nothing if the data is shorter or null.
   *
   * @param data the data
   * @param length the length
   * @return The truncated data
   */
  public static float[] truncate(float[] data, int length) {
    if (data != null && data.length > length) {
      return Arrays.copyOf(data, length);
    }
    return data;
  }

  /**
   * Truncate the input data to the given length. Does nothing if the data is shorter or null.
   *
   * @param data the data
   * @param length the length
   * @return The truncated data
   */
  public static double[] truncate(double[] data, int length) {
    if (data != null && data.length > length) {
      return Arrays.copyOf(data, length);
    }
    return data;
  }

  /**
   * Calculate a square region of size 2n+1 around the given coordinates. Respects the image
   * boundaries and so may return a non-square region.
   *
   * @param x the x
   * @param y the y
   * @param n the n
   * @return The region
   */
  public Rectangle getBoxRegionBounds(int x, int y, int n) {
    final Rectangle r1 = new Rectangle(x - n, y - n, 2 * n + 1, 2 * n + 1);
    return r1.intersection(new Rectangle(0, 0, width, height));
  }
}
