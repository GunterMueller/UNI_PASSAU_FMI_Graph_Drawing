// =============================================================================
//
//   PluginPathNode.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin;

import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A <code>PluginPathNode</code> represents a node in the plug-in tree. It has a
 * label and leaves (i.e. algorithms) and/or child nodes.
 * 
 * <code>PluginPathNodes</code> are used to construct e.g. a plug-in menu with
 * submenus.
 * 
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class PluginPathNode {
    /**
     * The node's label.
     */
    private String pathLabel = "";

    /**
     * The algorithms that are leaves of the node.
     */
    private Algorithm[] algorithms;

    /**
     * The node's children/subnodes.
     */
    private PluginPathNode[] children;

    /**
     * Constructs a new <code>PluginPathNode</code> and its subtree. If you
     * don't want to have any algorithms on this level, simply give an empty
     * array or <code>null</code> for the 'algorithms' parameter. The same
     * applies to the 'children' parameter.
     * 
     * @param pathLabel
     *            the node's label
     * @param algorithms
     *            the algorithms that belong directly to this node (its leaves)
     * @param children
     *            the children/subnodes of this node
     */
    public PluginPathNode(String pathLabel, Algorithm[] algorithms,
            PluginPathNode[] children) {
        this.pathLabel = pathLabel;
        this.algorithms = algorithms;
        this.children = children;
    }

    /**
     * Returns the node's child nodes (i.e. its subnodes) or <code>null</code>
     * if there are none.
     * 
     * @return the node's children or <code>null</code>.
     */
    public PluginPathNode[] getChildren() {
        return children;
    }

    /**
     * Returns the node's algorithms (i.e. its leaves) or <code>null</code> if
     * there are none.
     * 
     * @return the node's algorithms or <code>null</code>.
     */
    public Algorithm[] getAlgorithms() {
        return algorithms;
    }

    /**
     * Returns a label for the node.
     * 
     * @return the node's label.
     */
    public String getPathLabel() {
        return pathLabel;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
