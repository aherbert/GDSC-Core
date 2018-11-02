package uk.ac.sussex.gdsc.core.math.interpolation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"javadoc"})
public class ScaledIndexedCubicSplinePositionTest {
  // Note: Avoids testing the super-class methods again. Only those new to this
  // class.

  @Test
  public void testConstructor() {
    final int index = 0;
    final double scale = 2;
    final double x = 0.5;
    Assertions.assertNotNull(new ScaledIndexedCubicSplinePosition(index, x, scale));

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      @SuppressWarnings("unused")
      final ScaledIndexedCubicSplinePosition p = new ScaledIndexedCubicSplinePosition(index, x, 0);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      @SuppressWarnings("unused")
      final ScaledIndexedCubicSplinePosition p =
          new ScaledIndexedCubicSplinePosition(index, x, -1e-6);
    });
  }

  @Test
  public void testProperties() {
    final int index = 0;
    final double x = 0.5;
    for (int i = 1; i <= 5; i++) {
      final double scale = i * 0.5;
      final ScaledIndexedCubicSplinePosition p =
          new ScaledIndexedCubicSplinePosition(index, x, scale);
      Assertions.assertNotNull(p);
      Assertions.assertEquals(scale, p.getScaleFactor());
      Assertions.assertEquals(1 * scale, p.scale(1));
      Assertions.assertEquals(1 / scale, p.scaleGradient(1));
      Assertions.assertEquals(1 / scale / scale, p.scaleGradient2(1));
    }
  }
}
