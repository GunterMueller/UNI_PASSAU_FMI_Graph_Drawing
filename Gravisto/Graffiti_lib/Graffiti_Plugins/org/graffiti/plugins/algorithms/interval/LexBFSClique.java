// =============================================================================
//
//   LexBFSClique.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A container-class representing a clique of a graph.
 * 
 * @author struckmeier
 */
public class LexBFSClique {
    private LexBFSClique parent = null;

    private LexBFSClique child = null;

    private LinkedList<LexBFSClique> children = new LinkedList<LexBFSClique>();

    private LinkedList<LexBFSNode> nodes = new LinkedList<LexBFSNode>();

    private int number;

    private LexBFSClique vorgaenger;

    private LexBFSClique nachfolger;

    private Boolean done = false;

    private Boolean inTree = true;

    HashMap<LexBFSNode, Boolean> containing = new HashMap<LexBFSNode, Boolean>();

    /**
     * Sets the nodes.
     * 
     * @param nodes
     *            the nodes to set.
     */
    public void setNodes(LinkedList<LexBFSNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public LinkedList<LexBFSNode> getNodes() {
        return nodes;
    }

    /**
     * Sets the parent.
     * 
     * @param parent
     *            the parent to set.
     */
    public void setParent(LexBFSClique parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent.
     * 
     * @return the parent.
     */
    public LexBFSClique getParent() {
        return parent;
    }

    /**
     * Adds a LexBFSNode to the clique.
     * 
     * @param node
     *            the node to add.
     */
    public void addNode(LexBFSNode node) {
        nodes.add(node);
    }

    /**
     * Sets the children.
     * 
     * @param children
     *            the children to set.
     */
    public void setChildren(LinkedList<LexBFSClique> children) {
        this.children = children;
    }

    /**
     * Returns the children.
     * 
     * @return the children.
     */
    public LinkedList<LexBFSClique> getChildren() {
        return children;
    }

    /**
     * Adds a LexBFSClique to the children.
     * 
     * @param child
     *            the clique to add.
     */
    public void addChild(LexBFSClique child) {
        children.add(child);
    }

    /**
     * Removes a LexBFSClique from the children.
     * 
     * @param child
     *            the clique to remove.
     */
    public void removeChild(LexBFSClique child) {
        children.remove(child);
    }

    /**
     * Sets the child.
     * 
     * @param child
     *            the child to set.
     */
    public void setChild(LexBFSClique child) {
        this.child = child;
    }

    /**
     * Returns the child.
     * 
     * @return the child.
     */
    public LexBFSClique getChild() {
        return child;
    }

    /**
     * Sets the number.
     * 
     * @param number
     *            the number to set.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Returns the number.
     * 
     * @return the number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the vorgaenger.
     * 
     * @param vorgaenger
     *            the vorgaenger to set.
     */
    public void setVorgaenger(LexBFSClique vorgaenger) {
        this.vorgaenger = vorgaenger;
    }

    /**
     * Returns the vorgaenger.
     * 
     * @return the vorgaenger.
     */
    public LexBFSClique getVorgaenger() {
        return vorgaenger;
    }

    /**
     * Sets the nachfolger.
     * 
     * @param nachfolger
     *            the nachfolger to set.
     */
    public void setNachfolger(LexBFSClique nachfolger) {
        this.nachfolger = nachfolger;
    }

    /**
     * Returns the nachfolger.
     * 
     * @return the nachfolger.
     */
    public LexBFSClique getNachfolger() {
        return nachfolger;
    }

    /**
     * Sets the done.
     * 
     * @param done
     *            the done to set.
     */
    public void setDone(Boolean done) {
        this.done = done;
    }

    /**
     * Returns the done.
     * 
     * @return the done.
     */
    public Boolean isDone() {
        return done;
    }

    /**
     * Sets the inTree.
     * 
     * @param inTree
     *            the inTree to set.
     */
    public void setInTree(Boolean inTree) {
        this.inTree = inTree;
    }

    /**
     * Returns the inTree.
     * 
     * @return the inTree.
     */
    public Boolean isInTree() {
        return inTree;
    }

    public HashMap<LexBFSNode, Boolean> getContaining() {
        return containing;
    }

    public void addContaining(LexBFSNode node, Boolean bool) {
        containing.put(node, bool);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
