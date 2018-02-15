// =============================================================================
//
//   NodeHeight.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeHeight.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker;

import org.graffiti.graph.Node;

/**
 * This is used for the TreeKNaryMaker for the <code>strategy</code>
 * <code>BALANCED</code>. It is basically a pair that stores a node together
 * with its height (length of the longest path to a leaf Node).
 * 
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2006-11-08 00:45:24 +0100 (Mi, 08 Nov 2006)
 *          $
 */
public class NodeHeight {

    /**
     * contains the height information.
     */
    private int height;

    /**
     * the Node of this NodeHeight object
     */
    private Node node;

    /**
     * Constructs a new NodeHeight object
     * 
     * @param height
     *            the height of the given <code>node</code>
     * @param node
     */
    public NodeHeight(int height, Node node) {
        this.height = height;
        this.node = node;
    }

    /**
     * Returns the height.
     * 
     * @return the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the node.
     * 
     * @return the node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns a string containing the information of this NodeHeight.
     */
    @Override
    public String toString() {
        return "( " + this.height + ", " + this.node + " )";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
