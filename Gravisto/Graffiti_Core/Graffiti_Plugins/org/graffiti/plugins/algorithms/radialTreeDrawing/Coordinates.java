// =============================================================================
//
//   Coordinates.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class provides operations for coordinate calculations
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class Coordinates {

    /**
     * transforms the polar coordinates of each node into cartesian coordinates
     * 
     * @param g
     *            a graph
     */
    public void calculateCartesianCoordinates(Graph g) {

        for (Node n : g.getNodes()) {

            calculateCartesianCoordinates(n);
        }
    }

    /**
     * transforms the polar coordinates of a node into cartesian coordinates
     * 
     * @param n
     *            a node
     */
    private void calculateCartesianCoordinates(Node n) {

        double radius = n.getDouble(Constants.POLAR_RADIUS);
        double angle = n.getDouble(Constants.POLAR_ANGLE);

        CoordinateAttribute coordinate = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        coordinate.setX(Math.cos(angle) * radius);
        coordinate.setY(Math.sin(angle) * radius);
    }
}
