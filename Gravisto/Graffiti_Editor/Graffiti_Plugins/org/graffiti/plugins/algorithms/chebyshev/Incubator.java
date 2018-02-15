// =============================================================================
//
//   Incubator.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class Incubator {
    private Map<Node, AuxNode> map;

    public Incubator() {
        map = new HashMap<Node, AuxNode>();
    }

    public AuxNode get(Node node) {
        AuxNode auxNode = map.get(node);
        if (auxNode == null)
            // map is updated by register()
            return new AuxNode(node, this);
        else
            return auxNode;
    }

    public void register(Node node, AuxNode auxNode) {
        map.put(node, auxNode);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
