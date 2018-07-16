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
package gdsc.core.ij;

import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.TextEvent;
import java.io.File;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.Opener;
import ij.plugin.FolderOpener;

/**
 * Opens a series of images in a folder. The series is sorted numerically.
 * <p>
 * Adapted from {@link ij.plugin.FolderOpener }
 */
public class SeriesOpener
{
	private final String path;
	private String[] imageList = new String[0];
	private int currentImage = 0;
	private int width = -1, height = -1;
	private boolean variableSize = false;
	private int numberOfThreads = 0;

	/**
	 * Create an opener with the given path.
	 *
	 * @param path
	 *            the path
	 */
	public SeriesOpener(String path)
	{
		this.path = path;
		buildImageList();
	}

	/**
	 * Create an opener with the given path.
	 *
	 * @param path
	 *            the path
	 * @param showDialog
	 *            Open a dialog and allow the user to filter the images
	 * @param numberOfThreads
	 *            Set the number of threads specified in the input dialog. If zero then this field is not shown.
	 */
	public SeriesOpener(String path, boolean showDialog, int numberOfThreads)
	{
		this.path = path;
		this.numberOfThreads = Math.abs(numberOfThreads);
		buildImageList();

		if (showDialog)
			filterImageList();
	}

	private void buildImageList()
	{
		final String directory = path;
		if (directory == null)
			return;

		// Get a list of files
		final File[] fileList = (new File(directory)).listFiles();
		if (fileList == null)
			return;

		// Exclude directories
		String[] list = new String[fileList.length];
		int c = 0;
		for (int i = 0; i < list.length; i++)
			if (fileList[i].isFile())
				list[c++] = fileList[i].getName();
		if (c == 0)
			return;
		list = Arrays.copyOf(list, c);

		// Now exclude non-image files as per the ImageJ FolderOpener
		final FolderOpener fo = new FolderOpener();
		list = fo.trimFileList(list);
		if (list == null)
			return;

		imageList = fo.sortFileList(list);
	}

	/**
	 * Returns the number of images in the series. Note that the number is based on a list of filenames; each image is
	 * only opened with the nextImage() function.
	 *
	 * @return The number of images in the series
	 */
	public int getNumberOfImages()
	{
		return imageList.length;
	}

	/**
	 * Returns the path to the directory containing the images
	 *
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Returns the names of the images in the series
	 *
	 * @return The names of the image files
	 */
	public String[] getImageList()
	{
		return imageList;
	}

	/**
	 * Get the next image in the series (or null if no more images)
	 * <p>
	 * Only images that match the width and height of the first image are returned.
	 *
	 * @return The next image in the series
	 */
	public ImagePlus nextImage()
	{
		ImagePlus imp = null;
		while (currentImage < imageList.length && imp == null)
			imp = openImage(imageList[currentImage++]);
		return imp;
	}

	private ImagePlus openImage(String filename)
	{
		//System.out.printf("Opening %s %s\n", path, filename);
		final Opener opener = new Opener();
		opener.setSilentMode(true);
		Utils.setShowProgress(false);
		ImagePlus imp = opener.openImage(path, filename);
		Utils.setShowProgress(true);
		if (imp != null)
		{
			// Initialise dimensions using first image
			if (width == -1)
			{
				width = imp.getWidth();
				height = imp.getHeight();
			}

			// Check dimensions
			if (!variableSize)
				if (width != imp.getWidth() || height != imp.getHeight())
					imp = null;
		}
		return imp;
	}

	// Used to filter the image list
	private int n, start;
	private int increment;
	private String filter;
	private boolean isRegex;

	private void filterImageList()
	{
		String[] list = imageList;

		final ImagePlus imp = nextImage();

		// Reset image list
		currentImage = 0;
		imageList = new String[0];

		if (imp != null && showDialog(imp, list))
		{
			// Filter by name
			if (filter != null && (filter.equals("") || filter.equals("*")))
				filter = null;
			if (filter != null)
			{
				int filteredImages = 0;
				for (int i = 0; i < list.length; i++)
					if (isRegex && list[i].matches(filter))
						filteredImages++;
					else if (list[i].indexOf(filter) >= 0)
						filteredImages++;
					else
						list[i] = null;
				if (filteredImages == 0)
				{
					if (isRegex)
						IJ.error("Import Sequence", "None of the file names match the regular expression.");
					else
						IJ.error("Import Sequence", "None of the " + list.length + " files contain\n the string '" +
								filter + "' in their name.");
					return;
				}
				final String[] list2 = new String[filteredImages];
				int j = 0;
				for (int i = 0; i < list.length; i++)
					if (list[i] != null)
						list2[j++] = list[i];
				list = list2;
			}

			// Process only the requested number of images
			if (n < 1)
				n = list.length;
			if (start < 1 || start > list.length)
				start = 1;
			imageList = new String[list.length];
			int count = 0;
			for (int i = start - 1; i < list.length && count < n; i += increment, count++)
				imageList[count] = list[i];

			imageList = Arrays.copyOf(imageList, count);
		}
	}

