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
package uk.ac.sussex.gdsc.core.ij.roi;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;
import org.junit.jupiter.api.*;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;


import java.awt.Rectangle;
import java.awt.Shape;

import ij.gui.Roi;
import ij.gui.ShapeRoi;

/**
 * Class for testing if coordinates are within a composite ROI.
 */
public class CompositeRoiTest extends RoiTest {
  private final Shape shape;
  private final int ox, oy;

  /**
   * Instantiates a new composite roi test.
   *
   * @param roi the roi
   */
  public CompositeRoiTest(Roi roi) {
    if (roi.getType() == Roi.COMPOSITE) {
      // The composite shape is offset by the origin
      final Rectangle bounds = roi.getBounds();
      shape = ((ShapeRoi) roi).getShape();
      ox = bounds.x;
      oy = bounds.y;
    } else {
      throw new IllegalArgumentException("Require composite ROI");
    }
  }

  @Override
  public boolean contains(double x, double y) {
    return shape.contains(x - ox, y - oy);
  }
}
