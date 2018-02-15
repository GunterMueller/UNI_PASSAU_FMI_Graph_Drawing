// =============================================================================
//
//   EdgeAttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeAttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.ios.gml.GmlConstants;

/**
 * Class <code>EdgeAttributeMapping</code> provides an implementation of an
 * attribute mapping from GML key paths to Gravisto attribute paths for edge
 * attributes.
 * 
 * @author ruediger
 */
public class EdgeAttributeMapping extends GraphElementAttributeMapping {

    /**
     * Constructs a new <code>EdgeAttributeMapping</code> and adds the necessary
     * mappings for edges.
     */
    public EdgeAttributeMapping() {
        super();

        // some edge attribute mappings
        addMapping(GmlConstants.SMOOTH_PATH, GmlConstants.SHAPE_PATH, true);

        // ignorable GML edge attributes
        addIgnorableGML(GraphicAttributeConstants.SOURCE);
        addIgnorableGML(GraphicAttributeConstants.TARGET);
        addIgnorableGML(GmlConstants.TYPE_PATH);
        addIgnorableGML(GmlConstants.ARROW_PATH);
        addIgnorableGML(GmlConstants.JOINSTYLE_PATH);

        // ignorable Gravisto edge attribute
        addIgnorableGravisto(GmlConstants.SOURCE_PATH);
        addIgnorableGravisto(GmlConstants.TARGET_PATH);
        addIgnorableGravisto(GmlConstants.THICKNESS_PATH);
        addIgnorableGravisto(GmlConstants.ARROWHEAD_PATH);
        addIgnorableGravisto(GmlConstants.ARROWTAIL_PATH);
        addIgnorableGravisto(GmlConstants.LINETYPE_PATH);
    }

    /*
     * 
     */
    @Override
    public String getGMLPath(String gravistoPath) {
        String gmlPath = super.getGMLPath(gravistoPath);

        if (gmlPath == null) {
            if (gravistoPath.startsWith(GmlConstants.BENDS_PATH_START)) {
                gmlPath = GraphicAttributeConstants.LINE_POINT_PATH
                        + gravistoPath.replaceAll(
                                "\\.graphics.bends.bend[0-9]+", "");
            }
        }

        return gmlPath;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
