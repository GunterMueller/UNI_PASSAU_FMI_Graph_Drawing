// =============================================================================
//
//   SpaceNodeRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Graphics2D;

import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SpaceNodeRep extends AbstractNodeRep {
    public SpaceNodeRep(Node node) {
        super(node);
    }

    /**
     * @{inheritdoc
     */
    @Override
    protected void onDraw(Graphics2D g, DrawingSet set) {
        NodeGraphicAttribute attribute = (NodeGraphicAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        attribute.getCoordinate();
        // TODO: fetch the attributes
        // TODO: copy drawing code from SpeedNodeRep
    }

    /**
     * @{inheritdoc
     */
    @Override
    void buildShape(NodeGraphicAttribute attribute) {
        try {
            shape.buildShape(attribute);
        } catch (ShapeNotFoundException e) {
        }
    }

    @Override
    boolean isSelected() {
        return false; // TODO:
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
