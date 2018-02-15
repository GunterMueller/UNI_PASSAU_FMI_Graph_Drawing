// =============================================================================
//
//   SplitPacket.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

/**
 * Wenn ein Schnittpunkt entsteht beim mergen zwischen einer Projektion und die
 * Kante zum Grossvater (v,v.partent) dann muss man verschiedene Werte Speichern
 * um dann spaeter die KOntur an dieser Stelle in zwei teilen zu koennen
 */
public class SplitPacket<E> {

    Liste.Entry<E> listenElement;

    Liste.Entry<E> listenElementOtherDirection;

    HexaCoord originPunkt;

    HexaCoord schnitt;

    ContureHexa nichtBedeckt;

    SplitPacket(HexaCoord punkt, Liste.Entry<E> listElem,
            Liste.Entry<E> listElem2, HexaCoord schnitt,
            ContureHexa nichtBedeckt) {
        listenElement = listElem;
        this.originPunkt = punkt;
        this.schnitt = schnitt;
        this.nichtBedeckt = nichtBedeckt;
        this.listenElementOtherDirection = listElem2;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
