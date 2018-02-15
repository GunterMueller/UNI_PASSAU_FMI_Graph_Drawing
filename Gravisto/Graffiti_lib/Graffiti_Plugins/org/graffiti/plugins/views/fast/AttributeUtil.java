// =============================================================================
//
//   AttributeUtil.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.EdgeShapeAttribute;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AttributeUtil implements GraphicAttributeConstants {
    public static NodeShape getShape(Node node) {
        try {
            NodeShape shape = (NodeShape) InstanceLoader
                    .createInstance(((NodeShapeAttribute) node
                            .getAttribute(SHAPE_PATH)).getString());
            shape
                    .buildShape((NodeGraphicAttribute) node
                            .getAttribute(GRAPHICS));
            return shape;
        } catch (InstanceCreationException e) {
        } catch (ShapeNotFoundException e) {
        }
        assert (false);
        return null;
    }

    public static EdgeShape getShape(Edge edge, NodeShape sourceShape,
            NodeShape targetShape) {
        try {
            EdgeShape shape = (EdgeShape) InstanceLoader
                    .createInstance(((EdgeShapeAttribute) edge
                            .getAttribute(SHAPE_PATH)).getString());
            shape.buildShape(
                    (EdgeGraphicAttribute) edge.getAttribute(GRAPHICS),
                    sourceShape, targetShape);
            return shape;
        } catch (InstanceCreationException e) {
        } catch (ShapeNotFoundException e) {
        }
        assert (false);
        return null;
    }

    // public static

    public static Color getFrameColor(Node node) {
        return ((ColorAttribute) node.getAttribute(GRAPHICS
                + Attribute.SEPARATOR + FRAMECOLOR)).getColor();
    }

    public static Color getFrameColor(Edge edge) {
        return ((ColorAttribute) edge.getAttribute(GRAPHICS
                + Attribute.SEPARATOR + FRAMECOLOR)).getColor();
    }

    public static Color getFillColor(GraphElement graphElement) {
        return ((ColorAttribute) graphElement.getAttribute(FILLCOLOR_PATH))
                .getColor();
    }

    public static Point2D getPosition(Node node) {
        return ((CoordinateAttribute) node.getAttribute(COORD_PATH))
                .getCoordinate();
    }

    public static void setPosition(Node node, Point2D position) {
        CoordinateAttribute ca = (CoordinateAttribute) node
                .getAttribute(COORD_PATH);
        ca.setCoordinate(position);
    }

    public static void setPosition(Node node, Point2D position,
            UndoUtil undoUtil) {
        CoordinateAttribute ca = (CoordinateAttribute) node
                .getAttribute(COORD_PATH);
        undoUtil.preChange(ca);
        ca.setCoordinate(position);
    }

    public static Point2D getDimension(Node node) {
        Dimension dimension = ((DimensionAttribute) node.getAttribute(DIM_PATH))
                .getDimension();
        return new Point2D.Double(dimension.getWidth(), dimension.getHeight());
    }

    public static void setDimension(Node node, double width, double height) {
        DimensionAttribute da = (DimensionAttribute) node
                .getAttribute(DIM_PATH);
        da.setDimension(width, height);
    }

    public static void setDimension(Node node, double width, double height,
            UndoUtil undoUtil) {
        DimensionAttribute da = (DimensionAttribute) node
                .getAttribute(DIM_PATH);
        undoUtil.preChange(da);
        da.setDimension(width, height);
    }

    public static double getFrameThickness(Node node) {
        return ((DoubleAttribute) node.getAttribute(FRAMETHICKNESS_PATH))
                .getDouble();
    }

    public static double getFrameThickness(Edge edge) {
        return ((DoubleAttribute) edge.getAttribute(FRAMETHICKNESS_PATH))
                .getDouble();
    }

    public static Dash getDash(Node node) {
        return (Dash) node.getAttribute(
                GRAPHICS + Attribute.SEPARATOR + LINEMODE).getValue();
    }

    public static Dash getDash(Edge edge) {
        return (Dash) edge.getAttribute(
                GRAPHICS + Attribute.SEPARATOR + LINEMODE).getValue();
    }

    public static double getDepth(Node node) {
        return ((CoordinateAttribute) node.getAttribute(COORD_PATH)).getDepth();
    }

    public static double getDepth(Edge edge) {
        return ((DoubleAttribute) edge.getAttribute(DEPTH_PATH)).getDouble();
    }
    
    public static void setFrameColor(Node node, Color color) {
        ((ColorAttribute) node.getAttribute(GRAPHICS + Attribute.SEPARATOR + FRAMECOLOR)).setColor(color);
    }
    
    public static void setFrameColor(Edge edge, Color color) {
        ((ColorAttribute) edge.getAttribute(GRAPHICS + Attribute.SEPARATOR + FRAMECOLOR)).setColor(color);
    }
    
    public static void setFillColor(Node node, Color color) {
        ((ColorAttribute) node.getAttribute(GRAPHICS + Attribute.SEPARATOR + FILLCOLOR)).setColor(color);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
