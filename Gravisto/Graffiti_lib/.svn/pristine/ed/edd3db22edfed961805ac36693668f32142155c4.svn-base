// =============================================================================
//
//   PentaTree.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
/*
 * Diese Klasse enthält die beiden Kompaktierungsalgorithmen und nutzt
 * als Schnittstelle zwischen Gravisto und das vorhanden Code und meinen Programm
 */
package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;

/**
 * Datenstruktur eines local uniformen penta Baum. Alle Algorithmen werden davon
 * ausgehen das ein (gerichteter) Baum vorhanden ist.
 */
public class PentaTree {
    final int unit = 50; // Konstanten vom hexagonalen Gitter

    final double unitHeight = (Math.sqrt(3) / 2) * unit;

    private Node rootGraph; // Referenz zur Wurzel im Gravisto Graph

    private UniformNode rootPentaTree; // Referenz zur Wurzel im Baum

    private int heightPentaTree; // Hoehe des Baumes

    public PentaTree(Graph graph) {
        rootGraph = findRoot(graph);
        // Die Struktur wird kopiert unter der Annahme das es ein penta tree
        // ist.

        copyGraphtoPentaTreeStructure(graph);
        // Die Struktur ist jetzt kopiert und fuer jeden Knoten wird noch dazu
        // festgelegt welche Position er bezueglich seinen Vater den patterns
        // entsprechend hat.
    }

    private void copyGraphtoPentaTreeStructure(Graph graph) {
        rootPentaTree = new UniformNode(rootGraph, null);
        heightPentaTree = 0;
        copyRecursive(rootGraph, rootPentaTree);
    }

    // Die Reihenfolge der Kinder unter sich wird im Uhzeigersinn festgelegt
    private void copyRecursive(Node graphFather, UniformNode uniformFather) {
        if (heightPentaTree < uniformFather.depth) {
            heightPentaTree = uniformFather.depth;
        }
        // uniformFather muss jetzt die reihenfolge seiner Soehne festlegen.
        ArrayList<UniformNodeWrapper> tempList = new ArrayList<UniformNodeWrapper>();
        for (Node sonGraph : graphFather.getAllOutNeighbors()) {
            UniformNode son = new UniformNode(sonGraph, uniformFather);
            UniformNodeWrapper copy = new UniformNodeWrapper(son);
            tempList.add(copy); // Alle Soehne werden in einer temporaeren Liste
            // reingepackt
        }
        CoordinateAttribute fatherCoord = uniformFather.getCoordinates();

        double alpha;
        if (uniformFather.father != null) {
            CoordinateAttribute grandFatherCoord = uniformFather.father
                    .getCoordinates();
            // Alpha is the angle from the "starting" direction
            // to the father node. It is the cero reference..
            alpha = Math.atan2(grandFatherCoord.getY() - fatherCoord.getY(),
                    grandFatherCoord.getX() - fatherCoord.getX())
                    * (-180) / Math.PI;
            if (alpha < 0) {
                alpha = alpha + 360;
            }
        } else {
            alpha = 0;
        }

        // Zuerst werden alle soehne zum Punkt 0,0 transliert
        for (UniformNodeWrapper iter : tempList) {
            iter.x = iter.getCoordinates().getX(); // Die Koordinaten werden
            // kopiert bevor sie
            // veraendert werden
            iter.y = iter.getCoordinates().getY();

            iter.x -= fatherCoord.getX();
            iter.y -= fatherCoord.getY();
            double angle = Math.atan2(iter.y, iter.x) * (-180) / Math.PI;
            if (angle < 0) {
                angle = angle + 360;
            }
            angle = angle - alpha; // Rotation im Uhrzeigersinn
            if (angle < 0) {
                angle = angle + 360;
            }
            iter.angleToFather = angle;
        }
        Collections.sort(tempList);// Jetzt ist die tempList richtig sortiert

        // Jetzt wird fuer jeden Sohn seinen Pattern bzgl des Vaters festgelegt

        setOffsetsPatterns(uniformFather, tempList);

        // Die Soehne werden dann doch endgueltig der hilfsstruktur
        // "uniformGraph"
        // hinzugefuegt, um dann weiter mit diesen Baum zu arbeiten
        for (UniformNodeWrapper iter : tempList) {
            uniformFather.listSons.add(iter.original);
        }
        for (UniformNode iter : uniformFather.listSons) {
            // Rekursiver Aufruf auf die Soehne
            copyRecursive(iter.referenceToOriginal, iter);
        }
    }

