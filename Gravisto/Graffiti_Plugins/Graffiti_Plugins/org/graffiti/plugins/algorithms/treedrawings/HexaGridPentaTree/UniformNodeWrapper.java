// =============================================================================
//
//   UniformNodeWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import org.graffiti.graphics.CoordinateAttribute;

/**
 * Eine einfache wrapper klasse um am Anfang beim kopieren vom originellen Graph
 * ein paar Hilfsmethoden und attribute zur verfuegung zu haben. Da diese
 * Methoden und Attr. nur an dieser Stelle benutzt werden und nicht nachher wird
 * eine Wrapper Klasse benutzt um die UniformNode Struktur nicht unnoetig zu
 * belasten
 */
public class UniformNodeWrapper implements Comparable<UniformNodeWrapper> {
    public Double angleToFather;

    public UniformNode original;

    public double x;

    public double y;

    UniformNodeWrapper(UniformNode reference) {
        original = reference;
    }

    /**
     * Ein comparable wird implementiert um eine Liste von diesen Objekten nach
     * dem Attribut "angleToFather" aufsteigend sortieren zu k�nnen
     */
    public int compareTo(UniformNodeWrapper comparand) {
        if (this.angleToFather > comparand.angleToFather)
            return 1;
        else if (this.angleToFather < comparand.angleToFather)
            return -1;
        else
            return 0;
    }

    public CoordinateAttribute getCoordinates() {
        return original.getCoordinates();
    }
}
