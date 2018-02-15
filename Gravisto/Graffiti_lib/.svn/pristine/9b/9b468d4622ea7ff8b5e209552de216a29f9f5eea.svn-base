// =============================================================================
//
//   Sampler.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.sampler;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Sampler {
    public abstract double sample(SamplingContext context);

    protected double[] getArguments(Collection<Sampler> samplers,
            SamplingContext context) {
        double[] values = new double[samplers.size()];
        Iterator<Sampler> iter = samplers.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            values[i] = iter.next().sample(context);
        }
        return values;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
