package uk.ac.sussex.gdsc.core.math.interpolation;

import org.junit.jupiter.api.Test;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import uk.ac.sussex.gdsc.test.junit5.ExtraAssertions;

@SuppressWarnings({ "javadoc" })
public class CachedBicubicInterpolatorTest {
    @Test
    public void testGetValue() {

        // Test data
        final float[] fdata = new float[4 * 4];
        final float[][] ffdata = new float[4][4];
        final double[] ddata = new double[4 * 4];
        final double[][] dddata = new double[4][4];
        for (int y = 0, i = 0; y < 4; y++)
            for (int x = 0; x < 4; x++, i++) {
                fdata[i] = i;
                ddata[i] = i;
                ffdata[x][y] = i;
                dddata[x][y] = i;
            }
        final FloatProcessor fp = new FloatProcessor(4, 4, fdata);
        fp.setInterpolationMethod(ImageProcessor.BICUBIC);

        final double[] x = new double[] { 0.3, 0.5, 0.7 };
        final double[] x2 = new double[x.length];
        final double[] x3 = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x2[i] = x[i] * x[i];
            x3[i] = x[i] * x[i] * x[i];
        }

        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < x.length; j++) {

                final double e = fp.getInterpolatedPixel(1 + x[i], 1 + x[j]);

                CachedBicubicInterpolator in = new CachedBicubicInterpolator();
                in.updateCoefficients(fdata);

                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x[j]), 1e-5);
                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x2[i], x3[i], x[j], x2[j], x3[j]), 1e-5);

                in = new CachedBicubicInterpolator();
                in.updateCoefficients(ddata);

                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x[j]), 1e-5);
                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x2[i], x3[i], x[j], x2[j], x3[j]), 1e-5);

                in = new CachedBicubicInterpolator();
                in.updateCoefficients(ffdata);

                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x[j]), 1e-5);
                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x2[i], x3[i], x[j], x2[j], x3[j]), 1e-5);

                in = new CachedBicubicInterpolator();
                in.updateCoefficients(dddata);

                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x[j]), 1e-5);
                ExtraAssertions.assertEqualsRelative(e, in.getValue(x[i], x2[i], x3[i], x[j], x2[j], x3[j]), 1e-5);
            }
    }
}
