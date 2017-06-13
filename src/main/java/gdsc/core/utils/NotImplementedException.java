package gdsc.core.utils;

/*----------------------------------------------------------------------------- 
 * GDSC Software
 * 
 * Copyright (C) 2014 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/

/**
 * Exception to throw if a method is not implemented
 */
public class NotImplementedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NotImplementedException()
	{
		super();
	}

	public NotImplementedException(String message)
	{
		super(message);
	}

	public NotImplementedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NotImplementedException(Throwable cause)
	{
		super(cause);
	}
}
