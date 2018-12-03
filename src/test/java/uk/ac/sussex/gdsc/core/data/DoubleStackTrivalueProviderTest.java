package uk.ac.sussex.gdsc.core.data;

import uk.ac.sussex.gdsc.core.utils.SimpleArrayUtils;

import ij.process.FloatProcessor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"javadoc"})
public class DoubleStackTrivalueProviderTest {
  @SuppressWarnings("null")
  @Test
  public void canProvideData() {
    final int maxx = 5;
    final int maxy = 4;
    final int maxz = 3;
    final int size = maxx * maxy;
    final double[][] data = new double[maxz][];
    for (int z = 0; z < maxz; z++) {
      data[z] = SimpleArrayUtils.newArray(size, z, 1.0);
    }

    final DoubleStackTrivalueProvider f = new DoubleStackTrivalueProvider(data, maxx, maxy);

    final double[][][] values = new double[3][3][3];

    final int[] test = {-1, 0, 1};

    // Test with FloatProcessor as that is the likely source of the stack of data
    for (int z = 0; z < maxz; z++) {
      final FloatProcessor fp = new FloatProcessor(maxx, maxy, data[z]);
      FloatProcessor fpp = null;
      FloatProcessor fpn = null;
      if (z > 0 && z < maxz - 1) {
        fpp = new FloatProcessor(maxx, maxy, data[z - 1]);
        fpn = new FloatProcessor(maxx, maxy, data[z + 1]);
      }

      for (int y = 0; y < maxy; y++) {
        for (int x = 0; x < maxx; x++) {
          Assertions.assertEquals(fp.getPixelValue(x, y), f.get(x, y, z));

          if (x > 0 && x < maxx - 1 && y > 0 && y < maxy - 1 && fpp != null) {
            f.get(x, y, z, values);

            for (final int i : test) {
              for (final int j : test) {
                Assertions.assertEquals(fpp.getPixelValue(x + i, y + j), values[i + 1][j + 1][0]);
                Assertions.assertEquals(fp.getPixelValue(x + i, y + j), values[i + 1][j + 1][1]);
                Assertions.assertEquals(fpn.getPixelValue(x + i, y + j), values[i + 1][j + 1][2]);
              }
            }
          }
        }
      }
    }
  }

  @Test
  public void canConvertToArray() {
    final int maxx = 5;
    final int maxy = 4;
    final int maxz = 3;
    final int size = maxx * maxy;
    final double[][] data = new double[maxz][];
    for (int z = 0; z < maxz; z++) {
      data[z] = SimpleArrayUtils.newArray(size, z, (z + 1) * 2.0);
    }
    final DoubleStackTrivalueProvider f = new DoubleStackTrivalueProvider(data, maxx, maxy);
    final double[][][] e = new double[maxx][maxy][maxz];
    for (int x = 0; x < maxx; x++) {
      for (int y = 0; y < maxy; y++) {
        for (int z = 0; z < maxz; z++) {
          e[x][y][z] = f.get(x, y, z);
        }
      }
    }
    final double[][][] o = f.toArray();
    for (int x = 0; x < maxx; x++) {
      for (int y = 0; y < maxy; y++) {
        for (int z = 0; z < maxz; z++) {
          Assertions.assertEquals(e[x][y][z], o[x][y][z]);
        }
      }
    }
  }
}
