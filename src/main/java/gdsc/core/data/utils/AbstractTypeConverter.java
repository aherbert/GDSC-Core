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
 * Base class for converters
 */
public abstract class AbstractTypeConverter<T> implements TypeConverter<T>
{
	private final T from, to;

	/**
	 * Instantiates a new abstract unit converter.
	 *
	 * @param from
	 *            unit to convert from
	 * @param to
	 *            unit to convert to
	 * @throws ConversionException
	 *             If the input units are null
	 */
	public AbstractTypeConverter(T from, T to)
	{
		if (from == null)
			throw new ConversionException("From unit is null");
		if (to == null)
			throw new ConversionException("To unit is null");
		this.from = from;
		this.to = to;
	}

	/**
	 * Instantiates a new abstract unit converter.
	 *
	 * @param from
	 *            unit to convert from
	 * @param to
	 *            unit to convert to
	 * @param suppressExceptions
	 *            the suppress exceptions flag
	 * @throws ConversionException
	 *             If the input units are null (and exception are not suppressed)
	 */
	AbstractTypeConverter(T from, T to, boolean suppressExceptions)
	{
		if (from == null && !suppressExceptions)
			throw new ConversionException("From unit is null");
		if (to == null && !suppressExceptions)
			throw new ConversionException("To unit is null");
		this.from = from;
		this.to = to;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.smlm.data.utils.Converter#convert(float)
	 */
	public float convert(float value)
	{
		return (float) convert((double) value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.data.utils.Converter#convertBack(float)
	 */
	public float convertBack(float value)
	{
		return (float) convertBack((double) value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.smlm.units.UnitConverter#from()
	 */
	public T from()
	{
		return from;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.smlm.units.UnitConverter#to()
	 */
	public T to()
	{
		return to;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return to + " = f(x=" + from + ") = " + getFunction();
	}
}