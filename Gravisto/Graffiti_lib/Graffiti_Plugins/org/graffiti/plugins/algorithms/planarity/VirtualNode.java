package org.graffiti.plugins.algorithms.planarity;

import java.util.LinkedList;

/**
 * A <code>VirtualNode</code> is an auxiliary node used during the planarity
 * test
 * 
 * @author Wolfgang Brunner
 */
public class VirtualNode extends ArbitraryNode {

    /**
     * The <code>RealNode</code> corresponding to this node
     */
    public RealNode parent;

    /**
     * The depth first search child of this nodes <code>RealNode</code>
     */
    public RealNode child;

    /**
     * Stores whether this node is in a <code>pertinentRoots</code> list
     */
    public boolean inPertinentRoots;

    /**
     * The number of unembedded back edges on this node
     */
    public int unembeddedBackEdges;

    /**
     * Constructs a new <code>VirtualNode>
     * 
     * @param child
     *            The depth first search child of this nodes <code>RealNode
     *            </code>
     * @param DFSStartNumber
     *            The lowest depth first search index in this connected
     *            component
     */
    public VirtualNode(RealNode child, int DFSStartNumber) {
        this.child = child;
        parent = child.DFSParent;
        degree = 1;
        visited = null;
        edgeToChild = null;
        inPertinentRoots = false;
        this.DFSStartNumber = DFSStartNumber;
        adjacencyList = new LinkedList<ArbitraryNode>();
        bicomp = null;
        unembeddedBackEdges = 0;
        quadrant = NOT_ON_BORDER;
    }

    /**
     * Returns a textual representation of this node
     * 
     * @return The name of the node
     */
    @Override
    public String toString() {
        return "Node " + (parent.DFI + DFSStartNumber);
    }

    /**
     * Returns this nodes <code>RealNode</code>
     * 
     * @see RealNode#getRealNode
     */
    @Override
    public RealNode getRealNode() {
        return parent;
    }

    /**
     * Swaps the orientation of the adjacency list
     */
    public void swapAdjacencyList() {
        AdjacencyListLink temp = link[0];
        link[0] = link[1];
        link[1] = temp;
    }
}