    private Node findRoot(Graph graph) {
        // Von einem beliebigen Knoten aus wird entlang der "Gegenrichtungen"
        // hochgesucht
        // Klappt nicht falls es schleifen gibt, oder wenn es ein Wald ist....
        // Ist NUR korrekt wenn "graph" ein Baum ist.
        Node it = graph.getNodesIterator().next();

        while (it.getInDegree() > 0) { // Waehrend ich nicht an der Wurzel des
                                       // Baumes angekommen bin
            it = it.getAllInNeighbors().iterator().next();
        }
        return it;

    }

    @Override
    public String toString() {
        return ("height = " + heightPentaTree);
    }

    private void calculateLengthToChildren() {
        rootPentaTree.edgeLengthToChildren = (int) Math.pow(3.0,
                (double) heightPentaTree - 1);
        calculateRecursive(rootPentaTree);
    }

    private void calculateRecursive(UniformNode father) {
        for (UniformNode son : father.listSons) {
            if (son.getNumberOfChildren() != 0) {
                son.edgeLengthToChildren = (int) Math.pow(3.0, heightPentaTree
                        - son.depth - 1);
            } else {
                son.edgeLengthToChildren = 0;
            }
            calculateRecursive(son);
        }
    }

    /**
     * Ich gehe den ganzen Baum durch und speichere fuer jeden Sohn entsprechend
     * im welchen Pattern er ist den offset bezueglich seines Vaters. der dann
     * bei calCoord... mit der Richtung die der Vater hat + die endgueltigen
     * Koordinaten des Vaters auch seine endgueltige Koordinaten bekommen wird
     */
    private void setOffsetsPatterns(UniformNode actualNode,
            ArrayList<UniformNodeWrapper> tempList) {
        switch (tempList.size()) {
        case 0:
            break;
        case 1: // Straight Pattern
            tempList.get(0).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT);
            break;
        case 2: // Y Pattern
            tempList.get(0).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_RIGHT);
            tempList.get(1).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_LEFT);
            break;
        case 3: // Phi pattern
            tempList.get(0).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_RIGHT);
            tempList.get(1).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT);
            tempList.get(2).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_LEFT);
            break;
        case 4: // X pattern
            tempList.get(0).original.direction = Directions.add(
                    actualNode.direction, Directions.BACK_RIGHT);
            tempList.get(1).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_RIGHT);
            tempList.get(2).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_LEFT);
            tempList.get(3).original.direction = Directions.add(
                    actualNode.direction, Directions.BACK_LEFT);
            break;
        case 5: // Full pattern
            tempList.get(0).original.direction = Directions.add(
                    actualNode.direction, Directions.BACK_RIGHT);
            tempList.get(1).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_RIGHT);
            tempList.get(2).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT);
            tempList.get(3).original.direction = Directions.add(
                    actualNode.direction, Directions.STRAIGHT_LEFT);
            tempList.get(4).original.direction = Directions.add(
                    actualNode.direction, Directions.BACK_LEFT);
            break;
        }
    }

    /**
     * Mit einer Richtung und einer Laenge wird ein entsprechender Vektor gebaut
     */
    private Vector buildTranslationVector(Directions directions,
            int edgeLengthToChildren) {
        Vector vector = Directions.toVector(directions);
        vector.multScalar(edgeLengthToChildren);
        return vector;
    }

    // Die Koordinaten die die planaritaet garantieren werden berechnet
    public void calculateRecursiveCoordinates(UniformNode actualNode) {
        // Zu dieesm Punkt hat actualNode die selbe Koordinaten als sein Vater
        // Er speichert in node.richtung den Winkel in den er losgehen muss
        // und in seinem Vater ist die l�nge des Vektors gespeichert

        Vector vector = buildTranslationVector(actualNode.direction,
                actualNode.father.edgeLengthToChildren);
        // Actual Node wurde aktualisiert
        actualNode.hexa.add(vector);
        // Alle S�hne kriegen die selbe StartKoordinate als der Vater
        for (UniformNode son : actualNode.listSons) {
            son.hexa.copy(actualNode.hexa);
            calculateRecursiveCoordinates(son); // Rekursiver aufruf auf die
            // Soehne
        }
    }

    /**
     * Fuer jeden Knoten werden seine Koordinaten berechnet so das die
     * planaritaet des Baumes gewaehrleistet ist.
     */
    public void calculateCoordinatesPlanar() {
        calculateLengthToChildren(); // The constant length from a father to all
        // his child is calculated for each node

        // Start Koordinaten fuer die Wurzel
        rootPentaTree.hexa = new HexaCoord(0, 0);

        // Alle Soehne von der Wurzel kriegen erstmals die selben Koordinaten
        // als die Wurzel
        for (UniformNode son : rootPentaTree.listSons) {
            son.hexa.copy(rootPentaTree.hexa);
            calculateRecursiveCoordinates(son);
        }
    }

    /**
     * Die realen Koordinaten werden an den Knoten des originellen Graphes
     * geaendert
     */
    public void copyFinalCoordinatesToOriginalNodes() {
        CoordinateAttribute ca = rootPentaTree.getCoordinates();
        ca.setX(0.5 * unit); // *0.5 ist noetig damit die Wurzel auf der
        // Horizontalen richtig liegt
        ca.setY(0);
        copyRecursiveToOriginalNodes(rootPentaTree);
    }

    private void copyRecursiveToOriginalNodes(UniformNode actualNode) {
        CoordinateAttribute ca;
        for (UniformNode son : actualNode.listSons) {
            ca = son.getCoordinates();
            ca.setY(son.hexa.getY() * unitHeight);
            ca.setX((son.hexa.getX() + 0.5 - 0.5 * son.hexa.getY()) * unit); // *0.5
            copyRecursiveToOriginalNodes(son);
        }
    }

    /**
     * Der Kompaktierungs Algorithmus vom paper
     */
    ConvexContour computeCompactedEdgeLength(UniformNode r) {
        ConvexContour c = new ConvexContour(r);
        ArrayList<ConvexContour> contourList = new ArrayList<ConvexContour>();

        for (UniformNode son : r.listSons) {
            contourList.add(computeCompactedEdgeLength(son));
        }

        Integer edgeTrim = Integer.MAX_VALUE;

        for (int i = 0; i < contourList.size(); i++) {
            for (int j = 0; j < contourList.size(); j++) {
                if (i != j) {
                    ConvexContour c_i = contourList.get(i);
                    ConvexContour c_j = contourList.get(j);
                    UniformNode r_j = c_j.getNode();
                    UniformNode r_i = c_i.getNode();
                    Directions dire_i_j = Vector.cvtHexaVector(r_i.hexa,
                            r_j.hexa).direction();
                    edgeTrim = minBisNull(edgeTrim, Distances
                            .distanceSegmentConture(new Segment(r.hexa,
                                    r_j.hexa), c_i, dire_i_j) - 1);
                    Directions angle = Directions.subtract(r_i.direction,
                            r_j.direction);

                    if (angle == Directions.STRAIGHT_RIGHT
                            || angle == Directions.STRAIGHT_LEFT) {
                        edgeTrim = minBisNull(edgeTrim, Distances
                                .distanceConvexContureConture(c_i, c_j,
                                        dire_i_j) - 1);
                    } else {
                        edgeTrim = minBisNull(edgeTrim, (int) Math
                                .floor((Distances.distanceConvexContureConture(
                                        c_i, c_j, dire_i_j) - 1) / 2.0));

                    }
                }
            }
        }

        if (r.father != null) {
            for (ConvexContour s_i : contourList) {
                UniformNode t_i = s_i.getNode();
                edgeTrim = minBisNull(edgeTrim, Distances
                        .distanceSegmentConture(new Segment(r.hexa,
                                r.father.hexa), s_i, Vector.cvtHexaVector(
                                r.hexa, t_i.hexa).direction()) - 1);
            }
        } else {
            for (ConvexContour s_i : contourList) {
                UniformNode t_i = s_i.getNode();

                edgeTrim = minBisNull(
                        edgeTrim,
                        Distances.distanceHexaCoordConture(r.hexa, s_i, Vector
                                .cvtHexaVector(r.hexa, t_i.hexa).direction()) - 1);
            }
        }

        if (edgeTrim == (Integer.MAX_VALUE)) {
            edgeTrim = 0;
        } else {
            for (ConvexContour s_i : contourList) {
                s_i.move(edgeTrim, Directions.reverse(s_i.getNode().direction));
            }
        }
        // Wenn es ein Blatt ist dann macht merge keinen Sinn
        if (contourList.size() > 0) {
            c.merge(contourList);
        }

        r.edgeLengthToChildren -= edgeTrim;
        return c;

    }

    public void calculateCompactedCoordinates() {
        // computeCompactedEdgeLength(rootPentaTree);
        computeOptimalEdgeLength(rootPentaTree);

        // Da neue edgeToChildren Abstaende berechnet wurden
        // werden die neuen Koordinaten von allen Knoten einfach neu berechnet
        // Start Koordinaten fuer die Wurzel
        rootPentaTree.hexa = new HexaCoord(0, 0);

        // Alle Soehne von der Wurzel kriegen erstmals die selben Koordinaten
        // als die Wurzel
        for (UniformNode son : rootPentaTree.listSons) {
            son.hexa.copy(rootPentaTree.hexa);
            calculateRecursiveCoordinates(son);
        }

    }

    public void calculateCompactedCoordinatesConvexContours() {
        // computeCompactedEdgeLength(rootPentaTree);

        computeCompactedEdgeLength(rootPentaTree);
        // Da neue edgeToChildren Abstaende berechnet wurden
        // werden die neuen Koordinaten von allen Knoten einfach neu berechnet
        // Start Koordinaten fuer die Wurzel
        rootPentaTree.hexa = new HexaCoord(0, 0);

        // Alle Soehne von der Wurzel kriegen erstmals die selben Koordinaten
        // als die Wurzel
        for (UniformNode son : rootPentaTree.listSons) {
            son.hexa.copy(rootPentaTree.hexa);
            calculateRecursiveCoordinates(son);
        }

    }

    private int minBisNull(int a, int d) {
        int min = Math.min(a, d);
        if (min >= 0)
            return min;
        else
            return 0;
    }

    /**
     * Berechnet fuer den Knoten v, seine Kontur und gibt sie im rekursiven
     * Schritt zurueck auf seinen Vater
     */

    public Contour computeOptimalEdgeLength(UniformNode v) {
        if (v.listSons.isEmpty())
            return new Contour(v);

        Integer edgeTrim = Integer.MAX_VALUE;
        ArrayList<Contour> contourList = new ArrayList<Contour>();
        // Hier werden alle Konturen von allen Kindern von v sein.

        for (UniformNode son : v.listSons) {
            contourList.add(computeOptimalEdgeLength(son));
        }

        for (Contour c : contourList) {

            c.expand(v);
        }
        // Alle Konturen werden expandiert

        int anzahlSoehne = v.listSons.size();
        if (contourList.size() == 1) {
            edgeTrim = (int) (v.hexa
                    .distanceToPoint(contourList.get(0).root.hexa) - 1);
        } else {
            for (int i = 0; i < anzahlSoehne; i++) {
                Contour c_i = contourList.get(i);
                if (v.hexa.distanceToPoint(c_i.root.hexa) == 1) {
                    edgeTrim = 0;// Das erspart mir die abstandsberechnung bei
                    // den Mustern wo das eh nix bringt weil die
                    // alle die Kantenaenge zu v 1 haben
                } else {
                    for (int j = 0; j < anzahlSoehne; j++) {
                        if (i != j) {
                            Contour c_j = contourList.get(j);
                            Directions dire_j_i = Vector.cvtHexaVector(
                                    c_j.root.hexa, c_i.root.hexa).direction();
                            // Das ist IMMER (egal welcher Typ) die Richtung in
                            // der verglichen wird
                            Directions dire_i_j = Vector.cvtHexaVector(
                                    c_i.root.hexa, c_j.root.hexa).direction();
                            Directions watchDire = dire_i_j;

                            Directions angle = Directions.subtract(
                                    c_i.root.direction, c_j.root.direction);
                            // Wenn es eine HalfConture mit zwei Seiten ist,
                            // dann muss links = 0 mit rechts = 1 verglichen
                            // werden sonst einfach so vergleichen
                            HalfConture comparand1;
                            if (c_i.seite[dire_j_i.ordinal()][0].seite == Seite.Links) {
                                comparand1 = c_i.seite[dire_j_i.ordinal()][0];
                            } else {
                                comparand1 = c_i.seite[dire_j_i.ordinal()][1];
                            }

                            HalfConture comparand2;

                            if (c_j.seite[dire_i_j.ordinal()][0].seite == Seite.Rechts) {
                                comparand2 = c_j.seite[dire_i_j.ordinal()][0];
                            } else {
                                comparand2 = c_j.seite[dire_i_j.ordinal()][1];
                            }

                            // Jetzt habe ich die zwei haelften die ich
                            // vergleichen will
                            // Wenn beide verbunden mit root sind dann ist
                            // leicht

                            ContureHexa comp1;
                            ContureHexa comp2;
                            if (comparand1.hexaFirst.equal(v.hexa)) {
                                comp1 = comparand1.hexaFirst;
                            } else if (comparand1.hexaSecond.equal(v.hexa)) {
                                comp1 = comparand1.hexaSecond;
                            } else {
                                comp1 = null;
                            }

                            if (comparand2.hexaFirst.equal(v.hexa)) {
                                comp2 = comparand2.hexaFirst;
                            } else if (comparand2.hexaSecond.equal(v.hexa)) {
                                comp2 = comparand2.hexaSecond;
                            } else {
                                comp2 = null;
                            }

                            if ((comp1 == null || comp2 == null)) {
                                // Das heisst das eine von beiden nicht mit v
                                // verbunden ist, also muessen die Punkte
                                // sortiert werden und dann rausgenommen werden
                                if (comp1 == null && comp2 != null) {
                                    // Dann ist nur einer nicht verbunden
                                    if (v.hexa
                                            .distanceToPoint(comparand1.hexaFirst) > v.hexa
                                            .distanceToPoint(comparand1.hexaSecond)) {
                                        comp1 = comparand1.hexaSecond;
                                    } else {
                                        comp1 = comparand1.hexaFirst;
                                    }
                                } else if (comp1 != null && comp2 == null) {
                                    // klappt das mit distance to point wenn
                                    // start und ziel nicht auf einer gerade
                                    // sind!??! ich denke schon! kuerzester weg!
                                    if (v.hexa
                                            .distanceToPoint(comparand2.hexaFirst) > v.hexa
                                            .distanceToPoint(comparand2.hexaSecond)) {
                                        comp2 = comparand2.hexaSecond;
                                    } else {
                                        comp2 = comparand2.hexaFirst;
                                    }
                                } else {
                                    // Beide sind nicht verbunden
                                    LinkedList<ContureHexa> punkte = returnTheBeginnings(
                                            comparand1, comparand2, watchDire);
                                    comp1 = punkte.removeFirst();
                                    comp2 = punkte.removeFirst();
                                }
                            } // Jetzt sind comp1 und comp2 gesetzt!
                            // Falls eine von beiden v ist, aber leer, dann war
                            // es der Sonderfall von Typ2B,
                            // in diesem Fall wird kein Abstand berechnet, in
                            // allen anderen Faellen ja
                            if ((!comp1.equal(v.hexa) || !comp1.ausgehende
                                    .isEmpty())
                                    && (!comp2.equal(v.hexa) || !comp2.ausgehende
                                            .isEmpty())) {

                                if ((angle == Directions.STRAIGHT_RIGHT)) {
                                    edgeTrim = minBisNull(
                                            edgeTrim,
                                            (int) Distances
                                                    .distanceContourContour(
                                                            comp1, comp2,
                                                            watchDire,
                                                            dire_j_i, dire_i_j) - 1);
                                } else {
                                    edgeTrim = minBisNull(
                                            edgeTrim,
                                            (int) Distances
                                                    .distanceContourContour(
                                                            comp1, comp2,
                                                            watchDire,
                                                            dire_j_i, dire_i_j) / 2 - 1);
                                }
                            }

                        }

                    }
                }
            }

        }
        // Mergen
        // Zuerst werden linke und rechte Seite zusammengeschmolzen
        if (v.father != null) {
            for (Contour c : contourList) {
                c.meltFirstSecond(v.hexa);
            }
        }

        // Noch das edgetrim fuer die Vaterkante, jetzt sind sie alle
        // zusammengemeltet worden

        if (v.father != null && contourList.size() > 1) {
            for (int i = 0; i < anzahlSoehne; i++) {
                Contour c_i = contourList.get(i);
                Directions watchDire = Vector.cvtHexaVector(c_i.root.hexa,
                        v.hexa).direction();
                Directions oppWatch = Directions.reverse(watchDire);
                HalfConture kontur;

                if (!c_i.seite[oppWatch.ordinal()][0].isTrivial()
                        || !c_i.seite[oppWatch.ordinal()][1].isTrivial()) {
                    Segment grossvaterkante = new Segment(v.hexa, v.father.hexa);
                    HalfConture grossVaterKontur = grossvaterkante
                            .convertToHalfContour(watchDire);
                    ContureHexa anfangGross;
                    if (grossVaterKontur.hexaFirst.equal(v.hexa)) {
                        anfangGross = grossVaterKontur.hexaFirst;
                    } else if (grossVaterKontur.hexaSecond.equal(v.hexa)) {
                        anfangGross = grossVaterKontur.hexaSecond;
                    } else {
                        anfangGross = null;
                    }

                    if (!c_i.seite[oppWatch.ordinal()][0].isTrivial()
                            && !c_i.seite[oppWatch.ordinal()][1].isTrivial()) {
                        // Dann habe ich zwei,
                        if (c_i.seite[oppWatch.ordinal()][0].hexaFirst
                                .equal(v.hexa)) {
                            if (Vector
                                    .seiteMitRichtung(
                                            watchDire,
                                            v.hexa,
                                            c_i.seite[oppWatch.ordinal()][0].hexaSecond) == Vector
                                    .seiteMitRichtung(watchDire, v.hexa,
                                            anfangGross.otherEnd)) {
                                kontur = c_i.seite[oppWatch.ordinal()][0];
                            } else {
                                kontur = c_i.seite[oppWatch.ordinal()][1];
                            }
                        } else {
                            if (Vector.seiteMitRichtung(watchDire, v.hexa,
                                    c_i.seite[oppWatch.ordinal()][0].hexaFirst) == Vector
                                    .seiteMitRichtung(watchDire, v.hexa,
                                            anfangGross.otherEnd)) {
                                kontur = c_i.seite[oppWatch.ordinal()][0];
                            } else {
                                kontur = c_i.seite[oppWatch.ordinal()][1];
                            }
                        }

                    } else {
                        kontur = Contour.unused(
                                c_i.seite[oppWatch.ordinal()][0],
                                c_i.seite[oppWatch.ordinal()][1]);
                    }

                    ContureHexa anfangKont;
                    if (kontur.hexaFirst.equal(v.hexa)) {
                        anfangKont = kontur.hexaFirst;
                    } else if (kontur.hexaSecond.equal(v.hexa)) {
                        anfangKont = kontur.hexaSecond;
                    } else {
                        anfangKont = null;
                    }

                    if (!grossVaterKontur.isTrivial() && !kontur.isTrivial()) {
                        edgeTrim = minBisNull(edgeTrim, (int) Distances
                                .distanceContourContour(anfangGross,
                                        anfangKont, watchDire, watchDire,
                                        oppWatch) - 1);
                    }
                }

            }
        }

        // Jetzt hat edgeTrim den richtigen Wert, die Vaterkanten muessen
        // verkuerzt werden
        // Dazu reicht die begins und ends der Konturen + den ERSTEN (reicht)
        // Vektor der mit den Grossvater verbunden ist
        // verkleinern.

        if (edgeTrim > 0 && edgeTrim < Integer.MAX_VALUE / 4) {
            v.edgeLengthToChildren -= edgeTrim; // neue Kantenl�nge festsetzten
            if (v.father != null) {
                for (Contour c : contourList) {
                    c.compress(edgeTrim, v.hexa);
                }
            }
        }

        Contour ergebnis = null;
        if (v.father != null) {
            // muss man die letzte Konturen NICHT mergen, denn
            // der mindestabstand wurde schon vorher
            // berechnet und angewendet
            ergebnis = Contour.mergeAllContours(contourList, v);
        }

        return ergebnis;

    }

    /**
     * Falls die beiden haelften einen Punkt gemeinsam haben werden gleich die
     * zwei zurueckgegeben Falls eine mit v verbunden ist, wird dieser Punkt
     * gemeinsam mit dem am naehesten an v von der anderen zurueckgegeben Falls
     * keine mit v verbunden ist, wird sortiert und einfach zwei
     * aufeinanderfolgende zurueckgegeben
     */

    private LinkedList<ContureHexa> returnTheBeginnings(HalfConture comparand1,
            HalfConture comparand2, Directions watchDire) {
        LinkedList<ContureHexaWrapper> punkte = new LinkedList<ContureHexaWrapper>();
        LinkedList<ContureHexa> ergebnis = new LinkedList<ContureHexa>();
        punkte.add(new ContureHexaWrapper(comparand1.hexaFirst, watchDire,
                null, null));
        punkte.add(new ContureHexaWrapper(comparand1.hexaSecond, watchDire,
                null, null));
        punkte.add(new ContureHexaWrapper(comparand2.hexaFirst, watchDire,
                null, null));
        punkte.add(new ContureHexaWrapper(comparand2.hexaSecond, watchDire,
                null, null));
        Collections.sort(punkte);
        ContureHexa punkt1 = punkte.removeFirst().original;
        ContureHexa punkt2 = punkte.removeFirst().original;

        if (punkt1.otherEnd.equal(punkt2)) {
            // Dann ist anfang2 in der selben Kontur enthalten wie anfang 1
            punkt2 = punkte.removeFirst().original; // Jetzt muss es klappen
        }
        ergebnis.add(punkt1);
        ergebnis.add(punkt2);
        return ergebnis;

    }
}
