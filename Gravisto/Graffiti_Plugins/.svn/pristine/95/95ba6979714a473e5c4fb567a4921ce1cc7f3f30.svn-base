// =============================================================================
//
//   ShapeStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * An abstract superclass for all shape handling strategies
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public abstract class ShapeStrategy implements ShapeStrategyInterface {

    /**
     * The node
     */
    protected Node n;

    /**
     * The width of a node
     */
    protected double w;

    /**
     * The Height of a node
     */
    protected double h;

    /**
     * The nodes radius
     */
    protected double radius;

    /**
     * The start angle of the wedge for a node
     */
    protected double startAngle;

    /**
     * the apex angle of n
     */
    protected double apexAngle;

    /**
     * the bordering radius of n
     */
    protected double borderingRadius;

    /**
     * the polar angle
     */
    protected double polarAngle;

    /**
     * the polar radius
     */
    protected double polarRadius;

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategyInterface#
     * setNodeParameters(org.graffiti.graph.Node, double, double)
     */
    public void setNodeParameters(Node n, double radius, double startAngle) {

        this.n = n;
        this.radius = radius;
        this.startAngle = startAngle;

        // gives the dimension of the node
        DimensionAttribute da;

        try {
            n.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            n.addAttribute(new NodeGraphicAttribute(), "");
        }

        da = (DimensionAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);

        w = da.getWidth();
        h = da.getHeight();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategyInterface#
     * storeCalculatedValues()
     */
    public final void storeCalculatedValues() {

        if (radius < Constants.EPSILON) {

            n.setDouble(Constants.POLAR_RADIUS, 0);
            n.setDouble(Constants.POLAR_ANGLE, 0);
        } else {

            n.setDouble(Constants.POLAR_ANGLE, polarAngle);
            n.setDouble(Constants.POLAR_RADIUS, polarRadius);
        }
        n.setDouble(Constants.BORDERING_RADIUS, borderingRadius);
        n.setDouble(Constants.APEX_ANGLE, apexAngle);
        n.setDouble(Constants.WEDGE_TO, startAngle + apexAngle
                + Constants.CLEARANCE_DISTANCE);
        n.setDouble(Constants.SIZE, borderingRadius - radius);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategyInterface#
     * handleShape() This method must calculate the values of the members
     * apexAngle, borderingRadius, polarAngle and polarRadius
     */
    public abstract void handleShape();

    /**
     * calculates the apexAngle of the current node
     * 
     * @return the apex angle
     */
    protected abstract double apexAngle();

    /**
     * calculates the minimum radius of a circle that surrounds the current
     * node. The circle has its center in the root node.
     * 
     * @return the bordering radius
     */
    protected abstract double borderingRadius();

    /**
     * Returns the apexAngle.
     * 
     * @return the apexAngle.
     */
    public double getApexAngle() {
        return apexAngle;
    }

    /**
     * Returns the borderingRadius.
     * 
     * @return the borderingRadius.
     */
    public double getBorderingRadius() {
        return borderingRadius;
    }

    /**
     * Returns the polarAngle.
     * 
     * @return the polarAngle.
     */
    public double getPolarAngle() {
        return polarAngle;
    }

    /**
     * Returns the polarRadius.
     * 
     * @return the polarRadius.
     */
    public double getPolarRadius() {
        return polarRadius;
    }
}
