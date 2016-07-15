package ij.process;

import java.awt.Color;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2016 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

import ij.process.LUT;

/**
 * Contains functions for ImageJ LUTs
 */
public class LUTHelper
{
	public static final String[] luts = new String[] { "Red-Hot", "Ice", "Rainbow", "Fire", "Red-Yellow", "Red",
			"Green", "Blue", "Cyan", "Magenta", "Yellow" };

	/**
	 * Build a custom LUT
	 * 
	 * @param lut The LUT to create
	 * @return the LUT
	 */
	public static LUT createLUT(int lut)
	{
		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];
		int nColors;
		switch (lut)
		{
			case 0: // red-hot
			default:
				nColors = setColours(reds, greens, blues, Color.red, Color.yellow, Color.WHITE);
				break;
			case 1:
				nColors = ice(reds, greens, blues);
				break;
			case 2:
				nColors = rainbow(reds, greens, blues);
				break;
			case 3:
				nColors = firelight(reds, greens, blues);
				break;
			case 4: // red-yellow
				nColors = setColours(reds, greens, blues, Color.red, Color.yellow);
				break;
			case 5:
				nColors = setColours(reds, greens, blues, Color.red);
				break;
			case 6:
				nColors = setColours(reds, greens, blues, Color.green);
				break;
			case 7:
				nColors = setColours(reds, greens, blues, Color.blue);
				break;
			case 8:
				nColors = setColours(reds, greens, blues, Color.cyan);
				break;
			case 9:
				nColors = setColours(reds, greens, blues, Color.magenta);
				break;
			case 10:
				nColors = setColours(reds, greens, blues, Color.yellow);
				break;
		}
		if (nColors < 256)
			interpolate(reds, greens, blues, nColors);
		return new LUT(reds, greens, blues);
	}

	private static int rainbow(byte[] reds, byte[] greens, byte[] blues)
	{
		// Using HSV vary the Hue from 300 (magenta) to Red (0)
		int n = 0;
		for (int h = 300; h >= 0; h -= 2)
		{
			Color c = Color.getHSBColor(h / 360.0f, 1, 1);
			reds[n] = (byte) c.getRed();
			greens[n] = (byte) c.getGreen();
			blues[n] = (byte) c.getBlue();
			n++;
		}
		return n;
	}

	private static int setColours(byte[] reds, byte[] greens, byte[] blues, Color... colours)
	{
		int n = 0;
		if (colours.length == 1)
		{
			reds[n] = (byte) (colours[0].getRed() / 2);
			greens[n] = (byte) (colours[0].getGreen() / 2);
			blues[n] = (byte) (colours[0].getBlue() / 2);
			n++;
		}

		for (Color colour : colours)
		{
			reds[n] = (byte) colour.getRed();
			greens[n] = (byte) colour.getGreen();
			blues[n] = (byte) colour.getBlue();
			n++;
		}
		return n;
	}

	/**
	 * Copied from ij.plugin.LutLoader
	 * 
	 * @param reds
	 * @param greens
	 * @param blues
	 * @return
	 */
	private static int ice(byte[] reds, byte[] greens, byte[] blues)
	{
		int[] r = { 0, 0, 0, 0, 0, 0, 19, 29, 50, 48, 79, 112, 134, 158, 186, 201, 217, 229, 242, 250, 250, 250, 250,
				251, 250, 250, 250, 250, 251, 251, 243, 230 };
		int[] g = { 156, 165, 176, 184, 190, 196, 193, 184, 171, 162, 146, 125, 107, 93, 81, 87, 92, 97, 95, 93, 93, 90,
				85, 69, 64, 54, 47, 35, 19, 0, 4, 0 };
		int[] b = { 140, 147, 158, 166, 170, 176, 209, 220, 234, 225, 236, 246, 250, 251, 250, 250, 245, 230, 230, 222,
				202, 180, 163, 142, 123, 114, 106, 94, 84, 64, 26, 27 };
		for (int i = 0; i < r.length; i++)
		{
			reds[i] = (byte) r[i];
			greens[i] = (byte) g[i];
			blues[i] = (byte) b[i];
		}
		return r.length;
	}

	/**
	 * Adapted from ij.plugin.LutLoader to remove the dark colours
	 * 
	 * @param reds
	 * @param greens
	 * @param blues
	 * @return
	 */
	private static int firelight(byte[] reds, byte[] greens, byte[] blues)
	{
		int[] r = { //0, 0, 1, 25, 49, 
				73, 98, 122, 146, 162, 173, 184, 195, 207, 217, 229, 240, 252, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255 };
		int[] g = { //0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101, 117, 133, 147, 161, 175, 190, 205, 219, 234, 248, 255, 255,
				255, 255 };
		int[] b = { //0, 61, 96, 130, 165, 
				192, 220, 227, 210, 181, 151, 122, 93, 64, 35, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223,
				255 };
		for (int i = 0; i < r.length; i++)
		{
			reds[i] = (byte) r[i];
			greens[i] = (byte) g[i];
			blues[i] = (byte) b[i];
		}
		return r.length;
	}

	/**
	 * Copied from ij.plugin.LutLoader.
	 * 
	 * @param reds
	 * @param greens
	 * @param blues
	 * @param nColors
	 */
	private static void interpolate(byte[] reds, byte[] greens, byte[] blues, int nColors)
	{
		byte[] r = new byte[nColors];
		byte[] g = new byte[nColors];
		byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		double scale = nColors / 256.0;
		int i1, i2;
		double fraction;
		for (int i = 0; i < 256; i++)
		{
			i1 = (int) (i * scale);
			i2 = i1 + 1;
			if (i2 == nColors)
				i2 = nColors - 1;
			fraction = i * scale - i1;
			//IJ.write(i+" "+i1+" "+i2+" "+fraction);
			reds[i] = (byte) ((1.0 - fraction) * (r[i1] & 255) + fraction * (r[i2] & 255));
			greens[i] = (byte) ((1.0 - fraction) * (g[i1] & 255) + fraction * (g[i2] & 255));
			blues[i] = (byte) ((1.0 - fraction) * (b[i1] & 255) + fraction * (b[i2] & 255));
		}
	}
}
