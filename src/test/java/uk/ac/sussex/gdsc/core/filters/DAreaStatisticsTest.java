package uk.ac.sussex.gdsc.core.filters;

import java.awt.Rectangle;
import java.util.logging.Logger;

import org.apache.commons.rng.UniformRandomProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ij.process.FloatProcessor;
import ij.process.ImageStatistics;
import uk.ac.sussex.gdsc.core.utils.Random;
import uk.ac.sussex.gdsc.core.utils.Statistics;
import uk.ac.sussex.gdsc.test.junit5.ExtraAssertions;
import uk.ac.sussex.gdsc.test.junit5.ExtraAssumptions;
import uk.ac.sussex.gdsc.test.junit5.RandomSeed;
import uk.ac.sussex.gdsc.test.junit5.SeededTest;
import uk.ac.sussex.gdsc.test.junit5.SpeedTag;
import uk.ac.sussex.gdsc.test.rng.RNGFactory;
import uk.ac.sussex.gdsc.test.utils.BaseTimingTask;
import uk.ac.sussex.gdsc.test.utils.TestComplexity;
import uk.ac.sussex.gdsc.test.utils.TestLog;
import uk.ac.sussex.gdsc.test.utils.TimingService;

@SuppressWarnings({ "javadoc" })
public class DAreaStatisticsTest
{
    private static Logger logger;

    @BeforeAll
    public static void beforeAll()
    {
        logger = Logger.getLogger(DAreaStatisticsTest.class.getName());
    }

    @AfterAll
    public static void afterAll()
    {
        logger = null;
    }

    boolean[] rolling = new boolean[] { true, false };
    int[] boxSizes = new int[] { 15, 9, 5, 3, 2, 1 };
    int maxx = 97, maxy = 101;

    @SeededTest
    public void canComputeGlobalStatistics(RandomSeed seed)
    {
        final double[] data = createData(RNGFactory.create(seed.getSeed()));
        final Statistics s = new Statistics(data);
        final DAreaStatistics a = new DAreaStatistics(data, maxx, maxy);
        for (final boolean r : rolling)
        {
            a.setRollingSums(r);
            double[] o = a.getStatistics(0, 0, maxy);
            Assertions.assertEquals(s.getN(), o[DAreaSum.N]);
            ExtraAssertions.assertEqualsRelative(s.getSum(), o[DAreaSum.SUM], 1e-6);
            ExtraAssertions.assertEqualsRelative(s.getStandardDeviation(), o[DAreaStatistics.SD], 1e-6);

            o = a.getStatistics(new Rectangle(maxx, maxy));
            Assertions.assertEquals(s.getN(), o[DAreaSum.N]);
            ExtraAssertions.assertEqualsRelative(s.getSum(), o[DAreaSum.SUM], 1e-6);
            ExtraAssertions.assertEqualsRelative(s.getStandardDeviation(), o[DAreaStatistics.SD], 1e-6);
        }
    }

    @SeededTest
    public void canComputeNxNRegionStatistics(RandomSeed seed)
    {
        final UniformRandomProvider r = RNGFactory.create(seed.getSeed());
        final double[] data = createData(r);
        final DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
        a1.setRollingSums(true);
        final DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
        a2.setRollingSums(false);

        final FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

        for (final int x : Random.sample(5, maxx, r))
            for (final int y : Random.sample(5, maxy, r))
                for (final int n : boxSizes)
                {
                    final double[] e = a1.getStatistics(x, y, n);
                    final double[] o = a2.getStatistics(x, y, n);
                    ExtraAssertions.assertArrayEqualsRelative(e, o, 1e-6);
                    //TestLog.debug(logger,"%s vs %s", toString(e), toString(o));

                    // Check with ImageJ
                    fp.setRoi(new Rectangle(x - n, y - n, 2 * n + 1, 2 * n + 1));
                    final ImageStatistics s = fp.getStatistics();

                    Assertions.assertEquals(s.area, o[DAreaSum.N]);
                    final double sum = s.mean * s.area;
                    ExtraAssertions.assertEqualsRelative(sum, o[DAreaSum.SUM], 1e-6);
                    ExtraAssertions.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
                }
    }

