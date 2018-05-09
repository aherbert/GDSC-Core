package gdsc.core.ij.roi;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/*----------------------------------------------------------------------------- 
 * GDSC Plugins for ImageJ
 * 
 * Copyright (C) 2018 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * Class for working with image ROIs.
 */
public class RoiHelper
{
	/**
	 * Interface for accessing the pixels
	 */
	public interface IPixelProcedure
	{
		/**
		 * Executes this procedure.
		 *
		 * @param value
		 *            the pixel value
		 */
		void execute(int value);
	}

	/**
	 * Interface for accessing the pixels
	 */
	public interface FPixelProcedure
	{
		/**
		 * Executes this procedure.
		 *
		 * @param value
		 *            the pixel value
		 */
		void execute(float value);
	}

	/**
	 * Build a byte mask of all pixels in an ROI. If no area ROI is present then the mask
	 * will be null.
	 * 
	 * @param imp
	 *            The input image
	 * @return a byte mask (255 inside the ROI, else 0)
	 */
	public static ByteProcessor getMask(ImagePlus imp)
	{
		int maxx = imp.getWidth();
		int maxy = imp.getHeight();

		final Roi roi = imp.getRoi();

		if (roi == null || !roi.isArea())
			return null;

		// Check if this is a standard rectangle ROI that covers the entire image
		if (roi.getType() == Roi.RECTANGLE && roi.getRoundRectArcSize() == 0)
		{
			final Rectangle roiBounds = roi.getBounds();
			if (roiBounds.width == maxx && roiBounds.height == maxy)
				return null;
		}

		ByteProcessor bp = new ByteProcessor(maxx, maxy);
		bp.setColor(255);
		bp.fill(roi);
		return bp;
	}

	/**
	 * For each pixel inside the ROI execute the procedure. If the ROI is null then all pixels will be sampled.
	 *
	 * @param roi
	 *            the roi
	 * @param ip
	 *            the image processor
	 * @param p
	 *            the procedure
	 */
	public static void forEach(Roi roi, ImageProcessor ip, FPixelProcedure p)
	{
		if (roi == null)
		{
			for (int i = 0, n = ip.getPixelCount(); i < n; i++)
				p.execute(ip.getf(i));
			return;
		}

		// Ensure the roi bounds fit inside the processor
		final int maxx = ip.getWidth();
		final int maxy = ip.getHeight();
		final Rectangle roiBounds = roi.getBounds().intersection(new Rectangle(maxx, maxy));
		final int xOffset = roiBounds.x;
		final int yOffset = roiBounds.y;
		final int rwidth = roiBounds.width;
		final int rheight = roiBounds.height;
		if (rwidth == 0 || rheight == 0)
			return;

		ImageProcessor mask = roi.getMask();
		if (mask == null)
		{
			for (int y = 0; y < rheight; y++)
			{
				for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
				{
					p.execute(ip.getf(i));
				}
			}
		}
		else
		{
			for (int y = 0, j = 0; y < rheight; y++)
			{
				for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
				{
					if (mask.get(j++) != 0)
						p.execute(ip.getf(i));
				}
			}
		}
	}

	/**
	 * For each pixel inside the ROI execute the procedure. If the ROI is null then all pixels will be sampled.
	 *
	 * @param roi
	 *            the roi
	 * @param stack
	 *            the stack
	 * @param p
	 *            the procedure
	 */
	public static void forEach(Roi roi, ImageStack stack, FPixelProcedure p)
	{
		if (roi == null)
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int i = 0, n = ip.getPixelCount(); i < n; i++)
					p.execute(ip.getf(i));
			}
			return;
		}

		// Ensure the roi bounds fit inside the processor
		final int maxx = stack.getWidth();
		final int maxy = stack.getHeight();
		final Rectangle roiBounds = roi.getBounds().intersection(new Rectangle(maxx, maxy));
		final int xOffset = roiBounds.x;
		final int yOffset = roiBounds.y;
		final int rwidth = roiBounds.width;
		final int rheight = roiBounds.height;
		if (rwidth == 0 || rheight == 0)
			return;

		ImageProcessor mask = roi.getMask();
		if (mask == null)
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int y = 0; y < rheight; y++)
				{
					for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
					{
						p.execute(ip.getf(i));
					}
				}
			}
		}
		else
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int y = 0, j = 0; y < rheight; y++)
				{
					for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
					{
						if (mask.get(j++) != 0)
							p.execute(ip.getf(i));
					}
				}
			}
		}
	}
	/**
	 * For each pixel inside the ROI execute the procedure. If the ROI is null then all pixels will be sampled.
	 *
	 * @param roi
	 *            the roi
	 * @param ip
	 *            the image processor
	 * @param p
	 *            the procedure
	 */
	public static void forEach(Roi roi, ImageProcessor ip, IPixelProcedure p)
	{
		if (roi == null)
		{
			for (int i = 0, n = ip.getPixelCount(); i < n; i++)
				p.execute(ip.get(i));
			return;
		}

		// Ensure the roi bounds fit inside the processor
		final int maxx = ip.getWidth();
		final int maxy = ip.getHeight();
		final Rectangle roiBounds = roi.getBounds().intersection(new Rectangle(maxx, maxy));
		final int xOffset = roiBounds.x;
		final int yOffset = roiBounds.y;
		final int rwidth = roiBounds.width;
		final int rheight = roiBounds.height;
		if (rwidth == 0 || rheight == 0)
			return;

		ImageProcessor mask = roi.getMask();
		if (mask == null)
		{
			for (int y = 0; y < rheight; y++)
			{
				for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
				{
					p.execute(ip.get(i));
				}
			}
		}
		else
		{
			for (int y = 0, j = 0; y < rheight; y++)
			{
				for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
				{
					if (mask.get(j++) != 0)
						p.execute(ip.get(i));
				}
			}
		}
	}

	/**
	 * For each pixel inside the ROI execute the procedure. If the ROI is null then all pixels will be sampled.
	 *
	 * @param roi
	 *            the roi
	 * @param stack
	 *            the stack
	 * @param p
	 *            the procedure
	 */
	public static void forEach(Roi roi, ImageStack stack, IPixelProcedure p)
	{
		if (roi == null)
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int i = 0, n = ip.getPixelCount(); i < n; i++)
					p.execute(ip.get(i));
			}
			return;
		}

		// Ensure the roi bounds fit inside the processor
		final int maxx = stack.getWidth();
		final int maxy = stack.getHeight();
		final Rectangle roiBounds = roi.getBounds().intersection(new Rectangle(maxx, maxy));
		final int xOffset = roiBounds.x;
		final int yOffset = roiBounds.y;
		final int rwidth = roiBounds.width;
		final int rheight = roiBounds.height;
		if (rwidth == 0 || rheight == 0)
			return;

		ImageProcessor mask = roi.getMask();
		if (mask == null)
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int y = 0; y < rheight; y++)
				{
					for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
					{
						p.execute(ip.get(i));
					}
				}
			}
		}
		else
		{
			for (int slice = 1; slice <= stack.getSize(); slice++)
			{
				ImageProcessor ip = stack.getProcessor(slice);
				for (int y = 0, j = 0; y < rheight; y++)
				{
					for (int x = 0, i = (y + yOffset) * maxx + xOffset; x < rwidth; x++)
					{
						if (mask.get(j++) != 0)
							p.execute(ip.get(i));
					}
				}
			}
		}
	}
}
