/*
 * LocalNode.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Aug 1, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

/**
 * @author ma
 * 
 *         a class for intersection two edge in graph
 */

public class LocalNode {

    /** x coordinate of node */
    private double xCoordinate;

    /** y coordinate of node */
    private double yCoordinate;

    /** Edge, which is above other edge */
    private LocalEdge topEdge;

    /** Edge, which is below other edge */
    private LocalEdge bottomEdge;

    /**
     * constructor of class
     * 
     * @param xCoor
     *            double, x coordinate
     * @param yCoor
     *            double, y coordinate
     * @param topEdge
     *            Edge, upper edge
     * @param bottomEdge
     *            Edge, lower edge
     */
    public LocalNode(double xCoor, double yCoor, LocalEdge topEdge,
            LocalEdge bottomEdge) {
        this.xCoordinate = xCoor;
        this.yCoordinate = yCoor;
        this.topEdge = topEdge;
        this.bottomEdge = bottomEdge;
    }

    /** get the x coordinate */
    public double getXCoordiante() {
        return this.xCoordinate;
    }

    /** get the y coordinate */
    public double getYCoordiante() {
        return this.yCoordinate;
    }

    /** get the upper edge */
    public LocalEdge getTopEdge() {
        return this.topEdge;
    }

    /** get the lower edge */
    public LocalEdge getBottomEdge() {
        return this.bottomEdge;
    }

    /***/
    public boolean isLeftNode() {
        return this.bottomEdge == null;
    }

    /***/
    public boolean isCrossNode() {
        return (this.topEdge != null && this.bottomEdge != null);
    }

}
