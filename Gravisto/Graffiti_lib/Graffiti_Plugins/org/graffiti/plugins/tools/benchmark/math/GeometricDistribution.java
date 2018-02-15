package org.graffiti.plugins.tools.benchmark.math;

public class GeometricDistribution extends DiscreteInversionDistribution {
    private double p;

    public GeometricDistribution(int upperBound, double p) {
        super(upperBound);
        this.p = p;
    }

    @Override
    protected double evalDistributionFunction(int value) {
        return 1 - Math.pow(1 - p, value);
    }
}
