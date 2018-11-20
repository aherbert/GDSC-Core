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

package uk.ac.sussex.gdsc.core.ij;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.Opener;
import ij.plugin.FolderOpener;

import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.TextEvent;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Opens a series of images in a folder. The series is sorted numerically.
 *
 * <p>Adapted from {@link ij.plugin.FolderOpener}.
 */
public class SeriesOpener {
  private final String path;
  private String[] imageList = new String[0];
  private int currentImage;
  private int width = -1;
  private int height = -1;
  private boolean variableSize;
  private int numberOfThreads;

  // Used to filter the image list
  private int maximumNumberOfImages;
  private int start;
  private int increment;
  private String filter;
  private boolean isRegex;

  /**
   * Create an opener with the given path.
   *
   * @param path the path
   */
  public SeriesOpener(String path) {
    this.path = path;
    buildImageList();
  }

  /**
   * Create an opener with the given path.
   *
   * @param path the path
   * @param showDialog Open a dialog and allow the user to filter the images
   * @param numberOfThreads Set the number of threads specified in the input dialog. If zero then
   *        this field is not shown.
   * @return the series opener
   */
  public static SeriesOpener create(String path, boolean showDialog, int numberOfThreads) {
    SeriesOpener opener = new SeriesOpener(path);
    opener.numberOfThreads = Math.abs(numberOfThreads);
    if (showDialog) {
      opener.filterImageList();
    }
    return opener;
  }

  private void buildImageList() {
    final String directory = path;
    if (directory == null) {
      return;
    }

    // Get a list of files
    final File[] fileList = (new File(directory)).listFiles();
    if (fileList == null) {
      return;
    }

    // Exclude directories
    String[] list = new String[fileList.length];
    int count = 0;
    for (int i = 0; i < list.length; i++) {
      if (fileList[i].isFile()) {
        list[count++] = fileList[i].getName();
      }
    }
    if (count == 0) {
      return;
    }
    list = Arrays.copyOf(list, count);

    // Now exclude non-image files as per the ImageJ FolderOpener
    final FolderOpener fo = new FolderOpener();
    list = fo.trimFileList(list);
    if (list == null) {
      return;
    }

    imageList = fo.sortFileList(list);
  }

  /**
   * Returns the number of images in the series. Note that the number is based on a list of
   * filenames; each image is only opened with the nextImage() function.
   *
   * @return The number of images in the series
   */
  public int getNumberOfImages() {
    return imageList.length;
  }

  /**
   * Returns the path to the directory containing the images.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns the names of the images in the series.
   *
   * @return The names of the image files
   */
  public String[] getImageList() {
    return imageList.clone();
  }

  /**
   * Get the next image in the series (or null if no more images).
   *
   * <p>Only images that match the width and height of the first image are returned.
   *
   * @return The next image in the series
   */
  public ImagePlus nextImage() {
    ImagePlus imp = null;
    while (currentImage < imageList.length && imp == null) {
      ImageJUtils.showSlowProgress(currentImage, imageList.length);
      imp = openImage(imageList[currentImage++]);
      if (currentImage == imageList.length) {
        ImageJUtils.clearSlowProgress();
      }
    }
    return imp;
  }

  private ImagePlus openImage(String filename) {
    final Opener opener = new Opener();
    opener.setSilentMode(true);
    ImagePlus imp = opener.openImage(path, filename);
    if (imp != null) {
      // Initialise dimensions using first image
      if (width == -1) {
        width = imp.getWidth();
        height = imp.getHeight();
      }

      // Check dimensions
      if (!variableSize && (width != imp.getWidth() || height != imp.getHeight())) {
        imp = null;
      }
    }
    return imp;
  }

  private void filterImageList() {
    String[] list = imageList;

    final ImagePlus imp = nextImage();

    // Reset image list
    currentImage = 0;
    imageList = new String[0];

    if (imp != null && showDialog(imp, list)) {
      // Filter by name
      if (filter != null && (filter.equals("") || filter.equals("*"))) {
        filter = null;
      }
      if (filter != null) {
        int filteredImages = 0;
        if (isRegex) {
          final Pattern pattern = Pattern.compile(filter);
          for (int i = 0; i < list.length; i++) {
            if (pattern.matcher(list[i]).matches()) {
              filteredImages++;
            } else {
              list[i] = null;
            }
          }
        } else {
          for (int i = 0; i < list.length; i++) {
            if (list[i].indexOf(filter) >= 0) {
              filteredImages++;
            } else {
              list[i] = null;
            }
          }
        }
        if (filteredImages == 0) {
          if (isRegex) {
            IJ.error("Import Sequence", "None of the file names match the regular expression.");
          } else {
            IJ.error("Import Sequence", "None of the " + list.length
                + " files contain\n the string '" + filter + "' in their name.");
          }
          return;
        }
        final String[] list2 = new String[filteredImages];
        int count = 0;
        for (int i = 0; i < list.length; i++) {
          if (list[i] != null) {
            list2[count++] = list[i];
          }
        }
        list = list2;
      }

      // Process only the requested number of images
      if (maximumNumberOfImages < 1) {
        maximumNumberOfImages = list.length;
      }
      if (start < 1 || start > list.length) {
        start = 1;
      }
      imageList = new String[list.length];
      int count = 0;
      for (int i = start - 1; i < list.length && count < maximumNumberOfImages; i +=
          increment, count++) {
        imageList[count] = list[i];
      }

      imageList = Arrays.copyOf(imageList, count);
    }
  }

