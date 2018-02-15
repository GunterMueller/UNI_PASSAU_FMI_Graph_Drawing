// =============================================================================
//
//   ContureHexa.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Diese Klasse implementiert den Endpunkt einer Kontur. Jeder Endpunkt verweist
 * auf zwei Listen, eine mit den eingehenden Vektoren der Kontur und die andere
 * mit den ausgehenden Vektoren der Kontur
 */
public class ContureHexa extends HexaCoord {
    public Liste<Vector> eingehende; // Eingehende Vektoren

    public Liste<Vector> ausgehende; // Ausgehende Vektoren

    public ContureHexa otherEnd; // Verweis auf den anderen Endpunkt

    public HalfConture referenceHalf;// Verweis auf die hälfte wo man sich

    // befindet

    ListIterator<Vector> itAusgehende() {
        return ausgehende.listIterator();
    }

    ContureHexa(Liste<Vector> eingehende, Liste<Vector> ausgehende,
            ContureHexa otherEnd, HalfConture referenceHalf) {
        this.eingehende = eingehende;
        this.ausgehende = ausgehende;
        this.otherEnd = otherEnd;
        this.referenceHalf = referenceHalf;

    }

    /*
     * Überprüft ob other eine andere Kontur ist
     */
    public boolean sameConture(ContureHexa other) {
        return (this.otherEnd.equal(other));
    }

    /*
     * Falls mehrere ausgehende Vektoren parallel zur Betrachtungsrichtung dire
     * verlaufen, gibt diese Methode den ersten Vektor zurück der nicht parallel
     * zu dire verläuft
     */
    public HexaCoord returnFirstSichtbar(Directions dire) {
        HexaCoord ergebnis = this.clone();
        Iterator<Vector> iter = this.ausgehende.iterator();

        while (iter.hasNext() && this.aims(dire, ergebnis)) {
            ergebnis.add(iter.next());
        }

        return ergebnis;

    }

    /*
     * Falls mehrere ausgehende Vektoren parallel zur Betrachtungsrichtung dire
     * verlaufen, gibt diese Methode den letzten Vektor zurück der parallel zu
     * dire verläuft
     */
    public HexaCoord returnLastNotSichtbar(Directions dire) {
        HexaCoord bedeckt = this.clone();
        HexaCoord ergebnis = this.clone();
        Iterator<Vector> iter = this.ausgehende.iterator();

        while (iter.hasNext() && this.aims(dire, bedeckt)) {
            ergebnis.copy(bedeckt);
            bedeckt.add(iter.next());
        }
        return ergebnis;

    }
}
