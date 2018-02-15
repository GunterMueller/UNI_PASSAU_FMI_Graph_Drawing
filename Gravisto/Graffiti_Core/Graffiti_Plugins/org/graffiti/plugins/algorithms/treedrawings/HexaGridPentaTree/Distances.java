// =============================================================================
//
//   Distances.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.Iterator;

/**
 * Wichtige Klasse im Kompaktierungsalgorithmus vom paper Stellt die Methoden
 * zur verfuegung um den Abstand zwischen: Punkt-Kontur, Segment-Kontur, und
 * Kontur-Kontur zu berechnen. Die letzteren bauen auf die vorherigen auf. Zu
 * beachten ist das jede von diesen Operation in Konstanter Zeit laufen muss Da
 * die Abstaende nicht nur zwischen realen Knoten berechnet werden muessen, wird
 * immer als "Punkt" eine Hexa Koordinate betrachtet
 */
public class Distances {
    /**
     * Fuer einen Punkt im Gitter eine Richtung und einen Segment berechnet
     * diese Methode die Projektion vom Punkt auf dem Segment, entsprechend der
     * Definition in der Arbeit
     */
    public static HexaCoord calculateProjectionPointOnSegmentWithDire(
            HexaCoord point, Segment segment, Directions dire) {
        HexaCoord result = calculateProjectionPointOnSegment(point, segment,
                dire);
        if (result != null) {
            if (point.aims(dire, result))
                return result;
        }
        return null;
    }

