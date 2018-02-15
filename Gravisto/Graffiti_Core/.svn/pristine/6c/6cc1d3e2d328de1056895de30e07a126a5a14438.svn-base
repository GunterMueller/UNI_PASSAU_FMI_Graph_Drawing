// =============================================================================
//
//   AbstractNodeRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractNodeRep extends AbstractJava2DRep {
    protected NodeShape shape;
    protected Point2D position;
    protected Node node;

    protected AbstractNodeRep(Node node) {
        super();
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    protected void setPosition(Point2D position) {
        this.position = position;
    };

    protected Point2D getPosition() {
        return position;
    }

    void setShape(NodeShape shape) {
        this.shape = shape;
    }

    public NodeShape getShape() {
        return shape;
    }

    abstract void buildShape(NodeGraphicAttribute attribute);

    void setFrameThickness(double frameThickness) {
    };

    double getFrameThickness() {
        return GraphicAttributeConstants.DEFAULT_NODE_FRAMETHICKNESS;
    };

    void setDash(Dash dash) {
    };

    Dash getDash() {
        return null;
    };

    void setFrameColor(Color frameColor) {
    };

    void setFillColor(Color fillColor) {
    };

    void setStroke(Stroke stroke) {
    };

    void setBackgroundImage(BufferedImage image, boolean maximized,
            boolean tiled) {
    };

    void setSelected(boolean isSelected) {
    };

    abstract boolean isSelected();

    void setHover(boolean isHover) {
    };

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
