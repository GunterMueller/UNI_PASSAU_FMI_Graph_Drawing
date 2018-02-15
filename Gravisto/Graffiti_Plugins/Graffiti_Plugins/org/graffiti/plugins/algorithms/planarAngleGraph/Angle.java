// =============================================================================
//
//   Angle.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * An <code>Angle</code> object represents an angle in a <code>Face</code>. The
 * object consists of two rays sharing a common endpoint, the
 * <code>org.graffiti.graph.Node</code> vertex.
 * 
 * @author Mirka Kossak
 */
public class Angle {
    private Node vertex;

    // the first edge in clockwise order
    private Edge first;

    // the second edge in clockwise order
    private Edge second;

    private double value;

    // the face this angle belongs to
    private Face face;

    public Angle() {
    }

    /**
     * Returns the face.
     * 
     * @return the face.
     */
    public Face getFace() {
        return face;
    }

    /**
     * Sets the face.
     * 
     * @param face
     *            the face to set.
     */
    public void setFace(Face face) {
        this.face = face;
    }

    /**
     * Returns the value.
     * 
     * @return the value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *            the value to set.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the first.
     * 
     * @return the first.
     */
    public Edge getFirst() {
        return first;
    }

    /**
     * Sets the first.
     * 
     * @param first
     *            the first to set.
     */
    public void setFirst(Edge first) {
        this.first = first;
    }

    /**
     * Returns the second.
     * 
     * @return the second.
     */
    public Edge getSecond() {
        return second;
    }

    /**
     * Sets the second.
     * 
     * @param second
     *            the second to set.
     */
    public void setSecond(Edge second) {
        this.second = second;
    }

    /**
     * Returns the vertex.
     * 
     * @return the vertex.
     */
    public Node getVertex() {
        return vertex;
    }

    /**
     * Sets the vertex.
     * 
     * @param vertex
     *            the vertex to set.
     */
    public void setVertex(Node vertex) {
        this.vertex = vertex;
    }

    /**
     * Returns the Node at the end of the first edge
     * 
     * @return the Node at the end of the first edge
     */
    public Node getEndOfFirst() {
        if (this.first.getSource() == this.vertex)
            return first.getTarget();
        else
            return first.getSource();
    }

    /**
     * Returns the Node at the end of the second edge
     * 
     * @return the Node at the end of the second edge
     */
    public Node getEndOfSecond() {
        if (this.second.getSource() == this.vertex)
            return second.getTarget();
        else
            return second.getSource();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
