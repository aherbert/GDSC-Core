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
package uk.ac.sussex.gdsc.core.logging;

/**
 * Ignore all method calls from the {@link TrackProgress} interface.
 */
public class NullTrackProgress implements TrackProgress
{

	/** An instance to ignore progress reporting. */
	public static final NullTrackProgress INSTANCE = new NullTrackProgress();

	/**
	 * Creates an instance if the argument is null, else return the argument.
	 *
	 * @param trackProgress
	 *            the track progress (may be null)
	 * @return the track progress (not null)
	 */
	public static TrackProgress createIfNull(TrackProgress trackProgress)
	{
		return (trackProgress == null) ? INSTANCE : trackProgress;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gdsc.utils.fitting.results.TrackProgress#progress(double)
	 */
	@Override
	public void progress(double fraction)
	{
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gdsc.utils.fitting.results.TrackProgress#progress(long, long)
	 */
	@Override
	public void progress(long position, long total)
	{
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#incrementProgress(double)
	 */
	@Override
	public void incrementProgress(double fraction)
	{
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#log(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void log(String format, Object... args)
	{
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#status(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void status(String format, Object... args)
	{
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#isEnded()
	 */
	@Override
	public boolean isEnded()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#isProgress()
	 */
	@Override
	public boolean isProgress()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#isLogging()
	 */
	@Override
	public boolean isLog()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see uk.ac.sussex.gdsc.core.logging.TrackProgress#isStatus()
	 */
	@Override
	public boolean isStatus()
	{
		return false;
	}
}