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
package uk.ac.sussex.gdsc.core.ij.text;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;
import org.junit.jupiter.api.*;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;


import java.util.ArrayList;

import ij.text.TextWindow;

/**
 * Uses a TextPanel to displays text in a window. <p> Copied from {@link ij.text.TextWindow}. Added
 * functionality to allow the window to be configured before display.
 *
 * @see TextPanel
 */
public class TextWindow2 extends TextWindow {
  private static final long serialVersionUID = -6164933921278234275L;

  private boolean constructed = false;

  /**
   * Opens a new single-column text window.
   *
   * @param title the title of the window
   * @param text the text initially displayed in the window
   * @param width the width of the window in pixels
   * @param height the height of the window in pixels
   */
  public TextWindow2(String title, String text, int width, int height) {
    this(title, "", text, width, height);
  }

  /**
   * Opens a new multi-column text window.
   *
   * @param title title of the window
   * @param headings the tab-delimited column headings
   * @param text text initially displayed in the window
   * @param width width of the window in pixels
   * @param height height of the window in pixels
   */
  public TextWindow2(String title, String headings, String text, int width, int height) {
    super(title, headings, text, width, height);
    constructed = true;
  }

  /**
   * Opens a new multi-column text window.
   *
   * @param title title of the window
   * @param headings tab-delimited column headings
   * @param text ArrayList containing the text to be displayed in the window
   * @param width width of the window in pixels
   * @param height height of the window in pixels
   */
  public TextWindow2(String title, String headings, ArrayList<String> text, int width, int height) {
    super(title, headings, text, width, height);
    constructed = true;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void show() {
    // Do not show upon construction
    if (constructed) {
      super.show();
    }
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      // Do not show upon construction
      if (!constructed) {
        return;
      }
    }
    super.setVisible(b);
  }
}
