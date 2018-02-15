// =============================================================================
//
//   BottomCenterToTopCenterEdgeLayoutStrategy.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.LinkedList;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.plugins.views.defaults.StraightLineEdgeShape;

/**
 * Lays the edges so that each edge leaves the parent at the center of the
 * bottom side and enters the child at the center of the top side.
 * <p>
 * <center> <img
 * src="doc-files/BottomCenterToTopCenterEdgeLayoutStrategy-1.png"> </center>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see EdgeLayout#BOTTOM_CENTER_TO_TOP_CENTER
 */
public class BottomCenterToTopCenterEdgeLayoutStrategy implements
        EdgeLayoutStrategy {
    /**
     * Singleton for <code>BottomCenterToTopCenterEdgeLayoutStrategy</code.
     */
    static BottomCenterToTopCenterEdgeLayoutStrategy singleton = new BottomCenterToTopCenterEdgeLayoutStrategy();

    /**
     * Returns the single <code>BottomCenterToTopCenterEdgeLayoutStrategy</code>
     * object.
     * 
     * @return the single <code>BottomCenterToTopCenterEdgeLayoutStrategy</code>
     *         object.
     */
    public static BottomCenterToTopCenterEdgeLayoutStrategy getSingleton() {
        return singleton;
    }

    /**
     * {@inheritDoc}
     */
    public void calculateContours(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        BasicContourNodeList oldLeftContour = tree.getLeftContour();
        BasicContourNodeList oldRightContour = tree.getRightContour();
        BasicContourNodeList newLeftContour = new BasicContourNodeList();
        BasicContourNodeList newRightContour = new BasicContourNodeList();
        double nodeWidth = tree.getNodeWidth();
        double nodeHeight = tree.getNodeHeight();
        double nodeLeft = tree.getNodeLeft();
        double verticalNodeDistance = tree.getVerticalNodeDistance();
        double deltaY = nodeHeight + verticalNodeDistance;
        newLeftContour.addNode(0.0, nodeHeight);
        newLeftContour.addNode(nodeWidth / 2.0, 0.0);
        newRightContour.addNode(nodeWidth, 0.0);
        newRightContour.addNode(0.0, nodeHeight);
        newRightContour.addNode(-nodeWidth / 2.0, 0.0);
        LinkedList<Tree> children = tree.getChildren();
        Tree leftMostChild = children.getFirst();
        double deltaX = leftMostChild.getLeft() + leftMostChild.getNodeLeft()
                - nodeLeft;
        if (children.size() > 1) {
            Tree rightMostChild = children.getLast();
            newLeftContour.addNode(deltaX - nodeWidth / 2.0
                    + leftMostChild.getNodeWidth() / 2.0, verticalNodeDistance);

            newRightContour
                    .addNode(-nodeLeft - nodeWidth / 2.0
                            + rightMostChild.getLeft()
                            + rightMostChild.getNodeLeft()
                            + rightMostChild.getNodeWidth() / 2.0,
                            verticalNodeDistance);

        } else {
            newLeftContour.addNode(0.0, verticalNodeDistance);
            newRightContour.addNode(0.0, verticalNodeDistance);
        }
        newLeftContour.getLast().connectToLeftContour(oldLeftContour, deltaX,
                deltaY);
        newRightContour.getLast().connectToRightContour(oldRightContour,
                deltaX, deltaY);
        tree.setLeftContour(newLeftContour);
        tree.setRightContour(newRightContour);
    }

    /**
     * {@inheritDoc}
     */
    public void layEdges(Tree tree, double xOrigin, double yOrigin,
            ReingoldTilfordAlgorithm algorithm) {
        LinkedList<Tree> children = tree.getChildren();
        LinkedList<Port> ports = new LinkedList<Port>();
        ports.add(algorithm.createPort("tc", 0, -1));
        if (!children.isEmpty()) {
            ports.add(algorithm.createPort("bc", 0, 1));
        }
        PortsAttribute portsAttribute = (PortsAttribute) tree.getNode()
                .getAttribute("graphics.ports");
        portsAttribute.setCommonPorts(ports);
        for (Tree child : children) {
            Edge edge = child.getEdge();
            DockingAttribute docking = (DockingAttribute) edge
                    .getAttribute("graphics.docking");
            docking.setSource("bc");
            docking.setTarget("tc");
            EdgeGraphicAttribute edgeAttribute = (EdgeGraphicAttribute) edge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");
            edgeAttribute.setBends(bends);
            edgeAttribute.setShape(StraightLineEdgeShape.class.getName());
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
