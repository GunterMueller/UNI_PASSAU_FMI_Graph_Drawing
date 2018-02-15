// =============================================================================
//
//   Contour.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Implementiert die neuen Konturen als zwei dimensionales array. Eine Dimension
 * stellt die 12 moelichen Betrachtungsrichtungen dar die andere die erste und
 * zweite haelfte einer Kontur. In dieser Klasse werden auch alle Methoden zum
 * mergen von Konturen implementiert
 */
public class Contour {
    public UniformNode root; // Referenz zum Knoten der diese Kontur besitzt

    public HalfConture[][] seite = new HalfConture[12][2]; // 2 Seiten,

    // 12 Betrachtungsrichtungen
    public ContourType type[] = new ContourType[12]; // Typ der jeweiligen

    // Konturen

    /**
     * Die triviale Kontur wird erstellt
     * 
     * @param v
     *            Vaterknoten der Kontur
     */
    public Contour(UniformNode v) {
        root = v;
        for (Directions dire : Directions.values()) {
            seite[dire.ordinal()][0] = new HalfConture(null, this.root.hexa);
            seite[dire.ordinal()][0].hexaFirst.copy(v.hexa);
            seite[dire.ordinal()][0].hexaSecond.copy(v.hexa);
            seite[dire.ordinal()][0].hexaFirst.otherEnd = seite[dire.ordinal()][0].hexaSecond;
            seite[dire.ordinal()][0].hexaSecond.otherEnd = seite[dire.ordinal()][0].hexaFirst;
            seite[dire.ordinal()][1] = new HalfConture(
                    seite[dire.ordinal()][0], this.root.hexa); // Die zweite
                                                               // Seite
            // wird leer sein

            seite[dire.ordinal()][0].otherHalf = seite[dire.ordinal()][1];
            seite[dire.ordinal()][1].used = true;
            if (v.hexa.aims(dire, v.father.hexa)) {
                type[dire.ordinal()] = ContourType.One;
            } else {
                type[dire.ordinal()] = ContourType.TwoB;
            }
        }

    }

    /**
     * Expandieren von Konturen. Die Kontur this wird mit ihrem Vater v
     * expandiert Davon abhaengig welches Typ sie hat wird sie auch anders
     * expandiert.
     * 
     * @param v
     */
    public void expand(UniformNode v) {
        Segment vaterkante = new Segment(v.hexa, root.hexa);

        for (Directions dire : Directions.values()) {
            if (this.type[dire.ordinal()] == ContourType.One) {
                // Falls Typ 0 passiert nichts
            } else if (this.type[dire.ordinal()] == ContourType.TwoA) { // Es
                                                                        // liegt
                                                                        // eine
                                                                        // Typ
                                                                        // 2A
                                                                        // Kontur
                                                                        // vor
                                                                        // also
                                                                        // wird
                // eine von beiden mind. teilweise von der Vaterkante
                // bedeckt

                HalfConture ersteSeite = this.seite[dire.ordinal()][0];
                HalfConture zweiteSeite = this.seite[dire.ordinal()][1];

                ContureHexa comparand1 = null, comparand2 = null;
                // Zuerst werden die Seiten festgelegt und es wird ermittelt
                // welchen Punkt beide Konturen gemeinsam haben.

                if (ersteSeite.hexaFirst.equal(zweiteSeite.hexaFirst)) {
                    comparand1 = ersteSeite.hexaFirst;
                    comparand2 = zweiteSeite.hexaFirst;
                } else if (ersteSeite.hexaFirst.equal(zweiteSeite.hexaSecond)) {
                    comparand1 = ersteSeite.hexaFirst;
                    comparand2 = zweiteSeite.hexaSecond;
                } else if (ersteSeite.hexaSecond.equal(zweiteSeite.hexaFirst)) {
                    comparand1 = ersteSeite.hexaSecond;
                    comparand2 = zweiteSeite.hexaFirst;
                } else if (ersteSeite.hexaSecond.equal(zweiteSeite.hexaSecond)) {
                    comparand1 = ersteSeite.hexaSecond;
                    comparand2 = zweiteSeite.hexaSecond;
                }

                HexaCoord punktOnContour1 = comparand1
                        .addTemp(comparand1.ausgehende.getFirst());
                HexaCoord punktOnContour2 = comparand2
                        .addTemp(comparand2.ausgehende.getFirst());
                Vector toAdd = Vector.cvtHexaVector(comparand1, v.hexa);

                if (Segment.bedeckt(dire, root.hexa, v.hexa, comparand2,
                        punktOnContour2)
                        && (Segment.bedeckt(dire, root.hexa, v.hexa,
                                comparand1, punktOnContour1))) {
                    // Wenn keiner den anderen bedeckt dann muss es der
                    // sonderfall sein!! also werden die haelften expandiert die
                    // nicht trivial sind
                    Vector toAdd1 = Vector.cvtHexaVector(comparand1, v.hexa);
                    comparand1.add(toAdd1);
                    comparand1.eingehende.addLast(toAdd1);
                    comparand1.ausgehende.addFirst(toAdd1.inverse());
                    ersteSeite.seite = Vector.seiteMitRichtung(dire, root.hexa,
                            comparand1.otherEnd);

                    Vector toAdd2 = Vector.cvtHexaVector(comparand2, v.hexa);
                    comparand2.add(toAdd2);
                    comparand2.eingehende.addLast(toAdd2);
                    comparand2.ausgehende.addFirst(toAdd2.inverse());
                    zweiteSeite.seite = Seite.opposite(ersteSeite.seite);
                } else if (Segment.bedeckt(dire, comparand1, punktOnContour1,
                        comparand2, punktOnContour2)
                        || (Segment.bedeckt(dire, root.hexa, v.hexa,
                                comparand2, punktOnContour2))) {
                    // Erste bedeckt die zweite
                    comparand1.add(toAdd);
                    comparand1.eingehende.addLast(toAdd);
                    comparand1.ausgehende.addFirst(toAdd.inverse());
                    ersteSeite.seite = Vector.seiteMitRichtung(dire, root.hexa,
                            comparand1);
                    zweiteSeite.seite = Seite.opposite(ersteSeite.seite);
                } else if (Segment.bedeckt(dire, comparand2, punktOnContour2,
                        comparand1, punktOnContour1)
                        || (Segment.bedeckt(dire, root.hexa, v.hexa,
                                comparand1, punktOnContour1))) {
                    // Muss es genau andersrum sein
                    comparand2.add(toAdd);
                    comparand2.eingehende.addLast(toAdd);
                    comparand2.ausgehende.addFirst(toAdd.inverse());
                    zweiteSeite.seite = Vector.seiteMitRichtung(dire,
                            root.hexa, comparand2);
                    ersteSeite.seite = Seite.opposite(zweiteSeite.seite);
                }

            } else if (this.type[dire.ordinal()] == ContourType.TwoB) {// Es
                                                                       // liegt
                                                                       // eine
                                                                       // Typ2B
                                                                       // Kontur
                                                                       // vor
                ContureHexa anfangVaterkante;

                HalfConture nichtTriviale = unused(
                        this.seite[dire.ordinal()][0], this.seite[dire
                                .ordinal()][1]);

                HalfConture vaterKanteKontur = vaterkante
                        .convertToHalfContour(dire);

                if (!vaterKanteKontur.isTrivial()) { // das kann nur sein weil
                                                     // sie "von unten" oder von
                                                     // oben
                    // angeschaut wird

                    if (vaterKanteKontur.hexaFirst.equal(root.hexa)) {
                        anfangVaterkante = vaterKanteKontur.hexaFirst;
                    } else {
                        anfangVaterkante = vaterKanteKontur.hexaSecond;
                        // Die enden der grossvaterkante muessen mindestens
                        // einen
                        // der
                        // zwei Endpunkte der Kontur bedecken
                    }

                    if (nichtTriviale.hexaFirst.equal(root.hexa)
                            || nichtTriviale.hexaSecond.equal(root.hexa)) {
                        ContureHexa connection = null;
                        if (nichtTriviale.hexaFirst.equal(root.hexa)) {
                            connection = nichtTriviale.hexaFirst;
                        } else {
                            connection = nichtTriviale.hexaSecond;
                        }

                        if (Vector.seiteMitRichtung(dire, root.hexa,
                                anfangVaterkante.otherEnd) != Vector
                                .seiteMitRichtung(dire, root.hexa,
                                        connection.otherEnd)
                                && Vector.seiteMitRichtung(dire, root.hexa,
                                        connection.otherEnd) != Seite.Oben) {

                            HalfConture.concatenate(nichtTriviale,
                                    vaterKanteKontur);
                        } else {
                            HexaCoord proj1 = Distances
                                    .calculateProjectionPointOnSegmentWithDire(
                                            connection.otherEnd, vaterkante,
                                            dire);
                            if (proj1 == null) {
                                // Die nichttriviale wird verdeckt
                                nichtTriviale.hexaFirst
                                        .copy(vaterKanteKontur.hexaFirst);
                                nichtTriviale.hexaSecond
                                        .copy(vaterKanteKontur.hexaSecond);
                                nichtTriviale.firstSecond = vaterKanteKontur.firstSecond;
                                nichtTriviale.secondFirst = vaterKanteKontur.secondFirst;
                                nichtTriviale.hexaFirst.ausgehende = nichtTriviale.firstSecond;
                                nichtTriviale.hexaFirst.eingehende = nichtTriviale.secondFirst;
                                nichtTriviale.hexaSecond.eingehende = nichtTriviale.firstSecond;
                                nichtTriviale.hexaSecond.ausgehende = nichtTriviale.secondFirst;
                                nichtTriviale.used = false;

                            } else {
                                HalfConture.mergeExp(connection,
                                        anfangVaterkante, dire, v.hexa);
                            }
                        }

                    } else {
                        HexaCoord proj1 = Distances
                                .calculateProjectionPointOnSegmentWithDire(
                                        nichtTriviale.hexaFirst, vaterkante,
                                        dire);
                        HexaCoord proj2 = Distances
                                .calculateProjectionPointOnSegmentWithDire(
                                        nichtTriviale.hexaSecond, vaterkante,
                                        dire);
                        HexaCoord proj3 = Distances
                                .calculateProjectionPointOnSegmentWithDire(
                                        nichtTriviale.hexaFirst, vaterkante,
                                        Directions.reverse(dire));
                        HexaCoord proj4 = Distances
                                .calculateProjectionPointOnSegmentWithDire(
                                        nichtTriviale.hexaSecond, vaterkante,
                                        Directions.reverse(dire));

                        if (proj1 != null || proj2 != null) {
                            // eine von beiden ist auf der selben Linie mit
                            // root.
                            if (proj1 != null && proj2 != null) {
                                // Eine muss von root geaimt werden
                                if (root.hexa.aims(dire, proj1)) {
                                    HalfConture.mergeExp(
                                            nichtTriviale.hexaFirst,
                                            anfangVaterkante, dire, v.hexa);
                                } else if (root.hexa.aims(dire, proj2)) {
                                    HalfConture.mergeExp(
                                            nichtTriviale.hexaSecond,
                                            anfangVaterkante, dire, v.hexa);
                                }
                            } else {
                                // normaler vaterkante bedeckt KOntur
                                if (proj1 != null) {
                                    HalfConture.mergeExp(
                                            nichtTriviale.hexaSecond,
                                            anfangVaterkante, dire, v.hexa);
                                } else if (proj2 != null) {
                                    HalfConture.mergeExp(
                                            nichtTriviale.hexaFirst,
                                            anfangVaterkante, dire, v.hexa);
                                }
                            }

                        } else if (proj3 != null || proj4 != null) {
                            if (proj3 != null && proj4 != null) {
                                // Eine muss von root geaimt werden
                                if (root.hexa.aims(dire, proj3)) {
                                    HalfConture.mergeExp(anfangVaterkante,
                                            nichtTriviale.hexaSecond, dire,
                                            v.hexa);
                                } else if (root.hexa.aims(dire, proj4)) {
                                    HalfConture.mergeExp(anfangVaterkante,
                                            nichtTriviale.hexaFirst, dire,
                                            v.hexa);
                                }
                            } else {
                                // normaler vaterkante bedeckt KOntur
                                if (proj3 != null) {
                                    HalfConture.mergeExp(
                                            anfangVaterkante.otherEnd,
                                            nichtTriviale.hexaFirst, dire,
                                            v.hexa);
                                } else if (proj4 != null) {
                                    HalfConture.mergeExp(
                                            anfangVaterkante.otherEnd,
                                            nichtTriviale.hexaSecond, dire,
                                            v.hexa);
                                }
                            }
                        }

                    }

                    // Eine von beiden seiten enthaelt die 2b KOntur die andere
                    // muss ich einpuenktig initialisieren
                    if (this.seite[dire.ordinal()][0].isTrivial()) {
                        // Die nullte Seite ist die triviale
                        // **Bei der anderen haelfte wird eine einpuenktige
                        // Kontur
                        // gestellt
                        this.seite[dire.ordinal()][0] = new HalfConture(
                                this.seite[dire.ordinal()][1], this.root.hexa);
                        this.seite[dire.ordinal()][1].otherHalf = this.seite[dire
                                .ordinal()][0];
                    } else {
                        this.seite[dire.ordinal()][1] = new HalfConture(
                                this.seite[dire.ordinal()][0], this.root.hexa);
                        this.seite[dire.ordinal()][0].otherHalf = this.seite[dire
                                .ordinal()][1];
                    }

                    nichtTriviale.otherHalf.hexaFirst.copy(this.root.hexa);
                    // zeigt weiter auf die die NICHT geaendert wurde
                    nichtTriviale.otherHalf.hexaSecond.copy(this.root.hexa);

                    // eine von beiden ecken von nichtTriviale ist jetzt mit v
                    // verbunden die andere nicht
                    ContureHexa anfangKontur;
                    if (nichtTriviale.hexaFirst.equal(v.hexa)) {
                        anfangKontur = nichtTriviale.hexaFirst;
                    } else {
                        anfangKontur = nichtTriviale.hexaSecond;
                    }
                    nichtTriviale.seite = Vector.seiteMitRichtung(dire,
                            root.hexa, anfangKontur);

                    nichtTriviale.otherHalf.seite = Seite
                            .opposite(nichtTriviale.seite);
                } else { // Also "leere" Kontur, den Typ 1 wurde schon vorher
                    // abgeprueft!
                    // Auf jedem Fall hier schon einp�nktig
                    if (vaterKanteKontur.hexaFirst.equal(v.hexa)) {
                        nichtTriviale.hexaFirst
                                .copy(vaterKanteKontur.hexaFirst);
                        nichtTriviale.hexaSecond
                                .copy(vaterKanteKontur.hexaSecond);
                        nichtTriviale.seite = Seite.Links;
                        nichtTriviale.otherHalf.seite = Seite.Rechts;
                        // Die Kontur bleibt 2B nur der Punkt ist jetzt tiefer,
                        // die haelfte bleibt "unused"!
                    }

                }
            }

        }
        // Was hier geschieht gilt fuer "alle" Richtungen

    }

