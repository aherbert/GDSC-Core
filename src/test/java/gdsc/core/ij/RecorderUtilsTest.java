package gdsc.core.ij;

import org.junit.Assert;
import org.junit.Test;

import ij.plugin.frame.Recorder;

public class RecorderUtilsTest
{
	// We need an instance to allow recording
	Recorder recorder = new Recorder(false);
	
	@Test
	public void canResetRecorder()
	{
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("", ""), null);
	}

	@Test
	public void canResetRecorderWithQuotedValues()
	{
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2 2"), toArray("c", "d"), toArray("", ""), null);

		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", "2"), toArray("c", "d"), toArray("", ""), null);

		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1 1", ""), toArray("c", "d"), toArray("", ""), null);

		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("1", "2 2"), toArray("c", "d"), toArray("", ""), null);

		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", "2 2"), toArray("c", "d"), toArray("", ""), null);

		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("3 3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("3 3", "4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("3 3", ""), null);
		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("3", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("", "4 4"), null);
		canResetRecorder(toArray("a", "b"), toArray("", ""), toArray("c", "d"), toArray("", ""), null);
	}

	@Test
	public void resetRecorderIgnoresInvalidKeys()
	{
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), toArray("e", "f"));
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), toArray("e", "f"));
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", ""), toArray("e", "f"));
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("3", "4"), toArray("e", "f"));
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("", "4"), toArray("e", "f"));
		canResetRecorder(toArray("a", "b"), toArray("1", "2"), toArray("c", "d"), toArray("", ""), toArray("e", "f"));
	}

	private String[] toArray(String... values)
	{
		return values;
	}

	private void canResetRecorder(String[] keys1, String[] values1, String[] keys2, String[] values2, String[] badKeys)
	{
		clearRecorder();
		record(keys1, values1);
		String e1 = Recorder.getCommandOptions();
		clearRecorder();
		record(keys2, values2);
		String e2 = Recorder.getCommandOptions();
		clearRecorder();
		record(keys1, values1);
		record(keys2, values2);
		String e3 = Recorder.getCommandOptions();
		RecorderUtils.resetRecorder(keys2);
		String o1 = Recorder.getCommandOptions();
		Assert.assertNotEquals("-keys2 did not change", e3, o1);
		Assert.assertEquals("-keys2", e1, o1);
		RecorderUtils.resetRecorder(badKeys);
		String o1b = Recorder.getCommandOptions();
		Assert.assertEquals("-badkeys2", o1, o1b);
		clearRecorder();
		record(keys1, values1);
		record(keys2, values2);
		RecorderUtils.resetRecorder(keys1);
		String o2 = Recorder.getCommandOptions();
		Assert.assertNotEquals("-keys1 did not change", e3, o2);
		Assert.assertEquals("-keys1", e2, o2);
		RecorderUtils.resetRecorder(badKeys);
		String o2b = Recorder.getCommandOptions();
		Assert.assertEquals("-badkeys1", o2, o2b);
	}

	private void clearRecorder()
	{
		Recorder.saveCommand();
		Recorder.setCommand("Test");
	}

	private void record(String[] keys1, String[] values1)
	{
		for (int i = 0; i < keys1.length; i++)
		{
			if (values1[i] != "")
				Recorder.recordOption(keys1[i], values1[i]);
			else
				Recorder.recordOption(keys1[i]);
		}
	}
}