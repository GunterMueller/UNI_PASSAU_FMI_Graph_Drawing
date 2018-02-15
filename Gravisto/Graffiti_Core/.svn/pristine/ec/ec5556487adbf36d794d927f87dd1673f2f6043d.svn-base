// =============================================================================
//
//   CenterToCenterEdgeLayoutStrategy.java
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
 * Lays the edges in a straight line between the parent and the respective
 * child.
 * <p>
 * If {@link #stayOnTopSide} is <code>false</code>, edges may intersect the
 * siblings of the incident child.
 * <p>
 * The following pictures show the results of the different assignments of
 * {@link #stayOnBottomSide} and {@link #stayOnTopSide}.
 * <p>
 * <center> {@code !stayOnTopSide && !stayOnBottomSide}&nbsp; <img
 * src="doc-files/CenterToCenterEdgeLayoutStrategy-1.png"> </center>
 * <p>
 * <center> {@code stayOnTopSide && !stayOnBottomSide}&nbsp;&nbsp;&nbsp; <img
 * src="doc-files/CenterToCenterEdgeLayoutStrategy-2.png"> </center>
 * <p>
 * <center> {@code stayOnTopSide && stayOnBottomSide}&nbsp;&nbsp;&nbsp; <img
 * src="doc-files/CenterToCenterEdgeLayoutStrategy-3.png"> </center>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see EdgeLayout#CENTER_TO_CENTER
 * @see EdgeLayout#CENTER_TO_TOP
 * @see EdgeLayout#BOTTOM_TO_TOP
 */
public class CenterToCenterEdgeLayoutStrategy implements EdgeLayoutStrategy {
    /**
     * Denotes if the port where the edge leaves the parent is restricted to the
     * bottom side of the parent.
     */
    private boolean stayOnBottomSide;

    /**
     * Denotes if the port where the edge enters the child is restricted to the
     * top side of the child. If <code>stayOnTopSide</code> is
     * <code>false</code>, edges may intersect the siblings of the incident
     * child.
     */
    private boolean stayOnTopSide;

    /**
     * {@inheritDoc}
     */
    public void calculateContours(Tree tree, ReingoldTilfordAlgorithm algorithm) {
        BasicContourNodeList oldLeftContour = tree.getLeftContour();
        BasicContourNodeList oldRightContour = tree.getRightContour();
        BasicContourNodeList newLeftContour = new BasicContourNodeList();
        BasicContourNodeList newRightContour = new BasicContourNodeList();
        double nodeWidth = tree.getNodeWidth();
        newRightContour.addNode(nodeWidth, 0.0);
        double nodeHeight = tree.getNodeHeight();
        double nodeLeft = tree.getNodeLeft();
        double verticalNodeDistance = tree.getVerticalNodeDistance();
        double deltaY = nodeHeight + verticalNodeDistance;

        LinkedList<Tree> children = tree.getChildren();
        Tree leftMostChild = children.getFirst();

        double deltaX = leftMostChild.getLeft() + leftMostChild.getNodeLeft()
                - nodeLeft;

        Tree rightMostChild = children.getLast();

        double x1 = nodeLeft + nodeWidth / 2.0;
        double y1 = nodeHeight / 2.0;
        double x2 = leftMostChild.getLeft() + leftMostChild.getNodeLeft()
                + leftMostChild.getNodeWidth() / 2.0;
        double y2 = deltaY + leftMostChild.getNodeHeight() / 2.0;
        // Calculate coordinates in contour space where the leftmost edge leaves
        // the parent.
        double fromX;
        double fromY;
        double beta = nodeHeight / 2.0 / (y2 - y1);

        // Where does the leftmost edge leave the parent.
        double xIntersect = beta * (x2 - x1);
        if (stayOnBottomSide) {
            xIntersect = Math.max(xIntersect, -nodeWidth / 2.0);
        }
        if (xIntersect < -nodeWidth / 2.0) {
            // Edge leaves parent at the left side.
            beta = nodeWidth / 2.0 / (x1 - x2);
            double yIntersect = beta * (y2 - y1);
            fromX = nodeLeft;
            fromY = nodeHeight / 2.0 + yIntersect;
            newLeftContour.addNode(0.0, fromY);
        } else {
            // Edge leaves parent at the bottom side.
            newLeftContour.addNode(0.0, nodeHeight);
            fromX = nodeLeft + nodeWidth / 2.0 + xIntersect;
            fromY = nodeHeight;
            newLeftContour.addNode(fromX - nodeLeft, 0.0);
        }
        beta = leftMostChild.getNodeHeight() / 2.0 / (y2 - y1);
        double toX = x2 + beta * (x1 - x2);
        if (stayOnTopSide) {
            toX = Math.min(toX, x2 + leftMostChild.getNodeWidth() / 2.0);
        }
        newLeftContour.addNode(toX - fromX, deltaY - fromY);

        // Calculate coordinates in contour space where the rightmost edge
        // leaves the parent.
        x2 = rightMostChild.getLeft() + rightMostChild.getNodeLeft()
                + rightMostChild.getNodeWidth() / 2.0;
        y2 = deltaY + rightMostChild.getNodeHeight() / 2.0;
        beta = nodeHeight / 2.0 / (y2 - y1);
        xIntersect = beta * (x2 - x1);
        if (stayOnBottomSide) {
            xIntersect = Math.min(xIntersect, nodeWidth / 2.0);
        }
        if (xIntersect > nodeWidth / 2.0) {
            // Edge leaves parent at the right side.
            beta = nodeWidth / 2.0 / (x2 - x1);
            double yIntersect = beta * (y2 - y1);
            fromX = nodeLeft + nodeWidth;
            fromY = nodeHeight / 2.0 + yIntersect;

            newRightContour.addNode(0.0, fromY);
        } else {
            // Edge leaves parent at the bottom side.
            fromX = nodeLeft + nodeWidth / 2.0 + xIntersect;
            fromY = nodeHeight;
            newRightContour.addNode(0.0, nodeHeight);
            newRightContour.addNode(fromX - nodeLeft - nodeWidth, 0.0);
        }
        beta = rightMostChild.getNodeHeight() / 2.0 / (y2 - y1);
        toX = x2 + beta * (x1 - x2);
        if (stayOnTopSide) {
            toX = Math.max(toX, x2 - leftMostChild.getNodeWidth() / 2.0);
        }
        newRightContour.addNode(toX - fromX, deltaY - fromY);

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
        double x1 = tree.getNodeLeft() + tree.getNodeWidth() / 2.0;
        double y1 = tree.getNodeHeight() / 2.0;
        PortsAttribute parentPortsAttribute = (PortsAttribute) tree.getNode()
                .getAttribute("graphics.ports");
        LinkedList<Port> parentPorts = new LinkedList<Port>();
        for (Tree child : children) {
            double x2 = child.getLeft() + child.getNodeLeft()
                    + child.getNodeWidth() / 2.0;
            double y2 = tree.getNodeHeight() + tree.getVerticalNodeDistance()
                    + child.getNodeHeight() / 2.0;
            Edge edge = child.getEdge();
            DockingAttribute docking = (DockingAttribute) edge
                    .getAttribute("graphics.docking");
            PortsAttribute portsAttribute = (PortsAttribute) child.getNode()
                    .getAttribute("graphics.ports");

            double portX1 = 0.0;
            double edgeFromX = x1;
            double edgeFromY = y1;

            if (stayOnBottomSide) {
                double beta = tree.getNodeHeight() / 2.0 / (y2 - y1);
                double xIntersect = beta * (x2 - x1) / tree.getNodeWidth() * 2;
                if (xIntersect < -1) {
                    portX1 = -1.0;
                    edgeFromX = tree.getNodeLeft();

                } else if (xIntersect > 1) {
                    portX1 = 1.0;
                    edgeFromX = tree.getNodeLeft() + tree.getNodeWidth();
                }
                edgeFromY = tree.getNodeHeight();
            }

            double portX2 = 0.0;
            double edgeToX = x2;
            double edgeToY = y2;
            if (stayOnTopSide) {
                double beta = child.getNodeHeight() / 2.0 / (y2 - y1);
                double xIntersect = beta * (x1 - x2) / child.getNodeWidth() * 2;
                if (xIntersect < -1) {
                    portX2 = -1.0;
                    edgeToX = child.getLeft() + child.getNodeLeft();
                } else if (xIntersect > 1) {
                    portX2 = 1.0;
                    edgeToX = child.getLeft() + child.getNodeLeft()
                            + child.getNodeWidth();
                }
                edgeToY = tree.getNodeHeight() + tree.getVerticalNodeDistance();
            }

            if (portX1 == 0.0) {
                docking.setSource("");
            } else {
                double portY1 = 1.0;
                if (tree.hasRoundShape()) {
                    double alpha = (edgeToY - edgeFromY) * child.getNodeWidth()
                            / (edgeToX - edgeFromX) / child.getNodeHeight();
                    if (portX1 == -1.0) {
                        portX1 = -(alpha * alpha + alpha + Math.sqrt(-2.0
                                * alpha))
                                / (1 + alpha * alpha);
                        portY1 = 1 + alpha * (portX1 + 1);
                    } else if (portX1 == 1.0) {
                        portX1 = alpha * alpha - alpha + Math.sqrt(2.0 * alpha)
                                / (1 + alpha * alpha);
                        portY1 = 1 - alpha * (1 - portX1);
                    } else {
                        double r = Math.sqrt(portX1 * portX1 + portY1 * portY1);
                        portX1 /= r;
                        portY1 /= r;
                    }
                }
                String portName = String.valueOf(child.getChildIndex());
                parentPorts.add(algorithm.createPort(portName, portX1, portY1));
                docking.setSource(portName);
            }

            if (portX2 == 0.0) {
                portsAttribute.setIngoingPorts(new LinkedList<Port>());
                docking.setTarget("");
            } else {
                LinkedList<Port> ports = new LinkedList<Port>();
                double portY2 = -1.0;
                if (child.hasRoundShape()) {
                    double alpha = (edgeToY - edgeFromY) * child.getNodeWidth()
                            / (edgeToX - edgeFromX) / child.getNodeHeight();
                    if (portX2 == -1.0) {
                        portX2 = -(alpha * alpha - alpha + Math
                                .sqrt(2.0 * alpha))
                                / (1 + alpha * alpha);
                        portY2 = -1 + alpha * (1 + portX2);
                    } else if (portX2 == 1.0) {
                        portX2 = alpha * alpha + alpha
                                + Math.sqrt(-2.0 * alpha) / (1 + alpha * alpha);
                        portY2 = -1 - alpha * (1 - portX2);
                    } else {
                        double r = Math.sqrt(portX2 * portX2 + portY2 * portY2);
                        portX2 /= r;
                        portY2 /= r;
                    }
                }
                ports.add(algorithm.createPort("t", portX2, portY2));
                portsAttribute.setIngoingPorts(ports);
                docking.setTarget("t");
            }
            EdgeGraphicAttribute edgeAttribute = (EdgeGraphicAttribute) edge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");
            edgeAttribute.setBends(bends);
            edgeAttribute.setShape(StraightLineEdgeShape.class.getName());
        }
        parentPortsAttribute.setOutgoingPorts(parentPorts);
    }

    /**
     * Creates a new <code>CenterToCenterEdgeLayoutStrategy</code>.
     * 
     * @param stayOnTopSide
     *            See {@link #stayOnTopSide}
     * @param stayOnBottomSide
     *            See {@link #stayOnBottomSide}
     */
    public CenterToCenterEdgeLayoutStrategy(boolean stayOnTopSide,
            boolean stayOnBottomSide) {
        this.stayOnTopSide = stayOnTopSide;
        this.stayOnBottomSide = stayOnBottomSide;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