    /**
     * Merged alle Konturen zusammen. Zuerst die Typ 1 unter sich. Dann die Typ
     * 2. Zuletzt das Ergebnis der Typ 1 mit Typ 2
     * 
     * @param contourList
     *            Ergebnis der Konturen in den vorherigen Schritte
     * @return Zusammengemergte Konturen und den neu berechneten "middle" Punkt
     */
    public static Contour mergeAllContours(ArrayList<Contour> contourList,
            UniformNode v) {
        if (contourList.size() > 1) {
            Segment grossVaterKante = new Segment(v.hexa, v.father.hexa);

            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte = new ArrayList<LinkedList<SplitPacket<Vector>>>();
            // Initialisierung des Felds um die Schnitte mit (v,v.parent) zu
            // speichern
            for (int i = 0; i < 12; i++) {
                schnitte.add(new LinkedList<SplitPacket<Vector>>());
            }

            // Ich muss immer aufpassen, das beim mergen von zwei beliebigen
            // Konturen wenn ich auf die grossvaterkante stosse, das ich den
            // Punkt
            // speichere

            // Dann werden kreuzungen zwischen den Typ 2 hälften und die Typ 1
            // haelften gesucht
            seekCrossingsTyp2WithTyp1(contourList, v.hexa, v.father.hexa,
                    grossVaterKante);

            // Zuerst alle Typ 1 unter ich
            mergeTyp1Contours(contourList, v.hexa, grossVaterKante, schnitte);

            // Dann alle Typ 2 unter sich
            mergeTyp2Contours(contourList, v.hexa, grossVaterKante, schnitte);

            // Jetzt sollten nur noch 1 Typ 1 KOntur uebrig sein und zwei Typ 2
            mergeTyp2WithTyp1(contourList, v.hexa, v.father.hexa,
                    grossVaterKante, schnitte);

            Contour ergebnis = copyTogether(contourList, v);

            clean(schnitte, v.hexa);
            split(ergebnis, v.hexa, v.father.hexa, schnitte);
            mergeTyp2AreTyp1(ergebnis, v.hexa, v.father.hexa);

            decideType(ergebnis, v.hexa, v.father.hexa);
            // Jetzt sollte nur noch die endgueltige uebrig sein
            makeUp(ergebnis);
            return ergebnis;
        } else
        // Wenn v nur einen Sohn hat kann sich nicht viel geandert haben
        {
            contourList.get(0).root = v;
            makeUp(contourList.get(0));
            decideType(contourList.get(0), v.hexa, v.father.hexa);
            return contourList.get(0);
        }

    }

    /**
     * Dies ist noetig um die referenzen der Ergebnis Kontur wieder richtig zu
     * haben
     * 
     * @param ergebnis
     */
    private static void makeUp(Contour ergebnis) {
        for (Directions dire : Directions.values()) {
            ergebnis.seite[dire.ordinal()][0].otherHalf = ergebnis.seite[dire
                    .ordinal()][1];
            ergebnis.seite[dire.ordinal()][1].otherHalf = ergebnis.seite[dire
                    .ordinal()][0];
            ergebnis.seite[dire.ordinal()][0].refRoot = ergebnis.root.hexa;
            ergebnis.seite[dire.ordinal()][1].refRoot = ergebnis.root.hexa;
            ergebnis.seite[dire.ordinal()][0].listCrossings = new LinkedList<HalfConture>();
            ergebnis.seite[dire.ordinal()][0].listSwallowed = new LinkedList<HalfConture>();
            ergebnis.seite[dire.ordinal()][1].listCrossings = new LinkedList<HalfConture>();
            ergebnis.seite[dire.ordinal()][1].listSwallowed = new LinkedList<HalfConture>();
        }
    }

    /**
     * Diese Methode sucht nach kreuzungen zwischen den Konturen ohne das mergen
     * durchzufuehren. Die kreuzungen werden dann bei jeder einzelnen Kontur
     * gespeichert
     * 
     * @param contourList
     * @param grossVaterKante
     */

