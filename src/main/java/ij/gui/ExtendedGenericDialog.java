/*
 * 
 */
package ij.gui;

/*----------------------------------------------------------------------------- 
 * GDSC Plugins for ImageJ
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

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import gdsc.core.ij.RecorderUtils;
import gdsc.core.ij.Utils;
import gdsc.core.utils.TurboList;
import ij.plugin.frame.Recorder;

/**
 * Extension of the ij.gui.GenericDialog class to add functionality
 */
public class ExtendedGenericDialog extends GenericDialog
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2405780565152258007L;
	private final GridBagLayout grid;

	private TurboList<OptionListener<?>> listeners;

	/** The labels. Used to reset the recorder. */
	private TurboList<String> labels = new TurboList<String>();

	/**
	 * Instantiates a new extended generic dialog.
	 *
	 * @param title
	 *            the title
	 * @param parent
	 *            the parent
	 */
	public ExtendedGenericDialog(String title, Frame parent)
	{
		super(title, parent);
		grid = (GridBagLayout) getLayout();
	}

	/**
	 * Instantiates a new extended generic dialog.
	 *
	 * @param title
	 *            the title
	 */
	public ExtendedGenericDialog(String title)
	{
		super(title);
		grid = (GridBagLayout) getLayout();
	}

	// Record all the field names

	@Override
	public void addCheckbox(String label, boolean defaultValue)
	{
		labels.add(label);
		super.addCheckbox(label, defaultValue);
	}

	@Override
	public void addCheckboxGroup(int rows, int columns, String[] labels, boolean[] defaultValues)
	{
		for (String label : labels)
			this.labels.add(label);
		super.addCheckboxGroup(rows, columns, labels, defaultValues);
	}

	@Override
	public void addCheckboxGroup(int rows, int columns, String[] labels, boolean[] defaultValues, String[] headings)
	{
		for (String label : labels)
			this.labels.add(label);
		super.addCheckboxGroup(rows, columns, labels, defaultValues, headings);
	}

	@Override
	public void addChoice(String label, String[] items, String defaultItem)
	{
		labels.add(label);
		super.addChoice(label, items, defaultItem);
	}

	@Override
	public void addNumericField(String label, double defaultValue, int digits)
	{
		labels.add(label);
		super.addNumericField(label, defaultValue, digits);
	}

	@Override
	public void addNumericField(String label, double defaultValue, int digits, int columns, String units)
	{
		labels.add(label);
		super.addNumericField(label, defaultValue, digits, columns, units);
	}

	@Override
	public void addSlider(String label, double minValue, double maxValue, double defaultValue)
	{
		labels.add(label);
		super.addSlider(label, minValue, maxValue, defaultValue);
	}

	@Override
	public void addStringField(String label, String defaultText)
	{
		labels.add(label);
		super.addStringField(label, defaultText);
	}

	@Override
	public void addStringField(String label, String defaultText, int columns)
	{
		labels.add(label);
		super.addStringField(label, defaultText, columns);
	}

	@Override
	public void addTextAreas(String text1, String text2, int rows, int columns)
	{
		labels.add("text1");
		if (text2 != null)
			labels.add("text2");
		super.addTextAreas(text1, text2, rows, columns);
	}

	/**
	 * Adds and then gets a checkbox.
	 *
	 * @param label
	 *            the label
	 * @param defaultValue
	 *            the default value
	 * @return the checkbox
	 */
	public Checkbox addAndGetCheckbox(String label, boolean defaultValue)
	{
		addCheckbox(label, defaultValue);
		return (Checkbox) tail(getCheckboxes());
	}

	/**
	 * Tail.
	 *
	 * @param v
	 *            the v
	 * @return the object
	 */
	private Object tail(Vector<?> v)
	{
		return v.get(v.size() - 1);
	}

	/**
	 * Adds and then gets a choice.
	 *
	 * @param label
	 *            the label
	 * @param items
	 *            the items
	 * @param defaultItem
	 *            the default item
	 * @return the choice
	 */
	public Choice addAndGetChoice(String label, String[] items, String defaultItem)
	{
		addChoice(label, items, defaultItem);
		return (Choice) tail(getChoices());
	}

	/**
	 * Adds and then gets a string field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default text
	 * @return the text field
	 */
	public TextField addAndGetStringField(String label, String defaultText)
	{
		addStringField(label, defaultText);
		return (TextField) tail(getStringFields());
	}

	/**
	 * Adds and then gets a string field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default text
	 * @param columns
	 *            the columns
	 * @return the text field
	 */
	public TextField addAndGetStringField(String label, String defaultText, int columns)
	{
		addStringField(label, defaultText, columns);
		return (TextField) tail(getStringFields());
	}

	/**
	 * Adds and then gets a numeric field.
	 *
	 * @param label
	 *            the label
	 * @param defaultValue
	 *            the default value
	 * @param digits
	 *            the digits
	 * @param columns
	 *            the columns
	 * @param units
	 *            the units
	 * @return the text field
	 */
	public TextField addAndGetNumericField(String label, double defaultValue, int digits, int columns, String units)
	{
		addNumericField(label, defaultValue, digits, columns, units);
		return (TextField) tail(getNumericFields());
	}

	/**
	 * Adds and then gets a numeric field.
	 *
	 * @param label
	 *            the label
	 * @param defaultValue
	 *            the default value
	 * @param digits
	 *            the digits
	 * @return the text field
	 */
	public TextField addAndGetNumericField(String label, double defaultValue, int digits)
	{
		addNumericField(label, defaultValue, digits);
		return (TextField) tail(getNumericFields());
	}

	/**
	 * Adds and then gets a slider.
	 *
	 * @param label
	 *            the label
	 * @param minValue
	 *            the min value
	 * @param maxValue
	 *            the max value
	 * @param defaultValue
	 *            the default value
	 * @return the scrollbar
	 */
	public Scrollbar addAndGetSlider(String label, double minValue, double maxValue, double defaultValue)
	{
		addSlider(label, minValue, maxValue, defaultValue);
		return (Scrollbar) tail(getSliders());
	}

	/**
	 * Adds the button.
	 *
	 * @param label
	 *            the label
	 * @param actionListener
	 *            the action listener (must not be null)
	 * @return the button
	 * @throws NullPointerException
	 *             if the action listener is null
	 */
	public Button addAndGetButton(String label, final ActionListener actionListener)
	{
		if (actionListener == null)
			throw new NullPointerException("Action listener is missing for the button");

		// To make room for the button we add a message and then remove that from the dialog
		addMessage("-- Empty --");

		// Get the message and 'steal' the constraints so we get the current row
		Label msg = (Label) getMessage();
		GridBagConstraints c = grid.getConstraints(msg);

		// Remove the dummy message
		remove(msg);

		// Add a button		
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		Button button = new Button(label);
		button.addActionListener(actionListener);
		buttons.add(button);
		c.gridx = 1;
		//c.gridy = y; // Same as the message label we removed
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		grid.setConstraints(buttons, c);
		add(buttons);

		return button;
	}

	// Add fields as per the normal add methods but have a custom OptionListener interface
	// to call when an options button is pressed. Add a ... button at the end of the row
	// with a mouse over tooltip that calls the option listener

	/**
	 * Invoked to collect options for the field value.
	 */
	public interface OptionListener<T>
	{
		/**
		 * Gets the options using the current value of the field.
		 *
		 * @param field
		 *            the field
		 */
		public void collectOptions(T field);

		/**
		 * Gets the options using the previously read value of the field.
		 * <p>
		 * This will be called when the parent field is read using the appropriate getNext(...) method. It allows macros
		 * to be supported by either recording the options in the Recorder or reading the options from the Macro
		 * options. The simple implementation is to construct an ExtendedGenericDialog to collect the options but do not
		 * present the dialog using the showDialog() method, just proceed direct to reading the fields..
		 */
		public void collectOptions();
	}

	/**
	 * Adds the string field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default text
	 * @param optionListener
	 *            the option listener
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addStringField(String label, String defaultText, final OptionListener<TextField> optionListener)
	{
		addStringField(label, defaultText, 8, optionListener);
	}

	/**
	 * Adds the string field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default text
	 * @param columns
	 *            the columns
	 * @param optionListener
	 *            the option listener
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addStringField(String label, String defaultText, int columns,
			final OptionListener<TextField> optionListener)
	{
		if (optionListener == null)
			throw new NullPointerException("Option listener is null");

		TextField tf = addAndGetStringField(label, defaultText, columns);
		GridBagConstraints c = grid.getConstraints(tf);
		remove(tf);

		add(optionListener);

		JButton button = createOptionButton();
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				optionListener.collectOptions(tf);
			}
		});

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.add(tf);
		panel.add(button);
		grid.setConstraints(panel, c);
		add(panel);
	}

	private JButton createOptionButton()
	{
		return createOptionButton("Extra options");
	}

	private JButton createOptionButton(String tooltip)
	{
		JButton button = new JButton("...");
		button.setToolTipText(tooltip);
		button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(1),
				// For some reason an extra 2 pixels looks correct on a mac
				BorderFactory.createEmptyBorder(1, 1, 1, 3)));
		return button;
	}

	private int add(OptionListener<?> optionListener)
	{
		if (listeners == null)
		{
			listeners = new TurboList<>();
		}
		int id = listeners.size();
		listeners.add(optionListener);
		return id;
	}

	/**
	 * Adds the filename field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default filename
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addFilenameField(String label, String defaultText)
	{
		addFilenameField(label, defaultText, 30);
	}

	/**
	 * Adds the filename field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default filename
	 * @param columns
	 *            the columns
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addFilenameField(String label, String defaultText, int columns)
	{
		TextField tf = addAndGetStringField(label, defaultText, columns);
		GridBagConstraints c = grid.getConstraints(tf);
		remove(tf);

		JButton button = createOptionButton("Select a file");
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean record = Recorder.record;
				String filename = Utils.getFilename(label, tf.getText());
				Recorder.record = record;
				if (filename != null)
					tf.setText(filename);
			}
		});

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.add(tf);
		panel.add(button);
		grid.setConstraints(panel, c);
		add(panel);
	}

	/**
	 * Adds the directory field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default directory
	 * @param columns
	 *            the columns
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addDirectoryField(String label, String defaultText)
	{
		addDirectoryField(label, defaultText, 30);
	}

	/**
	 * Adds the directory field.
	 *
	 * @param label
	 *            the label
	 * @param defaultText
	 *            the default directory
	 * @param columns
	 *            the columns
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addDirectoryField(String label, String defaultText, int columns)
	{
		TextField tf = addAndGetStringField(label, defaultText, columns);
		GridBagConstraints c = grid.getConstraints(tf);
		remove(tf);

		JButton button = createOptionButton("Select a directory");
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean record = Recorder.record;
				String filename = Utils.getDirectory(label, tf.getText());
				Recorder.record = record;
				if (filename != null)
					tf.setText(filename);
			}
		});

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.add(tf);
		panel.add(button);
		grid.setConstraints(panel, c);
		add(panel);
	}

	/**
	 * Adds the choice.
	 *
	 * @param label
	 *            the label
	 * @param items
	 *            the items
	 * @param defaultItem
	 *            the default item
	 * @param optionListener
	 *            the option listener
	 * @throws NullPointerException
	 *             if the option lister is null
	 */
	public void addChoice(String label, String[] items, String defaultItem, final OptionListener<Choice> optionListener)
	{
		if (optionListener == null)
			throw new NullPointerException("Option listener is null");

		Choice choice = addAndGetChoice(label, items, defaultItem);
		GridBagConstraints c = grid.getConstraints(choice);
		remove(choice);

		add(optionListener);

		JButton button = createOptionButton();
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				optionListener.collectOptions(choice);
			}
		});

		Panel panel = new Panel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.add(choice);
		panel.add(button);
		grid.setConstraints(panel, c);
		add(panel);
	}

	/**
	 * Reset the recorder for all the named fields that have been added to the dialog. This should be called if the
	 * dialog is to be reused as repeat calls to getNext(...) for fields with the same name will call ImageJ to show a
	 * duplicate field error.
	 */
	public void resetRecorder()
	{
		RecorderUtils.resetRecorder(labels.toArray(new String[labels.size()]));
	}

	/**
	 * Show the dialog.
	 *
	 * @param resetRecorder
	 *            Set to true to reset the recorder for all the named fields that have been added to the dialog.
	 * @see ij.gui.GenericDialog#showDialog()
	 */
	public void showDialog(boolean resetRecorder)
	{
		if (resetRecorder)
			resetRecorder();
		super.showDialog();
	}

	/**
	 * Collect the options from all the option listeners silently. Calls all the listeners since the value may have been
	 * changed since they were last called interactively.
	 * <p>
	 * This should be called after all the fields have been read. This allows the fields to be read correctly from Macro
	 * option arguments. It also allows the options to be recorded to the Recorder.
	 * <p>
	 * This method does nothing if the Recorder is disabled or this is not running in a macro, i.e. there is no point
	 * collecting options again.
	 */
	public void collectOptions()
	{
		if (listeners == null)
			return;
		if (!(Recorder.record || Utils.isMacro()))
			return;
		for (int i = 0; i < listeners.size(); i++)
		{
			listeners.getf(i).collectOptions();
		}
	}
}
