// =============================================================================
//
//   UniformNode.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
/*
 * Als die Gravisto Datenstruktur kopiert wird, werden die alten Knoten 
 * in diesen UniformNodes gespeichert. Die KLasse bietet funktionalitaet die
 * auf die Algorithmen zugeschnitten ist
 */

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.ArrayList;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Implementiert den Knoten eines local uniformen Penta Baum Diese Hilfsstruktur
 * wird benutzt um die eigene Datenstruktur zu benutzten und nicht die von
 * Gravisto die wahrscheinlich langsamer sein wuerde. Ausserdem ist es weit
 * komfortabler so, denn man kann aendern was man will. Was nicht so einfach der
 * Fall gewesen waere wenn die Gravisto Graph Struktur benutzt wuerde
 */

public class UniformNode {
    public ConvexContour c; // Jeder Knoten im Baum hat eine Kontur zugeordnet

    // sei er Blatt oder ein normaler innerer Knoten
    public Node referenceToOriginal; // Die Referenz zum "originalen" Gravisto

    // knoten, wird benutzt um die realen Koordinaten auszulesen und aendern
    public UniformNode father; // Referenz zum Vater

    public int depth; // Tiefe des Knoten im Baum

    public int edgeLengthToChildren; // Laenge zu den eigenen Kindern.

    public HexaCoord hexa; // Koordinate des Knoten im Hexagonalen Gitter

    public ArrayList<UniformNode> listSons; // Liste von Kinder

    public Directions direction; // Die Richtung die der Vater zum Knoten hatte

    public Contour contour;

    public UniformNode() {
    }

    /**
     * Erstellt einen neuen Knoten mit einer Referenz zum Fater und zum
     * originellen Graph Knoten
     */
    public UniformNode(Node referenceToOriginal, UniformNode father) {
        this.referenceToOriginal = referenceToOriginal;
        this.father = father;
        if (father != null) {
            this.depth = father.depth + 1;
        } else {
            depth = 0;
        }
        hexa = new HexaCoord();
        listSons = new ArrayList<UniformNode>();
        direction = Directions.STRAIGHT;
    }

    public int getNumberOfChildren() {
        return listSons.size();
    }

    CoordinateAttribute getCoordinates() {
        return ((NodeGraphicAttribute) referenceToOriginal
                .getAttribute("graphics")).getCoordinate();
    }

    public HexaCoord getHexa() {
        return hexa;
    }

    @Override
    public String toString() {
        return hexa.toString();
    }
}
