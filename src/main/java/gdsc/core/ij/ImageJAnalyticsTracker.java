/*----------------------------------------------------------------------------- 
 * GDSC Core for ImageJ
 * 
 * Copyright (C) 2016 Alex Herbert
 * Genome Damage and Stability Centre
 * University of Sussex, UK
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *---------------------------------------------------------------------------*/
package gdsc.core.ij;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import gdsc.core.analytics.ClientParameters;
import gdsc.core.analytics.ClientParametersManager;
import gdsc.core.analytics.JGoogleAnalyticsTracker;
import gdsc.core.analytics.JGoogleAnalyticsTracker.DispatchMode;
import gdsc.core.analytics.JGoogleAnalyticsTracker.MeasurementProtocolVersion;
import ij.IJ;
import ij.ImageJ;
import ij.Prefs;
import ij.gui.GenericDialog;

/**
 * Provide a global reference to JGoogleAnalyticsTracker for tracking GDSC ImageJ plugins
 */
public class ImageJAnalyticsTracker
{
	/**
	 * Google Analytics (GA) tracking Id.
	 * <p>
	 * This is the GA property which is used to track all the user activity for
	 * the GDSC ImageJ plugins.
	 * <p>
	 * <b>If you copy this code then please use your own tracking Id!</b>
	 */
	private static final String GA_TRACKING_ID = "UA-74666243-1";
	/**
	 * Google Analytics (GA) application name.
	 * <p>
	 * This is the application name for GA. It doesn't really matter what the value is but it is a required field within
	 * GA.
	 */
	private static final String APPLICATION_NAME = "GDSC ImageJ Plugins";

	// The following constants are used to store user preferences for Google Analytics tracking

	/**
	 * Store the user's client Id. This allows tracking repeat sessions.
	 */
	private static final String PROPERTY_GA_CLIENT_ID = "gdsc.ga.clientId";
	/**
	 * Store the user's opt in/out state. This prevents asking each time they start a new session.
	 */
	private static final String PROPERTY_GA_STATE = "gdsc.ga.state";
	/**
	 * Store the version of the code when the opt in/out state decision was made. This allows us to re-ask the question
	 * if the code has had significant changes.
	 */
	private static final String PROPERTY_GA_LAST_VERSION = "gdsc.ga.lastVersion";
	/**
	 * Store the user's decision to use an anonymous IP address.
	 */
	private static final String PROPERTY_GA_ANONYMIZE = "gdsc.ga.anonymize";
	/**
	 * Disabled state flag
	 */
	private static final int DISABLED = -1;
	/**
	 * Flag to use when the state is unknown
	 */
	private static final int UNKNOWN = 0;
	/**
	 * Enabled state flag
	 */
	private static final int ENABLED = 1;

	private static ClientParameters clientParameters = null;
	private static JGoogleAnalyticsTracker tracker = null;
	private static boolean loggedPreferrences = false;

	/**
	 * Flag indicating that the user has opted in/out of analytics
	 */
	private static int state = (int) Prefs.get(PROPERTY_GA_STATE, UNKNOWN);
	/**
	 * Flag indicating that the IP address of the sender will be anonymized
	 */
	private static int anonymized = (int) Prefs.get(PROPERTY_GA_ANONYMIZE, UNKNOWN);

	/**
	 * Track a page view
	 * 
	 * @param documentPath
	 *            The document path (must not be null)
	 * @param documentTitle
	 *            The document title
	 */
	public static void pageview(String pageUrl, String pageTitle)
	{
		initialiseTracker();
		if (isDisabled())
			return;
		tracker.pageview(pageUrl, pageTitle);
	}

	/**
	 * Create the client parameters for the tracker and populate with information about ImageJ and the system
	 */
	public static void initialise()
	{
		if (clientParameters == null)
		{
			synchronized (ImageJAnalyticsTracker.class)
			{
				// In case another thread was waiting to do this
				if (clientParameters != null)
					return;

				// Get the client parameters
				final String clientId = Prefs.get(PROPERTY_GA_CLIENT_ID, null);
				clientParameters = new ClientParameters(GA_TRACKING_ID, clientId, APPLICATION_NAME);

				ClientParametersManager.populate(clientParameters);

				// Record for next time
				Prefs.set(PROPERTY_GA_CLIENT_ID, clientParameters.getClientId());

				// Record the version of analytics we are using
				clientParameters.setApplicationVersion(gdsc.core.analytics.Version.VERSION_X_X_X);

				// Use custom dimensions to record client data. These should be registered
				// in the analytics account for the given tracking ID

				// Record the ImageJ information.
				clientParameters.addCustomDimension(1, getImageJInfo());

				// Java version
				clientParameters.addCustomDimension(2, System.getProperty("java.version"));

				// OS
				clientParameters.addCustomDimension(3, System.getProperty("os.name"));
				clientParameters.addCustomDimension(4, System.getProperty("os.version"));
				clientParameters.addCustomDimension(5, System.getProperty("os.arch"));
			}
		}
	}