    private static void seekCrossingsTyp2WithTyp1(
            ArrayList<Contour> contourList, HexaCoord v, HexaCoord grossVater,
            Segment grossVaterKante) {
        ContureHexa anfangB0, anfangTyp1B0 = null;

        for (int k = 0; k < 2; k++) {
            for (Directions dire : Directions.values()) {
                Contour a, b;

                for (int i = 0; i < contourList.size(); i++) {
                    for (int j = 0; j < contourList.size(); j++) {
                        if (i != j) {
                            a = contourList.get(i);
                            HexaCoord rootA = a.root.hexa;
                            HalfConture unbenutztA = unused(a.seite[dire
                                    .ordinal()][0], a.seite[dire.ordinal()][1]);
                            if (unbenutztA != null)
                                if (a.type[dire.ordinal()] == ContourType.One) {
                                    b = contourList.get(j);

                                    HalfConture bSeite = b.seite[dire.ordinal()][k];
                                    if (!bSeite.used
                                            && (b.type[dire.ordinal()] == ContourType.TwoA || b.type[dire
                                                    .ordinal()] == ContourType.TwoB)) {
                                        HexaCoord rootB = b.root.hexa;
                                        Directions direSiblings = Vector
                                                .cvtHexaVector(rootA, rootB)
                                                .direction();
                                        if (!bSeite.isTrivial()) {

                                            Directions trennwand = Vector
                                                    .cvtHexaVector(v,
                                                            a.root.hexa)
                                                    .direction();

                                            if (bSeite.hexaFirst.equal(v)) {
                                                anfangB0 = bSeite.hexaFirst;
                                            } else {
                                                anfangB0 = bSeite.hexaSecond;
                                            }

                                            // Zuerst fragen ob eine der zwei
                                            // enden der Typ1 Kontur auf v
                                            // projeziert
                                            if (unbenutztA.hexaFirst.aims(dire,
                                                    v)
                                                    || unbenutztA.hexaSecond
                                                            .aims(dire, v)) {
                                                if (unbenutztA.hexaFirst.aims(
                                                        dire, v)) {
                                                    anfangTyp1B0 = unbenutztA.hexaSecond;
                                                } else {
                                                    anfangTyp1B0 = unbenutztA.hexaFirst;
                                                }

                                                if (Vector.seiteMitRichtung(
                                                        dire, v, anfangTyp1B0) == Vector
                                                        .seiteMitRichtung(
                                                                dire,
                                                                v,
                                                                anfangB0.otherEnd)) {
                                                    anfangTyp1B0 = anfangTyp1B0.otherEnd; // Falls
                                                }
                                            } else {
                                                if (Vector.seiteMitRichtung(
                                                        trennwand, v,
                                                        unbenutztA.hexaFirst) == Seite.Oben) {
                                                    if (Vector
                                                            .seiteMitRichtung(
                                                                    trennwand,
                                                                    v,
                                                                    unbenutztA.hexaSecond) != Seite.Oben) {
                                                        if (Vector
                                                                .seiteMitRichtung(
                                                                        trennwand,
                                                                        v,
                                                                        unbenutztA.hexaSecond) == Vector
                                                                .seiteMitRichtung(
                                                                        trennwand,
                                                                        v,
                                                                        anfangB0.otherEnd)) {
                                                            anfangTyp1B0 = unbenutztA.hexaFirst;
                                                        } else {
                                                            anfangTyp1B0 = unbenutztA.hexaSecond;
                                                        }
                                                    } else {
                                                        // Wenn also beide
                                                        // Seite.oben ergeben,
                                                        // dann sind beide
                                                        // Endpunkte der
                                                        // bedeckende Kontur auf
                                                        // der "trennwand" Linie
                                                        // EINFACH zufaellig
                                                        // einen von beiden
                                                        anfangTyp1B0 = unbenutztA.hexaFirst;
                                                    }
                                                } else {
                                                    if (Vector
                                                            .seiteMitRichtung(
                                                                    trennwand,
                                                                    v,
                                                                    unbenutztA.hexaFirst) == Vector
                                                            .seiteMitRichtung(
                                                                    trennwand,
                                                                    v,
                                                                    anfangB0.otherEnd)) {
                                                        anfangTyp1B0 = unbenutztA.hexaSecond;
                                                    } else {
                                                        anfangTyp1B0 = unbenutztA.hexaFirst;
                                                    }

                                                }
                                            }
                                            Directions dire1 = Vector
                                                    .cvtHexaVector(
                                                            v,
                                                            anfangTyp1B0.referenceHalf.refRoot)
                                                    .direction();
                                            Directions dire2 = Vector
                                                    .cvtHexaVector(
                                                            v,
                                                            anfangB0.referenceHalf.refRoot)
                                                    .direction();

                                            boolean vonUnten = schaueVonUnten2(
                                                    dire1, dire2, dire);
                                            if (!vonUnten) {
                                                HalfConture.findCrossings(
                                                        anfangTyp1B0, anfangB0,
                                                        dire, direSiblings,
                                                        grossVaterKante, v);
                                            }
                                        }
                                    }

                                }
                        }
                    }
                }

            }
        }
    }

    /**
     * Es kann geschehen sein das Konturen geteilt wurden, die aber im naechsten
     * Schritt v verdecken. Dies muss ueberprueft werden und falls das so ist
     * dann muessen diese beide Konturen zusammengemerd werden und zum Typ 1
     * umgewandelt werden
     * 
     * @param ergebnis
     */
    private static void mergeTyp2AreTyp1(Contour ergebnis, HexaCoord v,
            HexaCoord grossVater) {
        // Ich gehe alle haelften durch, wo es zwei gibt, die aber v.parent in
        // der betrachtungsrichtung BEDECKEN.. die werden zu einer verschmolzen,
        // und daraus spaeter ne Typ 1 gemacht
        for (Directions dire : Directions.values()) {
            HalfConture erste = ergebnis.seite[dire.ordinal()][0];
            HalfConture zweite = ergebnis.seite[dire.ordinal()][1];
            if (!erste.isTrivial() && !zweite.isTrivial()) {
                // Jetzt projezieren erstmals alle auf die grossvaterkante,
                // dafuer
                // benutzte ich die beide comparands
                ContureHexa comparand1 = null;
                ContureHexa comparand2 = null;
                // Zuerst werden die Seiten festgelegt
                if (erste.hexaFirst.equal(zweite.hexaFirst)) {
                    comparand1 = erste.hexaFirst;
                    comparand2 = zweite.hexaFirst;
                } else if (erste.hexaFirst.equal(zweite.hexaSecond)) {
                    comparand1 = erste.hexaFirst;
                    comparand2 = zweite.hexaSecond;
                } else if (erste.hexaSecond.equal(zweite.hexaFirst)) {
                    comparand1 = erste.hexaSecond;
                    comparand2 = zweite.hexaFirst;
                } else if (erste.hexaSecond.equal(zweite.hexaSecond)) {
                    comparand1 = erste.hexaSecond;
                    comparand2 = zweite.hexaSecond;
                }
                HexaCoord punktOnContour1 = comparand1
                        .addTemp(comparand1.ausgehende.getFirst());
                HexaCoord punktOnContour2 = comparand2
                        .addTemp(comparand2.ausgehende.getFirst());

                if (Segment.bedeckt(dire, v, grossVater, comparand1,
                        punktOnContour1)
                        || Segment.bedeckt(dire, v, grossVater, comparand2,
                                punktOnContour2)
                        || (Vector.cvtHexaVector(grossVater, v).direction() == dire)) {
                    // Dann lasse sie getrennt denn es ist eine Typ2A Kontur

                } else {
                    // MERGEN!
                    // Die seite 0 schluckt die seite, also comparand1.other end
                    // ist ein ende, und comparand2.otherend der andere
                    HalfConture.concatenate(erste, zweite);
                }

            }
        }

    }

