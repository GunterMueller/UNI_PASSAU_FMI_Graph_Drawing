// =============================================================================
//
//   NodeAttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeAttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.ios.gml.GmlConstants;

/**
 * Class <code>NodeAttributeMapping</code> provides an implementation of an
 * attribute mapping from GML key paths to Gravisto attribute paths for node
 * attributes.
 * 
 * @author ruediger
 */
public class NodeAttributeMapping extends GraphElementAttributeMapping {

    /**
     * Constructs a new <code>NodeAttributeMapping</code> and adds the necessary
     * mappings for nodes.
     */
    public NodeAttributeMapping() {
        super();

        // add node attributes to the list
        // coordinates
        addMapping(GmlConstants.GRAPHICS_X, Attribute.SEPARATOR
                + GraphicAttributeConstants.COORDX_PATH, false);
        addMapping(GmlConstants.GRAPHICS_Y, Attribute.SEPARATOR
                + GraphicAttributeConstants.COORDY_PATH, false);
        addMapping(GmlConstants.GRAPHICS_Z, Attribute.SEPARATOR
                + GmlConstants.COORDZ_PATH, false);

        // dimension
        addMapping(GmlConstants.GRAPHICS_W, Attribute.SEPARATOR
                + GraphicAttributeConstants.DIMW_PATH, false);
        addMapping(GmlConstants.GRAPHICS_H, Attribute.SEPARATOR
                + GraphicAttributeConstants.DIMH_PATH, false);
        addMapping(GmlConstants.GRAPHICS_D, Attribute.SEPARATOR
                + GmlConstants.DIMD_PATH, false);

        // other attributes
        addMapping(GmlConstants.TYPE_PATH, GmlConstants.SHAPE_PATH, true);

        // add ignorable GML node attributes
        addIgnorableGML(GmlConstants.ID);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
