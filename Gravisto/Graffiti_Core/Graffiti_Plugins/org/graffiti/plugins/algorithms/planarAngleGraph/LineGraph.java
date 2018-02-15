// =============================================================================
//
//   LineGraph.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * A <code>LineGraph</code> object represents the graph in a coordinate system.
 * The <code>org.graffiti.graph.Edge</code> objects are <code>Line</code>
 * objects, where one <code>Line</code> object is set as the X-axis.
 * 
 * @author Mirka Kossak
 */
public class LineGraph {
    private TestedGraph testedGraph;

    private Graph graph;

    private HashMap<Node, ArrayList<Angle>> nodeWithAngles;

    private HashMap<Edge, Line> edgeLine;

    private HashMap<Node, BFSNode> nodeBFSNode;

    private Queue bfsQueue;

    private EdgeMatrix edgeMatrix;

    private CalculateFaces calcFaces;

    /**
     * Constructs a new <code>LineGraph</code>.
     * 
     * @param testedGraph
     * @param graph
     * @param nodeWithAngles
     * @param calcFaces
     */
    public LineGraph(TestedGraph testedGraph, Graph graph,
            HashMap<Node, ArrayList<Angle>> nodeWithAngles,
            CalculateFaces calcFaces) {
        this.testedGraph = testedGraph;
        this.graph = graph;
        this.nodeWithAngles = nodeWithAngles;
        this.edgeLine = new HashMap<Edge, Line>();
        this.nodeBFSNode = new HashMap<Node, BFSNode>();
        this.bfsQueue = new Queue();
        this.edgeMatrix = new EdgeMatrix(graph);
        edgeMatrix.makeMatrix();
        this.calcFaces = calcFaces;
    }

    /**
     * Inits the map which gives to every edge its corresponding line.
     * 
     */
    public void initEdgeLineMap() {
        Collection<Edge> edges = graph.getEdges();
        for (Iterator<Edge> edgesIt = edges.iterator(); edgesIt.hasNext();) {
            Edge currentEdge = edgesIt.next();
            Line currentLine = new Line(currentEdge);
            edgeLine.put(currentEdge, currentLine);
        }
    }

    /**
     * Starts building the <code>LineGraph</code> with the help of the breadth
     * first search.
     * 
     */
    public void makeLineGraph() {
        initEdgeLineMap();
        initBFSNodesMap();
        Node startNode = testedGraph.getNodes().get(0);
        BFSNode startBFS = nodeBFSNode.get(startNode);
        startBFS.setPredecessor(null);
        startBFS.setVisited(true);
        getClockwiseNextAngles(startNode);
    }

    /**
     * Sets the absolute angle (= the angle in the coordinate system)
     * 
     * @param line
     * @param value
     * @param current
     * @param nextConnected
     */
    public void setAbsoluteAngle(Line line, double value, Node current,
            Node nextConnected) {
        if (!line.isVisited()) {
            line.setAngleToXAxis(value);
            line.setVisited(true);

            edgeMatrix.setXCoordinate(nodeBFSNode.get(current).getIndex(),
                    nodeBFSNode.get(nextConnected).getIndex(), value);
            edgeMatrix.setYCoordinate(nodeBFSNode.get(current).getIndex(),
                    nodeBFSNode.get(nextConnected).getIndex(), value);
        }
    }

