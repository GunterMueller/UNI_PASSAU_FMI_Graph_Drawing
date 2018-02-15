// =============================================================================
//
//   TestPlanarDrawing.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Tests whether the current drawing of the graph is planar. This test is used
 * if the option "drawing planar angle graph with the same faces" is chosen,
 * which works only if the current drawing is planar.
 * 
 * @author Mirka Kossak
 */
public class TestPlanarDrawing {
    /** Mapping between a node and its adjacency list. */
    protected Map<Node, ArrayList<Node>> node_adjacencylist = new HashMap<Node, ArrayList<Node>>();

    private TestedGraph testedGraph;

    private Graph graph;

    private NodeWithCoordinates[] nodesWithCo;

    private HashMap<Node, NodeWithCoordinates> nodeCoordinatesMap;

    /**
     * Constructor of the planar drawing test.
     * 
     * @param testedGraph
     * @param graph
     */
    public TestPlanarDrawing(TestedGraph testedGraph, Graph graph) {
        this.testedGraph = testedGraph;
        this.graph = graph;
        initMapping();
        this.nodeCoordinatesMap = new HashMap<Node, NodeWithCoordinates>();
        this.nodesWithCo = new NodeWithCoordinates[graph.getNumberOfNodes()];
        initNodesWithCo();
    }

