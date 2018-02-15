// =============================================================================
//
//   CliqueObject.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

/**
 * This class is used to store a number of cliques.
 * 
 * @author struckmeier
 */
public class CliqueObject {

    private CliqueTree tree;

    private CliqueSet set;

    private LexBFSNode[] nodesOrder = null;

    public CliqueObject(CliqueTree tree, CliqueSet set) {
        this.tree = tree;
        this.set = set;
    }

    /**
     * Sets the tree.
     * 
     * @param tree
     *            the tree to set.
     */
    public void setTree(CliqueTree tree) {
        this.tree = tree;
    }

    /**
     * Returns the tree.
     * 
     * @return the tree.
     */
    public CliqueTree getTree() {
        return tree;
    }

    /**
     * Sets the set.
     * 
     * @param set
     *            the set to set.
     */
    public void setSet(CliqueSet set) {
        this.set = set;
    }

    /**
     * Returns the set.
     * 
     * @return the set.
     */
    public CliqueSet getSet() {
        return set;
    }

    /**
     * Sets the nodesOrder.
     * 
     * @param nodesOrder
     *            the nodesOrder to set.
     */
    public void setNodesOrder(LexBFSNode[] nodesOrder) {
        this.nodesOrder = nodesOrder;
    }

    /**
     * Returns the nodesOrder.
     * 
     * @return the nodesOrder.
     */
    public LexBFSNode[] getNodesOrder() {
        return nodesOrder;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