    /**
     * Makes the <code>LineGraph</code> for the nodes which are adjacent to the
     * startnode of the breadth first search.
     * 
     * @param current
     */
    public void getClockwiseNextAngles(Node current) {
        ArrayList<Angle> allAngles = nodeWithAngles.get(current);
        List<Node> currentList;
        if (calcFaces instanceof CalculateFacesFromDrawing) {
            currentList = calcFaces.getAdjacencyListFor(current);
        } else {
            currentList = testedGraph.getAdjacencyList(current);
        }
        Edge first = getEdge(current, currentList.get(0));
        bfsQueue.enqueue(currentList.get(0));
        nodeBFSNode.get(currentList.get(0)).setPredecessor(current);
        nodeBFSNode.get(currentList.get(0)).setVisited(true);
        int absoluteAngleValue = 0;
        setAbsoluteAngle(edgeLine.get(first), absoluteAngleValue, current,
                currentList.get(0));
        edgeLine.get(first).setVisited(true);
        for (int i = 1; i < currentList.size(); i++) {
            Node nextConnected = currentList.get(i);
            bfsQueue.enqueue(nextConnected);
            nodeBFSNode.get(nextConnected).setVisited(true);
            Edge second = getEdge(current, nextConnected);
            Angle nextAngle = findAngle(first, second, allAngles);
            nodeBFSNode.get(nextConnected).setPredecessor(current);
            absoluteAngleValue += nextAngle.getValue();
            absoluteAngleValue %= 360;
            setAbsoluteAngle(edgeLine.get(second), absoluteAngleValue, current,
                    nextConnected);
            nodeBFSNode.get(nextConnected).setWay(edgeLine.get(second));
            first = second;
        }

        while (!bfsQueue.isEmpty()) {
            takeNext();
        }

        int numberOfCoordinates = edgeMatrix.getNumberOfCoordinates();
        for (int slack = 0; slack < edgeMatrix.getNumberOfEdges(); slack++) {
            edgeMatrix.setSlackAndMinValues(numberOfCoordinates + slack,
                    numberOfCoordinates + edgeMatrix.getNumberOfEdges(),
                    numberOfCoordinates + edgeMatrix.getNumberOfEdges() + 1
                            + slack);
        }
        for (int edgeMax = numberOfCoordinates; edgeMax < numberOfCoordinates
                + edgeMatrix.getNumberOfEdges(); edgeMax++) {
            edgeMatrix.setMaxSumEdges(edgeMax);
        }
        edgeMatrix.incrementCurrentRow();
        edgeMatrix.setMinEdge(numberOfCoordinates
                + edgeMatrix.getNumberOfEdges());
    }

