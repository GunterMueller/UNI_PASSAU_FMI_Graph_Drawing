/*
 * GeometricAlgorithms.java
 *
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 *
 * Created on Aug 3, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.HashMap;

/**
 * @author ma
 * 
 *         the implementation of the method into algorithmic geometry
 */
public class GeometricAlgorithms {

    /** it contains the the variable for Edge */
    private HashMap<LocalEdge, HashMap<LocalEdge, Boolean>> edgeAttribute;

    /**
     * Computation the multi of two edge. that is term from algorithmic geometry
     * 
     * @param node1X
     *            three node of the edges
     */
    public double multi(double node1X, double node1Y, double node2X,
            double node2Y, double node0X, double node0Y) {
        double result;

        result = ((node1X - node0X) * (node2Y - node0Y))
                - ((node2X - node0X) * (node1Y - node0Y));

        return result;
    }

    /**
     * Computation whether an edge stands above andre edge
     * 
     * @param edge1
     * @param edge2
     *            two edge
     */
    public boolean comp_sect(LocalEdge edge1, LocalEdge edge2) {
        double edge1LeftX = edge1.getLeftX();
        double edge1LeftY = edge1.getLeftY();
        double edge1RightX = edge1.getRightX();
        double edge1RightY = edge1.getRightY();
        double edge2LeftX = edge2.getLeftX();
        double edge2LeftY = edge2.getLeftY();
        double edge2RightX = edge2.getRightX();
        double edge2RightY = edge2.getRightY();

        boolean result = false;

        if ((edge1LeftX == edge2LeftX) && (edge1LeftY == edge2LeftY))
        // Two edge have equal left nodes
        {
            result = multi(edge1RightX, edge1RightY, edge2RightX, edge2RightY,
                    edge1LeftX, edge1LeftY) > 0;
        } else if ((edge1LeftX == edge2RightX) && (edge1LeftY == edge2RightY)) {
            result = true;
        } else if ((edge1RightX == edge2LeftX) && (edge1RightY == edge2LeftY)) {
            result = false;
        } else {
            HashMap<LocalEdge, Boolean> crossEdge = null;

            boolean isswap = false;

            try {
                crossEdge = this.edgeAttribute.get(edge1);
                isswap = crossEdge.get(edge2);
            } catch (Exception e) {
            }

            if (isswap) {
                LocalNode crossNode = this.getCrossNode(edge1, edge2);
                double crossNodeX = crossNode.getXCoordiante();
                double crossNodeY = crossNode.getYCoordiante();
                result = multi(edge1RightX, edge1RightY, edge2RightX,
                        edge2RightY, crossNodeX, crossNodeY) >= 0;
            } else if (edge1LeftX > edge2LeftX) {
                result = multi(edge2LeftX, edge2LeftY, edge2RightX,
                        edge2RightY, edge1LeftX, edge1LeftY) < 0;
            } else {
                result = multi(edge1LeftX, edge1LeftY, edge1RightX,
                        edge1RightY, edge2LeftX, edge2LeftY) >= 0;
            }
        }

        return result;
    }

    /**
     * Decisions whether two edge have a Krezung
     * 
     * @param node1LeftX
     *            node of two edge
     */
    private boolean isCross(double node1LeftX, double node1LeftY,
            double node1RightX, double node1RightY, double node2LeftX,
            double node2LeftY, double node2RightX, double node2RightY) {
        boolean isCross = false;

        if ((node1RightX >= node2LeftX)
                && (node2RightX >= node1LeftX)
                && (Math.max(node1LeftY, node1RightY) >= Math.min(node2LeftY,
                        node2RightY))
                && (Math.max(node2LeftY, node2RightY) >= Math.min(node1LeftY,
                        node1RightY))
                && ((multi(node2LeftX, node2LeftY, node1RightX, node1RightY,
                        node1LeftX, node1LeftY) * multi(node1RightX,
                        node1RightY, node2RightX, node2RightY, node1LeftX,
                        node1LeftY)) > 0)
                && ((multi(node1LeftX, node1LeftY, node2RightX, node2RightY,
                        node2LeftX, node2LeftY) * multi(node2RightX,
                        node2RightY, node1RightX, node1RightY, node2LeftX,
                        node2LeftY)) > 0)) {
            isCross = true;
        } else {
            isCross = false;
        }
        return isCross;
    }

    /**
     * Computation of the coordinate of the CorssNode of two edge
     * 
     * @param edge1
     * @param edge2
     *            two edge
     */
    public LocalNode getCrossNode(LocalEdge edge1, LocalEdge edge2) {
        LocalNode crossNode = null;

        double point1X = edge1.getLeftX();
        double point1Y = edge1.getLeftY();

        double point2X = edge1.getRightX();
        double point2Y = edge1.getRightY();

        double point3X = edge2.getLeftX();
        double point3Y = edge2.getLeftY();

        double point4X = edge2.getRightX();
        double point4Y = edge2.getRightY();

        double K1 = (point2Y - point1Y) / (point2X - point1X);
        double K2 = (point4Y - point3Y) / (point4X - point3X);

        double X = ((point3Y - (point3X * K2) + (point1X * K1)) - point1Y)
                / (K1 - K2);

        double Y = (K1 * (X - point1X)) + point1Y;

        crossNode = new LocalNode(X, Y, edge1, edge2);

        return crossNode;
    }

    /**
     * two edges have crossing
     * 
     * @param left
     * @param right
     *            two edge
     * @return true two edge have cross
     */
    public boolean crossEdge(LocalEdge left, LocalEdge right) {
        if ((left == null) || (right == null))
            return false;

        double point1X = left.getLeftX();
        double point1Y = left.getLeftY();

        double point2X = left.getRightX();
        double point2Y = left.getRightY();

        double point3X = right.getLeftX();
        double point3Y = right.getLeftY();

        double point4X = right.getRightX();
        double point4Y = right.getRightY();

        return isCross(point1X, point1Y, point2X, point2Y, point3X, point3Y,
                point4X, point4Y);
    }

    public void setCompareKey(
            HashMap<LocalEdge, HashMap<LocalEdge, Boolean>> compareKey) {
        this.edgeAttribute = compareKey;
    }

    @SuppressWarnings("unused")
    private String newString(LocalEdge edge) {
        return "(" + edge.getLeftX() + ", " + edge.getLeftY() + ")" + "   ("
                + edge.getRightX() + ", " + edge.getRightY() + ") -------";
    }
}
