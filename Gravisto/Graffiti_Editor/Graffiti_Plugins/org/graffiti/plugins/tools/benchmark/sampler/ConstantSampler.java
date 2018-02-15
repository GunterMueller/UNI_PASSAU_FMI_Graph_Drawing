// =============================================================================
//
//   ConstantSampler.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ConstantSampler extends Sampler {
    private double value;

    public ConstantSampler(double value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sample(SamplingContext context) {
        return value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