    /**
     * Wenn es einen Schnitt zwischen der neuen Kontur und (v,v.parent) gibt
     * dann gab es bis jetzt eine Kontur die in einen Stueck ist. An dieser
     * Stelle wird die Kontur geteilt um dann die zwei Teile einer Kontur des
     * Typs 2a zu haben
     * 
     * @param ergebnis
     * 
     * @param schnitte
     */
    private static void split(Contour ergebnis, HexaCoord v,
            HexaCoord grossVater,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        for (Directions dire : Directions.values()) {
            if (!ergebnis.seite[dire.ordinal()][0].isTrivial()
                    || !ergebnis.seite[dire.ordinal()][1].isTrivial()) {
                // Falls nicht habe ich eine Typ2A Kontur die aus EINEM St�ck
                // ist. Der Trennungspunkt ist v oder ein punkt auf "schnitte"
                // Eine von beiden seiten muss nichtTrivial sein
                SplitPacket<Vector> splitPunkt;
                LinkedList<SplitPacket<Vector>> punkte = schnitte.get(dire
                        .ordinal());

                if (!punkte.isEmpty() && punkte.get(0) != null) {
                    // Punkte ist nicht leer
                    splitPunkt = punkte.removeFirst();
                    for (SplitPacket<Vector> it : punkte) {
                        if (splitPunkt.schnitt.equal(v) && it.schnitt.equal(v)) {
                            // Also weiß ich es das die Problem situation ist
                            // wenn der schnitt gerade auf v gefallen ist. Ich
                            // muss wissen welche von den zwei potentielen
                            // Seiten von der Großvater kante bedeckt bleiben
                            // wird. Der schnitt MUSS entstehen aus einer Typ 1
                            // Kontur die darüber geflogen ist mit einer Typ 1
                            // oder Typ 2 . Sonst gäbe es keine Projektion
                            // Projektionen gibts sonst beim mergen von Typ 2
                            // KOnturen, also auch nicht möglich
                            // Wenn es zwei Typ 1 waren, dann ist es egal.

                            SplitPacket<Vector> next = null;
                            if (it.listenElement.next.element != null
                                    && it.listenElement.next.element
                                            .direction() != dire) {
                                next = it;
                            } else if (splitPunkt.listenElement.next.element != null
                                    && splitPunkt.listenElement.next.element
                                            .direction() != dire) {
                                next = splitPunkt;
                            }

                            if (Vector.seiteMitRichtung(dire, next.originPunkt,
                                    v.addTemp(next.listenElement.next.element)) == Vector
                                    .seiteMitRichtung(dire, next.originPunkt,
                                            grossVater)) {
                                splitPunkt = next;
                            } else {
                                if (next.equals(splitPunkt)) {
                                    splitPunkt = it;
                                }
                            }

                        } else if (grossVater.distanceToPoint(it.schnitt) < grossVater
                                .distanceToPoint(splitPunkt.schnitt)) {
                            splitPunkt = it;
                        }

                    }
                    // Jetzt kommt die chirurgie
                    // splitpunkt muss jetzt unbedingt ein Teil der Kontur sein

                    // Wenn die Kontur zwei benutzte Seiten hat und gesplittet
                    // werden muss, werden vorher die "anderen" zwei Teile
                    // zusammengeschmolzen
                    if (!ergebnis.seite[dire.ordinal()][0].isTrivial()
                            && !ergebnis.seite[dire.ordinal()][1].isTrivial()) {
                        HalfConture.concatenate(
                                ergebnis.seite[dire.ordinal()][0],
                                ergebnis.seite[dire.ordinal()][1]);
                    }

                    HalfConture toSplit = unused(
                            ergebnis.seite[dire.ordinal()][0],
                            ergebnis.seite[dire.ordinal()][1]);
                    HalfConture andereHaelfte = toSplit.otherHalf;
                    // toSplit muss einfach "irgendwo" aufgeteilt werden

                    ContureHexa anfangErste;

                    if (toSplit.firstSecond.getListElem(
                            splitPunkt.listenElement.location - 1).equals(
                            splitPunkt.listenElement)) {
                        anfangErste = toSplit.hexaFirst;
                    } else if (toSplit.secondFirst.get(
                            splitPunkt.listenElement.location - 1).equals(
                            splitPunkt.listenElement.element)) {
                        anfangErste = toSplit.hexaSecond;
                    } else {
                        anfangErste = null;
                    }

                    andereHaelfte.hexaFirst.copy(splitPunkt.schnitt);
                    andereHaelfte.hexaSecond.copy(anfangErste.otherEnd);
                    andereHaelfte.hexaSecond.otherEnd = andereHaelfte.hexaFirst;
                    andereHaelfte.hexaFirst.otherEnd = andereHaelfte.hexaSecond;

                    andereHaelfte.firstSecond = anfangErste.ausgehende
                            .cut(splitPunkt.listenElement);

                    andereHaelfte.hexaFirst.ausgehende = andereHaelfte.hexaSecond.eingehende = andereHaelfte.firstSecond;

                    Liste<Vector> temp = anfangErste.otherEnd.ausgehende
                            .cutBefore(splitPunkt.listenElementOtherDirection);

                    andereHaelfte.secondFirst = anfangErste.otherEnd.ausgehende;
                    andereHaelfte.hexaFirst.eingehende = andereHaelfte.secondFirst;
                    andereHaelfte.hexaSecond.ausgehende = andereHaelfte.secondFirst;

                    anfangErste.otherEnd.eingehende = anfangErste.ausgehende;
                    anfangErste.otherEnd.ausgehende = temp;
                    anfangErste.eingehende = temp;
                    // anfangErste.ausgehende wurde da obne schon
                    // zurechtgeschnitten

                    if (anfangErste.referenceHalf.hexaFirst.equal(anfangErste)) {
                        anfangErste.referenceHalf.firstSecond = anfangErste.ausgehende;
                        anfangErste.referenceHalf.secondFirst = anfangErste.eingehende;
                    } else {
                        anfangErste.referenceHalf.firstSecond = anfangErste.eingehende;
                        anfangErste.referenceHalf.secondFirst = anfangErste.ausgehende;
                    }

                    anfangErste.otherEnd.copy(splitPunkt.schnitt);
                    andereHaelfte.used = false;
                    anfangErste.referenceHalf.used = false;

                    // Jetzt noch schauen ob man bei den Verbindungsvektoren
                    // splitten muss
                    if (splitPunkt.originPunkt
                            .distanceToPoint(anfangErste.otherEnd) != anfangErste.ausgehende
                            .getLast().magnitude()) {
                        // Dann muss man die letzten zwei von anfangErste
                        // resizen, und am anfang von andereHaelfte .hF zwei
                        // hinzufuegen.
                        double groesse = splitPunkt.originPunkt
                                .distanceToPoint(anfangErste.otherEnd);

                        // Zuerst die zwei neuen hinzuf�gen
                        Vector toAdd = anfangErste.ausgehende.getLast().clone();
                        Vector toAdd2 = anfangErste.eingehende.getFirst()
                                .clone();

                        toAdd.shorten(groesse);
                        toAdd2.shorten(groesse);

                        if (toAdd.magnitude() > 0) {
                            andereHaelfte.hexaFirst.ausgehende.addFirst(toAdd);
                        }
                        if (toAdd2.magnitude() > 0) {
                            andereHaelfte.hexaFirst.eingehende.addLast(toAdd2);
                        }

                        anfangErste.ausgehende.getLast().shorten(
                                anfangErste.ausgehende.getLast().magnitude()
                                        - groesse);
                        if (anfangErste.ausgehende.getLast().magnitude() == 0) {
                            anfangErste.ausgehende.removeLast();
                        }
                        anfangErste.eingehende.getFirst().shorten(
                                anfangErste.eingehende.getFirst().magnitude()
                                        - groesse);
                        if (anfangErste.eingehende.getFirst().magnitude() == 0) {
                            anfangErste.eingehende.removeFirst();
                        }

                    }
                }
            }
        }
    }

    /**
     * Der Typ der neuen Ergebnis Kontur wird hier entschieden.
     * 
     * @param ergebnis
     */
    private static void decideType(Contour ergebnis, HexaCoord v,
            HexaCoord grossVater) {
        for (Directions dire : Directions.values()) {
            // Zuerst werden die Sonderfaelle abgefragt wo es trotzdem eine
            // Typ2B
            // werden kann anstatt 2A
            // Wenn eine von den zwei seiten Trivial ist, dann ist schon mal
            // keine Typ2A
            // Kontur, wenn zusaetzlich der nichttriviale Teil v beruehrt ist es
            // der SonderFall mit Typ2B

            if ((ergebnis.seite[dire.ordinal()][1].isTrivial() && ((ergebnis.seite[dire
                    .ordinal()][0].hexaFirst.equal(v) || ergebnis.seite[dire
                    .ordinal()][0].hexaSecond.equal(v))))
                    || (ergebnis.seite[dire.ordinal()][0].isTrivial() && ((ergebnis.seite[dire
                            .ordinal()][1].hexaFirst.equal(v) || ergebnis.seite[dire
                            .ordinal()][1].hexaSecond.equal(v))))) {
                ergebnis.type[dire.ordinal()] = ContourType.TwoB;

            } else {
                // Jetzt bin ich vor einer Typ 1 normalen Typ2A oder 2B
                if (!ergebnis.seite[dire.ordinal()][0].isTrivial()
                        && !ergebnis.seite[dire.ordinal()][1].isTrivial()) {

                    ergebnis.type[dire.ordinal()] = ContourType.TwoA;

                } else {
                    // Einzige Fall wo an dieser stelle BEIDE unused sein
                    // koennen
                    // ist im Sonderfall, denn Normal Typ2A wurde schon vorher
                    // abgefragt!!
                    if (!ergebnis.seite[dire.ordinal()][0].used
                            && !ergebnis.seite[dire.ordinal()][1].used) {
                        // Das kann NUR beim Sonderfall geschehen
                        ergebnis.type[dire.ordinal()] = ContourType.TwoA;

                    } else {
                        HalfConture unbenutzt = unused(ergebnis.seite[dire
                                .ordinal()][0],
                                ergebnis.seite[dire.ordinal()][1]);
                        double max = Math.max(unbenutzt.hexaFirst
                                .calculateCoordinate(dire),
                                unbenutzt.hexaSecond.calculateCoordinate(dire));
                        double min = Math.min(unbenutzt.hexaFirst
                                .calculateCoordinate(dire),
                                unbenutzt.hexaSecond.calculateCoordinate(dire));

                        if (min <= grossVater.calculateCoordinate(dire)
                                && max >= grossVater.calculateCoordinate(dire)) {
                            ergebnis.type[dire.ordinal()] = ContourType.One;
                        } else {
                            ergebnis.type[dire.ordinal()] = ContourType.TwoB;
                        }
                    }

                }
            }
        }

    }

