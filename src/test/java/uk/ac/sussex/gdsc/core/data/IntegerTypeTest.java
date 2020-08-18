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
 * Copyright (C) 2011 - 2020 Alex Herbert
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

package uk.ac.sussex.gdsc.core.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"javadoc"})
public class IntegerTypeTest {
  @Test
  public void canGenerateIntegerType() {
    final Level level = Level.FINEST;
    // This is not a test. It generates the Enum.
    // It is left to ensure the code will run.

    final StringBuilder sb = new StringBuilder();
    for (int bitDepth = 1; bitDepth <= 64; bitDepth++) {
      add(sb, true, bitDepth);
    }
    for (int bitDepth = 1; bitDepth <= 63; bitDepth++) {
      add(sb, false, bitDepth);
    }
    Logger.getLogger(getClass().getName()).log(level, () -> sb.toString());
  }

  private static void add(StringBuilder sb, boolean signed, int bitDepth) {
    if (signed) {
      sb.append("    /** A signed ").append(bitDepth).append("-bit integer. */\n");
      sb.append("    SIGNED_").append(bitDepth);
      sb.append("(\"Signed ").append(bitDepth).append("-bit integer\", ");
      sb.append(minSigned(bitDepth)).append("L, ");
      sb.append(maxSigned(bitDepth)).append("L, true, ");
    } else {
      sb.append("    /** An unsigned ").append(bitDepth).append("-bit integer */\n");
      sb.append("    UNSIGNED_").append(bitDepth);
      sb.append("(\"Unsigned ").append(bitDepth).append("-bit integer\", 0L, ");
      sb.append(maxUnsigned(bitDepth)).append("L, false, ");
    }
    sb.append(bitDepth).append("),\n");
  }

  @Test
  public void canProvideIntegerTypeData() {
    // This is a problem for 64-bit signed integers.
    Assertions.assertEquals(Long.MIN_VALUE, Math.abs(Long.MIN_VALUE),
        "abs(Long.MIN_VALUE) should should be Long.MIN_VALUE");

    for (final IntegerType type : IntegerType.values()) {
      final int bd = type.getBitDepth();
      Assertions.assertTrue(type.getTypeName().contains(Integer.toString(bd) + "-bit"));
      Assertions.assertEquals(type, IntegerType.forOrdinal(type.ordinal()));

      if (type.isSigned()) {
        // Signed
        Assertions.assertTrue(type.getTypeName().contains("Signed"));
        Assertions.assertEquals(minSigned(bd), type.getMin(), type.getTypeName());
        Assertions.assertEquals(maxSigned(bd), type.getMax(), type.getTypeName());
      } else {
        // Unsigned
        Assertions.assertTrue(type.getTypeName().contains("Unsigned"));
        Assertions.assertEquals(0L, type.getMin(), type.getTypeName());
        Assertions.assertEquals(maxUnsigned(bd), type.getMax(), type.getTypeName());
      }
    }
  }

  private static long maxUnsigned(int bd) {
    long max = 1;
    while (bd-- > 0) {
      max *= 2L;
    }
    return max - 1;
  }

  private static long maxSigned(int bd) {
    long max = 1;
    while (bd-- > 1) {
      max *= 2L;
    }
    return max - 1;
  }

  private static long minSigned(int bd) {
    long max = 1;
    while (bd-- > 1) {
      max *= 2L;
    }
    return -max;
  }
}
