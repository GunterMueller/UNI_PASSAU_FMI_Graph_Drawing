package org.graffiti.plugins.tools.math;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
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
