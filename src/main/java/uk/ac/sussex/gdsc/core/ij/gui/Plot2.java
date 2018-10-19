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

package uk.ac.sussex.gdsc.core.ij.gui;

import ij.gui.Plot;

/**
 * Extension of the {@link ij.gui.Plot} class to add functionality.
 */
public class Plot2 extends Plot {
  /** Draw a bar plot. */
  public static final int BAR = 999;

  // This uses the ImageJ parameter names
  // CHECKSTYLE.OFF: ParameterName

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param xValues the x values
   * @param yValues the y values
   */
  public Plot2(String title, String xLabel, String yLabel, float[] xValues, float[] yValues) {
    super(title, xLabel, yLabel, xValues, yValues);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param xValues the x values
   * @param yValues the y values
   */
  public Plot2(String title, String xLabel, String yLabel, double[] xValues, double[] yValues) {
    super(title, xLabel, yLabel, xValues, yValues);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param dummy the dummy
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param xValues the x values
   * @param yValues the y values
   */
  public Plot2(String dummy, String title, String xLabel, String yLabel, float[] xValues,
      float[] yValues) {
    super(title, xLabel, yLabel, xValues, yValues);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   */
  public Plot2(String title, String xLabel, String yLabel) {
    super(title, xLabel, yLabel, (float[]) null, (float[]) null);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param flags the flags
   */
  public Plot2(String title, String xLabel, String yLabel, int flags) {
    super(title, xLabel, yLabel, (float[]) null, (float[]) null, flags);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param xValues the x values
   * @param yValues the y values
   * @param flags the flags
   */
  public Plot2(String title, String xLabel, String yLabel, float[] xValues, float[] yValues,
      int flags) {
    super(title, xLabel, yLabel, xValues, yValues, flags);
  }

  /**
   * Instantiates a new plot 2.
   *
   * @param title the title
   * @param xLabel the x label
   * @param yLabel the y label
   * @param xValues the x values
   * @param yValues the y values
   * @param flags the flags
   */
  public Plot2(String title, String xLabel, String yLabel, double[] xValues, double[] yValues,
      int flags) {
    super(title, xLabel, yLabel, xValues, yValues, flags);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Support Bar plots by adding an extra point to draw a horizontal line and vertical line
   * between points.
   *
   * <p>This is a fudge as the values for the bar will be exported as the duplicated line. However
   * if a visual is all that is required then this works fine.
   *
   * @param shape CIRCLE, X, BOX, TRIANGLE, CROSS, DOT, LINE, CONNECTED_CIRCLES, or BAR
   */
  @Override
  public void addPoints(float[] xValues, float[] yValues, float[] yErrorBars, int shape,
      String label) {
    // This only works if the addPoints super method ignores the
    // BAR option but still stores the values.
    try {
      if (shape == BAR) {
        shape = Plot.LINE;

        if (xValues == null || xValues.length == 0) {
          xValues = new float[yValues.length];
          for (int i = 0; i < yValues.length; i++) {
            xValues[i] = i;
          }
        }

        final float[] x = createHistogramAxis(xValues);
        final float[] y = createHistogramValues(yValues);

        // No errors
        xValues = x;
        yValues = y;
      }
    } catch (final Throwable ex) { // Ignore
    } finally {
      super.addPoints(xValues, yValues, yErrorBars, shape, label);
    }
  }

  // CHECKSTYLE.ON: ParameterName

  /**
   * For the provided histogram x-axis bins, produce an x-axis for plotting. This functions doubles
   * up the histogram x-positions to allow plotting a square line profile using the ImageJ plot
   * command.
   *
   * @param histogramX the histogram X
   * @return the x-axis
   */
  public static float[] createHistogramAxis(float[] histogramX) {
    final float[] axis = new float[histogramX.length * 2 + 2];
    int index = 0;
    for (int i = 0; i < histogramX.length; ++i) {
      axis[index++] = histogramX[i];
      axis[index++] = histogramX[i];
    }
    if (histogramX.length > 0) {
      final float dx = (histogramX.length == 1) ? 1 : (histogramX[1] - histogramX[0]);
      axis[index++] = histogramX[histogramX.length - 1] + dx;
      axis[index] = histogramX[histogramX.length - 1] + dx;
    }
    return axis;
  }

  /**
   * For the provided histogram y-axis values, produce a y-axis for plotting. This functions doubles
   * up the histogram values to allow plotting a square line profile using the ImageJ plot command.
   *
   * @param histogramY the histogram Y
   * @return the y-axis
   */
  public static float[] createHistogramValues(float[] histogramY) {
    final float[] axis = new float[histogramY.length * 2 + 2];

    int index = 0;
    axis[index++] = 0;
    for (int i = 0; i < histogramY.length; ++i) {
      axis[index++] = histogramY[i];
      axis[index++] = histogramY[i];
    }
    return axis;
  }
}
