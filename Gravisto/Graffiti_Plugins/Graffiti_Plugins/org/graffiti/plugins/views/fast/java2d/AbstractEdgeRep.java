// =============================================================================
//
//   AbstractEdgeRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractEdgeRep extends AbstractJava2DRep {
    protected Edge edge;

    protected EdgeShape shape;

    protected AbstractEdgeRep(Edge edge) {
        super();
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }

    public EdgeShape getShape() {
        return shape;
    }

    void setShape(EdgeShape shape) {
        this.shape = shape;
    }

    abstract void buildShape(EdgeGraphicAttribute attribute,
            NodeShape sourceShape, NodeShape targetShape);

    void setFrameThickness(double frameThickness) {
    };

    double getFrameThickness() {
        return GraphicAttributeConstants.DEFAULT_EDGE_FRAMETHICKNESS;
    }

    void setFillColor(Color fillColor) {
    };

    void setFrameColor(Color frameColor) {
    };

    void setThickness(double thickness) {
    };

    double getThickness() {
        return GraphicAttributeConstants.DEFAULT_EDGE_THICKNESS;
    }

    void setDash(Dash dash) {
    };

    Dash getDash() {
        return null;
    }

    void setLineStroke(Stroke lineStroke) {
    };

    void setArrowStroke(Stroke arrowStroke) {
    };

    void setBackgroundImage(BufferedImage image, boolean maximized,
            boolean tiled) {
    };

    void setSelected(boolean isSelected) {
    };

    void setHover(boolean isHover) {
    };

    abstract boolean isSelected();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
