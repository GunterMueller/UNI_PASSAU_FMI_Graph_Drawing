package org.graffiti.plugins.tools.benchmark.math;

import java.math.BigInteger;
import java.util.Random;

public class DiscreteUniformDistribution<T> implements Distribution<T> {
    private FiniteSet<T> set;
    private int bitLength;

    public DiscreteUniformDistribution(FiniteSet<T> set) {
        this.set = set;
        bitLength = set.getSize().bitLength();
    }

    public T sample(Random random) {
        if (bitLength < 62)
            return set.get(BigInteger.valueOf(Math.abs(random.nextLong())
                    % set.getSize().longValue()));
        else {
            // Runtime is geometrically distributed!
            BigInteger upper = set.getSize();
            BigInteger value;
            do {
                value = new BigInteger(bitLength, random);
            } while (value.compareTo(upper) < 0);
            return set.get(value);
        }
    }
}
