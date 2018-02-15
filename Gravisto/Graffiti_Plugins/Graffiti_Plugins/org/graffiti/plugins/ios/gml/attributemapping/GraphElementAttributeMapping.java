// =============================================================================
//
//   GraphElementAttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementAttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.ios.gml.GmlConstants;

/**
 * Class <code>GraphElementAttributeMapping</code> provides an abstract
 * implementation of an attribute mapping from GML key paths to Gravisto
 * attribute paths for graph element attributes.
 * 
 * @author ruediger
 */
public abstract class GraphElementAttributeMapping extends
        AbstractAttributeMapping {

    /**
     * Constructs a new <code>GraphElementAttributeMapping</code> and adds some
     * default mappings for graph elements.
     */
    public GraphElementAttributeMapping() {
        super();

        // add attributes to the mapping
        // label (path not unique)
        addMapping(GraphicAttributeConstants.LABEL, ".label0.label", true);
        addMapping(GmlConstants.LABEL_PATH, GmlConstants.LABEL_LABEL_PATH, true);
        addMapping(GraphicAttributeConstants.LABEL,
                GmlConstants.LABEL_LABEL_PATH, true);
        addMapping(GmlConstants.WEIGHT, GmlConstants.WEIGHT_LABEL_PATH, true);
        addMapping(GmlConstants.CAPACITY, GmlConstants.CAPACITY_LABEL_PATH,
                true);

        // dimension
        addMapping(GmlConstants.GML_WIDTH_PATH,
                GmlConstants.FRAMETHICKNESS_PATH, false);

        // coloring
        addMapping(GmlConstants.GML_BACKGROUND_PATH,
                GmlConstants.BACKGROUND_PATH, true);
        addMapping(GmlConstants.GML_FOREGROUND_PATH,
                GmlConstants.FOREGROUND_PATH, true);
        addMapping(GmlConstants.GML_FILL_PATH, GmlConstants.FILLCOLOR_PATH,
                true);
        addMapping(GmlConstants.GML_OUTLINE_PATH, GmlConstants.FRAMECOLOR_PATH,
                true);

        // other attributes
        addMapping(GmlConstants.GML_LABELFILL_PATH,
                GmlConstants.LABEL_TEXTCOLOR_PATH, true);

        // add ignorable GML graph element attributes
        addIgnorableGML("graphics.stipple");
        addIgnorableGML("graphics.backgroundImage.tiled");

        // add ignorable Gravisto graph element attributes
        addIgnorableGravisto(".graphics.linemode");
        addIgnorableGravisto(".graphics.backgroundImage");

        addIgnorableGravisto(".label.alignment");
        addIgnorableGravisto(".label.textcolor");
        addIgnorableGravisto(".label.font");
        addIgnorableGravisto(".label.position");
        addIgnorableGravisto(".label.maxWidth");
        addIgnorableGravisto(".label.fontSize");

        addIgnorableGravisto(".label0.alignment");
        addIgnorableGravisto(".label0.textcolor");
        addIgnorableGravisto(".label0.font");
        addIgnorableGravisto(".label0.position");
        addIgnorableGravisto(".label0.maxWidth");
        addIgnorableGravisto(".label0.fontSize");

        addIgnorableGravisto(".weight.alignment");
        addIgnorableGravisto(".weight.textcolor");
        addIgnorableGravisto(".weight.font");
        addIgnorableGravisto(".weight.position");
        addIgnorableGravisto(".weight.maxWidth");
        addIgnorableGravisto(".weight.fontSize");

        addIgnorableGravisto(".capacity.alignment");
        addIgnorableGravisto(".capacity.textcolor");
        addIgnorableGravisto(".capacity.font");
        addIgnorableGravisto(".capacity.position");
        addIgnorableGravisto(".capacity.maxWidth");
        addIgnorableGravisto(".capacity.fontSize");

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
