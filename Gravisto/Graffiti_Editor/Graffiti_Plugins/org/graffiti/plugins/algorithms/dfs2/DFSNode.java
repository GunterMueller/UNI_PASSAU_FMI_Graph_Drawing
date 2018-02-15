// =============================================================================
//
//   DFSNode.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DFSNode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.dfs2;

import org.graffiti.graph.Node;

/**
 * A <code>DFSNode</code> object represents a node of the graph and corresponds
 * to a <code>org.graffiti.graph.Node</code>. It additionally has all numbers
 * needed for DFS
 * 
 * @author Diana Lucic
 */

public class DFSNode {
    /**
     * The <code>org.graffiti.graph.Node</code> which corresponds to this node
     */
    private Node originalNode;

    /**
     * The depth first search number of this node.
     */
    private int dfsNum;

    /**
     * The completion number of this node.
     */
    private int compNum;

    /**
     * The lowpoint number of this node.
     */
    private int lowpoint;

    /**
     * The st number of this node.
     */
    private int stNum;

    /**
     * shows if this node is already marked
     */
    private boolean marked;

    /**
     * reference to the dfs predecessor
     */
    private DFSNode pred;

    /**
     * Constructs a new <code>DFSNode</code>
     * 
     * @param originalNode
     *            The <code>org.graffiti.graph.Node</code> corresponding to this
     *            node
     */
    public DFSNode(Node originalNode) {
        this.originalNode = originalNode;
        dfsNum = -1;
        compNum = -1;
        lowpoint = -1;
    }

    /**
     * Returns the compNum.
     * 
     * @return the compNum.
     */
    public int getCompNum() {
        return compNum;
    }

    /**
     * Sets the compNum.
     * 
     * @param compNum
     *            the compNum to set.
     */
    public void setCompNum(int compNum) {
        this.compNum = compNum;
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
     * Sets the dfsNum.
     * 
     * @param dfsNum
     *            the dfsNum to set.
     */
    public void setDfsNum(int dfsNum) {
        this.dfsNum = dfsNum;
    }

    /**
     * Returns the lowpoint.
     * 
     * @return the lowpoint.
     */
    public int getLowpoint() {
        return lowpoint;
    }

    /**
     * Sets the lowpoint.
     * 
     * @param lowpoint
     *            the lowpoint to set.
     */
    public void setLowpoint(int lowpoint) {
        this.lowpoint = lowpoint;
    }

    /**
     * Returns the marked.
     * 
     * @return the marked.
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Sets the marked.
     * 
     * @param marked
     *            the marked to set.
     */
    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    /**
     * Returns the originalNode.
     * 
     * @return the originalNode.
     */
    public Node getOriginalNode() {
        return originalNode;
    }

    /**
     * Returns the stNum.
     * 
     * @return the stNum.
     */
    public int getStNum() {
        return stNum;
    }

    /**
     * Sets the stNum.
     * 
     * @param stNum
     *            the stNum to set.
     */
    public void setStNum(int stNum) {
        this.stNum = stNum;
    }

    /**
     * Returns the predecessor
     * 
     * @return the predecessor
     */
    public DFSNode getPred() {
        return pred;
    }

    /**
     * Sets the predecessor
     * 
     * @param pred
     *            the predecessor to set.
     */
    protected void setPred(DFSNode pred) {
        this.pred = pred;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
