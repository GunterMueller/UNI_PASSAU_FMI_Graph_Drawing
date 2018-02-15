// =============================================================================
//
//   EdgeMatrix.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import org.graffiti.graph.Graph;

/**
 * An <code>EdgeMatrix</code> object is needed for building the edge LP.
 * 
 * @author Mirka Kossak
 */
public class EdgeMatrix extends Matrix {
    private Graph graph;

    private int numberOfCoordinates;

    private int numberOfEdges;

    // the current row is always that row where values are set.
    private int currentRow;

    private int currentEdgeLengthIndex;

    private int helpSlack;

    private int helpSlack1;

    private int helpSlack2;

    /**
     * Constructs a new EdgeMatrix.
     * 
     * @param graph
     */
    public EdgeMatrix(Graph graph) {
        this.graph = graph;
        this.numberOfCoordinates = this.graph.getNumberOfNodes() * 2;
        this.currentRow = 2;
        this.currentEdgeLengthIndex = this.numberOfCoordinates;
        this.numberOfEdges = this.graph.getNumberOfEdges();
        this.helpSlack = this.numberOfCoordinates + this.numberOfEdges * 2 + 1;
        this.helpSlack1 = this.helpSlack;
        this.helpSlack2 = this.helpSlack;
    }

    /**
     * builds the edge matrix
     */
    @Override
    public void makeMatrix() {
        this.setNumberOfColumns(this.numberOfCoordinates + 1
                + this.numberOfEdges * 2 + this.numberOfEdges * 2);
        this.setNumberOfRows(2 + 3 * this.numberOfEdges + 1 + 1);
        init(this.getNumberOfRows(), this.getNumberOfColumns(), 0);

        this.setValue(0, 0, 1); // for fixing the position of first X
        this.setValue(1, 1, 1); // for fixing the position of first Y
    }

    /**
     * edge e = (u,v) in direction a. v.x = l.e * cos(a) + u.x. (Plus: The
     * <code>helpSlack1</code> variables are set for the case that there are
     * numerical inaccuracies.)
     * 
     * @param currentX
     *            = u
     * @param nextX
     *            = v
     * @param absoluteAngle
     *            = a
     */
    public void setXCoordinate(int currentX, int nextX, double absoluteAngle) {
        this.setValue(currentRow, currentX * 2, 1);
        this.setValue(currentRow, nextX * 2, -1);
        this.setValue(currentRow, helpSlack1++, 1);
        this.setValue(currentRow, helpSlack1++, -1);
        this.setValue(currentRow++, this.currentEdgeLengthIndex, Math
                .cos((absoluteAngle / 180) * Math.PI));
    }

    /**
     * edge e = (u,v) in direction a. v.y = l.e * sin(a) + u.y. (Plus: The
     * <code>helpSlack1</code> variables are set for the case that there are
     * numerical inaccuracies.)
     * 
     * @param currentY
     *            = u
     * @param nextY
     *            = v
     * @param absoluteAngle
     *            = a
     */
    public void setYCoordinate(int currentY, int nextY, double absoluteAngle) {
        this.setValue(currentRow, (currentY * 2) + 1, 1);
        this.setValue(currentRow, (nextY * 2) + 1, -1);
        this.setValue(currentRow, helpSlack1++, 1);
        this.setValue(currentRow, helpSlack1++, -1);
        this.setValue(currentRow++, this.currentEdgeLengthIndex++, (Math
                .sin((absoluteAngle / 180) * Math.PI)));
    }

    /**
     * Sets the slack variables. le - l - se = 0. l is a lower bound for the
     * edge length.
     * 
     * @param currentEdge
     *            = le
     * @param min
     *            = l
     * @param currentSlack
     *            = se
     */
    public void setSlackAndMinValues(int currentEdge, int min, int currentSlack) {
        this.setValue(currentRow, currentEdge, -1);
        this.setValue(currentRow, min, 1);
        this.setValue(currentRow++, currentSlack, 1);
    }

    /**
     * Sets the value in the matrix for the current edge.
     * 
     * @param currentEdge
     */
    public void setMaxSumEdges(int currentEdge) {
        this.setValue(currentRow, currentEdge, 1);
    }

    /**
     * Sets the value in the matrix for the lower bound. (Plus: sets the help
     * values for the case that there are numerical inaccuricies.)
     * 
     * @param lowerBound
     */
    public void setMinEdge(int lowerBound) {
        this.setValue(currentRow, lowerBound, 1);
        for (int i = 0; i < 2 * this.numberOfEdges; i++) {
            this.setValue(currentRow, helpSlack2++, -1);
        }
    }

    /**
     * Returns the number of coordinates.
     * 
     * @return The number of coordinates.
     */
    public int getNumberOfCoordinates() {
        return this.numberOfCoordinates;
    }

    /**
     * Returns the number of edges of the graph.
     * 
     * @return The number of edges of the graph.
     */
    public int getNumberOfEdges() {
        return this.numberOfEdges;
    }

    /**
     * Increments the counter of the row.
     * 
     */
    public void incrementCurrentRow() {
        this.currentRow++;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
