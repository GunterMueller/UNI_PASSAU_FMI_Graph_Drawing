// =============================================================================
//
//   SecondWalk.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.awt.geom.Point2D;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class SecondWalk {
    private Graph graph;

    public SecondWalk() {

    }

    public SecondWalk(Graph g) {
        this.graph = g;
    }

    /**
     * Executes all position changes calculated in the first step
     * 
     * @param node
     *            the current node
     * @param shiftX
     *            an additional value that has to be added to the calculated x
     *            value for the node
     * @param shiftY
     *            an additional value that has to be added to the calculated x
     *            value for the node
     */
    public void secondWalk(HexaNode node, double shiftX, double shiftY) {

        node.setX(node.getX() + shiftX);
        node.setY(node.getY() + shiftY);

        // Insert bend if necessary
        if (node.hasBend()) {
            Edge edge = graph.getEdges(node.getNode(), node.getBend())
                    .iterator().next();

            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");
            Point2D currentBend = new Point2D.Double(node.getX() + 0.5
                    * HexaConstants.unit, node.getY()
                    + HexaConstants.unitHeight);
            bends.add(new CoordinateAttribute("bend0", currentBend));
            edgeAttr.setBends(bends);
            edgeAttr.setShape(PolyLineEdgeShape.class.getName());
        }

        for (int i = 0; i < node.getNumberOfChildren(); i++) {
            HexaNode n = (HexaNode) node.getChildren().get(i);

            // use the x and y attributes of node as a shift value since all
            // positions are calculated relative to its father
            secondWalk(n, node.getX(), node.getY());

        }

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
