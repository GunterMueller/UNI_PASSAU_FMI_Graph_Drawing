// =============================================================================
//
//   Drawing.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Draws the graph with the calculated coordinates.
 * 
 * @author Mirka Kossak
 * 
 */
public class Drawing {
    // Logger used to print information
    public static final Logger logger = Logger
            .getLogger(PlanarAngleGraphAlgorithm.class.getPackage().getName());

    double[] edgeLPSolution;

    private Graph graph;

    private TestedGraph testedGraph;

    private Collection<Edge> allEdges;

    private ArrayList<ArrayList<Edge>> edgeLists;

    private ArrayList<ArrayList<Edge>> selfloopLists;

    private NodeWithCoordinates[] nodesWithCo;

    /**
     * 
     * @param edgeLPSolution
     * @param graph
     *            Graph to calculate the edge lengths for.
     * @param testedGraph
     *            Stores the embedding of the graph.
     * @param allEdges
     *            All edges of the graph.
     */
    public Drawing(double[] edgeLPSolution, Graph graph,
            TestedGraph testedGraph, Collection<Edge> allEdges) {
        this.edgeLPSolution = edgeLPSolution;
        this.graph = graph;
        this.testedGraph = testedGraph;
        this.allEdges = allEdges;
        // collects the edges in lists. self loops are in an own list.
        this.edgeLists = new ArrayList<ArrayList<Edge>>();
        this.selfloopLists = new ArrayList<ArrayList<Edge>>();
        this.nodesWithCo = new NodeWithCoordinates[graph.getNumberOfNodes()];

    }

    /**
     * Returns the smallest x coordinate.
     * 
     * @param solution
     *            The solution of the edge LP
     * @return The smallest x coordinate
     */
    public double getSmallestX(double[] solution) {
        int index = solution.length
                - (2 * graph.getNumberOfEdges() + 1 + 2
                        * graph.getNumberOfNodes() + 2 * graph
                        .getNumberOfEdges());
        double smallest = solution[index];
        for (int i = (index + 2); i < (solution.length - (2 * graph
                .getNumberOfEdges() + 1 + 2 * graph.getNumberOfEdges())); i += 2) {
            if (solution[i] < smallest) {
                smallest = solution[i];
                index = i;
            }
        }
        return smallest;
    }

    /**
     * Returns the smallest y coordinate.
     * 
     * @param solution
     *            The solution of the edge LP
     * @return the smallest y coordinate
     */
    public double getSmallestY(double[] solution) {
        int index = solution.length
                - (2 * graph.getNumberOfEdges() + 1 + 2
                        * graph.getNumberOfNodes() + 2 * graph
                        .getNumberOfEdges()) + 1;
        double smallest = solution[index];
        for (int i = (index + 2); i < (solution.length - (2 * graph
                .getNumberOfEdges() + 1 + 2 * graph.getNumberOfEdges())); i += 2) {
            if (solution[i] < smallest) {
                smallest = solution[i];
                index = i;
            }
        }
        return smallest;
    }

    /**
     * Draws the graph with the calculated coordinates.
     */
    public void drawSolution() {
        HashMap<String, Node> nodeLabelMap = nodeLabelMap();

        double smallestX = getSmallestX(edgeLPSolution) - 100;
        double smallestY = getSmallestY(edgeLPSolution) - 100;
        logger.info("");
        logger.info("Calculated coordinates:");
        int solutionIndex = edgeLPSolution.length
                - (2 * graph.getNumberOfEdges() + 1 + 2
                        * graph.getNumberOfNodes() + 2 * graph
                        .getNumberOfEdges());
        for (Edge mulEdge : allEdges) {
            if (!graph.containsEdge(mulEdge)) {
                graph.addEdgeCopy(mulEdge, mulEdge.getSource(), mulEdge
                        .getTarget());
            }
        }
        getMultiEdges(graph.getEdges());
        for (int i = 0; i < graph.getNumberOfNodes(); i++) {
            Node node = nodeLabelMap.get("Node " + i);
            logger.info(testedGraph.toString(node));
            NodeWithCoordinates nodeWithCoordinates = new NodeWithCoordinates(
                    node);
            try {
                node.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException e) {
                node.addAttribute(new NodeGraphicAttribute(), "");
            }

            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            double x = (edgeLPSolution[solutionIndex++] - smallestX);
            nodeWithCoordinates.setXCoordinate(x);
            logger.info("x: " + x);
            double y = (edgeLPSolution[solutionIndex++] - smallestY);
            nodeWithCoordinates.setYCoordinate(y);
            logger.info("y: " + y);
            ca.setCoordinate(new Point2D.Double(x, y));
            nodesWithCo[i] = nodeWithCoordinates;
        }
        bendMultiedges();
    }

