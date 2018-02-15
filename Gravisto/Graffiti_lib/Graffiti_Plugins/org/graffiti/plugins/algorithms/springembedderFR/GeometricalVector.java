// =============================================================================
//
//   GeometricalVector.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GeometricalVector.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

import java.awt.geom.Point2D;

/**
 * @author matzeder
 */
public class GeometricalVector {

    /**
     * Value of the vector on x direction
     */
    private double x;

    /**
     * Value of the vector on y direction
     */
    private double y;

    /**
     * Creates a new GeometricalVector.
     * 
     */
    public GeometricalVector() {
        this.x = 0.0d;
        this.y = 0.0d;
    }

    /**
     * Creates a new GeometricalVector with position of the node u.
     * 
     * @param u
     *            The given FRNode to calculate the GeometricalVector
     */
    public GeometricalVector(FRNode u) {
        this.setGeometricalVector(u.getXPos(), u.getYPos());
    }

    /**
     * Creates a new GeometricalVector between two FRNodes
     * 
     * @param u
     *            First given FRNode
     * @param v
     *            Second given FRNode
     */
    public GeometricalVector(FRNode u, FRNode v) {
        this.setGeometricalVector(u.getXPos() - v.getXPos(), u.getYPos()
                - v.getYPos());
    }

    /**
     * Creates a new GeometricalVector between two Point2D
     * 
     * @param u
     *            First given FRNode
     * @param v
     *            Second given FRNode
     */
    public GeometricalVector(Point2D u, Point2D v) {
        this.setGeometricalVector(u.getX() - v.getX(), u.getY() - v.getY());
    }

    /**
     * To create a geometrical vector in 2D.
     * 
     * @param x
     *            Value in x direction
     * @param y
     *            Value in y direction
     */
    public GeometricalVector(double x, double y) {

        this.x = x;
        this.y = y;

    }

    /**
     * Creates a GeometricalVector with difference between the position of a
     * node u and a GeometricalVector v.
     * 
     * @param u
     *            The given FRNode
     * @param v
     *            The given GeometricalVector
     */
    public GeometricalVector(FRNode u, GeometricalVector v) {

        this.setGeometricalVector(u.getXPos() - v.getX(), u.getYPos()
                - v.getX());
    }

    /**
     * Returns the length of a geometrical vector g
     * 
     * @param g
     *            The given geometrical vector
     * @return the absolute value of the geometrical vector
     */
    public static double getLength(GeometricalVector g) {

        return Math.sqrt(g.x * g.x + g.y * g.y);

    }

    /**
     * Returns a orthogonal vector to the given GeometricalVector v.
     * 
     * @param v
     *            The given GeometricalVector
     * @return Orthogonal vector
     */
    public static GeometricalVector getOrthogonalUnitVector(GeometricalVector v) {

        // v has no direction (not possible)
        if (v.getX() == 0.0d && v.getY() == 0.0d)
            return null;
        else if (v.getX() == 0.0d)
            return new GeometricalVector(0.0d, 1.0d);
        else if (v.getY() == 0.0d)
            return new GeometricalVector(1.0d, 0.0d);
        else
            return new GeometricalVector(v.getUnitVector().getY(), -v
                    .getUnitVector().getX());

    }

    /**
     * The returned vector will have length one. Direction is turned by exactly
     * 90�.
     * 
     * @return orthogonal unit vector (counterclockwise)
     * @author scholz
     * @see GeometricalVector#getOrthogonalUnitVector - other than the above
     *      implementation, this one will always return a vector (avoiding
     *      crashes at the caller) and with defined direction.
     */
    public static GeometricalVector getClockwiseOrthogonalUnitVector(
            GeometricalVector v) {
        // v has no direction: too many solutions - choose one
        if (v.getX() == 0.0d && v.getY() == 0.0d)
            return new GeometricalVector(1, 0);
        else
            return (new GeometricalVector(-v.getY(), v.getX()).getUnitVector());

    }

