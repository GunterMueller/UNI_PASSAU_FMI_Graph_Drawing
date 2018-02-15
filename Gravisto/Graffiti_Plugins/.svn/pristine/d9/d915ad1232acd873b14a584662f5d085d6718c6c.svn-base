// =============================================================================
//
//   BinomialDistribution.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.math;

import java.util.Random;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BinomialDistribution implements Distribution<Integer> {
    private int n;
    private BernoulliDistribution bernoulli;

    public BinomialDistribution(int n, double p) {
        this.n = n;
        bernoulli = new BernoulliDistribution(p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer sample(Random random) {
        // Naive approach.
        int value = 0;

        for (int i = 0; i < n; i++) {
            if (bernoulli.sample(random)) {
                value++;
            }
        }
        return value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
