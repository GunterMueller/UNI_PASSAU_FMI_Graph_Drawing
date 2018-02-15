// =============================================================================
//
//   HalfConture.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Diese Klasse ist eine Verwaltungsklasse für die Konturen. Es werden hier die
 * zwei Endpunkte einer Kontur wie auch die referenzen zu den eingehenden und
 * ausgehenden Vektoren. Es wird hier auch das mergen der Konturen implementiert
 */

public class HalfConture {

    public ContureHexa hexaFirst; // Ein Endpunkt

    public ContureHexa hexaSecond; // der andere Endpunkt

    public Liste<Vector> firstSecond; // Vektoren der Kontur die vom ersten in
    // richtung zweiten gehen

    public Liste<Vector> secondFirst; // Vektoren der Kontur in die
    // entgegengesetzte Richtung

    public HalfConture otherHalf; // Referenz zur anderen hälfte

    public HexaCoord refRoot; // Referezn zum Knoten v

    public Seite seite; // Links oder Rechts, das wird zur Abstandsberechnung
                        // gebraucht

    public boolean used; // used braucht man beim mergen um zu wissen ob die
                         // Vektoren
    // dieser hälfte schon von jemand anderen genommen wurden.

    public LinkedList<HalfConture> listCrossings; // beim mergen speichert mit
                                                  // welchen
    // anderen konturen sich diese kreuzt

    public LinkedList<HalfConture> listSwallowed; // liste der hinzugefügten
                                                  // Konturen

    HalfConture(HalfConture otherHalf, HexaCoord refRoot) {
        firstSecond = new Liste<Vector>();
        secondFirst = new Liste<Vector>();
        // Eingehende und dann ausgehende
        hexaFirst = new ContureHexa(secondFirst, firstSecond, null, this);
        hexaSecond = new ContureHexa(firstSecond, secondFirst, hexaFirst, this);
        hexaFirst.otherEnd = hexaSecond;
        this.otherHalf = otherHalf;
        this.used = false;
        listCrossings = new LinkedList<HalfConture>();
        listSwallowed = new LinkedList<HalfConture>();

        this.refRoot = refRoot;
    }

    /**
     * Prueft ob diese Kontur "benutzt" wurde oder leer ist, in beiden Faellen
     * bedeutet dies das die Kontur uninteressant ist
     */
    public boolean isTrivial() {
        return (firstSecond.isEmpty() || this.used);
    }

    /**
     * Merged zwei Typ 2 Konturen die sich gegenseitig bedecken Falls beim
     * projezieren die grossvaterkante beruehrt wird, wird der Schnittpunkt
     * zurueckgegeben. Beruecksichtigt nicht die Moeglichkeit von Kreuzungen
     * zwischen den Konturen
     */
    public static SplitPacket<Vector> mergeTyp2(ContureHexa nichtBedeckt,
            ContureHexa deep, Directions dire, Segment grossvaterKante,
            HexaCoord v) {
        ContureHexa high = nichtBedeckt.otherEnd;

        SplitPacket<Vector> pack = null;
        if (!deep.ausgehende.isEmpty()) {
            if (deep.equal(high)) {
                HalfConture.concatenate(nichtBedeckt.referenceHalf,
                        deep.referenceHalf);
            } else {
                HexaCoord lastBedeckt = deep.clone();
                double valDire = deep.calculateCoordinateWithRefWOSign(dire,
                        high);
                double copyValDire = valDire;
                while (deep.calculateCoordinateWithRef(dire, high.otherEnd) <= high
                        .calculateCoordinateWithRef(dire, high.otherEnd)
                        && !deep.ausgehende.isEmpty()) {

                    lastBedeckt.copy(deep);
                    deep.add(deep.ausgehende.removeFirst());
                    deep.eingehende.removeLast();
                }

                if (deep.calculateCoordinateWithRefWOSign(dire, high)
                        * copyValDire < 0
                        || copyValDire == 0) {

                    // Das heisst das die bedeckte mindestens an high angekommt
                    Segment segCrawl = new Segment(lastBedeckt, deep);

                    HexaCoord projOnDirePath = Distances
                            .calculateProjectionPointOnSegment(high, segCrawl,
                                    dire);
                    Vector toAdd2 = Vector.cvtHexaVector(high, projOnDirePath);

                    HexaCoord backUP = high.clone(); // Das hier ist f�r das
                    // erinnern

                    if (toAdd2.magnitude() != 0) {
                        high.add(toAdd2);
                        high.eingehende.addLast(toAdd2);
                        high.ausgehende.addFirst(toAdd2.inverse());
                    }
                    // Wenn der schnitt mit der grossvaterkante.. existiert aber
                    // nicht zwischen start und projektions punkt steht,
                    // sondern SPAETER, dann soll er auch nicht betrachtet
                    // werden
                    if (grossvaterKante != null) {
                        HexaCoord schnitt = Distances
                                .calculateProjectionPointOnSegmentWithDire(
                                        backUP, grossvaterKante, dire);
                        if (schnitt != null
                                && backUP.distanceToPoint(schnitt) <= backUP
                                        .distanceToPoint(projOnDirePath)) {
                            pack = new SplitPacket<Vector>(backUP,
                                    high.eingehende.getPointerToLastElem(),
                                    high.ausgehende.getPointerToFirstElem(),
                                    schnitt, high.otherEnd);
                        }
                    }

                    // Dann den stueckchen BIS zur naechsten Kontur
                    // vervollstaendigen
                    Vector toAdd = Vector.cvtHexaVector(projOnDirePath, deep);
                    if (toAdd.magnitude() != 0) {
                        high.add(toAdd);
                        high.eingehende.addLast(toAdd);
                        high.ausgehende.addFirst(toAdd.inverse());
                    }
                    HalfConture.concatenate(high.referenceHalf,
                            deep.referenceHalf);
                } else {
                    deep.referenceHalf.used = true;
                    // Dann war die bedeckte vollstaendig bedeckt
                }

            }
        } else {
            deep.referenceHalf.used = true;
            // Falls die deep "leer" wahr, wird die haelfte als benutzt markiert
        }

        return pack;
    }

