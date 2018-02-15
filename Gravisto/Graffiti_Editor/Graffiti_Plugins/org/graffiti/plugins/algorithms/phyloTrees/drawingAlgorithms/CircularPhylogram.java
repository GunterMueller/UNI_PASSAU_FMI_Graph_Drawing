package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;

public class CircularPhylogram extends AbstractCircular {
    /** The name of this algorithm. */
    public static final String ALGORITHM_NAME = "Circular Phylogram";

    /**
     * Returns the name of this algorithm.
     * 
     * @return The name of this algorithm.
     * @see PhylogeneticTree
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public boolean getIgnoreLastEdgeWeight() {
        return false;
    }
}