  private boolean showDialog(ImagePlus imp, String[] list) {
    final int fileCount = list.length;
    final FolderOpenerDialog gd = new FolderOpenerDialog("Sequence Options", imp, list);
    gd.addMessage(
        "Folder: " + path + "\nFirst image: " + imp.getOriginalFileInfo().fileName + "\nWidth: "
            + imp.getWidth() + "\nHeight: " + imp.getHeight() + "\nFrames: " + imp.getStackSize());
    gd.addNumericField("Number of images:", fileCount, 0);
    gd.addNumericField("Starting image:", 1, 0);
    gd.addNumericField("Increment:", 1, 0);
    gd.addStringField("File name contains:", "", 10);
    gd.addStringField("or enter pattern:", "", 10);
    if (numberOfThreads > 0) {
      gd.addNumericField("Series_number_of_threads:", numberOfThreads, 0);
    }
    gd.addMessage("[info...]");
    gd.showDialog();
    if (gd.wasCanceled()) {
      return false;
    }
    maximumNumberOfImages = (int) gd.getNextNumber();
    start = (int) gd.getNextNumber();
    increment = (int) gd.getNextNumber();
    if (increment < 1) {
      increment = 1;
    }
    filter = gd.getNextString();
    final String regex = gd.getNextString();
    if (!regex.equals("")) {
      filter = regex;
      isRegex = true;
    }
    if (numberOfThreads > 0) {
      numberOfThreads = Math.abs((int) gd.getNextNumber());
    }
    return true;
  }

  /**
   * Set to true to allow subsequent images after the first to have different XY dimensions.
   *
   * @param variableSize True for vairable size images
   */
  public void setVariableSize(boolean variableSize) {
    this.variableSize = variableSize;
  }

  /**
   * Gets the number of threads specified in the input dialog.
   *
   * @return The number of threads specified in the input dialog.
   */
  public int getNumberOfThreads() {
    return numberOfThreads;
  }

  private static class FolderOpenerDialog extends GenericDialog {
    private static final long serialVersionUID = 7944532917923080862L;
    transient ImagePlus imp;
    String[] list;

    public FolderOpenerDialog(String title, ImagePlus imp, String[] list) {
      super(title);
      this.imp = imp;
      this.list = list;
    }

    @Override
    protected void setup() {
      setStackInfo();
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
      // Do nothing
    }

    @Override
    public void textValueChanged(TextEvent event) {
      setStackInfo();
    }

    void setStackInfo() {
      int localNumberOfImages = getNumber(numberField.elementAt(0));

      // Filter by name
      String localFilter = ((TextField) stringField.elementAt(0)).getText();
      final String regex = ((TextField) stringField.elementAt(1)).getText();
      Pattern pattern = null;
      if (!regex.equals("")) {
        localFilter = regex;
        pattern = Pattern.compile(localFilter);
      }

      if (!localFilter.equals("") && !localFilter.equals("*")) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
          if (pattern != null) {
            if (pattern.matcher(list[i]).matches()) {
              count++;
            }
          } else if (list[i].indexOf(localFilter) >= 0) {
            count++;
          }
        }
        if (count < localNumberOfImages) {
          localNumberOfImages = count;
        }
      }

      // Now count using the input settings
      int localStart = getNumber(numberField.elementAt(1));
      if (localStart < 1 || localStart > localNumberOfImages) {
        localStart = 1;
      }
      int localIncrement = getNumber(numberField.elementAt(2));
      if (localIncrement < 1) {
        localIncrement = 1;
      }

      int count = 0;
      for (int i = localStart - 1; i < list.length && count < localNumberOfImages; i +=
          localIncrement, count++) {
        // count increment
      }

      final int frames = imp.getStackSize() * count;
      ((Label) theLabel).setText(String.format("%d image%s (%d frame%s)", count,
          (count == 1) ? "" : "s", frames, (frames == 1) ? "" : "s"));
    }

    /**
     * Gets the number.
     *
     * @param field the field
     * @return the number
     */
    public int getNumber(Object field) {
      final TextField tf = (TextField) field;
      try {
        return Integer.parseInt(tf.getText());
      } catch (final NumberFormatException ex) {
        // Not an integer
      }
      return 0;
    }
  }
}
