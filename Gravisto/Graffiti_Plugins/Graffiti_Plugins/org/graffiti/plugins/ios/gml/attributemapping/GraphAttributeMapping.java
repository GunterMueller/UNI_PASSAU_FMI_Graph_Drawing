// =============================================================================
//
//   GraphAttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphAttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.ios.gml.GmlConstants;

/**
 * Class <code>GraphAttributeMapping</code> provides an implementation of an
 * attribute mapping from GML key paths to Gravisto attribute paths for graph
 * attributes.
 * 
 * @author ruediger
 */
public class GraphAttributeMapping extends AbstractAttributeMapping {

    /**
     * Constructs a new <code>GraphAttributeMapping</code>.
     */
    public GraphAttributeMapping() {
        super();

        // ignorable graph attributes
        addIgnorableGravisto(GmlConstants.DIRECTED);
        addIgnorableGravisto(Attribute.SEPARATOR
                + GraphicAttributeConstants.GRID_PATH);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