	private boolean showDialog(ImagePlus imp, String[] list)
	{
		final int fileCount = list.length;
		final FolderOpenerDialog gd = new FolderOpenerDialog("Sequence Options", imp, list);
		gd.addMessage("Folder: " + path + "\nFirst image: " + imp.getOriginalFileInfo().fileName + "\nWidth: " +
				imp.getWidth() + "\nHeight: " + imp.getHeight() + "\nFrames: " + imp.getStackSize());
		gd.addNumericField("Number of images:", fileCount, 0);
		gd.addNumericField("Starting image:", 1, 0);
		gd.addNumericField("Increment:", 1, 0);
		gd.addStringField("File name contains:", "", 10);
		gd.addStringField("or enter pattern:", "", 10);
		if (numberOfThreads > 0)
			gd.addNumericField("Series_number_of_threads:", numberOfThreads, 0);
		gd.addMessage("[info...]");
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		n = (int) gd.getNextNumber();
		start = (int) gd.getNextNumber();
		increment = (int) gd.getNextNumber();
		if (increment < 1)
			increment = 1;
		filter = gd.getNextString();
		final String regex = gd.getNextString();
		if (!regex.equals(""))
		{
			filter = regex;
			isRegex = true;
		}
		if (numberOfThreads > 0)
			numberOfThreads = Math.abs((int) gd.getNextNumber());
		return true;
	}

	/**
	 * Set to true to allow subsequent images after the first to have different XY dimensions
	 *
	 * @param variableSize
	 *            True for vairable size images
	 */
	public void setVariableSize(boolean variableSize)
	{
		this.variableSize = variableSize;
	}

	/**
	 * @return The number of threads specified in the input dialog.
	 */
	public int getNumberOfThreads()
	{
		return numberOfThreads;
	}

	private class FolderOpenerDialog extends GenericDialog
	{
		private static final long serialVersionUID = -7650551696737633887L;
		ImagePlus imp;
		String[] list;

		public FolderOpenerDialog(String title, ImagePlus imp, String[] list)
		{
			super(title);
			this.imp = imp;
			this.list = list;
		}

		@Override
		protected void setup()
		{
			setStackInfo();
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// Do nothing
		}

		@Override
		public void textValueChanged(TextEvent e)
		{
			setStackInfo();
		}

		void setStackInfo()
		{
			int n = getNumber(numberField.elementAt(0));
			int start = getNumber(numberField.elementAt(1));
			int inc = getNumber(numberField.elementAt(2));

			// Filter by name
			TextField tf = (TextField) stringField.elementAt(0);
			String filter = tf.getText();
			tf = (TextField) stringField.elementAt(1);
			final String regex = tf.getText();
			java.util.regex.Pattern p = null;
			if (!regex.equals(""))
			{
				filter = regex;
				p = java.util.regex.Pattern.compile(filter);
			}

			if (!filter.equals("") && !filter.equals("*"))
			{
				int n2 = 0;
				for (int i = 0; i < list.length; i++)
					if (p != null && p.matcher(list[i]).matches())
						n2++;
					else if (list[i].indexOf(filter) >= 0)
						n2++;
				if (n2 < n)
					n = n2;
			}

			// Now count using the input settings
			if (start < 1 || start > n)
				start = 1;
			if (inc < 1)
				inc = 1;

			int count = 0;
			for (int i = start - 1; i < list.length && count < n; i += inc)
				count++;

			final int frames = imp.getStackSize() * count;
			((Label) theLabel).setText(String.format("%d image%s (%d frame%s)", count, (count == 1) ? "" : "s", frames,
					(frames == 1) ? "" : "s"));
		}

		/**
		 * Gets the number.
		 *
		 * @param field
		 *            the field
		 * @return the number
		 */
		public int getNumber(Object field)
		{
			final TextField tf = (TextField) field;
			final String theText = tf.getText();
			Double d;
			try
			{
				d = new Double(theText);
			}
			catch (final NumberFormatException e)
			{
				d = null;
			}
			if (d != null)
				return (int) d.doubleValue();
			return 0;
		}
	}
}