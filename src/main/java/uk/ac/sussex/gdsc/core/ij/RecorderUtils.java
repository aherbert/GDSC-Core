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
 * Copyright (C) 2011 - 2019 Alex Herbert
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

package uk.ac.sussex.gdsc.core.ij;

import ij.plugin.frame.Recorder;
import uk.ac.sussex.gdsc.core.utils.TurboList;

/**
 * Contains helper functions for the recorder.
 */
public final class RecorderUtils {

  /** No construction. */
  private RecorderUtils() {}

  /**
   * Reset the recorder for all the named keys.
   *
   * @param keys the keys
   */
  public static void resetRecorder(String[] keys) {
    if (keys == null || keys.length == 0) {
      return;
    }

    // Get the Recorder options, remove all the labels, and update the reduced Recorder options
    final String commandName = Recorder.getCommand();
    final String commandOptions = Recorder.getCommandOptions();
    if (commandName == null || commandOptions == null) {
      return;
    }

    // We only support labels added with
    // Recorder.recordOption(String)
    // Recorder.recordOption(String,String)
    // These will create a key in the command options of:
    // " "+key
    // " "+key+"="+value
    // " "+key+"=["+value+"]"
    boolean ignored = false;
    final TurboList<String[]> pairs = new TurboList<>();
    int current = 0;
    final int len = commandOptions.length();
    while (current < len) {
      // Find the next non-space character, this will be the start of a key
      while (current < len && commandOptions.charAt(current) == ' ') {
        current++;
      }
      if (current == len) {
        break;
      }

      // Find the end of the key.
      // This could be a space or an equals.
      final int keyEnd = findKeyEnd(commandOptions, len, current);

      final String key = commandOptions.substring(current, keyEnd);

      current = keyEnd;

      // Find the value if present
      String value = null;
      if (keyEnd < len && commandOptions.charAt(keyEnd) == '=') {
        // There is a value. This may be surrounded by brackets
        int valueStart = keyEnd + 1;
        int valueEnd;
        if (valueStart < len && commandOptions.charAt(valueStart) == '[') {
          valueStart++;
          valueEnd = commandOptions.indexOf(']', valueStart);
        } else {
          valueEnd = commandOptions.indexOf(' ', valueStart);
        }
        if (valueEnd < 0) {
          valueEnd = len;
        }
        value = commandOptions.substring(valueStart, valueEnd);

        current = valueEnd + 1;
      }

      // Check key should be ignored
      if (ignore(key, keys)) {
        ignored = true;
      } else {
        pairs.add(new String[] {key, value});
      }
    }

    if (!ignored) {
      // No keys to remove
      return;
    }

    // Reset
    Recorder.setCommand(null);
    Recorder.saveCommand();
    // Re-record all the remaining pairs
    Recorder.setCommand(commandName);
    for (int i = 0; i < pairs.size(); i++) {
      final String[] pair = pairs.getf(i);
      final String key = pair[0];
      String value = pair[1];
      if (value == null) {
        Recorder.recordOption(key);
      } else {
        // As per the GenericDialog ensure that empty strings are wrapped
        if (value.isEmpty()) {
          value = "[]";
        }
        Recorder.recordOption(key, value);
      }
    }
  }

  private static int findKeyEnd(String commandOptions, int len, int start) {
    int index = start;
    while (index < len) {
      final char c = commandOptions.charAt(index);
      if (c == ' ' || c == '=') {
        break;
      }
      index++;
    }
    return index;
  }

  private static boolean ignore(String key, String[] keys) {
    for (final String value : keys) {
      if (value.equalsIgnoreCase(key)) {
        return true;
      }
    }
    return false;
  }
}
