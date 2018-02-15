// =============================================================================
//
//   NodePosition.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodePosition.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.algorithm;

import org.graffiti.graph.Node;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision: 5768 $
 */
public class NodePosition {

    /** DOCUMENT ME! */
    Node n;

    /** DOCUMENT ME! */
    double x;

    /** DOCUMENT ME! */
    double y;

    /**
     * Creates a new NodePosition object.
     * 
     * @param n
     *            DOCUMENT ME!
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     */
    NodePosition(Node n, double x, double y) {
        this.n = n;
        this.x = x;
        this.y = y;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
