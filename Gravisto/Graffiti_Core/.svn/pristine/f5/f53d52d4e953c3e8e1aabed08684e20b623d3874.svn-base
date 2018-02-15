package org.graffiti.plugins.algorithms.dfs;

import org.graffiti.graph.Node;

/**
 * a DFSNodeLabeler is used from the dfs to mark nodes
 * 
 */
public interface DFSNodeLabeler {

    /**
     * processNode is called if dfs gets a new actual node and has the chance to
     * add a label
     * 
     * @param v
     *            is the node
     */
    public void processNode(Node v);

    /**
     * processNeighbor is called if neighbor nodes of the actual node expands
     * and has the chance to add a label
     * 
     * @param v
     *            is the node
     */

    public void processNeighbor(Node v);

    /**
     * resets the labeler in a fixed initial state
     * 
     */
    public void reset();

    /**
     * processNodeFinally is called if the node is processed and has the chance
     * to add a label
     * 
     * @param current
     *            is the current node
     */
    public void processNodeFinally(Node current);

    /**
     * processNeighborFinally is called if the neighbour is processed and has
     * the chance to add a label
     * 
     * @param neighbour
     *            is the neighbour node
     */
    public void processNeighborFinally(Node neighbour);

}