    /***
     * Merged auch zwei Konturen beruecksichtigt aber dabei die moeglichkeit das
     * die Konturen sich kreuzen
     */
    public static SplitPacket<Vector> mergeTypOne(ContureHexa nichtBedeckt,
            ContureHexa deep, Directions dire, Directions direSiblings,
            Segment grossvaterKante, HexaCoord v) {
        ContureHexa high = nichtBedeckt.otherEnd;

        SplitPacket<Vector> pack = null;
        if (!deep.ausgehende.isEmpty()) {
            if (deep.equal(high)) {
                HalfConture.concatenate(nichtBedeckt.referenceHalf,
                        deep.referenceHalf);
            } else {
                HexaCoord lastBedeckt = deep.clone();
                double valDire = deep.calculateCoordinateWithRefWOSign(dire,
                        high);
                double copyValDire = valDire;
                double valSibl = deep.calculateCoordinateWithRefWOSign(
                        direSiblings, high);
                double copyValSibl = valSibl;

                while (deep.calculateCoordinateWithRef(dire, high.otherEnd) <= high
                        .calculateCoordinateWithRef(dire, high.otherEnd)
                        && !deep.ausgehende.isEmpty()) {

                    while (deep.calculateCoordinateWithRef(dire, high.otherEnd) <= high
                            .calculateCoordinateWithRef(dire, high.otherEnd)
                            && (deep.calculateCoordinateWithRefWOSign(
                                    direSiblings, high)
                                    * copyValSibl >= 0)
                            && !deep.ausgehende.isEmpty()) {
                        lastBedeckt.copy(deep);
                        deep.add(deep.ausgehende.removeFirst());
                        deep.eingehende.removeLast();
                    }

                    if ((deep.calculateCoordinateWithRefWOSign(direSiblings,
                            high)
                            * copyValSibl < 0)
                            && (deep.calculateCoordinateWithRef(dire,
                                    high.otherEnd) <= high
                                    .calculateCoordinateWithRef(dire,
                                            high.otherEnd))) {
                        // Falls leer oder nicht leer ist, aber man noch
                        // wechseln koennte, UND NACH dem wechseln der andere
                        // NICHT leer ist, UND man nicht in der ganz normalen
                        // projektion situation ist. DANN tauschen
                        ContureHexa temp = deep;
                        deep = high;
                        high = temp;
                        valDire = deep.calculateCoordinateWithRefWOSign(dire,
                                high);
                        copyValDire = valDire;
                        valSibl = deep.calculateCoordinateWithRefWOSign(
                                direSiblings, high);
                        copyValSibl = valSibl;
                    }
                }

                if (deep.calculateCoordinateWithRefWOSign(dire, high)
                        * copyValDire < 0
                        || copyValDire == 0) {

                    // Das heisst die bedeckte ist mindestens an high angekommen
                    Segment segCrawl = new Segment(lastBedeckt, deep);

                    HexaCoord projOnDirePath = Distances
                            .calculateProjectionPointOnSegmentWithDire(high,
                                    segCrawl, dire);
                    Vector toAdd2 = Vector.cvtHexaVector(high, projOnDirePath);

                    HexaCoord backUP = high.clone();
                    if (toAdd2.magnitude() != 0) {
                        high.add(toAdd2);
                        high.eingehende.addLast(toAdd2);
                        high.ausgehende.addFirst(toAdd2.inverse());
                    }
                    // Wenn der schnitt mit der grossvaterkante existiert aber
                    // nicht zwischen start und projektions punkt steht,
                    // sondern spaeter, dann soll er auch nicht betrachtet
                    // werden
                    HexaCoord schnitt = Distances
                            .calculateProjectionPointOnSegmentWithDire(backUP,
                                    grossvaterKante, dire);
                    if (schnitt != null
                            && backUP.distanceToPoint(schnitt) <= backUP
                                    .distanceToPoint(projOnDirePath)) {
                        pack = new SplitPacket<Vector>(backUP, high.eingehende
                                .getPointerToLastElem(), high.ausgehende
                                .getPointerToFirstElem(), schnitt,
                                high.otherEnd);
                    }

                    // Dann den stueckchen BIS zur naechsten Kontur
                    // vervollstaendigen
                    Vector toAdd = Vector.cvtHexaVector(projOnDirePath, deep);
                    if (toAdd.magnitude() != 0) {
                        high.add(toAdd);
                        high.eingehende.addLast(toAdd);
                        high.ausgehende.addFirst(toAdd.inverse());
                    }
                    HalfConture.concatenate(high.referenceHalf,
                            deep.referenceHalf);
                } else {
                    deep.referenceHalf.used = true;
                    // Dann wurde die bedeckte wohl vollstaendig deep
                }
            }
        } else {
            deep.referenceHalf.used = true;
            // Also fall die deep "leer" wahr, wird die haelfte als benutzt
            // markiert
        }

        return pack;
    }

