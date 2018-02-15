// =============================================================================
//
//   GraphElementComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * Class that shares common members for all GraphElementComponents.
 * 
 * @version $Revision: 5768 $
 */
public abstract class GraphElementComponent extends JPanel implements
        GraffitiViewComponent, GraphicAttributeConstants,
        GraphElementComponentInterface {

    /**
     * 
     */
    private static final long serialVersionUID = 6496536237986687540L;

    /**
     * Returns whether the given coordinates lie within this component and
     * within its encapsulated shape. The coordinates are assumed to be relative
     * to the coordinate system of this component.
     * 
     * @see java.awt.Component#contains(int, int)
     */
    @Override
    public boolean contains(int x, int y) {
        return super.contains(x, y);
    }

    /**
     * Paints the graph element contained in this component.
     * 
     * @param g
     *            the graphics context in which to paint.
     * 
     * @see javax.swing.JComponent#paintComponent(Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
