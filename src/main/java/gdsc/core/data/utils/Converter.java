package gdsc.core.data.utils;

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

/**
 * Define conversion of a value
 */
public interface Converter
{
	/**
	 * Convert the value.
	 *
	 * @param value
	 *            the value
	 * @return the new value
	 */
	public double convert(double value);
	
	/**
	 * Convert the value.
	 *
	 * @param value
	 *            the value
	 * @return the new value
	 */
	public float convert(float value);

	/**
	 * Convert the value back. This performs the opposite of {@link #convert(double)}.
	 *
	 * @param value
	 *            the value
	 * @return the new value
	 */
	public double convertBack(double value);
	
	/**
	 * Convert the value back. This performs the opposite of {@link #convert(float)}.
	 *
	 * @param value
	 *            the value
	 * @return the new value
	 */
	public float convertBack(float value);
	
	/**
	 * Gets the conversion function, f(x). The function should represent what conversion is performed on the function
	 * input value x.
	 *
	 * @return the function
	 */
	public String getFunction();
}