    /**
     * Befreit das "schnitt" Feld von doppelten und falschen Eintraege
     * 
     * @param schnitte
     * @param v
     */
    private static void clean(
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte, HexaCoord v) {
        for (Directions dire : Directions.values()) {

            LinkedList<SplitPacket<Vector>> punkte = schnitte.get(dire
                    .ordinal());
            if (!punkte.isEmpty()) {
                for (int i = 0; i < punkte.size(); i++) {
                    punkte.remove(null);
                }

                if (!punkte.isEmpty()) {
                    if (punkte.get(0) == null) {
                        punkte.remove(0);
                        if (punkte.isEmpty()) {
                            punkte.clear();
                        }
                    } else {

                        // for (int i = 0;i < punkte.size();i++) {
                        // if (punkte.get(i).schnitt.equal(v)) punkte.remove(i);
                        // }
                        //                  
                        //                  
                        // if (punkte.get(0).schnitt.equal(v)) {
                        // punkte.clear();
                        // } else {

                        for (int i = 0; i < punkte.size(); i++) {
                            HexaCoord xx = punkte.get(i).schnitt;
                            for (int j = (i + 1); j < punkte.size(); j++) {
                                if (punkte.get(j).schnitt.equal(xx)
                                        && !punkte.get(j).schnitt.equal(v)) {
                                    punkte.remove(j);
                                }
                            }
                        }
                        for (int i = 0; i < punkte.size(); i++) {
                            if (punkte.get(i).listenElement.element == null) {
                                punkte.remove(i);
                            }
                        }
                        // }

                    }
                }
            }
        }
    }

    /**
     * Die Ergebnis Kontur wird aus allen ungemergten Konturen in allen
     * Richtungen zusammengestellt. Diese werden in dieser Methode gesucht und
     * als Ergebnis Kontur zurueckgegeben
     * 
     * @param contourList
     */
    private static Contour copyTogether(ArrayList<Contour> contourList,
            UniformNode v) {
        Contour ergebnis = new Contour(v);
        for (int k = 0; k < 2; k++) {

            for (Directions dire : Directions.values()) {
                for (Contour it : contourList) {

                    if (!it.seite[dire.ordinal()][k].used) {
                        // Dann muss ich diese haelften auf die endkontur
                        // draufkopieren
                        if (ergebnis.seite[dire.ordinal()][k].isTrivial()) {
                            ergebnis.seite[dire.ordinal()][k] = it.seite[dire
                                    .ordinal()][k];
                        } else {
                            ergebnis.seite[dire.ordinal()][1 - k] = it.seite[dire
                                    .ordinal()][k];
                        }
                    }
                }
            }
        }

        return ergebnis;

    }

    /**
     * Merged alle Typ 1 Konturen unter sich Die gemergte Typ1 Kontur wird
     * zwingend v bedecken, ausser: a) sie ist leer oder b) (trivial) oder sie
     * endet auf "v drauf" Aus allen diesen Faellen ergibt sich eine Kontur vom
     * Typ 2a
     * 
     * @param contourList
     * @param v
     */
    private static void mergeTyp1Contours(ArrayList<Contour> contourList,
            HexaCoord v, Segment grossVaterKante,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        ArrayList<LinkedList<ContureHexaWrapper>> richtungen = new ArrayList<LinkedList<ContureHexaWrapper>>();

        for (Directions dire : Directions.values()) {
            Contour a;
            LinkedList<ContureHexaWrapper> punkte = new LinkedList<ContureHexaWrapper>();
            richtungen.add(punkte);

            for (int i = 0; i < contourList.size(); i++) {

                a = contourList.get(i);

                // Bis jetzt wurde keine einzige Typ 1 Kontur benutzt
                if (a.type[dire.ordinal()] == ContourType.One) {
                    HalfConture verfuegbar = unused(a.seite[dire.ordinal()][0],
                            a.seite[dire.ordinal()][1]);
                    ContureHexaWrapper aHF = new ContureHexaWrapper(
                            verfuegbar.hexaFirst, dire, null, v);
                    ContureHexaWrapper aHS = new ContureHexaWrapper(
                            verfuegbar.hexaSecond, dire, aHF, v);
                    aHF.other = aHS;
                    aHF.referenceCont = a;
                    aHS.referenceCont = a;
                    punkte.add(aHF);
                    punkte.add(aHS);
                }
            }
            Collections.sort(punkte);

        }

        // Jetzt habe ich alle Punkte in "richtungen" und die jeweiligen
        // ContureHexaWrapper Listen sortiert enthalten
        LinkedList<ContureHexaWrapper> geordnetePunkte;
        for (Directions dire : Directions.values()) {
            geordnetePunkte = richtungen.get(dire.ordinal());

            if (!geordnetePunkte.isEmpty()) {
                ContureHexaWrapper erste = geordnetePunkte.removeFirst();
                geordnetePunkte.remove(erste.other); // Der andere Punkt wird
                // auch gleich von der Liste entfernt damit er nicht
                // faelschlicherweise
                // spaeter entfernt wird
                // Am "anfang" werden jetzt alle anderen KOnturen rangemerged,
                // davon abhaengig
                ContureHexa anfang1 = erste.original;
                ContureHexa anfang2 = anfang1.otherEnd;
                HexaCoord rootErste = erste.referenceCont.root.hexa.clone();
                while (geordnetePunkte.size() >= 2) {

                    ContureHexaWrapper zweite = geordnetePunkte.removeFirst();
                    HexaCoord rootZweite = zweite.referenceCont.root.hexa
                            .clone();
                    geordnetePunkte.remove(zweite.other);
                    // WEnn size von der Liste > 2 gibt es mehr als eine Kontur,
                    // und da sie sich alle ueberlappen muss der "naechste"
                    // PUnkt einer anderen Kontur angehoeren

                    ContureHexa anfangAndere1 = zweite.other.original;
                    ContureHexa anfangAndere2 = anfangAndere1.otherEnd;

                    Directions direSiblings = Vector.cvtHexaVector(rootErste,
                            rootZweite).direction();
                    if (anfangAndere2.calculateCoordinateWithRef(dire, anfang1) <= anfang2
                            .calculateCoordinateWithRef(dire, anfang1)) {
                        // Dann passiert nix, denn die zweite Kontur wird von
                        // der ersten vollkommen bedeckt

                        // Der Vollstaendigkeit halber wird die haelfte von der
                        // zweiten auf used gestellt
                        anfangAndere1.referenceHalf.used = true;
                    } else if (anfang2.calculateCoordinate(dire) == anfangAndere1
                            .calculateCoordinate(dire)) {
                        // Wenn der ende von der ersten auf der selben hoehe ist
                        // als anfang der zweiten.
                        if (anfang2.equal(anfangAndere1)) {
                            HalfConture.concatenate(anfang1.referenceHalf,
                                    anfang2.referenceHalf); // Dann beruehren
                                                            // sie
                            // sich sogar!!!
                        } else { // Man muss entscheiden ob anfang2 unter
                            // anfangAndere1
                            // ist oder umgekehrt
                            if (anfang2.calculateCoordinateWithRef(Directions
                                    .perpendicular(direSiblings), v) > anfangAndere1
                                    .calculateCoordinateWithRef(Directions
                                            .perpendicular(direSiblings), v)) {
                                // Dann ist anfangAndere1 versteckt
                                schnitte.get(dire.ordinal()).add(
                                        HalfConture.mergeTypOne(anfang1,
                                                anfangAndere1, dire,
                                                direSiblings, grossVaterKante,
                                                v));
                            } else {// Dann ist anfang2 versteckt
                                schnitte.get(dire.ordinal()).add(
                                        HalfConture.mergeTypOne(anfangAndere2,
                                                anfang2, dire, direSiblings,
                                                grossVaterKante, v));
                                // Die zweite hat die erste geschluckt also wird
                                // die zweite zur "aktuellen"
                                anfang1 = anfangAndere1;
                                anfang2 = anfangAndere2;
                                rootErste.copy(rootZweite);
                            }
                        } // Gleiche Koordiinate bzgl v koennen anfang2 und
                        // anfang andere2 NICHT haben denn dann waeren sie
                        // GLEICH und das wurde schon vorher abgefragt

                    } else { // Eine von beiden bedeckt die andere, ich muss die
                        // einfach zusammen mergen,
                        // Also vorher entscheiden welcher PUnkt am anfang deep
                        // ist und welcher high.otherEnd,
                        if (anfang2.calculateCoordinateWithRef(Directions
                                .perpendicular(direSiblings), v) > anfangAndere1
                                .calculateCoordinateWithRef(Directions
                                        .perpendicular(direSiblings), v)) {
                            // Dann ist anfang2 = deep und anfangAndere1 = high
                            schnitte.get(dire.ordinal()).add(
                                    HalfConture.mergeTypOne(anfang1,
                                            anfangAndere1, dire, direSiblings,
                                            grossVaterKante, v));
                        } else {
                            schnitte.get(dire.ordinal()).add(
                                    HalfConture.mergeTypOne(anfangAndere2,
                                            anfang2, dire, direSiblings,
                                            grossVaterKante, v));
                            anfang1 = anfangAndere1;
                            anfang2 = anfangAndere2;
                            rootErste.copy(rootZweite);
                        }
                    }
                }
            }
        }

    }

