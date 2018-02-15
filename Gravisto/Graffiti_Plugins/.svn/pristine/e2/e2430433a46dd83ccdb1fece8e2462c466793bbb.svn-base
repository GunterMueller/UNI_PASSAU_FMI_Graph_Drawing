package org.graffiti.plugins.tools.benchmark.math;

import java.util.Random;

public class BernoulliDistribution implements Distribution<Boolean> {
    private double p;

    public BernoulliDistribution(double p) {
        this.p = p;
    }

    public Boolean sample(Random random) {
        return random.nextDouble() < p;
    }
}