    /**
     * Makes the <code>LineGraph</code> for the next node of the queue and
     * continues the breadth first search.
     * 
     */
    public void takeNext() {
        Node currentNode = bfsQueue.dequeue();
        Node predecessor = nodeBFSNode.get(currentNode).getPredecessor();
        ArrayList<Angle> allAngles = nodeWithAngles.get(currentNode);
        List<Node> currentList;
        if (calcFaces instanceof CalculateFacesFromDrawing) {
            currentList = calcFaces.getAdjacencyListFor(currentNode);
        } else {
            currentList = testedGraph.getAdjacencyList(currentNode);
        }

        // find suitable edge
        int start = 0;
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i) == predecessor) {
                start = i;
                break;
            }
        }

        Edge first = getEdge(currentNode, currentList.get(start));
        Line currentLine = edgeLine.get(first);
        // set to correct direction of rotation
        double absoluteAngleValue = (currentLine.getAngleToXAxis() + 180) % 360;
        for (int i = 1; i < currentList.size(); i++) {
            Node nextConnected = currentList.get((start + i)
                    % currentList.size());
            if (!nodeBFSNode.get(nextConnected).isVisited()) {
                bfsQueue.enqueue(nextConnected);
                nodeBFSNode.get(nextConnected).setVisited(true);
            }
            Edge second = getEdge(currentNode, nextConnected);
            Angle nextAngle = findAngle(first, second, allAngles);
            nodeBFSNode.get(nextConnected).setPredecessor(currentNode);
            absoluteAngleValue += nextAngle.getValue();
            absoluteAngleValue %= 360;
            setAbsoluteAngle(edgeLine.get(second), absoluteAngleValue,
                    currentNode, nextConnected);
            nodeBFSNode.get(nextConnected).setWay(edgeLine.get(second));
            first = second;
        }
    }

    /**
     * Inits the map which gives to every node its <code>BFSNode</code>.
     * 
     */
    public void initBFSNodesMap() {
        List<Node> nodes = testedGraph.getNodes();
        int nodeIndex = 0;
        for (int n = 0; n < nodes.size(); n++) {
            Node currentNode = nodes.get(n);
            BFSNode bfsNode = new BFSNode(currentNode, nodeIndex++);
            nodeBFSNode.put(currentNode, bfsNode);
        }
    }

    /**
     * Returns the edge between source and target
     * 
     * @param source
     *            <code>Node</code>
     * @param target
     *            <code>Node</code>
     * 
     * @return the edge <code>Edge</code> of (source, target).
     */
    public Edge getEdge(Node source, Node target) {
        Collection<Edge> betweenEdge = graph.getEdges(source, target);
        Edge myEdge = betweenEdge.iterator().next();
        return myEdge;
    }

    /**
     * Returns the angle between the <code>first</code> and the
     * <code>second</code> edge.
     * 
     * @param first
     * @param second
     * @param allAngles
     * @return The angle between the <code>first</code> and the
     *         <code>second</code> edge.
     */
    public Angle findAngle(Edge first, Edge second, ArrayList<Angle> allAngles) {
        Angle searched = allAngles.get(0);
        boolean found = false;
        for (int i = 0; i < allAngles.size(); i++) {
            Angle current = allAngles.get(i);
            if (current.getFirst() == first && current.getSecond() == second) {
                searched = current;
                found = true;
                break;
            }
        }
        if (found == false) {
            for (int i = 0; i < allAngles.size(); i++) {
                Angle current = allAngles.get(i);
                if (current.getSecond() == first
                        && current.getFirst() == second) {
                    searched = current;
                    found = true;
                    break;
                }
            }
        }
        return searched;
    }

    /**
     * Returns the edgeMatrix.
     * 
     * @return the edgeMatrix.
     */
    public EdgeMatrix getEdgeMatrix() {
        return edgeMatrix;
    }

    /**
     * Sets the edgeMatrix.
     * 
     * @param edgeMatrix
     *            the edgeMatrix to set.
     */
    public void setEdgeMatrix(EdgeMatrix edgeMatrix) {
        this.edgeMatrix = edgeMatrix;
    }

    /**
     * A <code>BFSNode</code> object represents a
     * <code>org.graffiti.graph.Node</code> in a <code>Queue</code>. Used for
     * the breadth first search.
     * 
     * @author Mirka Kossak
     * 
     */
    private class BFSNode {
        // private Node node;

        private int index;

        private boolean visited;

        private Node predecessor;

        // the way from the predecessor node to this node
        // private Line way;

        /**
         * Constructs a new <code>BFSNode</code>.
         * 
         * @param node
         *            The <code>org.graffiti.graph.Node</code>.
         * @param index
         *            The <code>index</code> of the <code>BFSNode</code>.
         */
        public BFSNode(Node node, int index) {
            // this.node = node;
            this.visited = false;
            this.predecessor = null;
            this.index = index;
        }

        // /**
        // * Returns the node.
        // *
        // * @return the node.
        // */
        // public Node getNode()
        // {
        // return node;
        // }

        /**
         * Returns the index
         * 
         * @return the index
         */
        public int getIndex() {
            return this.index;
        }

        // /**
        // * Sets the node.
        // *
        // * @param node the node to set.
        // */
        // public void setNode(Node node)
        // {
        // this.node = node;
        // }

        /**
         * Returns the predecessor.
         * 
         * @return the predecessor.
         */
        public Node getPredecessor() {
            return predecessor;
        }

        /**
         * Sets the predecessor.
         * 
         * @param predecessor
         *            the predecessor to set.
         */
        public void setPredecessor(Node predecessor) {
            this.predecessor = predecessor;
        }

        // /**
        // * Returns the way.
        // *
        // * @return the way.
        // */
        // public Line getWay()
        // {
        // return way;
        // }

        /**
         * Sets the way.
         * 
         * @param way
         *            the way to set.
         */
        public void setWay(Line way) {
            // this.way = way;
        }

        /**
         * Sets the visited.
         * 
         * @param visited
         *            the visited to set.
         */
        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        /**
         * Returns the visited.
         * 
         * @return the visited.
         */
        public boolean isVisited() {
            return visited;
        }

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
