// =============================================================================
//
//   NodeShapeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeShapeEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import javax.swing.JComboBox;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ComboBoxEditComponent;

/**
 * Class used to display different node shapes.
 * 
 * @version $Revision: 5772 $
 */
public class NodeShapeEditComponent extends ComboBoxEditComponent {

    /**
     * Constructor sets the correct entries of the combo box. And creates a new
     * combo box.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public NodeShapeEditComponent(Displayable<?> disp) {
        super(disp);
        this.comboText = new String[] { "Rectangle", "Circle", "Ellipse" };
        this.comboValue = new String[] {
                "org.graffiti.plugins.views.defaults.RectangleNodeShape",
                "org.graffiti.plugins.views.defaults.CircleNodeShape",
                "org.graffiti.plugins.views.defaults.EllipseNodeShape" };
        this.comboBox = new JComboBox(this.comboText);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
