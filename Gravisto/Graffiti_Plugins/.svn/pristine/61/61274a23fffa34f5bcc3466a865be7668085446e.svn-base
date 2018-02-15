// =============================================================================
//
//   CliqueTree.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

/**
 * This class stores the maximal cliques of a graph represented as a tree.
 * 
 * @author struckmeier
 */
public class CliqueTree {

    private LexBFSClique rootElement;

    /**
     * Default ctor.
     */
    public CliqueTree() {
        super();
    }

    /**
     * Return true if the Tree has a root element.
     * 
     * *return true if the Tree has a root element.
     */
    public Boolean hasRoot() {
        return (this.rootElement != null);
    }

    /**
     * Return the root Node of the tree.
     * 
     * @return the root element.
     */
    public LexBFSClique getRootElement() {
        return this.rootElement;
    }

    /**
     * Set the root Element for the tree.
     * 
     * @param current
     *            the root element to set.
     */
    public void setRootElement(LexBFSClique current) {
        this.rootElement = current;
    }

    /**
     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * 
     * @return a List<Node<T>>.
     */
    /*
     * public List<LexBFSNode> toList() { List<LexBFSNode> list = new
     * ArrayList<LexBFSNode>(); walk(rootElement, list); return list; }
     */

    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     * 
     * @return the String representation of the Tree.
     */
    /*
     * public String toString() { return toList().toString(); }
     */

    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference
     * as it recurses down the tree.
     * 
     * @param element
     *            the starting element.
     * @param list
     *            the output of the walk.
     */
    /*
     * private void walk(LexBFSNode element, List<LexBFSNode> list) {
     * list.add(element); for (LexBFSNode data: element.getChildren()) {
     * walk(data, list); } }
     */
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
