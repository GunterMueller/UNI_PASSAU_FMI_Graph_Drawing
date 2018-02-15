// =============================================================================
//
//   NodeRepFactory.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class NodeRepFactory {
    /**
     * Creates the representant for the specified node.
     * 
     * @param node
     *            the node.
     * @return the representant for the specified node.
     */
    public abstract AbstractNodeRep create(Node node);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
