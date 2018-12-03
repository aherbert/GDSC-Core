package uk.ac.sussex.gdsc.core.utils;

import uk.ac.sussex.gdsc.test.junit5.RandomSeed;
import uk.ac.sussex.gdsc.test.junit5.SeededTest;
import uk.ac.sussex.gdsc.test.rng.RngUtils;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

@SuppressWarnings({"javadoc"})
public class DigestUtilsTest {
  @SeededTest
  public void canComputeMD5Hex(RandomSeed seed) throws IOException {
    final UniformRandomProvider r = RngUtils.create(seed.getSeedAsLong());

    final byte[] testBytes = new byte[50];

    for (int i = 0; i < 10; i++) {
      final String testString = nextHexString(r, 50);
      Assertions.assertEquals(org.apache.commons.codec.digest.DigestUtils.md5Hex(testString),
          DigestUtils.md5Hex(testString));
      r.nextBytes(testBytes);
      Assertions.assertEquals(org.apache.commons.codec.digest.DigestUtils.md5Hex(testBytes),
          DigestUtils.md5Hex(testBytes));
      Assertions.assertEquals(
          org.apache.commons.codec.digest.DigestUtils.md5Hex(new ByteArrayInputStream(testBytes)),
          DigestUtils.md5Hex(new ByteArrayInputStream(testBytes)));
    }
  }

  /**
   * Generates a random string of hex characters of length len.
   *
   * <p>Adapted from org.apache.commons.math3.random.RandomDataGenerator.
   *
   * @param ran the random provider
   * @param len the len
   * @return the string
   * @throws NotStrictlyPositiveException the not strictly positive exception
   */
  private static String nextHexString(UniformRandomProvider ran, int len)
      throws NotStrictlyPositiveException {
    if (len <= 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
    }

    // Initialize output buffer
    final StringBuilder outBuffer = new StringBuilder();

    // Get int(len/2)+1 random bytes
    final byte[] randomBytes = new byte[(len / 2) + 1];
    ran.nextBytes(randomBytes);

    // Convert each byte to 2 hex digits
    for (int i = 0; i < randomBytes.length; i++) {
      final String hex = Integer.toHexString(randomBytes[i] & 0xFF);

      // Make sure we add 2 hex digits for each byte
      if (hex.length() == 1) {
        outBuffer.append('0');
        outBuffer.append(hex.charAt(0));
      } else {
        outBuffer.append(hex);
      }
    }
    return outBuffer.toString().substring(0, len);
  }

  @Test
  public void canConvertToHexString() {
    byte[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, (byte) 255};
    String expected = "000102030405060708090a0b0c0d0e0f10ff";
    Assertions.assertEquals(expected, DigestUtils.toHex(data));
    Assertions.assertEquals(expected.toUpperCase(Locale.getDefault()),
        DigestUtils.toHex(data, false));
  }

  @Test
  public void getDigestWithBadAlgorithmThrows() {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> DigestUtils.getDigest("this is nonsense"));
  }
}