    /**
     * Returns the map which gives to every node its string representation.
     * 
     * @return The map which gives to every node its string representation.
     */
    public HashMap<String, Node> nodeLabelMap() {
        HashMap<String, Node> nodeLabel = new HashMap<String, Node>();
        List<Node> nodes = graph.getNodes();
        for (int i = 0; i < nodes.size(); i++) {
            Node current = nodes.get(i);
            String label = testedGraph.toString(current);
            nodeLabel.put(label, current);
        }
        return nodeLabel;
    }

    /**
     * Returns the lists with all nodes and its coordinates.
     * 
     * @return The lists with all nodes and its coordinates.
     */
    public NodeWithCoordinates[] getNodeWithCoordinates() {
        return this.nodesWithCo;
    }

    /**
     * Puts all multiedges to the <code>edgeLists</code> and all selfloops to
     * the <code>selfloopsLists</code>.
     * 
     * @see org.graffiti.plugins.algorithms.generators.AbstractGenerator
     * 
     * @param allEdges
     *            All edges of the origingal graph.
     */
    protected void getMultiEdges(Collection<Edge> allEdges) {
        // multi edges (=same source and same target) are each collected in one
        // list
        for (Edge edge : allEdges) {
            boolean found = false;

            for (int i = 0; i < edgeLists.size(); i++) {
                ArrayList<Edge> edges = edgeLists.get(i);
                Edge edge2 = edges.get(0);

                // add same multi edges to one list
                if ((edge.getSource() == edge2.getSource())
                        && (edge.getTarget() == edge2.getTarget())) {
                    edges.add(edge);
                    found = true;
                }
            }

            for (int i = 0; i < selfloopLists.size(); i++) {
                ArrayList<Edge> edges = selfloopLists.get(i);
                Edge edge2 = edges.get(0);

                // add same multi edges (self loops here) to one list
                if ((edge.getSource() == edge2.getSource())
                        && (edge.getSource() == edge.getTarget())) {
                    edges.add(edge);
                    found = true;
                }
            }

            // if an edge like the searched one was not found yet..
            if (!found) {
                // if it is a self loop...
                if (edge.getSource() == edge.getTarget()) {
                    // create a new list for these self loops
                    ArrayList<Edge> loops = new ArrayList<Edge>();
                    loops.add(edge);
                    selfloopLists.add(loops);
                }

                // if it is a normal edge
                else {
                    // create a new list for these edges
                    ArrayList<Edge> edges = new ArrayList<Edge>();
                    edges.add(edge);
                    edgeLists.add(edges);
                }
            }
        }
    }

    /**
     * Bends all multiedges and selfloops of the graph.
     * 
     * @see org.graffiti.plugins.algorithms.generators.AbstractGenerator
     */
    public void bendMultiedges() {
        // bend normal edges
        for (int i = 0; i < edgeLists.size(); i++) {
            ArrayList<Edge> edges = edgeLists.get(i);

            boolean hasReversal = false;

            if (edges.size() > 0) {
                hasReversal = hasReversal(edges.get(0));
            }

            for (int j = 0; j < edges.size(); j++) {
                Edge edge = edges.get(j);
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        GraphicAttributeConstants.BENDS);

                if (hasReversal) {
                    bends.add(new CoordinateAttribute("bend0",
                            computeBendPosition(edge, (j + 1) * 30.0)));
                } else {
                    bends.add(new CoordinateAttribute("bend0",
                            computeBendPosition(edge, j * 30.0)));
                }

                ega.setShape("org.graffiti.plugins.views.defaults."
                        + "SmoothLineEdgeShape");

                if (edges.size() > 1) {
                    ega.setBends(bends);
                }
            }
        }

        // bend self loops
        for (int i = 0; i < selfloopLists.size(); i++) {
            ArrayList<Edge> edges = selfloopLists.get(i);
            int k = 0;

            for (int j = 0; j < edges.size(); j++) {
                Edge loopingEdge = edges.get(j);
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) loopingEdge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        GraphicAttributeConstants.BENDS);
                CoordinateAttribute ca = (CoordinateAttribute) loopingEdge
                        .getSource().getAttribute(
                                GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);

                double nodeXPos = ca.getX();
                double nodeYPos = ca.getY();