    /**
     * Berechnet mit einer Fallunterscheidung die "Projektion" eines Punktes auf
     * einem Segment entlang einer gegebenen Richtung. Falls die Richtung und
     * der Segment Parallel sind gibt es keinen Schnittpunkt und es wird null
     * zurueckgegeben sonst der Schnittpunkt als HexaCoord
     */
    public static HexaCoord calculateProjectionPointOnSegment(HexaCoord point,
            Segment segment, Directions dire) {
        //
        HexaCoord schnittpunkt = new HexaCoord();

        // Falls der PUnkt im Segment enthalten ist dann ist
        // es der Schnittpunkt
        if (segment.contains(point))
            return point;
        // Von nun an ist dieser Fall also ausgeschlossen

        if (Directions.parallel(segment.direction(), dire)
                && (point.aims(dire, segment.start) || point.aims(Directions
                        .reverse(dire), segment.start))) {

            if (point.distanceToPoint(segment.start) > point
                    .distanceToPoint(segment.end)) {
                schnittpunkt.copy(segment.end);
            } else {
                schnittpunkt.copy(segment.start);
            }
            return schnittpunkt;
        } else {

            // Falls der Segment ein PUnkt ist, ist dieser PUnkt der
            // Schnittpunkt
            // nur im Falle das er in der von der Richtung und anfangspunkt
            // aufgespannte Gerade enthalten ist.
            if (Directions.parallel(dire, Directions.BACK_RIGHT)) {
                // Da bleibt die x-Koord von point fest
                if (segment.getStart().equal(segment.getEnd())) {
                    if (point.getX() == segment.getStart().getX()) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {
                    if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT)) {
                        // Dann sind die parallel. Einen Schnittpunkt gibt es
                        // nur wenn der Punkt selber
                        // im Segment enthalten ist. Diesen Fall habe ich aber
                        // schon
                        // am Anfang abgeprueft. Koennen also nur parallel sein,
                        // also kein Schnittpunkt
                        schnittpunkt = null;
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT)) {
                        schnittpunkt = new HexaCoord(point.getX(), segment
                                .getStart().getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT)) {
                        schnittpunkt = new HexaCoord(point.getX(), point.getX()
                                - (segment.getEnd().getX() - segment.getEnd()
                                        .getY()));
                    } else if (Directions.parallel(segment.direction(),
                            Directions.NORTH)) {
                        double n = 2 * (segment.getStart().getX() - point
                                .getX());
                        schnittpunkt = new HexaCoord(point.getX(), segment
                                .getStart().getY()
                                - n);
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT30)) {
                        double n = (segment.getStart().getX() - point.getX()) / 2;
                        schnittpunkt = new HexaCoord(point.getX(), segment
                                .getStart().getY()
                                - n);
                    } else if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT30)) {
                        schnittpunkt = new HexaCoord(point.getX(), segment
                                .getStart().getY()
                                - point.getX() + segment.getStart().getX());
                    }
                }
            } else if (Directions.parallel(dire, Directions.STRAIGHT)) {
                // bleibt die y Koord von point fest
                if (segment.getStart().equal(segment.getEnd())) {
                    if (point.getY() == segment.getStart().getY()) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {

                    if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT)) {
                        schnittpunkt = new HexaCoord(segment.getStart().getX(),
                                point.getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT)) {
                        schnittpunkt = null;
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT)) {
                        schnittpunkt = new HexaCoord(segment.getEnd().getX()
                                - segment.getEnd().getY() + point.getY(), point
                                .getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.NORTH)) {
                        double n = (segment.getStart().getY() - point.getY()) / 2;
                        schnittpunkt = new HexaCoord(segment.getStart().getX()
                                - n, point.getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT30)) {
                        double n = 2 * (segment.getStart().getY() - point
                                .getY());
                        schnittpunkt = new HexaCoord(segment.getStart().getX()
                                - n, point.getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT30)) {
                        schnittpunkt = new HexaCoord(segment.getStart().getY()
                                + segment.getStart().getX() - point.getY(),
                                point.getY());
                    }
                }

            } else if (Directions.parallel(dire, Directions.STRAIGHT_RIGHT)) {
                if (segment.getStart().equal(segment.getEnd())) {
                    if ((point.getX() - point.getY()) == (segment.getStart()
                            .getX() - segment.getStart().getY())) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {

                    if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT)) {

                        schnittpunkt = new HexaCoord(segment.getStart().getX(),
                                segment.getStart().getX()
                                        - (point.getX() - point.getY()));
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT)) {
                        schnittpunkt = new HexaCoord((point.getX() - point
                                .getY())
                                + segment.getStart().getY(), segment.getStart()
                                .getY());
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT)) {
                        schnittpunkt = null;

                    } else if (Directions.parallel(segment.direction(),
                            Directions.NORTH)) {
                        double n = 2 * segment.getStart().getX() - 2
                                * point.getX() + 2 * point.getY()
                                - segment.getStart().getY();
                        schnittpunkt = new HexaCoord(point.getX()
                                - point.getY() + n, n);
                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT30)) {
                        double n = 2 * segment.getStart().getY()
                                - segment.getStart().getX() + point.getX()
                                - point.getY();
                        schnittpunkt = new HexaCoord(point.getX()
                                - point.getY() + n, n);
                    } else if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT30)) {
                        double n = (segment.getStart().getX()
                                + segment.getStart().getY() - point.getX() + point
                                .getY()) / 2;
                        schnittpunkt = new HexaCoord(point.getX()
                                - point.getY() + n, n);
                    }
                    // es sind noch drei Richtungen frei
                }
            } else if (Directions.parallel(dire, Directions.STRAIGHT_RIGHT30)) {
                if (segment.getStart().equal(segment.getEnd())) {

                    if ((segment.getStart().getX() - point.getX()) / 2 == (segment
                            .getStart().getY() - point.getY())) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {
                    if (Directions.parallel(segment.direction(),
                            Directions.BACK_RIGHT)) {
                        double n = (point.getX() - segment.getStart().getX()) / 2;
                        schnittpunkt = new HexaCoord(segment.getStart().getX(),
                                point.getY() - n);

                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT)) {
                        double n = point.getY() - segment.getStart().getY();
                        schnittpunkt = new HexaCoord(point.getX() - 2 * n,
                                segment.getStart().getY());

                    } else if (Directions.parallel(segment.direction(),
                            Directions.STRAIGHT_RIGHT)) {
                        double n = segment.getStart().getY()
                                - segment.getStart().getX() + point.getX()
                                - point.getY();
                        schnittpunkt = new HexaCoord(point.getX() - 2 * n,
                                point.getY() - n);
                    }
                }

            } else if (Directions.parallel(dire, Directions.BACK_RIGHT30)) {
                if (segment.getStart().equal(segment.getEnd())) {

                    if ((segment.getStart().getX() - point.getX()) == (point
                            .getY() - segment.getStart().getY())) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {
                    if ((segment.start.getX() - segment.start.getY()) == (segment.end
                            .getX() - segment.end.getY())) {
                        double n = (segment.start.getX() - segment.start.getY() - (point
                                .getX() - point.getY())) / 2;
                        schnittpunkt = new HexaCoord(point.getX() + n, point
                                .getY()
                                - n);
                    } else if (segment.getStart().getX() == segment.getEnd()
                            .getX()) {
                        double n = segment.getStart().getX() - point.getX();
                        schnittpunkt = new HexaCoord(segment.getStart().getX(),
                                point.getY() - n);
                    } else if (segment.getStart().getY() == segment.getEnd()
                            .getY()) {
                        double n = point.getY() - segment.getStart().getY();
                        schnittpunkt = new HexaCoord(point.getX() + n, segment
                                .getStart().getY());
                    }

                }
                // Vertikal
            } else if (Directions.parallel(dire, Directions.NORTH)) {
                if (segment.getStart().equal(segment.getEnd())) {

                    if ((point.getX() - segment.start.getX()) == ((point.getY() - segment.start
                            .getY()) / 2)) {
                        schnittpunkt = segment.getStart();
                    } else {
                        schnittpunkt = null;
                    }
                } else {
                    if (segment.getStart().getY() == segment.getEnd().getY()) {
                        double n = (segment.getStart().getY() - point.getY()) / 2;

                        schnittpunkt = new HexaCoord(point.getX() + n, segment
                                .getStart().getY());
                    } else if (segment.getStart().getX() == segment.getEnd()
                            .getX()) {

                        double n = 2 * (point.getX() - segment.getStart()
                                .getX());
                        schnittpunkt = new HexaCoord(segment.getStart().getX(),
                                point.getY() - n);
                    } else if ((segment.getStart().getX() - segment.getEnd()
                            .getX()) == (segment.getStart().getY() - segment
                            .getEnd().getY())) {
                        double n = segment.getStart().getX()
                                - segment.getStart().getY() + point.getY()
                                - point.getX();
                        schnittpunkt = new HexaCoord(point.getX() - n, point
                                .getY()
                                - 2 * n);
                    }

                }

            }

            /*
             * Wenn jetzt schnittpunkt != null ist dann koennte es noch sein das
             * dieser Ergebnis Punkt nicht im Segment wo man projeziert
             * enthalten ist Wenn der Punkt "auf der Gerade vom Segment ist"
             * dann ist mindestens eine Koordinate vom Punkt gleich denen des
             * Segments
             */

            if (schnittpunkt != null) {
                if (segment.contains(schnittpunkt))
                    return schnittpunkt;
                else
                    return null;
            }

            // Wenn keines der vorherigen Faelle eingetreten ist
            // dann war der gefundene Schnittpunkt nicht im Segment enthalten

            return null;
        }
    }

    /**
     * Berechnet fuer einen Gegebenen Punkt den Abstand entlang einer Richtung
     * zwischen den Punkt und einen Segment. Wenn der Segment den Punkt in der
     * Richtung nicht schneidet dann kommt unendlich raus.
     */
    private static int calculateDistPointSegment(HexaCoord point,
            Segment segment, Directions dire) {

        // Falls die Projektion existiert, sonst null
        HexaCoord projektion = calculateProjectionPointOnSegment(point,
                segment, dire);

        if (projektion != null)
            return (int) point.distanceToPoint(projektion);
        else
            return Integer.MAX_VALUE;
    }

    /**
     * Berechnet den Abstand zwischen einem Punkt im Gitter und einer Kontur
     * entlang einer Richtung
     */
    public static int distanceHexaCoordConture(HexaCoord point,
            ConvexContour contour, Directions dire) {
        int minDistValue = Integer.MAX_VALUE;
        HexaCoord startSegment, endSegment;
        for (int i = 0; i < contour.surroundingPoints.size() - 1; i++) {
            startSegment = contour.surroundingPoints.get(i);
            endSegment = contour.surroundingPoints.get(i + 1);

            minDistValue = Math.min(minDistValue, calculateDistPointSegment(
                    point, new Segment(startSegment, endSegment), dire));
        }

        startSegment = contour.surroundingPoints.get(contour.surroundingPoints
                .size() - 1);
        endSegment = contour.surroundingPoints.get(0);
        minDistValue = Math.min(minDistValue, calculateDistPointSegment(point,
                new Segment(startSegment, endSegment), dire));

        return minDistValue;

    }

    /**
     * Abstand zwischen einen Segment und einer Kontur entlang einer Richtung
     */
    static int distanceSegmentConture(Segment segment, ConvexContour contour,
            Directions dire) {
        int distanceToStartSegment, distanceToEndSegment;
        int minDistContureToSegment = Integer.MAX_VALUE;
        int minDistFinal = Integer.MAX_VALUE;

        distanceToStartSegment = distanceHexaCoordConture(segment.getStart(),
                contour, dire);
        distanceToEndSegment = distanceHexaCoordConture(segment.getEnd(),
                contour, dire);

        minDistFinal = Math.min(distanceToStartSegment, distanceToEndSegment);

        for (HexaCoord perimeter : contour.surroundingPoints) {
            minDistContureToSegment = Math.min(minDistContureToSegment,
                    calculateDistPointSegment(perimeter, segment, dire));
        }
        minDistFinal = Math.min(minDistFinal, minDistContureToSegment);

        return minDistFinal;
    }

    /*
     * Berechnet den Abstand zwischen zwei Konvexen Konturen
     */
    static int distanceConvexContureConture(ConvexContour contour1,
            ConvexContour contour2, Directions dire) {

        int minDistFinal = Integer.MAX_VALUE;
        HexaCoord startSegment, endSegment;

        for (int i = 0; i < contour1.surroundingPoints.size() - 1; i++) {
            startSegment = contour1.surroundingPoints.get(i);
            endSegment = contour1.surroundingPoints.get(i + 1);

            minDistFinal = Math.min(minDistFinal, distanceSegmentConture(
                    new Segment(startSegment, endSegment), contour2, dire));
        }
        startSegment = contour1.surroundingPoints
                .get(contour1.surroundingPoints.size() - 1);
        endSegment = contour1.surroundingPoints.get(0);
        minDistFinal = Math.min(minDistFinal, distanceSegmentConture(
                new Segment(startSegment, endSegment), contour2, dire));

        return minDistFinal;
    }

    /**
     * Abstand zwischen zwei KOnturen, es koennte gut sein das der anfang von
     * der einen ziemlich entfernt vom anfang der zweiten ist
     */

    public static double distanceContourContour(ContureHexa cont1,
            ContureHexa cont2, Directions watchDire, Directions watchDire1,
            Directions watchDire2) {
        // Ich weiss erstmals nicht ob ich vor einer Kontur mit gleichem
        // Startpunkt oder nicht.
        // Oder beide oder keiner.. das aendert aber nur den Punkt an dem man
        // mit "crawl" anfaengt

        // Wenn eine der beiden leer ist, gilt Punkt Punkt abstand

        HexaCoord beginning = cont1.clone();
        HexaCoord actualCoordCont1 = cont1.clone();
        HexaCoord actualCoordCont2 = cont2.clone();

        Iterator<Vector> itCont1 = cont1.ausgehende.iterator();
        Iterator<Vector> itCont2 = cont2.ausgehende.iterator();

        Vector vecCont1, vecCont2;
        // Wenn eine von beiden einpuenktig ist
        if (cont1.ausgehende.isEmpty()) {
            // Das kann nur die andere Seite von einer Typ2B sein
            if (cont2.ausgehende.isEmpty())
                // Wenn also beide leer sind, dann ist natuerlich distanz punkt
                // punkt gefragt
                return cont1.distanceToPoint(cont2);
            else {
                // Dann muss ich bei cont2 so lange rumsuchen bis cont1 auf die
                // Kontur von cont2 projeziert
                HexaCoord nextTemp2 = cont2.addTemp(itCont2.next());
                Segment seg2 = new Segment(actualCoordCont2, nextTemp2);

                while (Distances.calculateProjectionPointOnSegment(cont1, seg2,
                        watchDire) == null
                        && itCont2.hasNext()) {
                    actualCoordCont2.copy(nextTemp2);
                    nextTemp2.add(itCont2.next());
                    seg2 = new Segment(actualCoordCont2, nextTemp2);
                }

                HexaCoord proj = Distances.calculateProjectionPointOnSegment(
                        cont1, seg2, watchDire);
                if (proj != null)
                    return cont1.distanceToPoint(proj);
                else
                    return Integer.MAX_VALUE;

            }
        }
        if (cont2.ausgehende.isEmpty()) {
            // Dann muss cont1.ausgehende NICHT leer sein
            // Dann muss ich bei cont2 so lange rumsuchen bis cont1 auf die
            // Kontur von cont2 projeziert
            HexaCoord nextTemp1 = cont1.addTemp(itCont1.next());
            Segment seg1 = new Segment(actualCoordCont1, nextTemp1);

            while (Distances.calculateProjectionPointOnSegment(cont2, seg1,
                    watchDire) == null
                    && itCont1.hasNext()) {
                actualCoordCont1.copy(nextTemp1);
                nextTemp1.add(itCont1.next());
                seg1 = new Segment(actualCoordCont1, nextTemp1);
            }

            HexaCoord proj = Distances.calculateProjectionPointOnSegment(cont2,
                    seg1, watchDire);
            if (proj != null)
                return cont2.distanceToPoint(proj);
            else
                return Integer.MAX_VALUE;
        }

        // Wenn ich also hier bin, weiss ich sicher das beide NICHT leer sind.
        // Also alle einpuenktige "andere" Seiten in den Typ2B sind schon weg
        // und der Sonderfall bei Typ2A eh schon viel frueher gefiltert
        vecCont1 = itCont1.next();
        vecCont2 = itCont2.next();

        HexaCoord nextTemp1, nextTemp2;
        double distance = Integer.MAX_VALUE;

        if (!cont1.equal(cont2)
                && cont1.calculateCoordinate(watchDire) != cont2
                        .calculateCoordinate(watchDire)) {
            // Dann schwebt oder nur eine, oder sogar beide
            // Also muss actualCoordCont1 oder zwei auf die richtige stelle
            // gebracht werden
            // Bei beiden wird .next gemacht
            nextTemp1 = actualCoordCont1.addTemp(vecCont1);
            nextTemp2 = actualCoordCont1.addTemp(vecCont2);

            Segment seg1 = new Segment(actualCoordCont1, nextTemp1);
            Segment seg2 = new Segment(actualCoordCont2, nextTemp2);

            if (Distances.calculateProjectionPointOnSegment(nextTemp1, seg2,
                    watchDire) == null
                    && Distances.calculateProjectionPointOnSegment(nextTemp2,
                            seg1, watchDire) == null) {
                // Wenn eine der beiden nexts, den segment vom anderen beruehrt
                // dann koennen die gleich in die normale
                // Abstandsberechnung gehen
                // Wenn das aber nicht so ist, dann ist einer vor dem anderen,
                // in welcher Richtung auch immer
                if (Math.abs(actualCoordCont2.calculateCoordinate(watchDire)
                        - nextTemp1.calculateCoordinate(watchDire)) < Math
                        .abs(actualCoordCont2.calculateCoordinate(watchDire)
                                - actualCoordCont1
                                        .calculateCoordinate(watchDire))) {
                    while (Distances.calculateProjectionPointOnSegment(
                            nextTemp1, seg2, watchDire) == null
                            && itCont1.hasNext()) {
                        nextTemp1.add(itCont1.next());
                    }
                    actualCoordCont1.copy(nextTemp1);
                } else { // (Distances.calculateProjectionPointOnSegment(nextTemp2,
                    // seg1, watchDire) != null) {
                    while (Distances.calculateProjectionPointOnSegment(
                            nextTemp2, seg1, watchDire) == null
                            && itCont2.hasNext()) {
                        nextTemp2.add(itCont2.next());
                    }
                    actualCoordCont2.copy(nextTemp2);
                }

            }

        } else if (!cont1.equal(cont2)
                && cont1.calculateCoordinate(watchDire) == cont2
                        .calculateCoordinate(watchDire)) {
            // Dann schweben beide auf der selben Gerade am Anfang
            distance = cont1.distanceToPoint(cont2);
        } else {
            // dann muss cont1.equal(cont2) sein
        }

        // Jetzt wurde der "erste" Punkt ueberstanden

        HexaCoord actual = new HexaCoord();
        HexaCoord proj = new HexaCoord();
        Segment segment;
        boolean finish = false;
        do {
            if (actualCoordCont1.addTemp(vecCont1).calculateCoordinateWithRef(
                    watchDire, beginning) > actualCoordCont2.addTemp(vecCont2)
                    .calculateCoordinateWithRef(watchDire, beginning)) {
                // Vorm projezieren, wird geschaut ob man nicht auf "next" gehen
                // kann
                segment = new Segment(actualCoordCont1, actualCoordCont1
                        .addTemp(vecCont1));

                actualCoordCont2.add(vecCont2);
                if (itCont2.hasNext()) {
                    vecCont2 = itCont2.next();
                } else {
                    finish = true;
                }
                if (vecCont2.direction() == Directions.reverse(watchDire2)) {
                    actualCoordCont2.add(vecCont2);
                    if (itCont2.hasNext()) {
                        vecCont2 = itCont2.next();
                    } else {
                        finish = true;
                    }
                }
                actual.copy(actualCoordCont2);

                if (Directions.parallel(vecCont2.direction(), watchDire)) {
                    actualCoordCont2.add(vecCont2);
                    if (itCont2.hasNext()) {
                        vecCont2 = itCont2.next();
                    } else {
                        finish = true;
                    }
                }
                proj = Distances.calculateProjectionPointOnSegment(actual,
                        segment, watchDire);

            } else if (actualCoordCont1.addTemp(vecCont1)
                    .calculateCoordinateWithRef(watchDire, beginning) < actualCoordCont2
                    .addTemp(vecCont2).calculateCoordinateWithRef(watchDire,
                            beginning)) {

                segment = new Segment(actualCoordCont2, actualCoordCont2
                        .addTemp(vecCont2));
                actualCoordCont1.add(vecCont1);
                if (itCont1.hasNext()) {
                    vecCont1 = itCont1.next();
                } else {
                    finish = true;
                }
                if (vecCont1.direction() == Directions.reverse(watchDire1)) {
                    actualCoordCont1.add(vecCont1);
                    if (itCont1.hasNext()) {
                        vecCont1 = itCont1.next();
                    } else {
                        finish = true;
                    }
                }

                actual.copy(actualCoordCont1);
                if (Directions.parallel(vecCont1.direction(), watchDire)) {
                    actualCoordCont1.add(vecCont1);
                    if (itCont1.hasNext()) {
                        vecCont1 = itCont1.next();
                    } else {
                        finish = true;
                    }
                }

                proj = Distances.calculateProjectionPointOnSegment(actual,
                        segment, watchDire);

            } else {
                // Sie sind gleich hoch
                actualCoordCont1.add(vecCont1);
                actualCoordCont2.add(vecCont2);
                if (itCont2.hasNext()) {
                    vecCont2 = itCont2.next();
                } else {
                    finish = true;
                }
                if (itCont1.hasNext()) {
                    vecCont1 = itCont1.next();
                } else {
                    finish = true;
                }

                if (vecCont2.direction() == Directions.reverse(watchDire2)) {
                    actualCoordCont2.add(vecCont2);
                    if (itCont2.hasNext()) {
                        vecCont2 = itCont2.next();
                    } else {
                        finish = true;
                    }
                }

                actual.copy(actualCoordCont2);
                if (Directions.parallel(vecCont2.direction(), watchDire)) {
                    actualCoordCont2.add(vecCont2);
                    if (itCont2.hasNext()) {
                        vecCont2 = itCont2.next();
                    } else {
                        finish = true;
                    }
                }

                if (vecCont1.direction() == Directions.reverse(watchDire1)) {
                    actualCoordCont1.add(vecCont1);
                    if (itCont1.hasNext()) {
                        vecCont1 = itCont1.next();
                    } else {
                        finish = true;
                    }
                }

                proj.copy(actualCoordCont1);
                if (Directions.parallel(vecCont1.direction(), watchDire)) {
                    actualCoordCont1.add(vecCont1);
                    if (itCont1.hasNext()) {
                        vecCont1 = itCont1.next();
                    } else {
                        finish = true;
                    }
                }

            }

            if (proj != null) {
                distance = Math.min(distance, proj.distanceToPoint(actual));
            } else {
                finish = true;
            }

        } while (!finish);
        return distance;
    }
}
