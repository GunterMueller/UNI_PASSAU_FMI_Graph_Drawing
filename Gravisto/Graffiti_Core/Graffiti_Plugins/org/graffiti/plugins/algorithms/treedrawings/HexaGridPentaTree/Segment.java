// =============================================================================
//
//   Segment.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.awt.geom.Point2D;

/**
 * Diese Klasse stellt einen Segment im Hexagonalen Gitter, der ganz einfach
 * durch zwei Hexa Koordinaten angegeben wird
 */
public class Segment {

    HexaCoord start;

    HexaCoord end;

    Segment(HexaCoord start, HexaCoord end) {
        this.start = start;
        this.end = end;
    }

    public HexaCoord getStart() {
        return start;
    }

    public void setStart(HexaCoord start) {
        this.start = start;
    }

    public HexaCoord getEnd() {
        return end;
    }

    public void setEnd(HexaCoord end) {
        this.end = end;
    }

    /**
     * Prueft ob ein Punkt im Segment enthalten ist
     */
    public boolean contains(HexaCoord point) {
        // Falls der Segment selber ein Punkt ist wird auf
        // Punkte Gleichheit gepr�ft
        if (start.equal(end))
            return start.equal(point);

        if ((Directions.parallel(direction(), Directions.BACK_RIGHT))
                && (point.getX() == start.getX())) {
            if (((start.getY() <= point.getY()) && (end.getY() >= point.getY()))
                    || ((end.getY() <= point.getY()) && (start.getY() >= point
                            .getY())))
                return true;
        } else if ((Directions.parallel(direction(), Directions.STRAIGHT))
                && (point.getY() == start.getY())) {
            if (((start.getX() <= point.getX()) && (end.getX() >= point.getX()))
                    || ((end.getX() <= point.getX()) && (start.getX() >= point
                            .getX())))
                return true;
        } else if ((Directions.parallel(direction(), Directions.STRAIGHT_RIGHT))
                && ((start.getX() - start.getY()) == (point.getX() - point
                        .getY()))) {
            if ((((start.getX() <= point.getX()) && (start.getY() <= point
                    .getY())) && ((end.getX() >= point.getX()) && (end.getY() >= point
                    .getY())))
                    || (((start.getX() >= point.getX()) && (start.getY() >= point
                            .getY())) && ((end.getX() <= point.getX()) && (end
                            .getY() <= point.getY()))))
                return true;
        } else if ((Directions.parallel(direction(), Directions.NORTH))
                && ((point.getX() - start.getX()) == (point.getY() - start
                        .getY()) / 2)) {
            // Hier nur noch das y abpr�fen
            if (((point.getY() <= start.getY()) && (point.getY() >= end.getY()))
                    || ((point.getY() >= start.getY()) && (point.getY() <= end
                            .getY())))
                return true;
        } else if ((Directions.parallel(direction(),
                Directions.STRAIGHT_RIGHT30))
                && ((point.getX() - start.getX()) / 2 == (point.getY() - start
                        .getY()))) {
            if (((point.getX() <= start.getX()) && (point.getX() >= end.getX()))
                    || ((point.getX() >= start.getX()) && (point.getX() <= end
                            .getX())))
                return true;
        } else if ((Directions.parallel(direction(), Directions.BACK_RIGHT30))
                && ((point.getX() - start.getX()) == (start.getY() - point
                        .getY()))) {
            if ((((point.getX() - point.getY()) <= (start.getX() - start.getY())) && ((point
                    .getX() - point.getY()) >= (end.getX() - end.getY())))
                    || (((point.getX() - point.getY()) >= (start.getX() - start
                            .getY())) && ((point.getX() - point.getY()) <= (end
                            .getX() - end.getY()))))
                return true;
        }

        return false;
    }

    /**
     * Die Methode ist nur bis auf die Orientierung eindeutig. Kann also nur
     * fuer
     */
    public Directions direction() {
        Point2D st = start.cvtHexaToCart();
        Point2D en = end.cvtHexaToCart();
        double alpha = Math.atan2(en.getY() - st.getY(), en.getX() - st.getX())
                * (-180) / Math.PI;
        if (alpha < 0) {
            alpha = alpha + 360;
        }
        int alphaChange = (12 - (int) ((Math.round(alpha / 10) / 3 + 6) % 12)) % 12;
        return Directions.intToDirections(alphaChange);
    }

    /**
     * Entscheidet auf welcher Seite des segments der PUnkt "Point" ist 1 ist
     * eine Seite, -1 die andere und 0 "drauf"
     */
    public int seite(HexaCoord point) {

        double x1 = start.cvtHexaToCart().getX();
        double y1 = start.cvtHexaToCart().getY();
        double x2 = end.cvtHexaToCart().getX();
        double y2 = end.cvtHexaToCart().getY();
        double x = point.cvtHexaToCart().getX();
        double y = point.cvtHexaToCart().getY();

        double result = y - y1 + (y2 - y1) * (x - x1) / (x2 - x1);
        if (result == 0)
            return 0;
        else if (result < 0)
            return -1;
        else if (result > 0)
            return 1;

        return Integer.MAX_VALUE;// Das sollte nie passieren

    }

    /**
     * Zum einfachen mergen beim expandieren wird aus dem Segment eine Kontur
     * erstellt
     */
    public HalfConture convertToHalfContour(Directions dire) {
        HalfConture ergebnis = new HalfConture(null, null);

        if (!Directions.parallel(dire, this.direction())) {
            ergebnis.hexaFirst.copy(start);
            ergebnis.hexaSecond.copy(end);
            Vector startEnd = Vector.cvtHexaVector(start, end);
            ergebnis.hexaFirst.ausgehende.add(startEnd);
            ergebnis.hexaFirst.eingehende.add(startEnd.inverse());
        } else {
            if (start.aims(dire, end)) {
                // Dann ist start davor, also die Triviale Kontur
                ergebnis.hexaFirst.copy(start);
                ergebnis.hexaSecond.copy(start);
            } else {
                ergebnis.hexaFirst.copy(end);
                ergebnis.hexaSecond.copy(end);
            }

        }

        return ergebnis;
    }

    /**
     * Prueft ob Segment (startF, endF) den Segment (startS, endS) mind.
     * teilweise bedeckt. Beide haben den Punkt start gemeinsam
     * 
     * @param dire
     * @param startF
     * @param endF
     * @param startS
     * @param endS
     * @return true falls der erste den zweiten bedeckt, false sonst. Wenn sie
     *         sich kreuzen oder gar nicht bedecken kommt null raus
     */
    public static boolean bedeckt(Directions dire, HexaCoord startF,
            HexaCoord endF, HexaCoord startS, HexaCoord endS) {
        HexaCoord proj1 = Distances.calculateProjectionPointOnSegmentWithDire(
                endS, new Segment(startF, endF), Directions.reverse(dire));
        HexaCoord proj2 = Distances.calculateProjectionPointOnSegmentWithDire(
                endF, new Segment(startS, endS), dire);
        return (proj1 != null || proj2 != null);

    }
}
