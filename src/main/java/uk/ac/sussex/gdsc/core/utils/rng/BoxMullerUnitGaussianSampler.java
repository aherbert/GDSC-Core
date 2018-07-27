/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.sussex.gdsc.core.utils.rng;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.SamplerBase;

//@formatter:off

/**
 * <a href="https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform">
 * Box-Muller algorithm</a> for sampling from a Gaussian distribution.
 * <p>
 * This is a copy implementation of 
 * {@link org.apache.commons.rng.sampling.distribution.BoxMullerGaussianSampler} with
 * the mean and standard deviation removed.

 * @deprecated It will be superceded when V1.1 of the library is released by implementations of the 
 * {@code NormalizedGaussianSampler} interface.
 */
@Deprecated
public class BoxMullerUnitGaussianSampler
    extends SamplerBase
    implements ContinuousSampler {
    /** Next gaussian. */
    private double nextGaussian = Double.NaN;

    /**
     * @param rng Generator of uniformly distributed random numbers.
     */
    public BoxMullerUnitGaussianSampler(UniformRandomProvider rng) {
        super(rng);
    }

    /** {@inheritDoc} */
    @Override
    public double sample() {
        final double random;
        if (Double.isNaN(nextGaussian)) {
            // Generate a pair of Gaussian numbers.

            final double x = nextDouble();
            final double y = nextDouble();
            final double alpha = 2 * Math.PI * x;
            final double r = Math.sqrt(-2 * Math.log(y));

            // Return the first element of the generated pair.
            random = r * Math.cos(alpha);

            // Keep second element of the pair for next invocation.
            nextGaussian = r * Math.sin(alpha);
        } else {
            // Use the second element of the pair (generated at the
            // previous invocation).
            random = nextGaussian;

            // Both elements of the pair have been used.
            nextGaussian = Double.NaN;
        }

        return random;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Box-Muller Unit Gaussian deviate [" + super.toString() + "]";
    }
}
