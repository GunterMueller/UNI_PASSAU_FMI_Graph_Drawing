package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public class Cladogram extends AbstractGramTree {
    /** The name of this algorithm. */
    public static final String ALGORITHM_NAME = "Cladogram";

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
    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        data.setUseWeight(false);
        super.drawGraph(graph, data);
        data.setUseWeight(true);
    }

    @Override
    public boolean getIgnoreLastEdgeWeight() {
        return true;
    }
}
