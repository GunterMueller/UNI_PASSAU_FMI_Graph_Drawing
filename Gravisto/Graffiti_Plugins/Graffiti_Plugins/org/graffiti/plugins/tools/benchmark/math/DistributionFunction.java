package org.graffiti.plugins.tools.benchmark.math;

/**
 * The distribution function of a random variable.
 */
public interface DistributionFunction<T> {
    /**
     * Returns the probability that the random variable is less then or equal to
     * the specified value.
     * 
     * @param value
     *            the value.
     * @return the probability that the random variable is less then or equal to
     *         the specified value.
     */
    public double eval(T value);
}
