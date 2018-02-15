// =============================================================================
//
//   NodeWithClassID.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import org.graffiti.graph.Node;

/**
 * @author Mary
 * @version $Revision$ $Date$
 */
public class NodeWithClassID implements Comparable<NodeWithClassID> {
    private Node node;

    private String classID;

    public NodeWithClassID(Node n, String c) {
        node = n;
        classID = c;
    }

    /**
     * Compares the member Strings 'classID' lexicographically.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(NodeWithClassID o) {
        return classID.compareTo(o.classID);
    }

    /**
     * Two NodeWithClassID are equal, if their member Strings 'classID' are
     * equal.
     * 
     * @param c1
     * @param c2
     * @return <code>true</code> if the member Strings 'classID' are equal, else
     *         <code>false</code>
     */
    public static boolean equals(NodeWithClassID c1, NodeWithClassID c2) {
        return c1.compareTo(c2) == 0;
    }

    /**
     * Returns the node.
     * 
     * @return the node.
     */
    public Node getNode() {
        return node;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
