// =============================================================================
//
//   Vector.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
/*
 * Einfache Vektor Klasse 
 */
package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.awt.geom.Point2D;

/**
 * 2-dimensionaler Vektor, mit doubles als Eintraege
 */
public class Vector {

    private final int N = 2; // laenge vom Vektor

    private double[] data; // array mit den Eintraegen

    // Null Vektor
    public Vector() {
        data = new double[N];
    }

    public Vector(double x, double y) {
        data = new double[N];
        data[0] = x;
        data[1] = y;
    }

    /**
     * Laenge des Vektors
     */
    public int length() {
        return N;
    }

    public double getX() {
        return data[0];
    }

    public double getY() {
        return data[1];
    }

    /**
     * Standardskalarprodukt von this und that
     */
    public double dot(Vector that) {
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            sum = sum + (this.data[i] * that.data[i]);
        }
        return sum;
    }

    /**
     * Euklidische Norm vom Vektor
     */
    public double magnitude() {
        HexaCoord orig = new HexaCoord(0, 0);
        HexaCoord ziel = new HexaCoord(data[0], data[1]);

        return orig.distanceToPoint(ziel);
    }

    /**
     * Euklidischer Abstand zwischen den Punkten this und that
     */
    public double distanceTo(Vector that) {
        return this.minus(that).magnitude();
    }

    /**
     * this + that
     */
    public Vector plus(Vector that) {
        Vector c = new Vector();
        for (int i = 0; i < N; i++) {
            c.data[i] = this.data[i] + that.data[i];
        }
        return c;
    }

    /**
     * this - that
     */
    public Vector minus(Vector that) {
        Vector c = new Vector();
        for (int i = 0; i < N; i++) {
            c.data[i] = this.data[i] - that.data[i];
        }
        return c;
    }

    /**
     * gibt die entsprechende Koordinate zurueck
     */
    public double cartesian(int i) {
        return data[i];
    }

    public void multScalar(double factor) {

        for (int i = 0; i < N; i++) {
            data[i] = factor * data[i];
        }

    }

    /**
     * gibt einen neuen Vektor zurueck der mit den Faktor factor multipliziert
     * wurde
     */
    public Vector multScalarTemp(double factor) {
        Vector c = new Vector();
        for (int i = 0; i < N; i++) {
            c.data[i] = factor * data[i];
        }
        return c;
    }

    /**
     * Normierter Vektor
     */

    public Vector unitVector() {
        return this.multScalarTemp(1.0 / this.magnitude());
    }

    /**
     * Gibt die Richtung vom Vektor zurueck
     */
    public Directions direction() {
        Point2D temp = this.cvtHexaCoord().cvtHexaToCart();
        double alpha = Math.atan2(temp.getY(), temp.getX()) * (-180) / Math.PI;

        if (alpha < 0) {
            alpha = alpha + 360;
        }
        int alphaChange = (int) ((18 - (Math.round(alpha / 10) / 3)) % 12);
        return Directions.intToDirections(alphaChange);
    }

    public HexaCoord cvtHexaCoord() {
        return new HexaCoord(data[0], data[1]);
    }

    public Vector inverse() {
        return new Vector(-data[0], -data[1]);
    }

    /**
     * Gibt einen Vektor zurueck der bei start beginnt und bei end endet
     */
    public static Vector cvtHexaVector(HexaCoord start, HexaCoord end) {
        HexaCoord temp = new HexaCoord(end.getX() - start.getX(), end.getY()
                - start.getY());
        return new Vector(temp.getX(), temp.getY());
    }

    /**
     * Rechts ist 1, links -1 und "drauf" ist 0
     */
    public static Seite seiteMitRichtung(Directions dire, HexaCoord reference,
            HexaCoord input) {

        Vector vec = Directions.toVector(dire);
        HexaCoord next = reference.addTemp(vec);
        Point2D next1 = next.cvtHexaToCart();
        Point2D reference1 = reference.cvtHexaToCart();
        Point2D input1 = input.cvtHexaToCart();
        double w = (input1.getY() - reference1.getY())
                * (next1.getX() - reference1.getX())
                - (input1.getX() - reference1.getX())
                * (next1.getY() - reference1.getY());

        if (w == 0)
            return Seite.Oben;
        else if (w < 0)
            return Seite.Links;
        else if (w > 0)
            return Seite.Rechts;

        return null;

    }

    /**
     * Der Vektor wird um "factor" verkuerzt.
     * 
     * @param factor
     */
    public void shorten(double factor) {
        Vector temp = Directions.toVector(this.direction());
        double newSize = this.magnitude() - factor;
        temp.multScalar(newSize);
        data[0] = temp.data[0];
        data[1] = temp.data[1];

    }

    /**
     * Ein Vektor mit der selben Richtung als this aber mit der groesse newSize
     * wird zurueckgegeben
     */

    public Vector resizeTemp(double newSize) {
        Vector result = Directions.toVector(this.direction());
        result.multScalar(newSize);
        return result;
    }

    @Override
    public Vector clone() {
        return new Vector(this.getX(), this.getY());
    }

    public boolean equals(Vector other) {
        return (this.data[0] == other.data[0] && this.data[1] == other.data[1]);
    }
}
