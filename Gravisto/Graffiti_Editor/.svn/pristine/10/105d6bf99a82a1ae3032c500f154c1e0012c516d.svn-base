// =============================================================================
//
//   SweepLine.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Used for testing the planarity of the drawn graph.
 * 
 * @author Mirka Kossak
 */
public class SweepLine {
    // The nodes with coordinates
    private NodeWithCoordinates[] nodesWithCo;

    private TestedGraph testedGraph;

    // Stores for every <code>org.graffiti.graph.Node</code> the
    // <code>NodeWithCoordinates</code> with its x- and y-values
    private HashMap<Node, NodeWithCoordinates> nodeCoordinatesMap;

    private CalculateFaces calcFaces;

    /**
     * Constructs a new <code>SweepLine</code>
     * 
     * @param nodesWithCo
     *            The array of all nodes with its coordinates of the
     *            <code>org.graffiti.graph.Graph</code>.
     * @param testedGraph
     *            The embedding of the <code>org.graffiti.graph.Graph</code>.
     * @param calcFaces
     *            Needed, because of the two different kinds of getting the
     *            faces.
     */
    public SweepLine(NodeWithCoordinates[] nodesWithCo,
            TestedGraph testedGraph, CalculateFaces calcFaces) {
        this.nodesWithCo = nodesWithCo;
        this.testedGraph = testedGraph;
        this.nodeCoordinatesMap = new HashMap<Node, NodeWithCoordinates>();
        this.calcFaces = calcFaces;
    }

    /**
     * Tests whether the drawn graph is planar. In the beginning the
     * <code>NodeWithCoordinates</code>'s will be sorted, so that the
     * <code>NodeWithCoordinates</code> with the smallest x and the largest
     * y-value will be the first and the <code>NodeWithCoordinates</code> with
     * the largest x and the smallest y-value will be the last one.
     * 
     */
    public void planarityTest() {
        this.sortXValues();
        HashMap<NodeWithCoordinates, List<Node>> sweepLine = new HashMap<NodeWithCoordinates, List<Node>>();
        double startX = nodesWithCo[0].getXCoordinate();
        int sameXValues = 1;
        for (int i = 1; i < nodesWithCo.length; i++) {
            if (Math.round(startX) == Math.round(nodesWithCo[i]
                    .getXCoordinate())) {
                sameXValues++;
                if (i == nodesWithCo.length - 1 && sameXValues > 1) {
                    sortYValues(sameXValues, i + 1);
                }
            } else {
                if (sameXValues > 1) {
                    sortYValues(sameXValues, i);
                }
                startX = nodesWithCo[i].getXCoordinate();
                sameXValues = 1;
            }
        }
        initNodeCoordinatesMap();
        // init sweepline
        List<Node> nextNodes;
        if (calcFaces instanceof CalculateFacesFromDrawing) {
            nextNodes = calcFaces.getAdjacencyListFor(nodesWithCo[0].getNode());
        } else {
            nextNodes = testedGraph.getAdjacencyList(nodesWithCo[0].getNode());
        }
        sweepLine.put(nodesWithCo[0], nextNodes);

        for (int j = 1; j < nodesWithCo.length; j++) {
            nextLine(j, sweepLine);
        }
    }

    /**
     * Sorts the nodes with coordinates in ascending x order.
     */
    public void sortXValues() {
        NodeWithCoordinates temp;
        for (int i = 0; i < nodesWithCo.length - 1; i++) {
            for (int j = nodesWithCo.length - 1; j > i; j--) {
                if (nodesWithCo[j - 1].getXCoordinate() > nodesWithCo[j]
                        .getXCoordinate()) {
                    temp = nodesWithCo[j - 1];
                    nodesWithCo[j - 1] = nodesWithCo[j];
                    nodesWithCo[j] = temp;
                }
            }
        }
    }

    /**
     * Inits the map which stores to every <code>org.graffiti.graph.Node</code>
     * the <code>NodeWithCoordinates</code> node with its x- and y-values.
     */
    public void initNodeCoordinatesMap() {
        for (int i = 0; i < nodesWithCo.length; i++) {
            nodeCoordinatesMap.put(nodesWithCo[i].getNode(), nodesWithCo[i]);
        }
    }

    /**
     * Gets the new <code>org.graffiti.graph.Edge</code>'s that are starting
     * from the next <code>org.graffiti.graph.Node</code> and tests the
     * intersection which all <code>org.graffiti.graph.Edge</code>'s that are
     * "alive".
     * 
     * @param index
     *            The <code>index</code> of the node in the
     *            <code>nodesWithCo</code> array.
     * @param sweepLine
     *            The HashMap which includes all
     *            <code>org.graffiti.graph.Edge</code>'s that are "alive".
     */
    public void nextLine(int index,
            HashMap<NodeWithCoordinates, List<Node>> sweepLine) {
        NodeWithCoordinates currentStartNode = nodesWithCo[index];
        // get all edges that are endnodes of the current node
        List<Node> nextNodes;
        if (calcFaces instanceof CalculateFacesFromDrawing) {
            nextNodes = calcFaces.getAdjacencyListFor(currentStartNode
                    .getNode());
        } else {
            nextNodes = testedGraph
                    .getAdjacencyList(currentStartNode.getNode());
        }
        List<Node> nextInSweep = new ArrayList<Node>();
        for (int endNode = 0; endNode < nextNodes.size(); endNode++) {
            Node currentEndNode = nextNodes.get(endNode);
            NodeWithCoordinates currEndNodeWithCo = nodeCoordinatesMap
                    .get(currentEndNode);
            if (sweepLine.containsKey(currEndNodeWithCo)) {
                List<Node> list = sweepLine.get(currEndNodeWithCo);
                for (int j = 0; j < list.size(); j++) {
                    if (currentStartNode.getNode() == list.get(j)) {
                        list.remove(j);
                    }
                }
            } else {
                nextInSweep.add(currentEndNode);
                for (int k = 0; k < index; k++) {
                    List<Node> otherList = sweepLine.get(nodesWithCo[k]);
                    if (!otherList.isEmpty()) {
                        for (int l = 0; l < otherList.size(); l++) {
                            if (otherList.get(l) == currentStartNode.getNode()) {
                                otherList.remove(l);
                            } else {
                                if (otherList.get(l) != currentEndNode) {
                                    testIntersection(nodesWithCo[k],
                                            nodeCoordinatesMap.get(otherList
                                                    .get(l)), currentStartNode,
                                            currEndNodeWithCo);
                                }
                            }
                        }
                    }
                }
            }
        }
        sweepLine.put(currentStartNode, nextInSweep);
    }