                /*
                 * if there are less than five self loop edges, they are ordered
                 * like this: (star is the node) | - * - |
                 */
                if (edges.size() <= 4) {
                    double bendPointDistance = 20.0;
                    double distFromNode = 60.0;
                    double x1 = nodeXPos - bendPointDistance;
                    double x2 = nodeXPos + bendPointDistance;
                    double y1 = nodeYPos - bendPointDistance;
                    double y2 = nodeYPos + bendPointDistance;

                    if (j == 0) {
                        y1 = nodeYPos + distFromNode;
                        y2 = nodeYPos + distFromNode;
                    } else if (j == 1) {
                        y1 = nodeYPos - distFromNode;
                        y2 = nodeYPos - distFromNode;
                    } else if (j == 2) {
                        x1 = nodeXPos + distFromNode;
                        x2 = nodeXPos + distFromNode;
                    } else if (j == 3) {
                        x1 = nodeXPos - distFromNode;
                        x2 = nodeXPos - distFromNode;
                    }

                    bends.add(new CoordinateAttribute("bend0",
                            new Point2D.Double(x1, y1)));
                    bends.add(new CoordinateAttribute("bend1",
                            new Point2D.Double(x2, y2)));
                }

                /*
                 * if there are more than four self loops, they are positioned
                 * on an imaginary circle around the node
                 */
                else {
                    double x = (Math.sin((0.5 * k) / (2.0 * edges.size())
                            * Math.PI * 8.0) * 80.0)
                            + nodeXPos;
                    double y = (Math.cos((0.5 * k) / (2.0 * edges.size())
                            * Math.PI * 8.0) * 80.0)
                            + nodeYPos;

                    bends.add(new CoordinateAttribute("bend0",
                            new Point2D.Double(x, y)));
                    k++;
                    x = (Math.sin((0.5 * k) / (2.0 * edges.size()) * Math.PI
                            * 8.0) * 80.0)
                            + nodeXPos;
                    y = (Math.cos((0.5 * k) / (2.0 * edges.size()) * Math.PI
                            * 8.0) * 80.0)
                            + nodeYPos;
                    bends.add(new CoordinateAttribute("bend1",
                            new Point2D.Double(x, y)));
                }

                ega.setShape("org.graffiti.plugins.views.defaults."
                        + "SmoothLineEdgeShape");
                ega.setBends(bends);
            }
        }
    }

    /**
     * Computes the bend point position in relation to the specified edge. The
     * position is a point on the "Mittelsenkrechte" between the edge's source
     * node and the edge's target node. *
     * 
     * @see org.graffiti.plugins.algorithms.generators.AbstractGenerator
     * 
     * @param edge
     *            This edges bend point position is computed.
     * @param distanceFactor
     *            The point has to be in this distance from the edge.
     * 
     * @return The bend point position in relation to the specified edge.
     */
    private Point2D.Double computeBendPosition(Edge edge, double distanceFactor) {
        /*
         * let the edge's source be node a and the edge's target node b. first
         * get the nodes' positions.
         */
        CoordinateAttribute ca = (CoordinateAttribute) edge.getSource()
                .getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
        double u = ca.getX();
        double v = ca.getY();
        ca = (CoordinateAttribute) edge.getTarget().getAttribute(
                GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);

        double x = ca.getX();
        double y = ca.getY();

        // compute the coordinates of point in the middle between node a and
        // node b
        double middlePointXPos = (u + x) / 2.0;
        double middlePointYPos = (v + y) / 2.0;

        // compute the x and y coordinate of the vertical (to the line between a
        // and b) vector
        double verticalVectorX = y - v;
        double verticalVectorY = u - x;

        // norm this vector dependent on the specified distance factor
        double normedXPos = (distanceFactor * verticalVectorX)
                / Math.sqrt((Math.pow(verticalVectorX, 2) + Math.pow(
                        verticalVectorY, 2)));
        double normedYPos = (distanceFactor * verticalVectorY)
                / Math.sqrt((Math.pow(verticalVectorX, 2) + Math.pow(
                        verticalVectorY, 2)));

        /*
         * the (x,y)-position of the point that lies on the vertical vector with
         * the distance distanceFactor from the middle point.
         */
        double xPos = middlePointXPos + normedXPos;
        double yPos = middlePointYPos + normedYPos;

        return new Point2D.Double(xPos, yPos);
    }

    /**
     * Checks whether this edge has an reversal edge or not.
     * 
     * @see org.graffiti.plugins.algorithms.generators.AbstractGenerator
     * @param edge
     *            The edge to check.
     * 
     * @return <code>true</code> if the edge has a reversal edge,
     *         <code>false</code> otherwise.
     */
    private boolean hasReversal(Edge edge) {
        Iterator<Edge> it;

        if (edge.isDirected()) {
            it = edge.getTarget().getDirectedOutEdgesIterator();
        } else {
            it = edge.getTarget().getUndirectedEdgesIterator();
        }

        while (it.hasNext()) {
            Edge outEdge = it.next();

            if (outEdge.getTarget() == edge.getSource())
                return true;
        }

        return false;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