    @SeededTest
    public void canComputeNxMRegionStatistics(RandomSeed seed)
    {
        final UniformRandomProvider r = RNGFactory.create(seed.getSeed());
        final double[] data = createData(r);
        final DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
        a1.setRollingSums(true);
        final DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
        a2.setRollingSums(false);

        final FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

        for (final int x : Random.sample(5, maxx, r))
            for (final int y : Random.sample(5, maxy, r))
                for (final int nx : boxSizes)
                    for (final int ny : boxSizes)
                    {
                        final double[] e = a1.getStatistics(x, y, nx, ny);
                        final double[] o = a2.getStatistics(x, y, nx, ny);
                        ExtraAssertions.assertArrayEqualsRelative(e, o, 1e-6);
                        //TestLog.debug(logger,"%s vs %s", toString(e), toString(o));

                        // Check with ImageJ
                        fp.setRoi(new Rectangle(x - nx, y - ny, 2 * nx + 1, 2 * ny + 1));
                        final ImageStatistics s = fp.getStatistics();

                        Assertions.assertEquals(s.area, o[DAreaSum.N]);
                        final double sum = s.mean * s.area;
                        ExtraAssertions.assertEqualsRelative(sum, o[DAreaSum.SUM], 1e-6);
                        ExtraAssertions.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
                    }
    }

    @SeededTest
    public void canComputeRectangleRegionStatistics(RandomSeed seed)
    {
        final UniformRandomProvider r = RNGFactory.create(seed.getSeed());
        final double[] data = createData(r);
        final DAreaStatistics a1 = new DAreaStatistics(data, maxx, maxy);
        a1.setRollingSums(true);
        final DAreaStatistics a2 = new DAreaStatistics(data, maxx, maxy);
        a2.setRollingSums(false);

        final int width = 10, height = 12;
        final Rectangle roi = new Rectangle(width, height);

        final FloatProcessor fp = new FloatProcessor(maxx, maxy, data);

        for (final int x : Random.sample(5, maxx - width, r))
            for (final int y : Random.sample(5, maxy - height, r))
            {
                roi.x = x;
                roi.y = y;
                final double[] e = a1.getStatistics(roi);
                final double[] o = a2.getStatistics(roi);
                ExtraAssertions.assertArrayEqualsRelative(e, o, 1e-6);
                //TestLog.debug(logger,"%s vs %s", toString(e), toString(o));

                // Check with ImageJ
                fp.setRoi(roi);
                final ImageStatistics s = fp.getStatistics();

                Assertions.assertEquals(s.area, o[DAreaSum.N]);
                ExtraAssertions.assertEqualsRelative(s.mean * s.area, o[DAreaSum.SUM], 1e-6);
                ExtraAssertions.assertEqualsRelative(s.stdDev, o[DAreaStatistics.SD], 1e-6);
            }
    }

