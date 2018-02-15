// =============================================================================
//
//   ModularDecompositionBipartiteGraphNode.java
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
 * Class is used to represent a node in the bipartite graph that is built up in
 * the v0-modules step of the permutation graph algorithm by Dalhaus.
 * 
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class ModularDecompositionBipartiteGraphNode {
    private List<Node> nodes;
    private boolean neighbor;
    private int index;
    private int dfsNum;
    private int lowLink;
    private boolean onStack;
    
    private static final int NUMBER_NOT_SET = -1;  

    /**
     * Standard constructor, setting the set of graphnodes this node represents.
     * 
     * @param nodes
     *            Underlying set of graph nodes.
     */
    public ModularDecompositionBipartiteGraphNode(List<Node> nodes, boolean neighbor, int index) {
        this.nodes = new ArrayList<Node>(nodes);
        this.neighbor = neighbor;
        this.index = index;
        this.setDfsNum(NUMBER_NOT_SET);
        this.setLowLink(NUMBER_NOT_SET);
        this.setOnStack(false);
    }

    /**
     * Sets the dfsNum.
     * 
     * @param dfsNum
     *            the dfsNum to set.
     */
    public void setDfsNum(int dfsNum) {
        this.dfsNum = dfsNum;
    }

    /**
     * Returns the dfsNum.
     * 
     * @return the dfsNum.
     */
    public int getDfsNum() {
        return dfsNum;
    }

    /**
     * Sets the lowLink.
     * 
     * @param lowLink
     *            the lowLink to set.
     */
    public void setLowLink(int lowLink) {
        this.lowLink = lowLink;
    }

    /**
     * Returns the lowLink.
     * 
     * @return the lowLink.
     */
    public int getLowLink() {
        return lowLink;
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Returns the neighbor.
     *
     * @return the neighbor.
     */
    public boolean isNeighbor() {
        return neighbor;
    }

    /**
     * Returns the index.
     *
     * @return the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the onStack.
     *
     * @param onStack the onStack to set.
     */
    public void setOnStack(boolean onStack) {
        this.onStack = onStack;
    }

    /**
     * Returns the onStack.
     *
     * @return the onStack.
     */
    public boolean isOnStack() {
        return onStack;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
