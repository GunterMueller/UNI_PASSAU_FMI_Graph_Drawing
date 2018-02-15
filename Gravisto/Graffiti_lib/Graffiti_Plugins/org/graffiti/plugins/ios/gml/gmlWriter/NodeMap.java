// =============================================================================
//
//   NodeMap.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeMap.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.util.HashMap;

import org.graffiti.graph.Node;

/**
 * This class provides a mapping from nodes to ids.
 * 
 * @author ruediger
 */
class NodeMap {

    /** Maps nodes to ids. */
    private HashMap<Node, Integer> map;

    /** Counter for the ids. */
    private int count;

    /**
     * Constructs a new <code>NodeMap</code>.
     */
    NodeMap() {
        this.map = new HashMap<Node, Integer>();
        this.count = -1;
    }

    /**
     * Returns the id corresponding to the specified node.
     * 
     * @param n
     *            the node of which to return the id.
     * 
     * @return the id corresponding to the specified node.
     */
    int getId(Node n) {
        return this.map.get(n);
    }

    /**
     * Adds a new node to the mapping and assigns it a new id.
     * 
     * @param n
     *            the node to be added.
     * 
     * @return the id of the node which has been added to the mapping.
     */
    int add(Node n) {
        this.map.put(n, new Integer(++count));

        return count;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
