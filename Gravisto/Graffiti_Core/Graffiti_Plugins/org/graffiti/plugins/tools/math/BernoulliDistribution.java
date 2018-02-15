package org.graffiti.plugins.tools.math;

import java.util.Random;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BernoulliDistribution implements Distribution<Boolean> {
    private double p;

    public BernoulliDistribution(double p) {
        this.p = p;
    }

    public Boolean sample(Random random) {
        return random.nextDouble() < p;
    }
}