    /**
     * Inits the list where all nodes with their coordinates are stored.
     */
    public void initNodesWithCo() {
        List<Node> nodes = graph.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            Node current = nodes.get(i);
            CoordinateAttribute coordinate = (CoordinateAttribute) current
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            double x = coordinate.getX();
            double y = coordinate.getY();
            NodeWithCoordinates nWC = new NodeWithCoordinates(current);
            nWC.setXCoordinate(x);
            nWC.setYCoordinate(y);
            nodesWithCo[i] = nWC;
        }
    }

    /**
     * Sorts the nodes with coordinates in ascending x order
     * 
     * @param nodesWithCo
     *            The nodes with coordinates
     * @return The sorted array of the nodes with coordinates
     */
    public NodeWithCoordinates[] sort(NodeWithCoordinates[] nodesWithCo) {
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
        return nodesWithCo;
    }

    /**
     * Initialize the mapping between nodes and their adjacency lists.
     */
    protected void initMapping() {
        for (Node node : testedGraph.getNodes()) {
            node_adjacencylist.put(node, getAdjacencyListFor(node));
        }
    }

    /**
     * Calculate the angle between two nodes.
     */
    private double calculateAngle(Node center, Node node) {
        CoordinateAttribute coordinate = (CoordinateAttribute) center
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        coordinate = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        double x2 = coordinate.getX();
        double y2 = coordinate.getY();
        if (x1 == x2 && y1 == y2)
            return 0;
        double angle = Math.atan2(y1 - y2, x2 - x1);
        if (angle < 0) {
            angle += 2.0 * Math.PI;
        }
        return angle;
    }

    /**
     * Calculate the adjacency list for a <code>node</code> depending on its
     * current plane drawing.
     * 
     * @param node
     *            Node to get adjacency list for.
     * @return Adjacency list of <code>node</code>.
     */
    protected ArrayList<Node> getAdjacencyListFor(Node node) {
        ArrayList<Node> list = new ArrayList<Node>(testedGraph
                .getAdjacencyList(node));
        ArrayList<NodeAngle> to_sort = new ArrayList<NodeAngle>(list.size());
        for (Node current : list) {
            to_sort.add(new NodeAngle(current, calculateAngle(node, current)));
        }
        Collections.sort(to_sort);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, to_sort.get(i).node);
        }
        return list;
    }

    private class NodeAngle implements Comparable<NodeAngle> {
        Node node;

        double angle;

        public NodeAngle(Node node, double angle) {
            this.node = node;
            this.angle = angle;
        }

        /*
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(NodeAngle o) {
            if (this.angle == o.angle)
                return 0;
            else if (this.angle < o.angle)
                return -1;
            else
                return 1;
        }
    }

    /**
     * Tests whether the drawn graph is planar.
     * 
     * @return true, if the drawing of the graph is planar. false, otherwise.
     */
    public boolean planarityTest() {
        boolean planar = true;
        HashMap<NodeWithCoordinates, List<Node>> sweepLine = new HashMap<NodeWithCoordinates, List<Node>>();
        double startX = nodesWithCo[0].getXCoordinate();
        int sameXValues = 1;
        for (int i = 1; i < nodesWithCo.length; i++) {
            if (startX == nodesWithCo[i].getXCoordinate()) {
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
        nextNodes = getAdjacencyListFor(nodesWithCo[0].getNode());

        sweepLine.put(nodesWithCo[0], nextNodes);

        for (int j = 1; j < nodesWithCo.length; j++) {
            planar = nextLine(j, sweepLine, planar);
        }
        return planar;
    }

    /**
     * Inits the map which gives to every node the node with its coordinates.
     */
    public void initNodeCoordinatesMap() {
        for (int i = 0; i < nodesWithCo.length; i++) {
            nodeCoordinatesMap.put(nodesWithCo[i].getNode(), nodesWithCo[i]);
        }
    }

    /**
     * 
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
     * @param planar
     *            Stores if there was an intersection so far.
     * @return true, if there was an intersection in the graph so far. false,
     *         otherwise
     */
    public boolean nextLine(int index,
            HashMap<NodeWithCoordinates, List<Node>> sweepLine, boolean planar) {
        NodeWithCoordinates currentStartNode = nodesWithCo[index];
        List<Node> nextNodes = getAdjacencyListFor(currentStartNode.getNode());
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
                                    planar = testIntersection(nodesWithCo[k],
                                            nodeCoordinatesMap.get(otherList
                                                    .get(l)), currentStartNode,
                                            currEndNodeWithCo, planar);
                                }
                            }
                        }
                    }
                }
            }
        }
        sweepLine.put(currentStartNode, nextInSweep);
        return planar;
    }

    /**
     * Tests the intersection of two edges.
     * 
     * @param start1
     *            Startnode of edge 1.
     * @param end1
     *            Endnode of edge 1.
     * @param start2
     *            Startnode of edge 2.
     * @param end2
     *            Endnode of ege 2.
     * @param planar
     *            Is the drawing of the graph still planar?
     * @return true, if there was an intersection in the graph so far. false,
     *         otherwise
     */
    public boolean testIntersection(NodeWithCoordinates start1,
            NodeWithCoordinates end1, NodeWithCoordinates start2,
            NodeWithCoordinates end2, boolean planar) {
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
        double sIntersection = (x1 * y4 + x4 * y3 + x3 * y1 - x4 * y1 - x3 * y4 - x1
                * y3)
                / (x1 * y4 + x4 * y2 + x2 * y3 + x3 * y1 - x4 * y1 - x1 * y3
                        - x3 * y2 - x2 * y4);

        if (tIntersection > 0 && tIntersection < 1 && sIntersection > 0
                && sIntersection < 1) {
            planar = false;
            setEdgeColor(start1.getNode(), end1.getNode(), Color.RED);
            setEdgeColor(start2.getNode(), end2.getNode(), Color.RED);
        }
        return planar;
    }

    /**
     * Sorts the nodes with the same x-values in descending y-values order.
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
    }

    /**
     * Sets the edge color to c if an edge intersects with another.
     * 
     * @param node1
     *            One end of the edge.
     * @param node2
     *            The other end of the edge.
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

    /**
     * Returns the embedding according to the current drawing of the graph.
     * 
     * @return The embedding according to the current drawing of the graph.
     */
    public Map<Node, ArrayList<Node>> getNodeAdjacencylist() {
        return this.node_adjacencylist;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
