// =============================================================================
//
//   NodeAttributeCreator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeAttributeCreator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Class <code>NodeAttributeCreator</code> is used for creating
 * <code>Node</code> attributes.
 * 
 * @author ruediger
 */
class NodeAttributeCreator extends AttributeCreator {

    /**
     * Constructs a new <code>NodeAttributeCreator</code>.
     */
    NodeAttributeCreator() {
        super();
    }

    /*
     * 
     */
    @Override
    CollectionAttribute createDefaultAttribute() {
        return new NodeGraphicAttribute();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
