// =============================================================================
//
//   ApexAngle.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class provides operations for calculating the apex area of some node
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class ApexArea {

    /**
     * a strategy for the shape handling
     */
    private ShapeStrategy strategy;

    /**
     * delegates the calculation of the apex area to a certain strategy
     * according to the nodes shape
     * 
     * @param n
     *            a node
     * @param radius
     *            the radius
     * @param startAngle
     *            the startAngle of the wedge
     */
    public void calculateApexArea(Node n, double radius, double startAngle) {

        // gives the shape of the node
        NodeShapeAttribute nsa;
        // The shape description of the node
        String shape;

        nsa = (NodeShapeAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR + GraphicAttributeConstants.SHAPE);

        // getting the shape
        shape = nsa.getString();

        if (shape.equals(GraphicAttributeConstants.RECTANGLE_CLASSNAME)) {

            strategy = new RectangleStrategy();
        } else if (shape.equals(GraphicAttributeConstants.CIRCLE_CLASSNAME)) {

            strategy = new CircleStrategy();
        } else if (shape.equals(GraphicAttributeConstants.ELLIPSE_CLASSNAME)) {

            strategy = new EllipseHeuristic1Strategy();
        } else if (shape.equals(GraphicAttributeConstants.POLYLINE_CLASSNAME))
            throw new UnsupportedOperationException("Not yet implemented.");
        else {

            System.err.println("Unknown Shape: " + shape);
        }

        strategy.setNodeParameters(n, radius, startAngle);
        strategy.handleShape();
        strategy.storeCalculatedValues();
    }
}