    @Test
    public void canComputeStatisticsWithinClippedBounds()
    {
        final double[] data = new double[] { 1, 2, 3, 4 };
        final DAreaStatistics a = new DAreaStatistics(data, 2, 2);
        final Statistics stats = new Statistics(data);
        final int c = stats.getN();
        final double u = stats.getSum();
        final double s = stats.getStandardDeviation();
        for (final boolean r : rolling)
        {
            a.setRollingSums(r);
            for (final int n : boxSizes)
            {
                double[] o = a.getStatistics(0, 0, n);
                Assertions.assertEquals(c, o[DAreaSum.N]);
                ExtraAssertions.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
                ExtraAssertions.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);

                final Rectangle bounds = new Rectangle(2 * n + 1, 2 * n + 1);
                o = a.getStatistics(bounds);
                Assertions.assertEquals(c, o[DAreaSum.N]);
                ExtraAssertions.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
                ExtraAssertions.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);

                bounds.x--;
                bounds.y--;
                o = a.getStatistics(bounds);
                Assertions.assertEquals(c, o[DAreaSum.N]);
                ExtraAssertions.assertEqualsRelative(u, o[DAreaSum.SUM], 1e-6);
                ExtraAssertions.assertEqualsRelative(s, o[DAreaStatistics.SD], 1e-6);
            }
        }
    }

    private class MyTimingtask extends BaseTimingTask
    {
        boolean rolling;
        int n;
        double[][] data;
        int[] sample;

        public MyTimingtask(boolean rolling, int n, double[][] data, int[] sample)
        {
            super(((rolling) ? "Rolling" : "Simple") + n);
            this.rolling = rolling;
            this.n = n;
            this.data = data;
            this.sample = sample;
        }

        @Override
        public int getSize()
        {
            return data.length;
        }

        @Override
        public Object getData(int i)
        {
            return data[i];
        }

        @Override
        public Object run(Object data)
        {
            final double[] d = (double[]) data;
            final DAreaStatistics a = new DAreaStatistics(d, maxx, maxy);
            a.setRollingSums(rolling);
            for (int i = 0; i < sample.length; i += 2)
                a.getStatistics(sample[i], sample[i + 1], n);
            return null;
        }
    }

    @SpeedTag
    @SeededTest
    public void simpleIsfasterAtLowDensityAndNLessThan10(RandomSeed seed)
    {
        // Test the speed for computing the noise around spots at a density of roughly 1 / 100 pixels.
        speedTest(seed, 1.0 / 100, false, 1, 10);
    }

    @SpeedTag
    @SeededTest
    public void simpleIsfasterAtMediumDensityAndNLessThan5(RandomSeed seed)
    {
        // Test the speed for computing the noise around each 3x3 box
        // using a region of 3x3 (n=1) to 9x9 (n=4)
        speedTest(seed, 1.0 / 9, false, 1, 4);
    }

    @SpeedTag
    @SeededTest
    public void rollingIsfasterAtHighDensity(RandomSeed seed)
    {
        // Since this is a slow test
        ExtraAssumptions.assume(TestComplexity.MEDIUM);

        // Test for sampling half the pixels. Ignore the very small box size
        speedTest(seed, 0.5, true, 2, Integer.MAX_VALUE);
    }

    private void speedTest(RandomSeed seed, double density, boolean rollingIsFaster, int minN, int maxN)
    {
        final UniformRandomProvider r = RNGFactory.create(seed.getSeed());

        final int k = (int) Math.round(maxx * maxy * density);
        final int[] x = Random.sample(k, maxx, r);
        final int[] y = Random.sample(k, maxy, r);
        final int[] sample = new int[k * 2];
        for (int i = 0, j = 0; i < x.length; i++)
        {
            sample[j++] = x[i];
            sample[j++] = y[i];
        }

        final double[][] data = new double[10][];
        for (int i = 0; i < data.length; i++)
            data[i] = createData(r);

        final TimingService ts = new TimingService();
        for (final int n : boxSizes)
        {
            if (n < minN || n > maxN)
                continue;
            ts.execute(new MyTimingtask(true, n, data, sample));
            ts.execute(new MyTimingtask(false, n, data, sample));
        }
        final int size = ts.getSize();
        ts.repeat();
        logger.info(ts.getReport(size));
        // Do not let this fail the test suite
        //Assertions.assertEquals(ts.get(-2).getMean() < ts.get(-1).getMean(), rollingIsFaster);
        logger.log(TestLog.getResultRecord(ts.get(-2).getMean() < ts.get(-1).getMean() == rollingIsFaster,
                "DAreaStatistics Density=%g RollingIsFaster=%b N=%d:%d: rolling %s vs simple %s", density,
                rollingIsFaster, minN, maxN, ts.get(-2).getMean(), ts.get(-1).getMean()));
    }

    private double[] createData(UniformRandomProvider r)
    {
        final double[] d = new double[maxx * maxy];
        for (int i = 0; i < d.length; i++)
            d[i] = r.nextDouble();
        return d;
    }

    static String toString(double[] d)
    {
        return java.util.Arrays.toString(d);
    }
}
