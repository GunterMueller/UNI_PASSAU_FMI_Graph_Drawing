// =============================================================================
//
//   ConvexContour.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.ArrayList;

/**
 * Diese Klasse implementiert eine Konvexe Kontur. Also die die im Algorithmus
 * im Paper benutzt wird.
 * 
 */
public class ConvexContour {
    // Wie im paper, die maximalen Werte der Kontur in jede Richtung
    private int min_x;

    private int min_y;

    private int min_x_y;

    private int max_x;

    private int max_y;

    private int max_x_y;

    // Die Eckpunkte der Kontur werden dazu gespeichert
    private HexaCoord cLinks;

    private HexaCoord cObenLinks;

    private HexaCoord cObenRechts;

    private HexaCoord cRechts;

    private HexaCoord cUntenRechts;

    private HexaCoord cUntenLinks;

    private UniformNode node;

    public ArrayList<HexaCoord> surroundingPoints;

    ConvexContour() {
        surroundingPoints = new ArrayList<HexaCoord>();
    }

    ConvexContour(UniformNode node) {
        this.node = node;
        surroundingPoints = new ArrayList<HexaCoord>();

        // Triviale Kontur aus einem Punkt
        min_x = max_x = (int) node.hexa.getX(); // Es ist selbstverständlich das
        // diese Werte ganzzahlig sind, denn in diesen Algorithmus kommen gar
        // nicht 0.5 Werte vor, allerdings wird die selbe Methode genommen als
        // bei
        // den normalen Konturen
        min_y = max_y = (int) node.hexa.getY();
        min_x_y = max_x_y = (int) node.hexa.getX() - (int) node.hexa.getY();
        cLinks = new HexaCoord();
        cObenLinks = new HexaCoord();
        cObenRechts = new HexaCoord();
        cRechts = new HexaCoord();
        cUntenRechts = new HexaCoord();
        cUntenLinks = new HexaCoord();
        calcPerimeterFromBonds(); // In diesem Fall trivial

        // Alle Eckpunkte werden in eine Liste eingefuegt um sie leichter
        // verwalten zu koennen
        surroundingPoints.add(cLinks);
        surroundingPoints.add(cObenLinks);
        surroundingPoints.add(cObenRechts);
        surroundingPoints.add(cRechts);
        surroundingPoints.add(cUntenRechts);
        surroundingPoints.add(cUntenLinks);
    }

    /*
     * Alle Konturen in der listContour werden zusammen gemergt Vorhanden sind
     * die 6 Werte max_x, max_y.. usw. von den n Konturen.. maximal nat�rlich 5
     */
    public void merge(ArrayList<ConvexContour> listContour) {
        contureBond(listContour);
        calcPerimeterFromBonds();
    }

    private int mult_min(int a, int b, int c, int d, int e, int f) {
        return Math.min(Math.min(Math.min(Math.min(Math.min(a, b), c), d), e),
                f);
    }

    private int mult_max(int a, int b, int c, int d, int e, int f) {
        return Math.max(Math.max(Math.max(Math.max(Math.max(a, b), c), d), e),
                f);
    }

    /*
     * Die neuen maximalen Werte in jede Richtung werden dem Paper entsprechend
     * berechnet
     */
    private void contureBond(ArrayList<ConvexContour> listContour) {
        ConvexContour a, b, c, d, e;
        if (listContour.size() >= 1) {
            a = listContour.get(0);
        } else {
            a = new ConvexContour();
            a.setToMax();
        }
        if (listContour.size() >= 2) {
            b = listContour.get(1);
        } else {
            b = new ConvexContour();
            b.setToMax();
        }
        if (listContour.size() >= 3) {
            c = listContour.get(2);
        } else {
            c = new ConvexContour();
            c.setToMax();
        }
        if (listContour.size() >= 4) {
            d = listContour.get(3);
        } else {
            d = new ConvexContour();
            d.setToMax();
        }

        if (listContour.size() >= 5) {
            e = listContour.get(4);
        } else {
            e = new ConvexContour();
            e.setToMax();
        }

        // Der Minimum zwischen allen Eckpunkte und der Vater Knoten
        int r_x_y = (int) (this.node.getHexa().getX() - this.node.getHexa()
                .getY());
        min_x = mult_min(a.min_x, b.min_x, c.min_x, d.min_x, e.min_x,
                (int) this.node.getHexa().getX());
        min_y = mult_min(a.min_y, b.min_y, c.min_y, d.min_y, e.min_y,
                (int) this.node.getHexa().getY());
        min_x_y = mult_min(a.min_x_y, b.min_x_y, c.min_x_y, d.min_x_y,
                e.min_x_y, r_x_y);
        max_x = mult_max(a.max_x, b.max_x, c.max_x, d.max_x, e.max_x,
                (int) this.node.getHexa().getX());
        max_y = mult_max(a.max_y, b.max_y, c.max_y, d.max_y, e.max_y,
                (int) this.node.getHexa().getY());
        max_x_y = mult_max(a.max_x_y, b.max_x_y, c.max_x_y, d.max_x_y,
                e.max_x_y, r_x_y);
    }

    /*
     * Diese Methode berechnet aus den bounds max_x.. usw. die Eckpunkte der
     * Kontur.
     */

    private void calcPerimeterFromBonds() {
        cLinks.setX(min_x);
        cLinks.setY(min_x - min_x_y);

        cObenLinks.setX(min_x);
        cObenLinks.setY(min_y);

        cObenRechts.setX(max_x_y + min_y);
        cObenRechts.setY(min_y);

        cRechts.setX(max_x);
        cRechts.setY(max_x - max_x_y);

        cUntenRechts.setX(max_x);
        cUntenRechts.setY(max_y);

        cUntenLinks.setX(min_x_y + max_y);
        cUntenLinks.setY(max_y);
    }

    /*
     * Diese Methode berechnet aus den Eckpunkte die maximalen Werte
     */
    private void calcBondsFromPerimeter() {
        min_x = (int) cObenLinks.getX();
        min_y = (int) cObenLinks.getY();
        min_x_y = (int) (cUntenLinks.getX() - cUntenLinks.getY());
        max_x = (int) cUntenRechts.getX();
        max_y = (int) cUntenRechts.getY();
        max_x_y = (int) (cObenRechts.getX() - cObenRechts.getY());
    }

    /*
     * Addiert einen Bewegungsvektor zu jeden Punkt der die Kontur ausmacht.
     * Also 6 additionen
     */
    public void move(int length, Directions dire) {
        Vector vector = Directions.toVector(dire);
        vector.multScalar(length);
        for (HexaCoord perimeter : surroundingPoints) {
            perimeter.add(vector);
        }
        calcBondsFromPerimeter();
    }

    public UniformNode getNode() {
        return node;
    }

    /*
     * Hilfsmethode um schnell eine Kontur zurueckzugeben die bei allen MinTest
     * nicht berueckstichtig wird
     */
    private void setToMax() {
        min_x = min_y = min_x_y = Integer.MAX_VALUE;
        max_x = max_y = max_x_y = Integer.MIN_VALUE;
    }
}