	/**
	 * @return The ImageJ information (Application name and version)
	 */
	private static String getImageJInfo()
	{
		ImageJ ij = IJ.getInstance();
		if (ij == null)
		{
			// Run embedded without showing
			ij = new ImageJ(ImageJ.NO_SHOW);
		}

		// ImageJ version
		// This call should return a string like:
		//   ImageJ 1.48a; Java 1.7.0_11 [64-bit]; Windows 7 6.1; 29MB of 5376MB (<1%)
		// (This should also be different if we are running within Fiji)
		String info = ij.getInfo();
		if (info.indexOf(';') != -1)
			info = info.substring(0, info.indexOf(';'));
		return info;
	}

	/**
	 * Add a custom dimension
	 * <p>
	 * Note that custom dimensions have to be created for your site before they can be used in analytics reports.
	 * 
	 * @see https://support.google.com/analytics/answer/2709829
	 * 
	 * @param index
	 *            The dimension index (1-20 or 1-200 for premium accounts)
	 * @param value
	 *            The dimension value (must not be null)
	 */
	public static void addCustomDimension(int index, String value)
	{
		initialise();
		clientParameters.addCustomDimension(index, value);
	}

	/**
	 * Create the tracker. Call this before sending any requests to Google Analytics.
	 */
	private static void initialiseTracker()
	{
		if (tracker == null)
		{
			synchronized (ImageJAnalyticsTracker.class)
			{
				// Check again since this may be a second thread that was waiting
				if (tracker != null)
					return;

				// Make sure we have created a client
				initialise();

				tracker = new JGoogleAnalyticsTracker(clientParameters, MeasurementProtocolVersion.V_1,
						DispatchMode.SINGLE_THREAD);

				tracker.setAnonymised(isAnonymized());

				// XXX - Disable in production code
				// DEBUG: Enable logging
				//tracker.setLogger(new gdsc.core.analytics.ConsoleLogger());
			}
		}
	}

	/**
	 * Provide a method to read an ImageJ properties file and create the map between the ImageJ plugin class and
	 * argument and the ImageJ menu path and plugin title.
	 * 
	 * @param map
	 *            The map object to populate
	 * @param pluginsStream
	 *            The ImageJ properties file
	 */
	public static void buildPluginMap(HashMap<String, String[]> map, InputStream pluginsStream)
	{
		BufferedReader input = null;
		try
		{
			input = new BufferedReader(new InputStreamReader(pluginsStream));
			String line;
			while ((line = input.readLine()) != null)
			{
				if (line.startsWith("#"))
					continue;
				String[] tokens = line.split(",");
				if (tokens.length == 3)
				{
					// Plugins have [Menu path, Name, class(argument)], e.g.
					// Plugins>GDSC>Colocalisation, "CDA (macro)", gdsc.colocalisation.cda.CDA_Plugin("macro")

					String documentTitle = tokens[1].replaceAll("[\"']", "").trim();
					String documentPath = getDocumentPath(tokens[0], documentTitle);
					String key = getKey(tokens[2]);
					map.put(key, new String[] { documentPath, documentTitle });
				}
			}
		}
		catch (IOException e)
		{
			// Ignore
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}

	}

	/**
	 * Split the menu path string and create a document path
	 * 
	 * @param menuPath
	 *            The ImageJ menu path string
	 * @param documentTitle
	 * @return The document path
	 */
	private static String getDocumentPath(String menuPath, String documentTitle)
	{
		StringBuilder sb = new StringBuilder();
		for (String field : menuPath.split(">"))
		{
			sb.append('/').append(field.trim());
		}
		sb.append('/').append(documentTitle);
		return sb.toString();
	}

	/**
	 * Get the raw class name and string argument from the ImageJ 'org.package.Class("argument")' field
	 * 
	 * @param string
	 *            The field contents
	 * @return The hash key
	 */
	private static String getKey(String string)
	{
		String name = string.trim(), argument = null;
		final int index = name.indexOf('(');
		if (index != -1)
		{
			// Get the remaining text and remove the quotes " and brackets ()
			argument = name.substring(index).replaceAll("[\"'()]", "").trim();
			// Get the class name
			name = name.substring(0, index);
		}
		return getKey(name, argument);
	}

	/**
	 * Get the key used for the given name and argument in the plugin map
	 * 
	 * @param name
	 * @param argument
	 * @return The key
	 */
	public static String getKey(String name, String argument)
	{
		return (argument != null && argument.length() > 0) ? name + '.' + argument : name;
	}

	/**
	 * @return True if analytics is disabled
	 */
	public static boolean isDisabled()
	{
		return (state == DISABLED);
	}

