// =============================================================================
//
//   ClassifiedNode.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import org.graffiti.graph.Node;

/**
 * TODO: kommentieren
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class ClassifiedNode implements Comparable<ClassifiedNode> {
    private Node node;

    private int[] classification;

    public ClassifiedNode(Node n, int[] c) {
        node = n;
        classification = c;
    }

    /**
     * Compares the member arrays 'classification' lexicographically.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ClassifiedNode o) {
        for (int i = 0; i < classification.length; i++) {
            if (classification[i] < o.classification[i])
                return -1;
            else if (classification[i] > o.classification[i])
                return 1;
        }
        return 0;
    }

    /**
     * Two ClassifiedNodes are equal, if their member arrays 'classification'
     * are equal.
     * 
     * @param c1
     * @param c2
     * @return <code>true</code> if the arrays 'classification' are equal, else
     *         <code>false</code>
     */
    public static boolean equals(ClassifiedNode c1, ClassifiedNode c2) {
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
