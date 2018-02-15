// =============================================================================
//
//   Line.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import org.graffiti.graph.Edge;

/**
 * A <code>Line</code> object represents an <code>org.graffiti.graph.Edge</code>
 * and the absolute Angle in the coordinate system.
 * 
 * @author Mirka Kossak
 */
public class Line {
    private Edge edge;

    private double angleToXAxis;

    private boolean visited;

    public Line(Edge edge) {
        this.edge = edge;
        visited = false;
    }

    /**
     * Returns the angleToXAxis.
     * 
     * @return the angleToXAxis.
     */
    public double getAngleToXAxis() {
        return angleToXAxis;
    }

    /**
     * Sets the angleToXAxis.
     * 
     * @param angleToXAxis
     *            the angleToXAxis to set.
     */
    public void setAngleToXAxis(double angleToXAxis) {
        this.angleToXAxis = angleToXAxis;
    }

    /**
     * Returns the visited.
     * 
     * @return the visited.
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Sets the visited.
     * 
     * @param visited
     *            the visited to set.
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * Returns the edge.
     * 
     * @return the edge.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Sets the edge.
     * 
     * @param edge
     *            the edge to set.
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
