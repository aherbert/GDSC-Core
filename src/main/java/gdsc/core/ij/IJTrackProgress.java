package gdsc.core.ij;

import gdsc.core.logging.TrackProgress;
import ij.IJ;

/**
 * Report the progress of processing results
 */
public class IJTrackProgress implements TrackProgress
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.utils.fitting.results.TrackProgress#progress(double)
	 */
	public void progress(double fraction)
	{
		IJ.showProgress(fraction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.utils.fitting.results.TrackProgress#progress(long, long)
	 */
	public void progress(long position, long total)
	{
		IJ.showProgress((double) position / total);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.logging.TrackProgress#log(java.lang.String, java.lang.Object[])
	 */
	public void log(String format, Object... args)
	{
		IJ.log(String.format(format, args));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.logging.TrackProgress#status(java.lang.String, java.lang.Object[])
	 */
	public void status(String format, Object... args)
	{
		IJ.showStatus(String.format(format, args));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.core.logging.TrackProgress#isEnded()
	 */
	public boolean isEnded()
	{
		return Utils.isInterrupted();
	}
}
