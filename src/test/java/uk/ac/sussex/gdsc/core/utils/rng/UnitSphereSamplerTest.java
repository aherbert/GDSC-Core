package uk.ac.sussex.gdsc.core.utils.rng;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.UnitSphereSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

@SuppressWarnings("javadoc")
public class UnitSphereSamplerTest {

    @Test
    public void testZeroLength() {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.MWC_256);
        UnitSphereSampler s = new UnitSphereSampler(0, rng);
        Assertions.assertArrayEquals(new double[0], s.nextVector());
    }
    
    @Test
    public void testNegativeLength() {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.MWC_256);
        UnitSphereSampler s = new UnitSphereSampler(-1, rng);
        Assertions.assertThrows(NegativeArraySizeException.class, () -> {
            s.nextVector();
        });
    }

    //@formatter:off
    @Test
    public void testBadRNG() {
        final UniformRandomProvider rng = new UniformRandomProvider() {
            @Override
            public long nextLong(long n) { return 0; }
            @Override
            public long nextLong() { return 0; }
            @Override
            public int nextInt(int n) { return 0; }
            @Override
            public int nextInt() { return 0; }
            @Override
            public float nextFloat() { return 0; }
            @Override
            public double nextDouble() { return 0;}
            @Override
            public void nextBytes(byte[] bytes, int start, int len) {}
            @Override
            public void nextBytes(byte[] bytes) {}
            @Override
            public boolean nextBoolean() { return false; }
        };
        UnitSphereSampler s = new UnitSphereSampler(1, rng);
        double[] v = s.nextVector();
        Assertions.assertNotNull(v);
        Assertions.assertEquals(1, v.length);
        Assertions.assertArrayEquals(new double[] { Double.NaN }, v);
        Assertions.assertTrue(Double.isNaN(v[0]));
    }
    //@formatter:on
}
