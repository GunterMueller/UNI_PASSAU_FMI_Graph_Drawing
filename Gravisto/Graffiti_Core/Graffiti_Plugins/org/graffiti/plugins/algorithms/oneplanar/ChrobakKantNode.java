// =============================================================================
//
//   ChrobakKantNode.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import java.util.Collection;
import java.util.HashSet;

import org.graffiti.graph.Node;

/**
 * Extended version of a graph node, including information about coordinates,
 * rank in the canonical ordering, and the under set of Chrobak Kant algorithm.
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class ChrobakKantNode {

    private int x;
    private int y;
    private HashSet<ChrobakKantNode> under;
    private int rank;
    private Node graphNode;

    /**
     * New node, initialising under set with this node in it, and given rank
     * 
     * @param graphNode
     *            the <code>Node</code> corresponding to this ChrobakKantNode
     * @param order
     *            the rank in the canonical ordering of this node
     */
    public ChrobakKantNode(Node graphNode, int order) {
        this(graphNode, 0, 0, order);
    }

    /**
     * New node, initialising under set with this node in it, and given rank and
     * coordinates
     * 
     * @param graphNode
     *            the <code>Node</code> corresponding to this ChrobakKantNode
     * @param x
     *            the x-coordinate for this node
     * @param y
     *            the y-coordinate for this node
     * @param order
     *            the rank in the canonical ordering of this node
     */
    public ChrobakKantNode(Node graphNode, int x, int y, int order) {
        this.graphNode = graphNode;
        this.x = x;
        this.y = y;
        rank = order;

        under = new HashSet<ChrobakKantNode>();
        under.add(this);
    }

    /**
     * Returns the x coordinate.
     * 
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets a new value for x
     * 
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Increases x value of this node by value
     */
    public void incX(int value) {
        x += value;
    }

    /**
     * Returns the y coordinate.
     * 
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets a new value for y
     * 
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the under set of this node.
     * 
     * @return the under set
     */
    public HashSet<ChrobakKantNode> getUnder() {
        return under;
    }

    /**
     * Adds a node to under set of this node
     * 
     * @param node
     *            the node to add
     * @return true if node was added, false if it already was in the set
     */
    public boolean addToUnder(ChrobakKantNode node) {
        return under.add(node);
    }

    /**
     * Adds all nodes in another collection to the under set of this node
     * 
     * @param nodes
     *            the node collection to add
     * @return true if the under set was changed
     */
    public boolean addToUnder(Collection<ChrobakKantNode> nodes) {
        return under.addAll(nodes);
    }

    /**
     * Returns the canonicalOrder.
     * 
     * @return the canonicalOrder
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the graphNode.
     * 
     * @return the graphNode.
     */
    public Node getGraphNode() {
        return graphNode;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
