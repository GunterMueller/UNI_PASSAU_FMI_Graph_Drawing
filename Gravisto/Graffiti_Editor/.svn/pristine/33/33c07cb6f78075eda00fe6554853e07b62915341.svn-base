// =============================================================================
//
//   NumberedTreeNode.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.HashMap;

/**
 * This class is used to represent a data object in a <tt>NumberedTree</tt>.<br>
 * It is only meant to be used by <tt>NumberedTree</tt>.
 * 
 * @author Christian Brunnermeier
 * @version $Revision$ $Date$
 * 
 */
class NumberedTreeNode {

    private NumberedTreeNode prevNode = null;

    private NumberedTreeNode nextNode = null;

    // the father node if this isn't the root
    protected InnerNumberedTreeNode father = null;

    /* The tree this node belongs to. */
    protected NumberedTree<?> tree = null;

    /*  ******************* FUNCTIONS ************************ */

    /**
     * Constructs a <tt>NuberedTreeNode</tt> and stores to which
     * <tt>NumberedTree</tt> it belongs.
     */
    public NumberedTreeNode(NumberedTree<?> iTree) {
        super();
        tree = iTree;
    }

    /**
     * Returns a <tt>String</tt> representation of this node.
     */
    @Override
    public String toString() {
        return tree.getNode(this).toString();
    }

    /*  ******************* GETTER AND SETTER **************** */

    /**
     * Returns the preceding node.
     * 
     * @return the preceding node
     */
    NumberedTreeNode getPrevNode() {
        return prevNode;
    }

    /**
     * Sets the preceding node.
     * 
     * @param node
     *            the new preceding node
     */
    void setPrevNode(NumberedTreeNode node) {
        prevNode = node;
    }

    /**
     * Returns the succeeding node.
     * 
     * @return the succeeding node
     */
    NumberedTreeNode getNextNode() {
        return nextNode;
    }

    /**
     * Sets the succeeding node.
     * 
     * @param node
     *            the new succeeding node
     */
    void setNextNode(NumberedTreeNode node) {
        nextNode = node;
    }

    /**
     * Returns the number of the node.
     * 
     * @return node number
     */
    int getNumber() {
        // O(1) - O(log n)
        HashMap<NumberedTreeNode, Integer> numbers = tree.getNumbers();
        Integer storedNumber = numbers.get(this);

        if (storedNumber == null) {
            // number is out-of-date and has to be reevaluated.
            assert father != null;

            int number = father.getNumberOfChild(this);
            numbers.put(this, number);
            return number;
        } else
            return storedNumber;
    }

    InnerNumberedTreeNode getFather() {
        return father;
    }

    void setFather(InnerNumberedTreeNode newFather) {
        father = newFather;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
