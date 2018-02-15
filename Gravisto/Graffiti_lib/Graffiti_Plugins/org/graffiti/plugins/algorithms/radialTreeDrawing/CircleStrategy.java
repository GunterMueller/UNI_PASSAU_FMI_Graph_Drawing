// =============================================================================
//
//   apexAreaCircle.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

/**
 * This class implements a strategy for drawing circular nodes.
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class CircleStrategy extends ShapeStrategy {

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#handleShape()
     */
    @Override
    public void handleShape() {

        apexAngle = apexAngle();
        borderingRadius = borderingRadius();
        polarAngle = startAngle + 0.5 * apexAngle;
        polarRadius = radius + Math.min(w, h) / 2;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#apexAngle()
     */
    @Override
    protected double apexAngle() {

        double nodeRadius = Math.min(w, h) / 2;
        return 2 * Math.asin(nodeRadius / (radius + nodeRadius));
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.radialDrawing.ShapeStrategy#borderingRadius
     * ()
     */
    @Override
    protected double borderingRadius() {

        if (radius < Constants.EPSILON)
            // bordering radius for root is equal to its radius
            return Math.min(w, h) / 2;
        return radius + Math.min(w, h);
    }
}