	/**
	 * Set the state of the analytics tracker
	 * 
	 * @param disabled
	 *            True to disable analytics
	 */
	public static void setDisabled(boolean disabled)
	{
		final int oldState = ImageJAnalyticsTracker.state;
		ImageJAnalyticsTracker.state = (disabled) ? DISABLED : ENABLED;

		Prefs.set(PROPERTY_GA_LAST_VERSION, getVersion());

		if (oldState != state)
		{
			Prefs.set(PROPERTY_GA_STATE, state);

			// Record this opt in/out status change

			// If the state was previously unknown and they opt out then do nothing.
			// This is a user who never wants to be tracked.
			if (oldState == UNKNOWN && disabled)
				return;

			// Otherwise record the in/out status change, either:
			// - The user was previously opt in but now opts out; or
			// - The user was previously opt out but now opts in

			final boolean enabled = !disabled;

			initialiseTracker();
			// Reset the session if tracking has been enabled.
			if (enabled)
				tracker.resetSession();
			// Track the opt status change with an event
			tracker.event("Tracking", Boolean.toString(enabled), getVersion(), null);
		}
	}

	/**
	 * @return True if the IP address of the sender will be anonymized
	 */
	public static boolean isAnonymized()
	{
		return (anonymized == ENABLED);
	}

	/**
	 * Set the state of IP anonymization
	 * 
	 * @param anonymize
	 *            True if the IP address of the sender will be anonymized
	 */
	public static void setAnonymized(boolean anonymize)
	{
		final int oldAnonymized = anonymized;
		ImageJAnalyticsTracker.anonymized = (anonymize) ? ENABLED : DISABLED;

		Prefs.set(PROPERTY_GA_LAST_VERSION, getVersion());

		if (oldAnonymized != anonymized)
		{
			Prefs.set(PROPERTY_GA_ANONYMIZE, anonymized);

			// Make sure the tracker is informed
			if (tracker != null)
				tracker.setAnonymised(isAnonymized());
		}
	}

	/**
	 * @return The version of the code
	 */
	private static String getVersion()
	{
		return gdsc.core.analytics.Version.VERSION_X_X;
	}

	/**
	 * Check the opt-in/out status. If it is not known for this version of the analytics code then return true.
	 */
	public static boolean unknownStatus()
	{
		String lastVersion = Prefs.get(PROPERTY_GA_LAST_VERSION, "");
		String thisVersion = getVersion();
		return (state == UNKNOWN || anonymized == UNKNOWN || !lastVersion.equals(thisVersion));
	}

	/**
	 * Show a dialog allowing users to opt in/out of Google Analytics
	 * 
	 * @param title
	 *            The dialog title
	 * @param autoMessage
	 *            Set to true to display the message about automatically showing
	 *            when the status is unknown
	 */
	public static void showDialog(String title, boolean autoMessage)
	{
		GenericDialog gd = new GenericDialog(title);
		// @formatter:off
		gd.addMessage(APPLICATION_NAME + "\n \n" 
				+ "The use of these plugins is free and unconstrained.\n"
				+ "The code uses Google Analytics to help us understand\n" 
				+ "how users are using the plugins.\n \n"
				+ "Privacy Policy\n \n" 
				+ "No personal information or data within ImageJ is recorded.\n \n"
				+ "We record the plugin name and the software running ImageJ.\n"
				+ "This happens in the background when nothing else is active so will\n"
				+ "not slow down ImageJ. IP anonymization will truncate your IP\n"
				+ "address to a region, usually a country. For more details click\n" 
				+ "the Help button.\n \n"
				+ "Click here to opt-out from Google Analytics");
		gd.addHelp("http://www.sussex.ac.uk/gdsc/intranet/microscopy/imagej/tracking");
		
		// Get the preferences
		boolean disabled = isDisabled();
		boolean anonymize = isAnonymized();
		
		gd.addCheckbox("Disabled", disabled);
		gd.addCheckbox("Anonymise IP", anonymize);
		if (autoMessage) {
			gd.addMessage("Note: This dialog is automatically shown if your preferences\n"
					+ "are not known, or after a release that changes the tracking data.");
		}
		// @formatter:on
		gd.hideCancelButton();
		gd.showDialog();

		if (!gd.wasCanceled())
		{
			// This will happen if the user clicks OK. 
			disabled = gd.getNextBoolean();
			anonymize = gd.getNextBoolean();
		}
		else
		{
			// We have hidden the cancel button. 
			// This code will run if:
			// - The user closes the dialog by other means (clicks escape/close window).
			// In this case assume they are happy with the current settings and store 
			// them. This should prevent the dialog being shown again for any code 
			// using the unknownStatus() method.
		}

		// Anonymize first to respect the user choice if they still have tracking on
		setAnonymized(anonymize);
		setDisabled(disabled);
	}

	/**
	 * Write the current user preferences for analytics to the ImageJ log. This log message is written once only unless
	 * the force flag is used.
	 * 
	 * @param force
	 *            Force the preferences to be logged
	 */
	public static void logPreferences(boolean force)
	{
		if (loggedPreferrences && !force)
			return;
		loggedPreferrences = true;
		// Get the preferences
		IJ.log(String.format("%s - Google Analytics settings: Disabled=%b; Anonymise IP=%b", APPLICATION_NAME,
				isDisabled(), isAnonymized()));
		IJ.log("You can change these at any time by running the Usage Tracker plugin");
	}
}
