// =============================================================================
//
//   DownGenerator.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.iterations;

import org.graffiti.plugins.algorithms.chebyshev.AuxGraph;
import org.graffiti.plugins.algorithms.chebyshev.Step;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class DownGenerator extends IterationGenerator {
    /**
     * {@inheritDoc}
     */
    @Override
    public StepList generate(AuxGraph graph) {
        StepList result = new StepList();
        int layerCount = graph.getLayerCount();
        for (int i = 0; i < layerCount - 1; i++) {
            result.add(new Step(i, i + 1));
        }
        return result;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