    /**
     * Tests the intersection of two edges.
     * 
     * @param start1
     *            StartNode of edge 1.
     * @param end1
     *            EndNode of edge 1.
     * @param start2
     *            StartNode of edge 2.
     * @param end2
     *            EndNode of edge 2.
     */
    public void testIntersection(NodeWithCoordinates start1,
            NodeWithCoordinates end1, NodeWithCoordinates start2,
            NodeWithCoordinates end2) {
        double x1 = start1.getXCoordinate();
        double y1 = start1.getYCoordinate();
        double x2 = end1.getXCoordinate();
        double y2 = end1.getYCoordinate();
        double x3 = start2.getXCoordinate();
        double y3 = start2.getYCoordinate();
        double x4 = end2.getXCoordinate();
        double y4 = end2.getYCoordinate();

        double tIntersection = (x1 * y2 + x2 * y3 + x3 * y1 - x2 * y1 - x3 * y2 - x1
                * y3)
                / (x1 * y4 + x4 * y2 + x2 * y3 + x3 * y1 - x4 * y1 - x1 * y3
                        - x3 * y2 - x2 * y4);
        if (tIntersection > 0 && tIntersection < 1) {
            double sIntersection = (x1 * y4 + x4 * y3 + x3 * y1 - x4 * y1 - x3
                    * y4 - x1 * y3)
                    / (x1 * y4 + x4 * y2 + x2 * y3 + x3 * y1 - x4 * y1 - x1
                            * y3 - x3 * y2 - x2 * y4);

            if (sIntersection > 0.000000001 && sIntersection < 0.999999999) {
                setEdgeColor(start1.getNode(), end1.getNode(), Color.RED);
                setEdgeColor(start2.getNode(), end2.getNode(), Color.RED);

            }
        }
    }

    /**
     * Sorts the <code>NodeWithCoordinates</code>'s with the same x-values in
     * descending y-values order. If there are <code>NodeWithCoordinates</code>
     * 's with the same x and the same y-value, these
     * <code>org.graffiti.graph.Node</code>'s will be coloured red.
     * 
     * @param sameXValues
     *            That much values has to be sorted.
     * @param end
     *            End of the same x-values in the array.
     */
    public void sortYValues(int sameXValues, int end) {
        NodeWithCoordinates temp;
        for (int i = end - sameXValues; i < end; i++) {
            for (int j = end - 1; j > i; j--) {
                if (nodesWithCo[j - 1].getYCoordinate() > nodesWithCo[j]
                        .getYCoordinate()) {
                    temp = nodesWithCo[j - 1];
                    nodesWithCo[j - 1] = nodesWithCo[j];
                    nodesWithCo[j] = temp;
                }
            }
        }
        int startSameCoordinates = end - sameXValues;
        for (int k = (end - sameXValues) + 1; k < end; k++) {
            if (Math.round(nodesWithCo[k].getYCoordinate()) == Math
                    .round(nodesWithCo[startSameCoordinates].getYCoordinate())) {
                nodesWithCo[k].setYCoordinate(Math.round(nodesWithCo[k]
                        .getYCoordinate()));
                nodesWithCo[k].setXCoordinate(Math.round(nodesWithCo[k]
                        .getXCoordinate()));
                nodesWithCo[startSameCoordinates].setYCoordinate(Math
                        .round(nodesWithCo[startSameCoordinates]
                                .getYCoordinate()));
                nodesWithCo[startSameCoordinates].setXCoordinate(Math
                        .round(nodesWithCo[startSameCoordinates]
                                .getXCoordinate()));
                setNodeColor(nodesWithCo[k].getNode(), Color.RED);
                setNodeColor(nodesWithCo[k - 1].getNode(), Color.RED);
            } else {
                startSameCoordinates = k;
            }
        }
    }

    /**
     * Sets the color of the <code>org.graffiti.graph.Node</code>.
     * 
     * @param node
     *            The <code>org.graffiti.graph.Node</code> to color.
     * @param c
     *            The new color.
     */
    public static void setNodeColor(Node node, Color c) {
        ColorAttribute ca = (ColorAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        ca.setColor(c);
    }

    /**
     * Sets the color of the <code>org.graffiti.graph.Edge</code>.
     * 
     * @param node1
     *            Source of the <code>org.graffiti.graph.Edge</code>.
     * @param node2
     *            Target of the <code>org.graffiti.graph.Edge</code>.
     * @param c
     *            The color.
     */
    public static void setEdgeColor(Node node1, Node node2, Color c) {
        for (Iterator<Edge> i = node1.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();
            if ((e.getTarget() == node2) || (e.getSource() == node2)) {
                ColorAttribute ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);

                ca.setColor(c);
                ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FRAMECOLOR);

                ca.setColor(c);
                return;
            }
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
