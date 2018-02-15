// =============================================================================
//
//   CrossingEdgePair.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import java.util.ArrayList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * Used to store a pair of edges, with a function to calculate the angle between
 * them. Implemented for crossing edges, but does not check if they cross.
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class CrossingEdgePair {

    private Edge edge1;

    private Edge edge2;

    /**
     * Generates a new Crossing edge pair of two given edges. Does not check if
     * edges actually cross, can be used to store any two edges.
     * 
     * @param e1
     *            first edge of the crossing pair
     * @param e2
     *            second edge of the crossing pair
     */
    public CrossingEdgePair(Edge e1, Edge e2) {
        this.edge1 = e1;
        this.edge2 = e2;
    }

    /**
     * Calculates the angle between the two edges of this crossing edge pair,
     * seen as vectors. Does not check if edges actually cross in a graph!
     * 
     * @return the angle < 90 degrees between the to edges, angular measured
     */
    public double getAngle() {
        // get node coordinates of all 4 nodes
        CoordinateAttribute ca;
        ca = (CoordinateAttribute) edge1.getSource().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double edge1X1 = ca.getCoordinate().getX();
        double edge1Y1 = ca.getCoordinate().getY();

        ca = (CoordinateAttribute) edge1.getTarget().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double edge1X2 = ca.getCoordinate().getX();
        double edge1Y2 = ca.getCoordinate().getY();
        ca = (CoordinateAttribute) edge2.getSource().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double edge2X1 = ca.getCoordinate().getX();
        double edge2Y1 = ca.getCoordinate().getY();

        ca = (CoordinateAttribute) edge2.getTarget().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double edge2X2 = ca.getCoordinate().getX();
        double edge2Y2 = ca.getCoordinate().getY();

        // calculate edges as vector
        double edge1X = edge1X1 - edge1X2;
        double edge1Y = edge1Y1 - edge1Y2;
        double edge2X = edge2X1 - edge2X2;
        double edge2Y = edge2Y1 - edge2Y2;

        // calculate angle between vectors
        double edge1Length = Math.sqrt(edge1X * edge1X + edge1Y * edge1Y);
        double edge2Length = Math.sqrt(edge2X * edge2X + edge2Y * edge2Y);
        double angleRadiant = Math.acos((edge1X * edge2X + edge1Y * edge2Y)
                / (edge1Length * edge2Length));

        // convert to angular
        double angleDegree = Math.toDegrees(angleRadiant);
        if (angleDegree > 90.0) {
            angleDegree = 180 - angleDegree;
        }
        return angleDegree;
    }

    /**
     * Returns the edge1.
     * 
     * @return the edge1.
     */
    public Edge getEdge1() {
        return edge1;
    }

    /**
     * Returns the edge2.
     * 
     * @return the edge2.
     */
    public Edge getEdge2() {
        return edge2;
    }

    /**
     * Returns source and target nodes of both edges of this edge pair.
     * 
     * @return collection of all nodes of this edge pair
     */
    public ArrayList<Node> getNodes() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(edge1.getTarget());
        nodes.add(edge1.getSource());
        nodes.add(edge2.getTarget());
        nodes.add(edge2.getSource());
        return nodes;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