    /**
     * Wenn zwei Konturen gemerged wurden, uebernimmt die eine die liste von
     * Vektoren der anderen. Dies wird hier implementiert
     */
    public static void concatenate(HalfConture erste, HalfConture zweite) {
        // Beide haben EINEN Punkt gemeinsam!
        if (!zweite.isTrivial()) {
            ContureHexa endeErste = null;
            ContureHexa anfangZweite = null;
            if (erste.hexaFirst.equal(zweite.hexaFirst)) {
                endeErste = erste.hexaSecond;
                anfangZweite = zweite.hexaFirst;
            } else if (erste.hexaFirst.equal(zweite.hexaSecond)) {
                endeErste = erste.hexaSecond;
                anfangZweite = zweite.hexaSecond;
            } else if (erste.hexaSecond.equal(zweite.hexaFirst)) {
                endeErste = erste.hexaFirst;
                anfangZweite = zweite.hexaFirst;
            } else if (erste.hexaSecond.equal(zweite.hexaSecond)) {
                endeErste = erste.hexaFirst;
                anfangZweite = zweite.hexaSecond;
            }

            if (erste.refRoot != null && zweite.refRoot != null) {
                erste.listSwallowed.add(zweite);
                // Also keine von beiden eine Vaterkante "kontur"
            }

            endeErste.otherEnd.copy(anfangZweite.otherEnd);

            // endeErste und anfangZweite.otherend sind die einzigen die nicht
            // beruehrt worden sind
            anfangZweite.otherEnd.ausgehende.addListe(endeErste.eingehende);

            endeErste.eingehende = anfangZweite.otherEnd.ausgehende;
            endeErste.otherEnd.ausgehende = anfangZweite.otherEnd.ausgehende;

            endeErste.ausgehende.addListe(anfangZweite.otherEnd.eingehende);
            endeErste.otherEnd.eingehende = endeErste.ausgehende;

            if (endeErste.referenceHalf.hexaFirst.equal(endeErste)) {
                endeErste.referenceHalf.firstSecond = endeErste.ausgehende;
                endeErste.referenceHalf.secondFirst = endeErste.eingehende;
            } else {
                endeErste.referenceHalf.secondFirst = endeErste.ausgehende;
                endeErste.referenceHalf.firstSecond = endeErste.eingehende;
            }

        }
        zweite.used = true;

    }

