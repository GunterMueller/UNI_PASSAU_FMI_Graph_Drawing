package org.graffiti.plugins.tools.benchmark.math;

import java.util.Random;

/**
 * Probability distribution simulated by the inversion of a distribution
 * function.
 * 
 * @author Andreas Glei&szlig;ner
 * @see DistributionFunction
 */
public class DiscreteInversionDistribution extends BinarySearch implements
        Distribution<Integer> {
    private DistributionFunction<Integer> distributionFunction;

    private int upperBound;

    private double u;

    /**
     * @param upperBound
     *            the upper bound, which must be less than or equal to {@code
     *            Integer.MAX_VALUE / 2}.
     */
    protected DiscreteInversionDistribution(int upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * @param upperBound
     *            the upper bound, which must be less than or equal to {@code
     *            Integer.MAX_VALUE / 2}.
     */
    public DiscreteInversionDistribution(int upperBound,
            DistributionFunction<Integer> distributionFunction) {
        this.upperBound = upperBound;
        this.distributionFunction = distributionFunction;
    }

    /**
     * Returns the probability that the random variable is less then or equal to
     * the specified value.
     * 
     * @param value
     *            the value.
     * @return the probability that the random variable is less then or equal to
     *         the specified value.
     */
    protected double evalDistributionFunction(int value) {
        return distributionFunction.eval(value);
    }

    /**
     * {@inheritDoc}
     */
    public final Integer sample(Random random) {
        u = random.nextDouble();
        return Math.min(search(), upperBound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean test(int value) {
        return evalDistributionFunction(value) >= u;
    }
}
