package gdsc.core.ij;

import ij.IJ;

/**
 * Log to the ImageJ log window
 */
public class IJLogger implements gdsc.core.logging.Logger
{
	public boolean showDebug = false;
	public boolean showError = true;

	public IJLogger()
	{
	}

	public IJLogger(boolean showDebug, boolean showError)
	{
		this.showDebug = showDebug;
		this.showError = showError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.smlm.fitting.logging.Logger#info(java.lang.String)
	 */
	public void info(String message)
	{
		IJ.log(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gdsc.smlm.fitting.logging.Logger#info(java.lang.String, java.lang.Object[])
	 */
	public void info(String format, Object... args)
	{
		IJ.log(String.format(format, args));
	}

	public void debug(String message)
	{
		if (showDebug)
			info(message);
	}

	public void debug(String format, Object... args)
	{
		if (showDebug)
			info(format, args);
	}

	public void error(String message)
	{
		if (showError)
			info(message);
	}

	public void error(String format, Object... args)
	{
		if (showError)
			info(format, args);
	}
}