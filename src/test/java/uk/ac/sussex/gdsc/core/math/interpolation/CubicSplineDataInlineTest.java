package uk.ac.sussex.gdsc.core.math.interpolation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to in-line the computation for the {@link DoubleCubicSplineData}.
 *
 * <p>The ordering of the computation is set to multiply by the power ZYX.
 */
@SuppressWarnings({"javadoc"})
public class CubicSplineDataInlineTest {
  private static Logger logger;

  @BeforeAll
  public static void beforeAll() {
    logger = Logger.getLogger(CubicSplineDataInlineTest.class.getName());
  }

  @AfterAll
  public static void afterAll() {
    logger = null;
  }

  /** Number of points. */
  private static final String NL = System.lineSeparator();

  /** Number of points. */
  private static final short N = 4;

  /**
   * Used to create the main code for the CubicSplineCoefficients.
   *
   * @return the text.
   */
  String inlineCubicSplineCoefficients() {
    final StringBuilder sb = new StringBuilder();
    try (Formatter formatter = new Formatter(sb)) {

      sb.append(NL);

      Object[] args = new Object[4];
      for (int k = 0, ai = 0; k < N; k++) {
        args[2] = k;
        for (int j = 0; j < N; j++) {
          args[1] = j;
          for (int i = 0; i < N; i++) {
            args[0] = i;
            args[3] = ai++;
            formatter.format("  /** Data for x^%d * y^%d * z^%d (data[%d]). */%n", args);
            formatter.format("  public final double x%dy%dz%d;%n", args);
          }
        }
      }

      // @formatter:off
      sb.append(NL);
      sb.append("  /**").append(NL);
      sb.append("   * Create a new instance of the cubic spline power table.").append(NL);
      sb.append("   * The data represents z^a * y^b * x^c with a,b,c in [0, 3].").append(NL);
      sb.append("   *").append(NL);
      sb.append("   * @param x x-coordinate of the interpolation point.").append(NL);
      sb.append("   * @param y y-coordinate of the interpolation point.").append(NL);
      sb.append("   * @param z z-coordinate of the interpolation point.").append(NL);
      sb.append("   */").append(NL);
      sb.append("  public DoubleCubicSplineData(CubicSplinePosition x, CubicSplinePosition y,").append(NL);
      sb.append("      CubicSplinePosition z) {").append(NL);
      sb.append("    // Table computed as if iterating: z^a * y^b * x^c a,b,c in [0, 3]").append(NL);
      // @formatter:on

      String powerZpowerY;

      for (int k = 0; k < N; k++) {
        for (int j = 0; j < N; j++) {
          powerZpowerY = create_power(formatter, j, k);

          for (int i = 1; i < N; i++) {
            formatter.format("    x%dy%dz%d = %s * x.x%d;%n", i, j, k, powerZpowerY, i);
          }
        }
      }
      sb.append("  }").append(NL);


      // @formatter:off
      sb.append(NL);
      sb.append("  /**").append(NL);
      sb.append("   * Create a new instance of the cubic spline coefficients.").append(NL);
      sb.append("   *").append(NL);
      sb.append("   * <p>Coefficients must be computed as if iterating: z^a * y^b * x^c with a,b,c in [0, 3].").append(NL);
      sb.append("   *").append(NL);
      sb.append("   * @param coefficients the coefficients.").append(NL);
      sb.append("   */").append(NL);
      sb.append("  DoubleCubicSplineData(double[] coefficients) {").append(NL);
      // @formatter:on

      for (int k = 0, ai = 0; k < N; k++) {
        for (int j = 0; j < N; j++) {
          for (int i = 0; i < N; i++) {
            formatter.format("    x%dy%dz%d = coefficients[%d];%n", i, j, k, ai++);
          }
        }
      }
      sb.append("  }").append(NL);

      // @formatter:off
      sb.append(NL);
      sb.append("  /**").append(NL);
      sb.append("   * Create a scaled copy instance.").append(NL);
      sb.append("   *").append(NL);
      sb.append("   * @param source the source.").append(NL);
      sb.append("   * @param scale the scale.").append(NL);
      sb.append("   */").append(NL);
      sb.append("  DoubleCubicSplineData(DoubleCubicSplineData source, int scale) {").append(NL);
      // @formatter:on

      for (int k = 0; k < N; k++) {
        for (int j = 0; j < N; j++) {
          for (int i = 0; i < N; i++) {
            formatter.format("    x%dy%dz%d = source.x%dy%dz%d * scale;%n", i, j, k, i, j, k);
          }
        }
      }
      sb.append("  }").append(NL);
    }

    return finaliseInlineFunction(sb);
  }

  static String create_power(Formatter formatter, int powerY, int powerZ) {
    String x0yYzZ = "x0y" + powerY + "z" + powerZ;
    String powerZpowerY;
    if (powerY == 0) {
      if (powerZ == 0) {
        powerZpowerY = "1";
        formatter.format("    %s = %s;%n", x0yYzZ, powerZpowerY);
      } else {
        powerZpowerY = "z.x" + powerZ;
        formatter.format("    %s = %s;%n", x0yYzZ, powerZpowerY);
      }
    } else if (powerZ == 0) {
      powerZpowerY = "y.x" + powerY;
      formatter.format("    %s = %s;%n", x0yYzZ, powerZpowerY);
    } else {
      powerZpowerY = x0yYzZ;
      formatter.format("    %s = z.x%d * y.x%d;%n", x0yYzZ, powerZ, powerY);
    }
    return powerZpowerY;
  }

  static String finaliseInlineFunction(StringBuilder sb) {
    String result = sb.toString();
    // Replace the use of 1 in multiplications
    result = result.replace("x.x0", "1");
    result = result.replace("y.x0", "1");
    result = result.replace("z.x0", "1");
    result = result.replace(" * 1", "");
    result = result.replace(" 1 *", "");

    return result;
  }

  private final Level level = Level.FINEST;

  @Test
  public void canConstructInlineCubicSplineData() {
    // DoubleCubicSplineData
    Assumptions.assumeTrue(logger.isLoggable(level));
    logger.log(level, inlineCubicSplineCoefficients());
  }
}
