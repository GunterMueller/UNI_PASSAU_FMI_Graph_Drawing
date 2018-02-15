// =============================================================================
//
//   Label.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.geom.Point2D;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugins.views.fast.MathUtil;
import org.graffiti.plugins.views.fast.label.alignment.LabelAlignment;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Label<L extends Label<L, LC>, LC extends LabelCommand>
        implements GraphicAttributeConstants {
    private GraphElement element;
    private String id;
    protected double width;
    protected double height;
    protected double left;
    protected double top;

    protected Point2D rotationCenter;
    protected double rotation;

    protected Label(GraphElement element, LabelAttribute attribute,
            LabelManager<L, LC> manager) {
        this.element = element;
        id = attribute.getPath();
        width = 0.0;
        height = 0.0;
        manager.changeFormat(getThis());
        manager.changePosition(getThis());
    }

    protected abstract L getThis();

    public final boolean represents(String path) {
        return id.equals(path);
    }

    protected final void setPosition(NodeLabelAttribute attribute) {
        NodeGraphicAttribute nga = (NodeGraphicAttribute) attribute
                .getAttributable().getAttribute(GRAPHICS);
        DimensionAttribute nodeSize = nga.getDimension();
        double nodeWidth = nodeSize.getWidth();
        double nodeHeight = nodeSize.getHeight();
        double frameThickness = nga.getFrameThickness();
        CoordinateAttribute nodePosition = nga.getCoordinate();
        Double xAlignment = 0d;
        Double yAlignment = 0d;
        NodeLabelPositionAttribute nlpa = attribute.getPosition();
        String alignmentString = ((StringAttribute) nlpa
                .getAttribute("alignmentX")).getString();
        xAlignment = LabelAlignment.calculateX(alignmentString, nodeWidth,
                nodeHeight, width, height, frameThickness);

        rotationCenter = new Point2D.Double(nodePosition.getX(), nodePosition
                .getY());
        rotation = nlpa.getRotationRadian();

        if (xAlignment != null) {
            left = nodePosition.getX() - nodeWidth / 2.0 + xAlignment;
        } else {
            left = nodePosition.getX() + nlpa.getRelativeXOffset() * nodeWidth
                    * 0.5 - width / 2.0;
        }
        left += nlpa.getAbsoluteXOffset();
        alignmentString = ((StringAttribute) nlpa.getAttribute("alignmentY"))
                .getString();
        yAlignment = LabelAlignment.calculateY(alignmentString, nodeWidth,
                nodeHeight, width, height, frameThickness);
        if (yAlignment != null) {
            top = nodePosition.getY() - nodeHeight / 2.0 + yAlignment;
        } else {
            top = nodePosition.getY() + nlpa.getRelativeYOffset() * nodeHeight
                    * 0.5 - height / 2.0;
        }
        top += nlpa.getAbsoluteYOffset();
    }

    protected final void setPosition(EdgeLabelAttribute attribute,
            EdgeShape shape) {
        EdgeLabelPositionAttribute elpa = attribute.getPosition();
        Point2D center = MathUtil.interpolate(shape,
                elpa.getAlignmentSegment(), elpa.getRelativeAlignment());
        if (center != null) {
            left = center.getX() - width / 2.0 + elpa.getAbsoluteXOffset();
            top = center.getY() - height / 2.0 + elpa.getAbsoluteYOffset();
            rotationCenter = new Point2D.Double(center.getX(), center.getY());
        }
    }

    protected final boolean setSize(Point2D size) {
        boolean hasChanged = width != size.getX() || height != size.getY();
        width = size.getX();
        height = size.getY();
        return hasChanged;
    }

    protected final GraphElement getGraphElement() {
        return element;
    }

    protected final LabelAttribute getAttribute() {
        return (LabelAttribute) element.getAttribute(id);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
