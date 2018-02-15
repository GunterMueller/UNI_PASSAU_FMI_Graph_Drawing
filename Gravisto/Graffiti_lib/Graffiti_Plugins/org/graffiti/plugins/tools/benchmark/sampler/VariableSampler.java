// =============================================================================
//
//   VariableSampler.java
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
public class VariableSampler extends Sampler {
    private String id;

    public VariableSampler(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sample(SamplingContext context) {
        return context.getVariable(id);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