    /**
     * Merged die Ergebnis Kontur vom Typ 1 mit der Ergebnis Kontur(en) vom Typ
     * 2
     * 
     * @param contourList
     * @param v
     * @param schnitte
     */
    private static void mergeTyp2WithTyp1(ArrayList<Contour> contourList,
            HexaCoord v, HexaCoord grossVater, Segment grossVaterKante,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        ContureHexa anfangB0, anfangTyp1B0 = null;
        boolean wiederholen = true;
        while (wiederholen) {
            wiederholen = false;

            for (int k = 0; k < 2; k++) {
                for (Directions dire : Directions.values()) {
                    Contour a, b;

                    for (int i = 0; i < contourList.size(); i++) {
                        for (int j = 0; j < contourList.size(); j++) {
                            if (i != j) {
                                a = contourList.get(i);
                                HalfConture unbenutztA = unused(a.seite[dire
                                        .ordinal()][0],
                                        a.seite[dire.ordinal()][1]);
                                if (unbenutztA != null)
                                    if (a.type[dire.ordinal()] == ContourType.One) {
                                        b = contourList.get(j);

                                        HalfConture bSeite = b.seite[dire
                                                .ordinal()][k];
                                        if (!bSeite.used
                                                && (b.type[dire.ordinal()] == ContourType.TwoA || b.type[dire
                                                        .ordinal()] == ContourType.TwoB)) {
                                            Directions direSiblings = HalfConture
                                                    .calculateDireSiblings(
                                                            unbenutztA, bSeite);

                                            if (!bSeite.isTrivial()) {
                                                if (bSeite.hexaFirst.equal(v)) {
                                                    anfangB0 = bSeite.hexaFirst;
                                                } else {
                                                    anfangB0 = bSeite.hexaSecond;
                                                }

                                                // Zuerst fragen ob eine der
                                                // zwei
                                                // enden der Typ1 Kontur auf v
                                                // projeziert
                                                if (unbenutztA.hexaFirst.aims(
                                                        dire, v)
                                                        || unbenutztA.hexaSecond
                                                                .aims(dire, v)) {
                                                    if (unbenutztA.hexaFirst
                                                            .aims(dire, v)) {
                                                        anfangTyp1B0 = unbenutztA.hexaSecond;
                                                    } else {
                                                        anfangTyp1B0 = unbenutztA.hexaFirst;
                                                    }

                                                    if (Vector
                                                            .seiteMitRichtung(
                                                                    dire, v,
                                                                    anfangTyp1B0) != Vector
                                                            .seiteMitRichtung(
                                                                    dire,
                                                                    v,
                                                                    anfangB0.otherEnd)) {
                                                        anfangTyp1B0 = anfangTyp1B0.otherEnd;
                                                    }
                                                } else {
                                                    if (Vector
                                                            .seiteMitRichtung(
                                                                    dire,
                                                                    v,
                                                                    unbenutztA.hexaFirst) == Vector
                                                            .seiteMitRichtung(
                                                                    dire,
                                                                    v,
                                                                    anfangB0.otherEnd)) {
                                                        anfangTyp1B0 = unbenutztA.hexaFirst;
                                                    } else {
                                                        anfangTyp1B0 = unbenutztA.hexaSecond;
                                                    }

                                                    // }
                                                }
                                                Directions dire1 = Vector
                                                        .cvtHexaVector(
                                                                v,
                                                                anfangTyp1B0.referenceHalf.refRoot)
                                                        .direction();
                                                Directions dire2 = Vector
                                                        .cvtHexaVector(
                                                                v,
                                                                anfangB0.referenceHalf.refRoot)
                                                        .direction();

                                                boolean vonUnten = schaueVonUnten2(
                                                        dire1, dire2, dire);

                                                SplitPacket<Vector> schnitt1 = null;
                                                if (vonUnten) {
                                                    schnitt1 = HalfConture
                                                            .mergeTyp2(
                                                                    anfangTyp1B0.otherEnd,
                                                                    anfangB0,
                                                                    dire,
                                                                    grossVaterKante,
                                                                    v);
                                                } else {
                                                    schnitt1 = HalfConture
                                                            .mergeTypOne(
                                                                    anfangTyp1B0.otherEnd,
                                                                    anfangB0,
                                                                    dire,
                                                                    direSiblings,
                                                                    grossVaterKante,
                                                                    v);
                                                }
                                                if (unbenutztA.used == true
                                                        && bSeite.used == false) {
                                                    b.type[dire.ordinal()] = ContourType.One;
                                                    wiederholen = true;
                                                }

                                                if (schnitt1 != null) {
                                                    if ((schnitt1.schnitt
                                                            .equal(v) && Vector
                                                            .seiteMitRichtung(
                                                                    Directions
                                                                            .perpendicular(dire),
                                                                    v,
                                                                    schnitt1.originPunkt) == Vector
                                                            .seiteMitRichtung(
                                                                    Directions
                                                                            .perpendicular(dire),
                                                                    v,
                                                                    grossVater))
                                                            || (!schnitt1.schnitt
                                                                    .equal(v))) {
                                                        schnitte.get(
                                                                dire.ordinal())
                                                                .add(schnitt1);
                                                    }
                                                }
                                            } else {
                                                bSeite.used = true;
                                            }
                                        }

                                    }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * Merged alle Typ 2 Konturen unter sich
     * 
     * @param contourList
     * @param schnitte
     */
    private static void mergeTyp2Contours(ArrayList<Contour> contourList,
            HexaCoord v, Segment grossVaterKante,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        // Zuerst die kreuzungen aufschreiben

        for (Directions dire : Directions.values()) {
            Contour a, b;
            for (int i = 0; i < contourList.size(); i++) {
                for (int j = 0; j < contourList.size(); j++) {
                    if (i != j) {
                        a = contourList.get(i);

                        if ((a.type[dire.ordinal()] == ContourType.TwoA || a.type[dire
                                .ordinal()] == ContourType.TwoB)) {
                            b = contourList.get(j);
                            if ((b.type[dire.ordinal()] == ContourType.TwoA || b.type[dire
                                    .ordinal()] == ContourType.TwoB)) {
                                Directions direSiblings = Vector.cvtHexaVector(
                                        a.root.hexa, b.root.hexa).direction();
                                seekCrossingsType2(a.seite[dire.ordinal()][0],
                                        a.seite[dire.ordinal()][1],
                                        b.seite[dire.ordinal()][0],
                                        b.seite[dire.ordinal()][1], v, dire,
                                        direSiblings, grossVaterKante, schnitte);
                            }
                        }
                    }
                }
            }
        }

        for (Directions dire : Directions.values()) {
            Contour a, b;
            for (int i = 0; i < contourList.size(); i++) {
                for (int j = 0; j < contourList.size(); j++) {
                    if (i != j) {
                        a = contourList.get(i);

                        if ((a.type[dire.ordinal()] == ContourType.TwoA || a.type[dire
                                .ordinal()] == ContourType.TwoB)) {
                            b = contourList.get(j);
                            if ((b.type[dire.ordinal()] == ContourType.TwoA || b.type[dire
                                    .ordinal()] == ContourType.TwoB)) {
                                merge2Contours4Halfs(
                                        a.seite[dire.ordinal()][0],
                                        a.seite[dire.ordinal()][1],
                                        b.seite[dire.ordinal()][0],
                                        b.seite[dire.ordinal()][1], v, dire,
                                        grossVaterKante, schnitte);
                                // Die haelften von b bleiben einfach wo und wie
                                // sie waren, hoffentlich werden sie nicht
                                // spaeter falsch ausgelesen
                                // Wenn Kontur a vorher Typ 2b war, aber jetzt
                                // jemand in der anderen Seite verschluckt hat,
                                // dann ist sie jetzt 2A!
                                // Aus einer 2A wird aber keine 2b
                                if (a.type[dire.ordinal()] == ContourType.TwoB
                                        && !a.seite[dire.ordinal()][0]
                                                .isTrivial()
                                        && !a.seite[dire.ordinal()][1]
                                                .isTrivial()) {
                                    a.type[dire.ordinal()] = ContourType.TwoA;
                                }
                                if (b.type[dire.ordinal()] == ContourType.TwoB
                                        && !b.seite[dire.ordinal()][0]
                                                .isTrivial()
                                        && !b.seite[dire.ordinal()][1]
                                                .isTrivial()) {
                                    b.type[dire.ordinal()] = ContourType.TwoA;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Diese Methode nimmt zwei Typ 2 Konturen und merged sie zusammen, egal ob
     * sie sich ueberlappen oder nicht und ob eine der Sonderfall ist oder nicht
     */
    private static void merge2Contours4Halfs(HalfConture a0, HalfConture a1,
            HalfConture b0, HalfConture b1, HexaCoord v, Directions dire,
            Segment grossVaterKante,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        ContureHexa anfangA0, anfangA1, anfangB0, anfangB1;

        if (a0.hexaFirst.equal(v)) {
            anfangA0 = a0.hexaFirst;
        } else if (a0.hexaSecond.equal(v)) {
            anfangA0 = a0.hexaSecond;
        } else {
            anfangA0 = null;
        }

        if (a1.hexaFirst.equal(v)) {
            anfangA1 = a1.hexaFirst;
        } else if (a1.hexaSecond.equal(v)) {
            anfangA1 = a1.hexaSecond;
        } else {
            anfangA1 = null;
        }

        if (b0.hexaFirst.equal(v)) {
            anfangB0 = b0.hexaFirst;
        } else if (b0.hexaSecond.equal(v)) {
            anfangB0 = b0.hexaSecond;
        } else {
            anfangB0 = null;
        }

        if (b1.hexaFirst.equal(v)) {
            anfangB1 = b1.hexaFirst;
        } else if (b1.hexaSecond.equal(v)) {
            anfangB1 = b1.hexaSecond;
        } else {
            anfangB1 = null;
        }

        boolean vonUnten;
        if (!a0.isTrivial() && !b0.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA0.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB0.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA0, anfangB0, v, dire);
                if (HalfConture.occludes(anfangA0, anfangB0, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangA0, anfangB0, dire,
                                    grossVaterKante, v, vonUnten));
                } else if (HalfConture.occludes(anfangB0, anfangA0, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangB0, anfangA0, dire,
                                    grossVaterKante, v, vonUnten));
                } else {
                    // Die haben nur v gemeinsam
                    HalfConture.concatenate(a0, b0);
                }
            }
        }

        if (!a0.isTrivial() && !b1.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA0.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB1.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA0, anfangB1, v, dire);
                if (HalfConture.occludes(anfangA0, anfangB1, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangA0, anfangB1, dire,
                                    grossVaterKante, v, vonUnten));
                } else if (HalfConture.occludes(anfangB1, anfangA0, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangB1, anfangA0, dire,
                                    grossVaterKante, v, vonUnten));
                } else {
                    HalfConture.concatenate(a0, b1);
                }
            }
        }

        if (!a1.isTrivial() && !b0.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA1.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB0.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA1, anfangB0, v, dire);
                if (HalfConture.occludes(anfangA1, anfangB0, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangA1, anfangB0, dire,
                                    grossVaterKante, v, vonUnten));
                } else if (HalfConture.occludes(anfangB0, anfangA1, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangB0, anfangA1, dire,
                                    grossVaterKante, v, vonUnten));
                } else {
                    HalfConture.concatenate(a1, b0);
                }
            }
        }

        if (!a1.isTrivial() && !b1.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA1.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB1.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA1, anfangB1, v, dire);
                if (HalfConture.occludes(anfangA1, anfangB1, dire)) {
                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangA1, anfangB1, dire,
                                    grossVaterKante, v, vonUnten));
                } else if (HalfConture.occludes(anfangB1, anfangA1, dire)) {

                    schnitte.get(dire.ordinal()).add(
                            HalfConture.mergeCompli(anfangB1, anfangA1, dire,
                                    grossVaterKante, v, vonUnten));
                } else {
                    HalfConture.concatenate(a1, b1);
                }
            }
        }
        // Jetzt sollten beide seiten so gemerged sein wie es geht, es kann nur
        // noch sein das man beide auf a zusammenfassen muss
        // Wenn jetzt noch immer eine Seite Trivial ist dann muss sie auf
        // "benutzt" gestellt werden