    /*
     * Prüft ob eine Kontur die mit der anderen v gemeinsam hat sich gegenseitig
     * bedecken
     */
    public static boolean occludes(ContureHexa anfangA, ContureHexa anfangB,
            Directions dire) {
        HexaCoord a1 = anfangA.returnLastNotSichtbar(dire);
        HexaCoord a2 = anfangA.returnFirstSichtbar(dire);
        HexaCoord b1 = anfangB.returnLastNotSichtbar(dire);
        HexaCoord b2 = anfangB.returnFirstSichtbar(dire);

        return (Segment.bedeckt(dire, a1, a2, b1, b2));
    }

    /**
     * Zwischenschritt beim mergen von zwei Konturen um zu pruefen ob kreuzungen
     * beruecksichtigen werden muessen oder nicht.
     */
    public static SplitPacket<Vector> mergeCompli(ContureHexa anfangA0,
            ContureHexa anfangB0, Directions dire, Segment grossVaterKante,
            HexaCoord v, boolean vonUnten) {
        if (vonUnten) // Dann kann es keine kreuzung geben.
            return HalfConture.mergeTyp2(anfangA0, anfangB0, dire,
                    grossVaterKante, v);
        else {
            Directions direSiblings = calculateDireSiblings(
                    anfangA0.referenceHalf, anfangB0.referenceHalf);

            return HalfConture.mergeTypOne(anfangA0, anfangB0, dire,
                    direSiblings, grossVaterKante, v);
        }

    }

    public static void seekCompli(ContureHexa anfangA0, ContureHexa anfangB0,
            Directions dire, Directions direSiblings, Segment grossVaterKante,
            HexaCoord v, boolean vonUnten) {
        if (!vonUnten) {
            HalfConture.findCrossings(anfangA0, anfangB0, dire, direSiblings,
                    grossVaterKante, v);
        }
    }

    /**
     * Ruft die Methode die die KOnturen merged, aber ohne die Vektoren zu
     * loeschen laesst also die Konturen selber unberuehrt und Ueberprueft nur
     * ob sich die Konturen kreuzen wuerden
     */
    public static void findCrossings(ContureHexa nichtBedeckt,
            ContureHexa deep, Directions dire, Directions direSiblings,
            Segment grossvaterKante, HexaCoord v) {
        ContureHexa high = nichtBedeckt.otherEnd;
        HexaCoord highDumb = high.clone();
        HexaCoord deepDumb = deep.clone();

        Iterator<Vector> itDeep = deep.ausgehende.iterator();
        if (itDeep.hasNext()) {
            if (!deepDumb.equal(highDumb)) {
                HexaCoord lastBedeckt = deepDumb.clone();
                double valSibl = deepDumb.calculateCoordinateWithRefWOSign(
                        direSiblings, highDumb);
                double copyValSibl = valSibl;

                while (deepDumb.calculateCoordinateWithRef(dire, high.otherEnd) <= highDumb
                        .calculateCoordinateWithRef(dire, high.otherEnd)
                        && itDeep.hasNext()) {

                    while (deepDumb.calculateCoordinateWithRef(dire,
                            high.otherEnd) <= highDumb
                            .calculateCoordinateWithRef(dire, high.otherEnd)
                            && (deepDumb.calculateCoordinateWithRefWOSign(
                                    direSiblings, highDumb)
                                    * copyValSibl >= 0) && itDeep.hasNext()) {
                        lastBedeckt.copy(deepDumb);
                        deepDumb.add(itDeep.next());
                    }

                    if ((deepDumb.calculateCoordinateWithRefWOSign(
                            direSiblings, high)
                            * copyValSibl < 0)
                            && (deepDumb.calculateCoordinateWithRef(dire,
                                    high.otherEnd) <= highDumb
                                    .calculateCoordinateWithRef(dire,
                                            high.otherEnd))) {
                        // Wenn ich hier bin gabs IMMER ein crossing
                        nichtBedeckt.referenceHalf.listCrossings
                                .add(deep.referenceHalf);
                        deep.referenceHalf.listCrossings
                                .add(nichtBedeckt.referenceHalf);
                        return;
                    }
                }

            }

        }
    }

