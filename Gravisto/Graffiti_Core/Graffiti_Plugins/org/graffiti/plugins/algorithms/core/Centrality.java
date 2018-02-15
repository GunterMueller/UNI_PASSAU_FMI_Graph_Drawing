package org.graffiti.plugins.algorithms.core;

import java.util.HashMap;

import org.graffiti.graph.Node;

/**
 * Interface for centrality measure
 * 
 * @author Matthias H�llm�ller
 * 
 */
public interface Centrality {

    /**
     * computes the centrality of the nodes of the graph according to the
     * current centrality measure
     * 
     * @return centrality value for each node
     */
    public HashMap<Node, Double> getCentrality();

    /**
     * computes a leveling for the current centrality values
     * 
     * @param centrality
     *            centrality values
     * @return leveling of each node
     */
    public HashMap<Node, Integer> cluster(HashMap<Node, Double> centrality);

}