        // Es koennen NICHT zwei triviale Konturen gleichzeitig hier sein.
        if (a0.isTrivial() && !a0.used) {
            a0.used = true;
        }
        if (a1.isTrivial() && !a1.used) {
            a1.used = true;
        }
        if (b0.isTrivial() && !b0.used) {
            b0.used = true;
        }
        if (b1.isTrivial() && !b1.used) {
            b1.used = true;
        }

    }

    /**
     * @param v
     * @param dire
     * @return true falls es keine kreuzung geben kann also wenn ich "von unten"
     *         schaue
     */
    private static boolean schaueVonUnten(ContureHexa anfang1,
            ContureHexa anfang2, HexaCoord v, Directions dire) {
        // Keine von den zwei Konturen bei anfang1 und 2 sind trivial, also kann
        // ich bei beiden .next machen.
        Directions dire1 = anfang1.ausgehende.getFirst().direction();
        Directions dire2 = anfang2.ausgehende.getFirst().direction();
        // dire1 und 2 könnne eh nur die "normalen" Gitterrichtungen sein

        return schaueVonUnten2(dire1, dire2, dire);

    }

    private static boolean schaueVonUnten2(Directions dire1, Directions dire2,
            Directions dire) {
        // dire1 und 2 könnne eh nur die "normalen" Gitterrichtungen sein

        int max, min, threshMax = 0, threshMin = 0;
        if (dire1.ordinal() == Math.max(dire1.ordinal(), dire2.ordinal())) {
            max = dire1.ordinal();
            min = dire2.ordinal();
        } else {
            max = dire2.ordinal();
            min = dire1.ordinal();
        }

        if (max == 10 && min == 2) {
            max = 2;
            min = 10;
        } else if (max == 8 && min == 0) {
            max = 0;
            min = 8;
        } else if (max == 10 && min == 0) {
            max = 0;
            min = 10;
        }

        int diff = (max - min + 12) % 12;
        if (diff == 2) {
            threshMax = (max + 2 + 12) % 12;
            threshMin = (min - 2 + 12) % 12;
            return direDazwischen(dire, threshMin, threshMax);
        } else if (diff == 4) {
            threshMax = (max + 1 + 12) % 12;
            threshMin = (min - 1 + 12) % 12;
            return direDazwischen(dire, threshMin, threshMax);
        } else if (diff == 6)
            return true;
        else
            return true;

    }

    /**
     * Testet ob die Betrachtungsrichtung dire "zwischen" den
     * Betrachtungsrichtungen threshMin und threshMax ist.
     * 
     * @param dire
     * @param threshMin
     * @param threshMax
     */
    private static boolean direDazwischen(Directions dire, int threshMin,
            int threshMax) {
        if (threshMin >= 6 && threshMax <= 5) {
            if ((dire.ordinal() >= threshMin && dire.ordinal() <= 11)
                    || (dire.ordinal() <= threshMax && dire.ordinal() >= 0))
                return true;
            else
                return false;
        } else
            // Ganz normaler Fall
            return (threshMin <= dire.ordinal() && threshMax >= dire.ordinal());
    }

    public static double getMostFarAwayMax(Directions dire, ContureHexa a,
            ContureHexa b, ContureHexa c, ContureHexa d) {
        double max = Math.max(Math.max(a.calculateCoordinate(dire), b
                .calculateCoordinate(dire)), Math.max(c
                .calculateCoordinate(dire), d.calculateCoordinate(dire)));
        return max;
    }

    public static double getMostFarAwayMin(Directions dire, ContureHexa a,
            ContureHexa b, ContureHexa c, ContureHexa d) {
        double min = Math.min(Math.min(a.calculateCoordinate(dire), b
                .calculateCoordinate(dire)), Math.min(c
                .calculateCoordinate(dire), d.calculateCoordinate(dire)));
        return min;
    }

    /**
     * Nachdem der edgeTrim berechnet wurde muessen die ausgehenden Kanten von v
     * um diesen Wert verkuerzt werden.
     * 
     * @param edgeTrim
     */
    public void compress(int edgeTrim, HexaCoord v) {
        Vector verschieben = Vector.cvtHexaVector(this.root.hexa, v)
                .resizeTemp(edgeTrim);
        for (Directions dire : Directions.values()) {
            if (type[dire.ordinal()] == ContourType.One) {
                unused(this.seite[dire.ordinal()][0],
                        this.seite[dire.ordinal()][1]).hexaFirst
                        .add(verschieben);
                unused(this.seite[dire.ordinal()][0],
                        this.seite[dire.ordinal()][1]).hexaSecond
                        .add(verschieben);

            } else {
                // Also 2a oder 2b, (wurden vorher schon gemelted)

                // Wenn es der sonderfall ist, MIT trivialen beiden seiten, dann
                // ist ausgehende leer
                if (this.type[dire.ordinal()] == ContourType.TwoB
                        && unused(this.seite[dire.ordinal()][0],
                                this.seite[dire.ordinal()][1]).hexaFirst
                                .equal(v)
                        && unused(this.seite[dire.ordinal()][0],
                                this.seite[dire.ordinal()][1]).hexaSecond
                                .equal(v)) {
                    // Das kann nur sein, wenn beide seiten leer waren, also
                    // muss es der sonderfall mit den trivialen seiten sein
                } else {
                    HalfConture unbenutzt = unused(
                            this.seite[dire.ordinal()][0], this.seite[dire
                                    .ordinal()][1]);
                    ContureHexa anfang;
                    if (unbenutzt.hexaFirst.equal(v)) {
                        anfang = unbenutzt.hexaFirst;
                    } else {
                        anfang = unbenutzt.hexaSecond;
                    }

                    if (anfang.ausgehende.getFirst().direction() != dire) {
                        if (anfang.ausgehende.getFirst().magnitude() > edgeTrim) {
                            anfang.otherEnd.add(verschieben);
                            anfang.ausgehende.getFirst().shorten(edgeTrim);
                            anfang.eingehende.getLast().shorten(edgeTrim);
                        } else {
                            // Edgetrim war groesser gleich als der erste
                            // Vektor,
                            // dann muss der "anfang" entfernt werden und die
                            // Kontur wird zu einer Typ 1 Kontur
                            // Der Typ2a sonderfall kann jetzt nicht vorkommen!
                            // muss ich also nicht betrachten, edgetrim wird
                            // naemlich nicht groesser sein koennen!
                            // Also rueckwirkend was beim expandieren gemacht
                            // wurde!!
                            if (this.type[dire.ordinal()] == ContourType.TwoA) {
                                // Dann muss man nur den ersten entfernen
                                Vector move = anfang.ausgehende.removeFirst();
                                anfang.eingehende.removeLast();
                                anfang.add(move);
                                // Jetzt sollten die zwei Seiten von der Typ 2A
                                // Kontur den Punkt "root" bzw. Anfang gemeinsam
                                // haben einfach zusammenfuehren
                                HalfConture.concatenate(this.seite[dire
                                        .ordinal()][0], this.seite[dire
                                        .ordinal()][1]);
                            } else if (this.type[dire.ordinal()] == ContourType.TwoB) {
                                // Dann die ersten zwei entfernen
                                Vector move = anfang.ausgehende.removeFirst();
                                anfang.eingehende.removeLast();
                                anfang.add(move);
                                if (!anfang.equal(root.hexa)) {
                                    // Wenn gleich dann war es der Typ2B
                                    // sonderfall wo die fliegende Kontur gleich
                                    // mit der grossvaterkante verbudnen ist.
                                    // Wenn aber nicht dann ist es der
                                    // Normalfall wo eben projeziert wurde und
                                    // dann expandiert.
                                    // Die projektion muss jetzt noch
                                    // rueckwaerts
                                    // gemacht werden
                                    move = anfang.ausgehende.removeFirst();
                                    anfang.eingehende.removeLast();
                                    anfang.add(move);
                                }
                            }

                            // Jetzt als Typ 1 Kontur behandeln und entsprechend
                            // bewegen
                            this.type[dire.ordinal()] = ContourType.One;
                            unused(this.seite[dire.ordinal()][0],
                                    this.seite[dire.ordinal()][1]).hexaFirst
                                    .add(verschieben);
                            unused(this.seite[dire.ordinal()][0],
                                    this.seite[dire.ordinal()][1]).hexaSecond
                                    .add(verschieben);

                        }
                    } else {
                        // Sonderfall, aber nicht mit trivialen seiten beide
                        // muessen verkuerzt werden!! Hier kann es NICHT sein
                        // dass
                        // edgetrimm zu gross ist und deswegen die KOntur zu
                        // Typ1
                        // konvertiert
                        // Beide seiten sind jeztt nicht leer und wurden
                        // expandiert
                        ContureHexa anfang1;
                        if (this.seite[dire.ordinal()][0].hexaFirst.equal(v)) {
                            anfang1 = this.seite[dire.ordinal()][0].hexaFirst;
                        } else {
                            anfang1 = this.seite[dire.ordinal()][0].hexaSecond;
                        }

                        anfang1.otherEnd.add(verschieben);
                        anfang1.ausgehende.getFirst().shorten(edgeTrim);
                        anfang1.eingehende.getLast().shorten(edgeTrim);

                        ContureHexa anfang2;
                        if (this.seite[dire.ordinal()][1].hexaFirst.equal(v)) {
                            anfang2 = this.seite[dire.ordinal()][1].hexaFirst;
                        } else {
                            anfang2 = this.seite[dire.ordinal()][1].hexaSecond;
                        }
                        anfang2.otherEnd.add(verschieben);
                        anfang2.ausgehende.getFirst().shorten(edgeTrim);
                        anfang2.eingehende.getLast().shorten(edgeTrim);

                    }
                }

            }
        }
        // Root von der ganzen Kontur aendert sich natuerlich mit, unabhaengig
        // von
        // den Richtungen

        this.root.hexa.add(verschieben);

    }

    /**
     * Die linke und rechte Seite der Konturen wird zusammengemeged um dann pro
     * Sohn genau eine Kontur zu haben.
     * 
     * @param v
     * 
     */
    public void meltFirstSecond(HexaCoord v) {

        for (Directions dire : Directions.values()) {
            if (this.type[dire.ordinal()] == ContourType.TwoB) {
                if (this.seite[dire.ordinal()][0].isTrivial()
                        && this.seite[dire.ordinal()][1].isTrivial()) {
                    // Dann sind wir im sonderfall, was kann es SONST sein!?!?
                } else {
                    // Die triviale seite muss "geloescht" werden, denn sie
                    // wurde
                    // nur initialisiert um die Abstandsberechnung zu machen hat
                    // aber beim mergen nix zu suchen
                    if (this.seite[dire.ordinal()][0].isTrivial()) {
                        this.seite[dire.ordinal()][0].used = true;
                    } else {
                        this.seite[dire.ordinal()][1].used = true;
                        // Eine von beiden MUSS die triviale gewesen sein //WAS
                        // MIT
                        // DEM SONDERFALL UND DER TRIVIALEN KONTUR!?!?!?
                    }
                }
            } else if (this.type[dire.ordinal()] == ContourType.TwoA) {
                // Wenn es der expandierte Sonderfall ist, also Typ 2A dann,
                // muss man NIX beim melten machen
                if (!v.aims(dire, this.root.hexa)) {
                    ContureHexa anfang = null;
                    ContureHexa andere = null;

                    if (this.seite[dire.ordinal()][0].hexaFirst.equal(v)) {
                        anfang = this.seite[dire.ordinal()][0].hexaFirst;
                    } else if (this.seite[dire.ordinal()][0].hexaSecond
                            .equal(v)) {
                        anfang = this.seite[dire.ordinal()][0].hexaSecond;
                    } else if (this.seite[dire.ordinal()][1].hexaFirst.equal(v)) {
                        anfang = this.seite[dire.ordinal()][1].hexaFirst;
                    } else if (this.seite[dire.ordinal()][1].hexaSecond
                            .equal(v)) {
                        anfang = this.seite[dire.ordinal()][1].hexaSecond;
                    }

                    // Jetzt habe ich die KOntur mit v in der hand.
                    // Die andere hat oder root oder einen punkt zwischen root
                    // und v
                    Segment grossVaterKante = new Segment(v, root.hexa);
                    HalfConture otherHalf = anfang.referenceHalf.otherHalf;

                    if (grossVaterKante.contains(otherHalf.hexaFirst)) {
                        andere = otherHalf.hexaFirst;
                    } else if (grossVaterKante.contains(otherHalf.hexaSecond)) {
                        andere = otherHalf.hexaSecond;
                    }
                    HalfConture.mergeTyp2(anfang.otherEnd, andere, dire, null,
                            v);
                    this.type[dire.ordinal()] = ContourType.TwoB;
                }
            }
            // Jetzt sind die wichtigen Infos auf jedem Fall auf der haelfte 0!
            // Und die haelfte 1 ist leer

        }
    }

    public static HalfConture unused(HalfConture a, HalfConture b) {
        if (!a.used)
            return a;
        else if (!b.used)
            return b;
        else
            return null;
    }

    private static void seekCrossingsType2(HalfConture a0, HalfConture a1,
            HalfConture b0, HalfConture b1, HexaCoord v, Directions dire,
            Directions direSiblings, Segment grossVaterKante,
            ArrayList<LinkedList<SplitPacket<Vector>>> schnitte) {
        ContureHexa anfangA0, anfangA1, anfangB0, anfangB1;

        if (a0.hexaFirst.equal(v)) {
            anfangA0 = a0.hexaFirst;
        } else if (a0.hexaSecond.equal(v)) {
            anfangA0 = a0.hexaSecond;
        } else {
            anfangA0 = null;
        }

        if (a1.hexaFirst.equal(v)) {
            anfangA1 = a1.hexaFirst;
        } else if (a1.hexaSecond.equal(v)) {
            anfangA1 = a1.hexaSecond;
        } else {
            anfangA1 = null;
        }

        if (b0.hexaFirst.equal(v)) {
            anfangB0 = b0.hexaFirst;
        } else if (b0.hexaSecond.equal(v)) {
            anfangB0 = b0.hexaSecond;
        } else {
            anfangB0 = null;
        }

        if (b1.hexaFirst.equal(v)) {
            anfangB1 = b1.hexaFirst;
        } else if (b1.hexaSecond.equal(v)) {
            anfangB1 = b1.hexaSecond;
        } else {
            anfangB1 = null;
        }

        boolean vonUnten;
        if (!a0.isTrivial() && !b0.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA0.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB0.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA0, anfangB0, v, dire);
                if (HalfConture.occludes(anfangA0, anfangB0, dire)) {
                    HalfConture.seekCompli(anfangA0, anfangB0, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                } else if (HalfConture.occludes(anfangB0, anfangA0, dire)) {
                    HalfConture.seekCompli(anfangB0, anfangA0, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                } // Die haben nur v gemeinsam, also sicher keine kreuzung

            }
        }

        if (!a0.isTrivial() && !b1.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA0.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB1.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA0, anfangB1, v, dire);

                if (HalfConture.occludes(anfangA0, anfangB1, dire)) {
                    HalfConture.seekCompli(anfangA0, anfangB1, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                } else if (HalfConture.occludes(anfangB1, anfangA0, dire)) {
                    HalfConture.seekCompli(anfangB1, anfangA0, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                }
            }
        }

        if (!a1.isTrivial() && !b0.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA1.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB0.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA1, anfangB0, v, dire);

                if (HalfConture.occludes(anfangA1, anfangB0, dire)) {
                    HalfConture.seekCompli(anfangA1, anfangB0, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                } else if (HalfConture.occludes(anfangB0, anfangA1, dire)) {
                    HalfConture.seekCompli(anfangB0, anfangA1, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                }
            }
        }

        if (!a1.isTrivial() && !b1.isTrivial()) {
            if (Vector.seiteMitRichtung(dire, v, anfangA1.otherEnd) == Vector
                    .seiteMitRichtung(dire, v, anfangB1.otherEnd)) {
                vonUnten = schaueVonUnten(anfangA1, anfangB1, v, dire);

                if (HalfConture.occludes(anfangA1, anfangB1, dire)) {
                    HalfConture.seekCompli(anfangA1, anfangB1, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                } else if (HalfConture.occludes(anfangB1, anfangA1, dire)) {
                    HalfConture.seekCompli(anfangB1, anfangA1, dire,
                            direSiblings, grossVaterKante, v, vonUnten);
                }
            }
        }
    }

}
