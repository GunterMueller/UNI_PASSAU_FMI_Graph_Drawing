// =============================================================================
//
//   Seite.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

/**
 * Bei der Abstandsberechnung ist es wichtig zu unterscheiden zwischen der
 * linken und rechten Seite einer Kontur. Wenn es nicht unterscheidbar ist wird
 * "Oben" gestellt
 */

public enum Seite {
    Links, Rechts, Oben;

    /**
     * Gibt die "entgegengesetzte" Seite zurück
     */
    public static Seite opposite(Seite seiteMitRichtung) {

        switch (seiteMitRichtung) {
        case Links:
            return Rechts;
        case Rechts:
            return Links;
        case Oben:
            return Oben;
        }
        return null;
    }
}
