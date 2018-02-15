// =============================================================================
//
//   EdgeShapeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeShapeEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import javax.swing.JComboBox;

import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ComboBoxEditComponent;

/**
 * Class used to display different edge shapes.
 * 
 * @version $Revision: 5772 $
 */
public class EdgeShapeEditComponent extends ComboBoxEditComponent {

    /**
     * Constructor sets the correct entries for the combo box. And creates a new
     * combo box.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public EdgeShapeEditComponent(Displayable<?> disp) {
        super(disp);
        this.comboText = new String[] { "Straight line", "Polyline",
                GraphicAttributeConstants.CIRCLE_LINE_SEGMENTATION_SHAPE,
                // "Quadratic spline",
                "Smooth line" };
        this.comboValue = new String[] {
                "org.graffiti.plugins.views.defaults.StraightLineEdgeShape",
                "org.graffiti.plugins.views.defaults.PolyLineEdgeShape",
                GraphicAttributeConstants.CIRCLE_LINE_SEGMENTATION_CLASSNAME,
                // "org.graffiti.plugins.views.defaults.QuadCurveEdgeShape",
                "org.graffiti.plugins.views.defaults.SmoothLineEdgeShape" };
        this.comboBox = new JComboBox(this.comboText);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
