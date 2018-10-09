package uk.ac.sussex.gdsc.core.math;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;
import org.junit.jupiter.api.*;
import uk.ac.sussex.gdsc.test.api.*;
import uk.ac.sussex.gdsc.test.utils.*;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;
import org.junit.jupiter.api.*;
import uk.ac.sussex.gdsc.test.api.*;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;
import org.junit.jupiter.api.*;

import uk.ac.sussex.gdsc.test.junit5.*;
import uk.ac.sussex.gdsc.test.rng.RngFactory;


import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.ac.sussex.gdsc.test.junit5.*;import uk.ac.sussex.gdsc.test.rng.RngFactory;import uk.ac.sussex.gdsc.core.data.DataException;

@SuppressWarnings({"javadoc"})
public class QuadraticUtilsTest {
  @SeededTest
  public void canGetDeterminant3x3(RandomSeed seed) {
    final UniformRandomProvider r = RngFactory.create(seed.getSeedAsLong());
    for (int i = 0; i < 5; i++) {
      final double[] m = new double[9];
      for (int j = 0; j < 9; j++) {
        m[j] = -5 + r.nextDouble() * 10;
      }

      final double e = QuadraticUtils.getDeterminant3x3(m, 1);
      for (int j = 0; j < 3; j++) {
        final double scale = r.nextDouble() * 100;
        final double o = QuadraticUtils.getDeterminant3x3(m, scale);
        TestAssertions.assertTest(e, o, TestHelper.almostEqualDoubles(1e-6, 0));
      }
    }
  }

  @SeededTest
  public void canSolveQuadratic(RandomSeed seed) {
    final double a = 3;
    final double b = -2;
    final double c = -4;
    final double[] e = new double[] {a, b, c};

    final UniformRandomProvider r = RngFactory.create(seed.getSeedAsLong());
    for (int i = 0; i < 5; i++) {
      // Avoid identical points
      final double x1 = -5 + r.nextDouble() * 10;
      double x2 = -5 + r.nextDouble() * 10;
      while (x2 == x1) {
        x2 = -5 + r.nextDouble() * 10;
      }
      double x3 = -5 + r.nextDouble() * 10;
      while (x3 == x1 || x3 == x2) {
        x3 = -5 + r.nextDouble() * 10;
      }

      // Order invariant
      canSolveQuadratic(a, b, c, e, x1, x2, x3);
      canSolveQuadratic(a, b, c, e, x1, x3, x2);
      canSolveQuadratic(a, b, c, e, x2, x1, x3);
      canSolveQuadratic(a, b, c, e, x2, x3, x1);
      canSolveQuadratic(a, b, c, e, x3, x1, x2);
      canSolveQuadratic(a, b, c, e, x3, x2, x1);
    }
  }

  private static void canSolveQuadratic(double a, double b, double c, double[] e, double x1,
      double x2, double x3) {
    final double[] o = solveQuadratic(a, b, c, x1, x2, x3);
    Assertions.assertNotNull(o);
    TestAssertions.assertArrayTest(e, o, TestHelper.almostEqualDoubles(1e-6, 0));
  }

  private static double[] solveQuadratic(double a, double b, double c, double x1, double x2,
      double x3) {
    final double y1 = a * x1 * x1 + b * x1 + c;
    final double y2 = a * x2 * x2 + b * x2 + c;
    final double y3 = a * x3 * x3 + b * x3 + c;
    return QuadraticUtils.solve(x1, y1, x2, y2, x3, y3);
  }

  @Test
  public void solveUsingColocatedPointsReturnsNull() {
    final double a = 3;
    final double b = -2;
    final double c = -4;
    Assertions.assertNull(solveQuadratic(a, b, c, -1, 0, 0));
    Assertions.assertNull(solveQuadratic(a, b, c, -1, -1, 0));
    Assertions.assertNull(solveQuadratic(a, b, c, 0, -1, 0));
  }

  @Test
  public void canFindMinMaxQuadratic() {
    TestAssertions.assertTest(0, findMinMaxQuadratic(1, 0, 0, -1, 0, 1), TestHelper.almostEqualDoubles(1e-6, 0));
    TestAssertions.assertTest(0, findMinMaxQuadratic(1, 0, -10, -1, 0, 1), TestHelper.almostEqualDoubles(1e-6, 0));
    TestAssertions.assertTest(-1, findMinMaxQuadratic(1, 2, 0, -1, 0, 1), TestHelper.almostEqualDoubles(1e-6, 0));
    TestAssertions.assertTest(-1, findMinMaxQuadratic(1, 2, -10, -1, 0, 1), TestHelper.almostEqualDoubles(1e-6, 0));
  }

  private static double findMinMaxQuadratic(double a, double b, double c, double x1, double x2,
      double x3) {
    final double y1 = a * x1 * x1 + b * x1 + c;
    final double y2 = a * x2 * x2 + b * x2 + c;
    final double y3 = a * x3 * x3 + b * x3 + c;
    return QuadraticUtils.findMinMax(x1, y1, x2, y2, x3, y3);
  }

  @Test
  public void findMinMaxUsingColocatedPointsThrows() {
    final double a = 3;
    final double b = -2;
    final double c = -4;
    Assertions.assertThrows(DataException.class, () -> {
      findMinMaxQuadratic(a, b, c, -1, 0, 0);
    });
  }

  @Test
  public void findMinMaxUsingColinearPointsThrows() {
    final double a = 0;
    final double b = 1;
    final double c = 0;
    Assertions.assertThrows(DataException.class, () -> {
      findMinMaxQuadratic(a, b, c, -1, 0, 1);
    });
  }
}
