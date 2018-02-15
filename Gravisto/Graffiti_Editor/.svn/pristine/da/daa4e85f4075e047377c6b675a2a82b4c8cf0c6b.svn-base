// =============================================================================
//
//   MCMCore.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.cores;

import org.graffiti.plugins.algorithms.chebyshev.AuxLayer;
import org.graffiti.plugins.algorithms.chebyshev.AuxNode;
import org.graffiti.plugins.algorithms.chebyshev.selectors.BranchSelector;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MCMCore extends AlgorithmCore {
    // private LocalImprover localImprover;
    private BranchSelector selector;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // localImprover = parameters.createLocalImprover(graph);
        selector = parameters.createBranchSelector(graph);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(int fixedLayerIndex, int activeLayerIndex,
            int neighborIndex) {
        AuxLayer fixedLayer = graph.getLayer(fixedLayerIndex);
        AuxLayer activeLayer = graph.getLayer(activeLayerIndex);

        AuxNode[] xNodes = fixedLayer.getXNodes();
        int upperBound = Math.max(activeLayer.getLength(),
                xNodes[xNodes.length - 1].getX() - xNodes[0].getX());

        MCMSearch search = new MCMSearch(fixedLayer, activeLayer, neighborIndex);
        int value = upperBound;
        do {
            value = search.search();
            selector.select(value, search);
        } while (!search.isCompletelyLocked());
        search.apply();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