    /**
     * Bevor Konturen gemerged werden die eine kreuzung haben, wird berechnet
     * welche v_i-v_j Richtung genommen muss, denn zu diesem Zeitpunkt weisst
     * man nicht welcher Knoten genau v_i ist und welcher Knoten v_j
     */
    public static Directions calculateDireSiblings(HalfConture a0,
            HalfConture b0) {
        Directions direSiblings = Vector.cvtHexaVector(a0.refRoot, b0.refRoot)
                .direction();

        for (HalfConture swall : b0.listSwallowed) {
            if (a0.listCrossings.contains(swall) && !swall.isTrivial()) {
                direSiblings = Vector.cvtHexaVector(a0.refRoot, swall.refRoot)
                        .direction();
            }
        }

        for (HalfConture swall : a0.listSwallowed) {
            if (swall.listCrossings.contains(b0) && !swall.isTrivial()) {
                direSiblings = Vector.cvtHexaVector(swall.refRoot, b0.refRoot)
                        .direction();
            }

            for (HalfConture swall2 : b0.listSwallowed) {

                if (swall.listCrossings.contains(swall2) && !swall.isTrivial()) {
                    direSiblings = Vector.cvtHexaVector(swall.refRoot,
                            swall2.refRoot).direction();
                }

            }
        }
        return direSiblings;
    }

    /**
     * Merged auch zwei Konturen zusammen, wird aber nur beim expandieren
     * benutzt betrachtet also weder die moeglichkeit von kreuzungen von
     * Konturen noch die moeglichkeit der kreuzung mit v,v.parent
     */
    public static void mergeExp(ContureHexa nichtBedeckt, ContureHexa deep,
            Directions dire, HexaCoord v) {
        ContureHexa high = nichtBedeckt.otherEnd;

        if (!deep.ausgehende.isEmpty()) {
            if (deep.equal(high)) {
                if (nichtBedeckt.referenceHalf.refRoot != null) {
                    HalfConture.concatenate(nichtBedeckt.referenceHalf,
                            deep.referenceHalf);
                } else {
                    HalfConture.concatenate(deep.referenceHalf,
                            nichtBedeckt.referenceHalf);
                }

            } else {
                HexaCoord lastBedeckt = deep.clone();
                double valDire = deep.calculateCoordinateWithRefWOSign(dire,
                        high);
                double copyValDire = valDire;
                while (deep.calculateCoordinateWithRef(dire, high.otherEnd) <= high
                        .calculateCoordinateWithRef(dire, high.otherEnd)
                        && !deep.ausgehende.isEmpty()) {

                    lastBedeckt.copy(deep);
                    deep.add(deep.ausgehende.removeFirst());
                    deep.eingehende.removeLast();
                }

                if (deep.calculateCoordinateWithRefWOSign(dire, high)
                        * copyValDire < 0
                        || copyValDire == 0) {

                    // Das heisst die bedeckte ist mindestens an high angekommen
                    Segment segCrawl = new Segment(lastBedeckt, deep);

                    HexaCoord projOnDirePath = Distances
                            .calculateProjectionPointOnSegment(high, segCrawl,
                                    dire);
                    Vector toAdd2 = Vector.cvtHexaVector(high, projOnDirePath);

                    if (toAdd2.magnitude() != 0) {
                        high.add(toAdd2);
                        high.eingehende.addLast(toAdd2);
                        high.ausgehende.addFirst(toAdd2.inverse());
                    }

                    // Dann den stueckchen BIS zur naechsten Kontur
                    // vervollstaendigen
                    Vector toAdd = Vector.cvtHexaVector(projOnDirePath, deep);
                    if (toAdd.magnitude() != 0) {
                        high.add(toAdd);
                        high.eingehende.addLast(toAdd);
                        high.ausgehende.addFirst(toAdd.inverse());
                    }

                    if (high.referenceHalf.refRoot != null) {
                        HalfConture.concatenate(high.referenceHalf,
                                deep.referenceHalf);
                    } else {
                        HalfConture.concatenate(deep.referenceHalf,
                                high.referenceHalf);
                    }
                } else {
                    if (deep.referenceHalf.refRoot != null) {
                        high.referenceHalf = deep.referenceHalf;
                    }
                }
                // Dann wurde die bedeckte vollstaendig deep

            }
        }
    }
}