    /**
     * Returns the difference of two geometrical vectors
     * 
     * @param g1
     *            First geometrical vector
     * @param g2
     *            Second geometrical vector
     * @return Difference of GeometricalVector g2 and g1
     */
    public static GeometricalVector subtract(GeometricalVector g1,
            GeometricalVector g2) {
        return new GeometricalVector(g1.x - g2.x, g1.y - g2.y);
    }

    /**
     * Returns the distance between two geometrical vectors
     * 
     * @param g1
     *            First given GeometricalVector
     * @param g2
     *            Second given GeometricalVector
     * @return The distance between the two GeometricalVectors
     */
    public static double getDistance(GeometricalVector g1, GeometricalVector g2) {
        double deltaX = g1.getX() - g2.getX();
        double deltaY = g1.getY() - g2.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Returns true, if the given GeometricalVector i_node is between the
     * sourceVector and targetVector, else false.
     * 
     * @param i_node
     *            GeometricalVector to check if it is between sourceVector and
     *            targetVector
     * @param sourceVector
     *            The given sourceVector
     * @param targetVector
     *            The given targetVector
     * @return True, if i_node lies between sourceVector and targetVector, else
     *         false
     */
    public static boolean isPointBetweenSourceAndTarget(
            GeometricalVector i_node, GeometricalVector sourceVector,
            GeometricalVector targetVector) {

        double edgeLength = GeometricalVector.getDistance(sourceVector,
                targetVector);
        double i_NodeToSource = GeometricalVector.getDistance(i_node,
                sourceVector);
        double i_NodeToTarget = GeometricalVector.getDistance(i_node,
                targetVector);

        // wenn i_node nicht auf der linie zwischen sourceVector und
        // targetVector liegt
        if (i_NodeToSource > edgeLength || i_NodeToTarget > edgeLength)
            return false;
        return true;
    }

    /**
     * Multiplies a GeometricalVector with a factor.
     * 
     * @param vector
     *            The given GeometricalVector
     * @param factor
     *            The given factor.
     * @return The multiplication of the GeometricalVector and a factor.
     */
    public static GeometricalVector mult(GeometricalVector vector, double factor) {

        return new GeometricalVector(vector.getX() * factor, vector.getY()
                * factor);

    }

    public static GeometricalVector div(GeometricalVector vector, double x) {

        if (x != 0)
            return new GeometricalVector(vector.getX() / x, vector.getY() / x);
        return new GeometricalVector();

    }

    /**
     * Subtraction of two GeometricalVectors
     * 
     * @param g1
     *            First given GeometricalVector
     * @param g2
     *            Second given GeometricalVector
     * @return Subtraction of two GeometricalVectors
     */
    public static GeometricalVector subt(GeometricalVector g1,
            GeometricalVector g2) {
        GeometricalVector diff = new GeometricalVector();
        diff.setGeometricalVector(g1.getX() - g2.getX(), g1.getY() - g2.getY());
        return diff;
    }

    /**
     * Returns the sum of two geometrical vectors g1 and g2
     * 
     * @param g1
     *            First geometrical vector
     * @param g2
     *            Second geometrical vector
     * @return the sum of two geometrical vectors
     */
    public static GeometricalVector add(GeometricalVector g1,
            GeometricalVector g2) {
        return new GeometricalVector(g1.x + g2.x, g1.y + g2.y);
    }

    /**
     * Returns the x.
     * 
     * @return the x.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y.
     * 
     * @return the y.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the unit vector of this.
     * 
     * @return Unit vector of this.
     */
    public GeometricalVector getUnitVector() {

        GeometricalVector unitVector = new GeometricalVector();

        double length = Math.sqrt(this.getX() * this.getX() + this.getY()
                * this.getY());

        unitVector.setGeometricalVector(this.getX() / length, this.getY()
                / length);

        return unitVector;
    }

    /**
     * Sets the GeometricalVector.
     * 
     * @param x
     *            x-coordinate
     * @param y
     *            y-coodinate
     */
    public void setGeometricalVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The output of a GeometricalVector
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
