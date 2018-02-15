// =============================================================================
//
//   PartitionClass.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * Class is used to represent an element in the vertexPartition method of the
 * transitive orientation algorithm.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PartitionClass {

    private int lastUsed;
    private List<Node> nodes;

    /**
     * Constructor, setting the set of nodes and the lastUsed variable. This
     * variable indicates, how big the class was in its last splitting.
     * 
     * @param nodes
     *            The set of nodes for this partition class.
     * @param lastUsed
     *            Variable that indicates the size before the last splitting of
     *            this class.
     */
    public PartitionClass(List<Node> nodes, int lastUsed) {
        this.nodes = new ArrayList<Node>(nodes);
        this.lastUsed = lastUsed;
    }

    /**
     * Constructor setting the nodes to the single node given and setting the
     * lastUsed variable. This variable indicates, how big the class was in its
     * last splitting.
     * 
     * @param node
     *            The single node of this partition class.
     * @param lastUsed
     *            Integer that indicates who big this class was before its last
     *            splitting operation.
     */
    public PartitionClass(Node node, int lastUsed) {
        this.lastUsed = lastUsed;
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(node);
        this.nodes = nodes;
    }

    /**
     * Adds the given node to this partition class.
     * 
     * @param node
     *            The node that is to be added.
     */
    public void addNode(Node node) {
        if (node != null) {
            this.nodes.add(node);
        }
    }

    /**
     * Method is used to split this partition class in relation to the given
     * node and its adjacencies. It then returns the list of nodes, which are in
     * this partition class AND adjacent to the node.
     * 
     * @param node
     *            Node which adjacency is to be used.
     * @return A list of nodes that are adjacent to the node and contained in
     *         this partition class.
     */
    public List<Node> removeAdjacentNodes(Node node) {
        List<Node> removedNodes = new ArrayList<Node>();

        List<Node> adjacentNodes = (List<Node>) node.getNeighbors();

        for (Node partitionNode : this.nodes) {
            if (adjacentNodes.contains(partitionNode)) {
                removedNodes.add(partitionNode);
            }
        }

        this.nodes.removeAll(removedNodes);

        return removedNodes;
    }

    /**
     * Sets the lastUsed.
     * 
     * @param lastUsed
     *            the lastUsed to set.
     */
    public void setLastUsed(int lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * Returns the lastUsed.
     * 
     * @return the lastUsed.
     */
    public int getLastUsed() {
        return lastUsed;
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
