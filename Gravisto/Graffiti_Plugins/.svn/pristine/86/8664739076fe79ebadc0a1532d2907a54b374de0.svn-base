package org.graffiti.plugins.algorithms.phyloTrees;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

/**
 * Interface for algorithms to draw phylogenetic trees.
 * 
 * Must be registered in {@link PhyloTreeAlgorithm} to be used as part of the
 * phylogenetic tree drawing plugin.
 */
public interface PhylogeneticTree {
    /**
     * Performs the drawing algorithm for all trees in a given graph.
     * 
     * @param graph
     *            The graph containing the Nodes of the tree.
     * @param data
     *            The data associated with the given Graph.
     */
    public void drawGraph(Graph graph, PhyloTreeGraphData data);

    /**
     * Redraws a part of a tree.
     * 
     * @param graph
     *            The graph containing the trees.
     * @param tainted
     *            The root node of the subtree that is to be repainted.
     * @param data
     *            The data associated with the given graph.
     */
    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data);

    /**
     * Returns the Name of the algorithm.
     * 
     * @return The name of the algorithm.
     */
    public String getName();

    /**
     * Returns the parameters of the graph drawing algorithm.
     * 
     * @return Array of Parameter necessary to draw the graph. Must not be null.
     */
    public Parameter<?>[] getParameters();
}
