// =============================================================================
//
//   EdgeRepFactory.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import org.graffiti.graph.Edge;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class EdgeRepFactory {
    /**
     * Creates the representant for the specified edge.
     * 
     * @param edge
     *            the edge.
     * @return the representant for the specified edge.
     */
    public abstract AbstractEdgeRep create(Edge edge);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
