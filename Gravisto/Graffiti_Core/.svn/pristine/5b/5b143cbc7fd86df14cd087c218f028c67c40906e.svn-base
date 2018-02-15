// =============================================================================
//
//   HexaCoordWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

/**
 * Dies ist eine hilfsklasse die z.b. erlaubt das sortieren von ContureHexa
 * Punkte (Endpunkte) ohne die Klasse ContureHexa unnötig zu belasten mit
 * funktionalität die nur einmal benutzt wird Diese Klasse implementiert
 * compareTo
 */

public class ContureHexaWrapper implements Comparable<ContureHexaWrapper> {
    public double coord; // Koordinate nach der sortiert wird

    public double distToV; // Abstand vom ContureHexa Punkt zu V

    public ContureHexa original; // Referenz zum Original

    public ContureHexaWrapper other; // Referenz zum wrapper vom anderen
    // Endpunkt

    public Contour referenceCont; // Referenz zur Kontur in der man sich

    // befindet

    ContureHexaWrapper(ContureHexa reference, Directions dire,
            ContureHexaWrapper other, HexaCoord v) {
        original = reference;
        if (v != null) {
            distToV = reference.calculateCoordinateWithRef(Directions
                    .perpendicular(dire), v);
        }
        coord = reference.calculateCoordinate(dire);
        this.other = other;
    }

    /*
     * Ein comparable wird implementiert um eine Liste von diesen Objekten nach
     * dem Attribut "coord" aufsteigend sortieren zu koennen
     */
    public int compareTo(ContureHexaWrapper comparand) {
        if (this.coord > comparand.coord)
            return 1;
        else if (this.coord < comparand.coord)
            return -1;
        else {
            // Wenn sie auf der selben höhe sind, entscheidet wer tiefer ist,
            // also vom v Knoten am meisten entfernt
            if (this.distToV > comparand.distToV)
                return -1;
            else if (this.distToV < comparand.distToV)
                return 1;
            else
                return 0;
        }
    }
}
