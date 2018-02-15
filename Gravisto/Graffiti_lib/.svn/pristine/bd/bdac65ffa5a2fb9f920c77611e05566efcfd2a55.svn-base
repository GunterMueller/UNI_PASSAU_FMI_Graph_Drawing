// =============================================================================
//
//   CalculateFaces.java
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

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Calculates all faces of the graph according to the planar embedding of the
 * planarity test.
 * 
 * @author Mirka Kossak
 */
public class CalculateFaces {
    private HashMap<Edge, VisitedEdge> visitedEdgesMap;

    private Graph graph;

    protected TestedGraph testedGraph;

    /**
     * This HashMaps includes to every node in the testedGraph a HashMap of node
     * - successor pairs
     */
    private HashMap<Node, HashMap<Node, Node>> nodesMap;

    private Collection<Node> nodes;

    private Collection<Edge> edges;

    /**
     * Constructor
     * 
     * @param graph
     * @param testedGraph
     */
    public CalculateFaces(Graph graph, TestedGraph testedGraph) {
        this.graph = graph;
        this.testedGraph = testedGraph;
        this.nodes = testedGraph.getNodes();
        this.edges = graph.getEdges();
        this.nodesMap = new HashMap<Node, HashMap<Node, Node>>();
        this.visitedEdgesMap = new HashMap<Edge, VisitedEdge>();
    }

    /**
     * Inits the HashMap visitedEdge
     */
    public void initVisitedEdgeMap() {
        for (Iterator<Edge> edgesIt = edges.iterator(); edgesIt.hasNext();) {
            Edge currentEdge = edgesIt.next();
            VisitedEdge initVisited = new VisitedEdge();
            visitedEdgesMap.put(currentEdge, initVisited);
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
     * Inits the nodes map. The nodes map stores for every node a map with nodes
     * successor pairs.
     */
    public void initNodesMap() {
        for (Iterator<Node> nodesIt = nodes.iterator(); nodesIt.hasNext();) {
            Node current = nodesIt.next();
            HashMap<Node, Node> nodeSuccessorMap = makeNodeMap(current);
            nodesMap.put(current, nodeSuccessorMap);
        }
    }

    /**
     * Euler's formula |V| + |F| - |E| = 2
     * 
     * @return The number of faces of the graph.
     */
    public int getNumberOfFaces() {
        return this.graph.getNumberOfEdges() + 2
                - this.testedGraph.getNumberOfNodes();
    }

    /**
     * Returns all faces of the graph
     * 
     * @return all faces of the graph
     */
    public Face[] getAllFaces() {
        initVisitedEdgeMap();
        initNodesMap();
        int numberOfFaces = this.getNumberOfFaces();
        Face[] faces = new Face[numberOfFaces];
        int faceIndex = 0;
        for (Iterator<Edge> edgesIt = edges.iterator(); edgesIt.hasNext();) {
            if (faceIndex < numberOfFaces) {
                Edge currentEdge = edgesIt.next();
                VisitedEdge isVisited = visitedEdgesMap.get(currentEdge);
                if (!isVisited.getForward()) {
                    Node source = currentEdge.getSource();
                    Node target = currentEdge.getTarget();
                    Face newFace = getFace(source, target, faceIndex,
                            currentEdge);
                    newFace.addEdge(currentEdge);
                    faces[faceIndex++] = newFace;
                    isVisited.setForward(true);
                    if (faceIndex == numberOfFaces) {
                        break;
                    }
                }
                if (!isVisited.getBackward()) {
                    Node source = currentEdge.getTarget();
                    Node target = currentEdge.getSource();
                    Face newFace = getFace(source, target, faceIndex,
                            currentEdge);
                    newFace.addEdge(currentEdge);
                    faces[faceIndex++] = newFace;
                    isVisited.setBackward(true);
                    if (faceIndex == numberOfFaces) {
                        break;
                    }
                }
            }
        }
        return faces;
    }

    /**
     * Returns the face that includes the edge that has source <code>Node</code>
     * source and target <code>Node</code> target
     * 
     * @param source
     *            <code>Node</code>
     * @param target
     *            <code>Node</code>
     * @param index
     *            index of the face
     * @return Face
     */
    public Face getFace(Node source, Node target, int index, Edge visitedEdge) {
        Face face = new Face();
        face.setIndex(index);
        face.addNode(source);
        face.addNode(target);
        Node current = target;
        Node help = source;
        int angleIndex = 0;
        Angle angleWithIntersectionSource = new Angle();
        angleIndex++;
        angleWithIntersectionSource.setSecond(visitedEdge);
        angleWithIntersectionSource.setVertex(source);
        // A face consists of at least 3 nodes
        while (true) {
            Angle angle = new Angle();
            angleIndex++;
            angle.setFirst(visitedEdge);
            angle.setVertex(current);
            HashMap<Node, Node> successorMap = nodesMap.get(current);
            Node next = successorMap.get(help);
            visitedEdge = getEdge(current, next);
            angle.setSecond(visitedEdge);
            face.addAngle(angle);
            VisitedEdge visited = visitedEdgesMap.get(visitedEdge);
            if (visitedEdge.getSource() == current) {
                visited.setForward(true);
            } else {
                visited.setBackward(true);
            }
            face.addEdge(visitedEdge);
            if (source == next) {
                HashMap<Node, Node> nextSuccessorMap = nodesMap.get(next);
                if (nextSuccessorMap.get(current) == target) {
                    angleWithIntersectionSource.setFirst(visitedEdge);
                    face.addAngle(angleWithIntersectionSource);
                    face.setNumberOfAngles(angleIndex);
                    break;
                }
            }
            face.addNode(next);
            help = current;
            current = next;
        }
        return face;
    }

    /**
     * Creates to the <code>Node</code> node a HashMap of node - successor pairs
     * 
     * @param node
     * @return the HashMap which gives to every node in the adjacencylist its
     *         successor.
     */
    public HashMap<Node, Node> makeNodeMap(Node node) {
        HashMap<Node, Node> nodeSuccessorMap = new HashMap<Node, Node>();
        Node first = getNode(node, 0);
        int size = testedGraph.getAdjacencyList(node).size();
        Node last = getNode(node, size - 1);
        nodeSuccessorMap.put(last, first);
        int index = 1;
        Node current = first;
        while (index < size) {
            Node next = getNode(node, index);
            nodeSuccessorMap.put(current, next);
            index++;
            current = next;
        }
        return nodeSuccessorMap;
    }

    /**
     * Get the node at position <code>index</code> in the adjacency list of node
     * <code>current</code>.
     * 
     * @param current
     *            Node whose adjacency list is used.
     * @param index
     *            Position of node to return in adjacency list.
     * @return Node at position <code>index</code> in <code>current</code>'s
     *         adjacency list.
     */
    protected Node getNode(Node current, int index) {
        return testedGraph.getAdjacencyList(current).get(index);
    }

    /**
     * Returns the map which gives to every node its hashmap of node-successor
     * pairs.
     * 
     * @return the map which gives to every node its hashmap of node-successor
     *         pairs.
     */
    public HashMap<Node, HashMap<Node, Node>> getNodesMap() {
        return this.nodesMap;
    }

    /**
     * Returns the list in which is stored to which this node is adjacent to.
     * 
     * @param node
     *            The node to get the adjacency list for.
     * @return The adjacency list.
     */
    protected ArrayList<Node> getAdjacencyListFor(Node node) {
        return new ArrayList<Node>();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
