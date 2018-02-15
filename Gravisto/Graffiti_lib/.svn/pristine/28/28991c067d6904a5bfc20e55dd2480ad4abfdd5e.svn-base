// =============================================================================
//
//   EllipseHeuristic1Strategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

/**
 * This class implements a heuristic for drawing ellipses. The areas of a
 * minimal bounding rectangle and a circle are calculated and the rectangle or
 * circle strategy is chosen in favour to the lower area.
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class EllipseHeuristic1Strategy extends ShapeStrategy {

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialTreeDrawing.ShapeStrategy#handleShape
     * ()
     */
    @Override
    public void handleShape() {

        ShapeStrategy strategy;
        double boundingRectangleArea = w * h;
        double boundingCircleRadius = Math.max(w, h) / 2;
        double boundingCircleArea = boundingCircleRadius * boundingCircleRadius
                * Math.PI;

        if (boundingRectangleArea < boundingCircleArea) {

            strategy = new RectangleStrategy();
        } else {

            strategy = new CircleStrategy();
        }

        strategy.setNodeParameters(n, radius, startAngle);
        strategy.handleShape();
        this.polarAngle = strategy.getPolarAngle();
        this.polarRadius = strategy.getPolarRadius();
        this.apexAngle = strategy.getApexAngle();
        this.borderingRadius = strategy.getBorderingRadius();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialTreeDrawing.ShapeStrategy#apexAngle
     * ()
     */
    @Override
    protected double apexAngle() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.radialTreeDrawing.ShapeStrategy#
     * borderingRadius()
     */
    @Override
    protected double borderingRadius() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
