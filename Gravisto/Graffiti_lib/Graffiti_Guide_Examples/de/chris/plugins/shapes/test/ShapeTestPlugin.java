// =============================================================================
//
//   ShapeTestPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShapeTestPlugin.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.shapes.test;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.view.GraffitiShape;

/**
 * DOCUMENT ME!
 * 
 * For the operation of this plugin the constructor of <code>
 * org.graffiti.plugins.editcomponents.defaults.NodeShapeEditComponent</code>
 * has to be changed according to the following because there is no automatic
 * load meachnism yet (plugin of a plugin yet). <code>
 * this.comboText = new String[] { "Rectangle", "Circle", "Ellipse", "Test Shape"};
 * this.comboValue = new String[]
 * {
 *     "org.graffiti.plugins.views.defaults.RectangleNodeShape",
 *     "org.graffiti.plugins.views.defaults.CircleNodeShape",
 *     "org.graffiti.plugins.views.defaults.EllipseNodeShape",
 *     "de.chris.plugins.shapes.test.TestNodeShape"
 * };
 * </code>
 * 
 * @author chris
 * @version $Revision: 5769 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class ShapeTestPlugin extends EditorPluginAdapter {

    /**
     * Creates a new AttributeComponentTestPlugin object.
     */
    public ShapeTestPlugin() {
        this.shapes = new GraffitiShape[1];
        shapes[0] = new TestNodeShape();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
