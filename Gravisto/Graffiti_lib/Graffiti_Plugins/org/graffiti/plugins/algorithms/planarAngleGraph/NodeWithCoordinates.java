// =============================================================================
//
//   NodeWithCoordinates.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import org.graffiti.graph.Node;

/**
 * A <code>NodeWithCoordinates</code> object contains the
 * <code>org.graffiti.graph.Node</code> and its x- and y-coodinates
 * 
 * @author Mirka Kossak
 */
public class NodeWithCoordinates {
    private Node node;

    private double XCoordinate;

    private double YCoordinate;

    public NodeWithCoordinates(Node node) {
        this.node = node;
    }

    /**
     * Returns the node.
     * 
     * @return the node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Sets the node.
     * 
     * @param node
     *            the node to set.
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Returns the xCoordinate.
     * 
     * @return the xCoordinate.
     */
    public double getXCoordinate() {
        return XCoordinate;
    }

    /**
     * Sets the xCoordinate.
     * 
     * @param coordinate
     *            the xCoordinate to set.
     */
    public void setXCoordinate(double coordinate) {
        XCoordinate = coordinate;
    }

    /**
     * Returns the yCoordinate.
     * 
     * @return the yCoordinate.
     */
    public double getYCoordinate() {
        return YCoordinate;
    }

    /**
     * Sets the yCoordinate.
     * 
     * @param coordinate
     *            the yCoordinate to set.
     */
    public void setYCoordinate(double coordinate) {
        YCoordinate = coordinate;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
