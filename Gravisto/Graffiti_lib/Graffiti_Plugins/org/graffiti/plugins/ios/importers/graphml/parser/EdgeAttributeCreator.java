// =============================================================================
//
//   EdgeAttributeCreator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeAttributeCreator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;

/**
 * Class <code>EdgeAttributeCreator</code> is used for reading <code>Edge</code>
 * attributes.
 * 
 * @author ruediger
 */
class EdgeAttributeCreator extends AttributeCreator {

    /**
     * Constructs a new <code>EdgeAttributeCreator</code>.
     */
    EdgeAttributeCreator() {
        super();
    }

    /*
     * 
     */
    @Override
    CollectionAttribute createDefaultAttribute() {
        return new EdgeGraphicAttribute();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
