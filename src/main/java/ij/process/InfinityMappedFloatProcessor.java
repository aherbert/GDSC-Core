package ij.process;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2017 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

import java.awt.image.ColorModel;

/**
 * Extends the ImageJ FloatProcess class to map the min-max range to 1-255 in the 8-bit image. Negative infinity is
 * mapped to 0 in the LUT.
 * <p>
 * This allows display of a range of float data using the special marker -Infinity to ignore pixels from display
 * (assuming the LUT has black for 0). -Infinity is ignored by ImageJ for most FloatProcessor functionality (histograms,
 * min/max value, etc). This is not the case for NaN which breaks ImageJ data display.
 * 
 * @author Alex Herbert
 */
public class InfinityMappedFloatProcessor extends FloatProcessor
{
	private boolean mapPositiveInfinity = false;

	/**
	 * Checks if positive infinity is mapped to zero.
	 *
	 * @return true, if positive infinity is mapped to zero
	 */
	public boolean isMapPositiveInfinity()
	{
		return mapPositiveInfinity;
	}

	/**
	 * Set to true to map positive infinity to zero.
	 *
	 * @param mapPositiveInfinity
	 *            the new map positive infinity flag
	 */
	public void setMapPositiveInfinity(boolean mapPositiveInfinity)
	{
		this.mapPositiveInfinity = mapPositiveInfinity;
	}

	/** Creates a new MappedFloatProcessor using the specified pixel array. */
	public InfinityMappedFloatProcessor(int width, int height, float[] pixels)
	{
		this(width, height, pixels, null);
	}

	/** Creates a new MappedFloatProcessor using the specified pixel array and ColorModel. */
	public InfinityMappedFloatProcessor(int width, int height, float[] pixels, ColorModel cm)
	{
		super(width, height, pixels, cm);
	}

	/**
	 * Creates a blank MappedFloatProcessor using the default grayscale LUT that
	 * displays zero as black. Call invertLut() to display zero as white.
	 */
	public InfinityMappedFloatProcessor(int width, int height)
	{
		super(width, height, new float[width * height], null);
	}

	/** Creates a MappedFloatProcessor from an int array using the default grayscale LUT. */
	public InfinityMappedFloatProcessor(int width, int height, int[] pixels)
	{
		super(width, height, pixels);
	}

	/** Creates a MappedFloatProcessor from a double array using the default grayscale LUT. */
	public InfinityMappedFloatProcessor(int width, int height, double[] pixels)
	{
		super(width, height, pixels);
	}

	/** Creates a MappedFloatProcessor from a 2D float array using the default LUT. */
	public InfinityMappedFloatProcessor(float[][] array)
	{
		super(array);
	}

	/** Creates a MappedFloatProcessor from a 2D int array. */
	public InfinityMappedFloatProcessor(int[][] array)
	{
		super(array);
	}

	@Override
	protected byte[] create8BitImage()
	{
		// Map all values to the range 1-255. Negative infinity maps to zero.
		int size = width * height;
		if (pixels8 == null)
			pixels8 = new byte[size];
		float[] pixels = (float[]) getPixels();
		float value;
		int ivalue;

		// Default min/max
		float min2 = (float) getMin(), max2 = (float) getMax();

		float scale = 254f / (max2 - min2);

		if (mapPositiveInfinity)
		{
			for (int i = 0; i < size; i++)
			{
				if (Float.isInfinite(pixels[i]))
				{
					// Infinity maps to zero.
					pixels8[i] = (byte) 0;
				}
				else
				{
					// Map all values to the range 1-255.
					value = pixels[i] - min2;
					ivalue = 1 + (int) ((value * scale) + 0.5f);
					if (ivalue > 255)
						ivalue = 255;
					pixels8[i] = (byte) ivalue;
				}
			}
		}
		else
		{
			for (int i = 0; i < size; i++)
			{
				if (pixels[i] < min2)
				{
					// Below min maps to zero. This is -Infinity.
					pixels8[i] = (byte) 0;
				}
				else
				{
					// Map all values to the range 1-255.
					value = pixels[i] - min2;
					ivalue = 1 + (int) ((value * scale) + 0.5f);
					if (ivalue > 255)
						ivalue = 255;
					pixels8[i] = (byte) ivalue;
				}
			}
		}
		return pixels8;
	}
}