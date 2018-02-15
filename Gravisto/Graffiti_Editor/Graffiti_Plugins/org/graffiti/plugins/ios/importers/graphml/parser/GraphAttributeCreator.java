// =============================================================================
//
//   GraphAttributeCreator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphAttributeCreator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;

/**
 * Class <code>GraphAttributeCreator</code> is used for reading and creating
 * graph attributes.
 * 
 * @author ruediger
 */
class GraphAttributeCreator extends AttributeCreator {

    /**
     * Constructs a new <code>GraphAttributeCreator</code>.
     */
    public GraphAttributeCreator() {
        super();
    }

    /*
     * 
     */
    @Override
    CollectionAttribute createDefaultAttribute() {
        return new GraphGraphicAttribute();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
