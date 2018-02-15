// =============================================================================
//
//   HexaCoord.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.awt.geom.Point2D;

/**
 * HexaCoords stellen einen Punkt im hexagonalen Gitter dar. Dies gewaehrleistet
 * das man nur ganze Zahlen benutzt in den berechnungen.
 */
public class HexaCoord {

    private double x;

    private double y;

    public HexaCoord(double i, double k) {
        x = i;
        y = k;
    }

    public HexaCoord() {
    }

    public void clean() {
        x = 0;
        y = 0;
    }

    public void copy(HexaCoord hexa) {
        this.x = hexa.x;
        this.y = hexa.y;
    }

    /**
     * Addiert einen Vektor von NxN zum Punkt im Gitter, stellt also eine
     * translation dar
     */
    public void add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
    }

    /**
     * Subtrahiert einen Vektor von NxN zum Punkt im Gitter, stellt also eine
     * translation dar
     */
    public void sub(Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
    }

    /**
     * Addiert einen Vektor und gibt den neuen Punkt zurück ohne den initialen
     * zu ändern
     */
    public HexaCoord addTemp(Vector vector) {
        HexaCoord temp = new HexaCoord();
        temp.copy(this);
        temp.x += vector.getX();
        temp.y += vector.getY();
        return temp;
    }

    /**
     * Subtrahiert einen Vektor und gibt den neuen Punkt zurück ohne den
     * initialen zu ändern
     */

    public HexaCoord subTemp(Vector vector) {
        HexaCoord temp = new HexaCoord();
        temp.copy(this);
        temp.x -= vector.getX();
        temp.y -= vector.getY();
        return temp;
    }

    /**
     * Abstand von einer Koordinate zur anderen entsprechend der Metrik die im
     * Paper definiert wird
     */
    public double distanceToPoint(HexaCoord ziel) {
        double val = Math
                .max(Math.max(Math.abs(ziel.getX() - this.getX()), Math
                        .abs(ziel.getY() - this.getY())), Math
                        .abs((ziel.getX() - ziel.getY())
                                - (this.getX() - this.getY())));
        return val;
    }

    public boolean equal(HexaCoord comparand) {
        return ((this.x == comparand.x) && (this.y == comparand.y));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Konvertierung von Hexagonalen Koordinaten zu Kartesischen. Es wird der
     * vom Gravisto definierten Kartesisches System betrachtet
     */
    public Point2D cvtHexaToCart() {
        return new Point2D.Double(getX() - getY() * 0.5, getY());
    }

    public double calculateCoordinateWithRef(Directions dire, HexaCoord ref) {
        return Math.abs(this.calculateCoordinateWithRefWOSign(dire, ref));
    }

    /**
     * Berechnet den Abstand zwischen den Punkt this und die Gerade durch ref
     * mit Richtung dire
     */
    public double calculateCoordinateWithRefWOSign(Directions dire,
            HexaCoord ref) {
        double x1 = this.x - ref.x;
        double y1 = this.y - ref.y;
        double result = 0;
        switch (dire) {
        case STRAIGHT:
            result = y1;
            break;
        case STRAIGHT_RIGHT30:
            result = (y1 - (x1 / 2));
            break;
        case STRAIGHT_RIGHT:
            result = (x1 - y1);
            break;
        case NORTH:
            result = (x1 - y1 / 2);
            break;
        case BACK_RIGHT:
            result = x1;
            break;
        case BACK_RIGHT30:
            result = ((x1 + y1) / 2);
            break;
        case BACK:
            result = y1;
            break;
        case BACK_LEFT30:
            result = (y1 - (x1 / 2));
            break;
        case BACK_LEFT:
            result = (x1 - y1);
            break;
        case SOUTH:
            result = (x1 - y1 / 2);
            break;
        case STRAIGHT_LEFT:
            result = x1;
            break;
        case STRAIGHT_LEFT30:
            result = ((x1 + y1) / 2);
            break;
        }
        return result;

    }

    public double calculateCoordinate(Directions dire) {
        switch (dire) {
        case STRAIGHT:
            return y;
        case STRAIGHT_RIGHT30:
            return (y - (x / 2));

        case STRAIGHT_RIGHT:
            return (x - y);
        case NORTH:
            return (x - y / 2);
        case BACK_RIGHT:
            return x;
        case BACK_RIGHT30:
            return ((x + y) / 2);
        case BACK:
            return y;
        case BACK_LEFT30:
            return (y - (x / 2));

        case BACK_LEFT:
            return (x - y);
        case SOUTH:

            return (x - y / 2);
        case STRAIGHT_LEFT:
            return x;
        case STRAIGHT_LEFT30:
            return ((x + y) / 2);
        }
        return 0;
    }

    /**
     * Berechnet den Abstand von this zu target, in Richtung dire Muss also
     * Positiv sein wenn target "hinter" this is
     */

    public double getHight(Directions dire, HexaCoord target) {
        double dist = target.distanceToPoint(this);

        Vector trans = Directions.toVector(dire);

        trans.multScalar(2 * dist);

        Segment seg = new Segment(target.subTemp(trans), target.addTemp(trans));

        HexaCoord proj = Distances.calculateProjectionPointOnSegment(this, seg,
                Directions.perpendicular(dire));
        if (proj == null) {
            System.out.println("PANIC :-)");
        }
        double dist2 = target.distanceToPoint(proj);

        // Wenn target "dahinter" ist ist die hoehe positiv, falls davor ist die
        // hoehe negativ
        if (proj.aims(dire, target))
            return Math.abs(dist2);
        else
            return (-Math.abs(dist2));

    }

    /**
     * Entscheidet ob der PUnkt "target" hinter "this" in Richtung dire ist.
     */

    public boolean aims(Directions dire, HexaCoord target) {

        double coordThis = this.calculateCoordinate(dire);
        double coordTarget = target.calculateCoordinate(dire);
        if (coordThis == coordTarget) {
            // Es soll ja nur true kommen wenn target "dahinter" ist
            // Jetzt sind sie beide auf der selben Linie also kann ich locker
            // den abstand rechnen
            double val = this.distanceToPoint(target);

            Vector toAdd = Directions.toVector(dire);
            toAdd.multScalar(val);// Jetzt wird mit toAdd "geschossen" und wenn
            // man bei target ankommt ist target "hinter"
            // this sonst nicht
            return (this.addTemp(toAdd).equal(target));
        }
        return false;
    }

    /**
     * Bzgl. einer Richtung wird ermittelt ob other "vor" oder nachher steht
     */
    public boolean isBefore(Directions dire, HexaCoord other) {
        // Wenn height > 0 ist other dahinter, also this davor
        return (this.getHight(dire, other) > 0);
    }

    @Override
    public HexaCoord clone() {
        return new HexaCoord(this.getX(), this.getY());
    }
}
