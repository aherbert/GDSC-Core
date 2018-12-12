package uk.ac.sussex.gdsc.core.utils;

import uk.ac.sussex.gdsc.test.utils.functions.IndexSupplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"javadoc"})
public class TriangleArrayTest {
  final int[] testN = new int[] {0, 1, 2, 5};

  @Test
  public void canComputeIndex() {
    for (final int n : testN) {
      final TriangleArray a = new TriangleArray(n);

      final int[] count = new int[a.getLength()];
      int[] ij = new int[2];

      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          final int k = a.toIndex(i, j);
          count[k]++;

          ij = a.fromIndex(k);
          Assertions.assertEquals(i, ij[0], () -> "fromIndex(int) " + k);
          Assertions.assertEquals(j, ij[1], () -> "fromIndex(int) " + k);

          a.fromIndex(k, ij);
          Assertions.assertEquals(i, ij[0], () -> "fromIndex(int,int[]) " + k);
          Assertions.assertEquals(j, ij[1], () -> "fromIndex(int,int[]) " + k);

          ij = TriangleArray.fromIndex(n, k);
          Assertions.assertEquals(i, ij[0], () -> "static fromIndex(int) " + k);
          Assertions.assertEquals(j, ij[1], () -> "static fromIndex(int) " + k);

          TriangleArray.fromIndex(n, k, ij);
          Assertions.assertEquals(i, ij[0], () -> "static fromIndex(int,int[]) " + k);
          Assertions.assertEquals(j, ij[1], () -> "static fromIndex(int,int[]) " + k);
        }
      }
      for (int i = count.length; i-- > 0;) {
        Assertions.assertEquals(1, count[i], "count");
      }
    }
  }

  @Test
  public void indexNotReversible() {
    final int n = 10;
    final TriangleArray a = new TriangleArray(n);

    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        final int k = a.toIndex(i, j);
        final int k2 = a.toIndex(j, i);
        if (k == k2) {
          continue;
        }
        return;
      }
    }

    Assertions.fail();
  }

  @Test
  public void safeIndexIsReversible() {
    final int n = 10;
    final TriangleArray a = new TriangleArray(n);

    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        final int k = a.toIndex(i, j);
        final int k2 = a.toSafeIndex(j, i);
        Assertions.assertEquals(k, k2);
      }
    }
  }

  @Test
  public void canFastComputePostIndex() {
    final IndexSupplier msg = new IndexSupplier(2);
    for (final int n : testN) {
      final TriangleArray a = new TriangleArray(n);

      for (int i = 0; i < n; i++) {
        msg.set(0, i);
        for (int j = i + 1, index = a.toIndex(i, j); j < n; j++, index++) {
          final int k = a.toIndex(i, j);
          Assertions.assertEquals(k, index, msg.set(1, j));
        }
      }
    }
  }

  @Test
  public void canFastComputePreIndex() {
    final IndexSupplier msg = new IndexSupplier(2);
    for (final int n : testN) {
      final TriangleArray a = new TriangleArray(n);

      for (int j = n; j-- > 0;) {
        msg.set(1, j);
        for (int i = j, index = a.toPrecursorIndex(j); i-- > 0;) {
          final int k = a.toIndex(i, j);
          final int k2 = a.precursorToIndex(index, i);
          Assertions.assertEquals(k, k2, msg.set(0, i));
        }
      }
    }
  }

  @Test
  public void canFastIterateNxN() {
    for (final int n : testN) {
      final TriangleArray a = new TriangleArray(n);

      for (int i = 0; i < n; i++) {
        for (int j = 0, precursor = a.toPrecursorIndex(i); j < i; j++) {
          final int k = a.toSafeIndex(i, j);
          final int k2 = a.precursorToIndex(precursor, j);
          Assertions.assertEquals(k, k2);
        }
        for (int j = i + 1, index = a.toIndex(i, j); j < n; j++, index++) {
          final int k = a.toSafeIndex(i, j);
          Assertions.assertEquals(k, index);
        }
      }
    }
  }

  @Test
  public void canCompareItoAnyJ() {
    for (final int n : testN) {
      final TriangleArray a = new TriangleArray(n);

      for (int i = 0; i < n; i++) {
        a.setup(i);
        for (int j = 0; j < n; j++) {
          if (i == j) {
            continue;
          }
          final int k = a.toSafeIndex(i, j);
          final int k2 = a.toIndex(j);
          Assertions.assertEquals(k, k2);
        }
      }
    }
  }
}
