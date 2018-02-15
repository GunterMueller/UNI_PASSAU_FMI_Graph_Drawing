/*
 * Created on Aug 30, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class calculate all faces of a graph. Basic requirement is a
 * (tri)connected plane graph.
 */
public class CalculateFace {

    // ~ Instance fields
    // ========================================================
    private static String PATH = "FPPnumber";

    private static final String FACE = "FPPFace";

    private TestedGraph tGraph;

    protected Face[] faces;

    private Edge[] edgePosition;

    private int numberOfFaces;

    protected int outerfaceIndex = 0;

    private Graph graph;

    private HashMap<String, Edge> edgesMap;

    // ~ Constructors
    // ===========================================================
    /**
     * @param graph
     *            <code>Graph</code>
     */
    public CalculateFace(Graph graph, TestedGraph tGraph) {
        this.graph = graph;
        this.tGraph = tGraph;
        numberOfFaces = getNumberOfFaces();
        faces = new Face[numberOfFaces];
        edgePosition = new Edge[graph.getEdges().size()];
        edgesMap = new HashMap<String, Edge>();
        getAllFace();
        outerfaceIndex = getOuterfaceIndex();
    }

    // ~ Methods
    // ================================================================
    /** Calculate all faces with the help of edges */
    private void getAllFace() {
        addAttributes();
        Collection<Edge> edges = graph.getEdges();
        int index = 0;
        int number;
        for (Iterator<Edge> i = edges.iterator(); i.hasNext()
                && index < numberOfFaces;) {
            Edge edge = i.next();
            number = index;
            index = evaluateFace(edge, number);
        }
    }

    /**
     * addAttributes() are mere a help in order to check visited edges
     */
    @SuppressWarnings("boxing")
    private void addAttributes() {
        Collection<Edge> edges = graph.getEdges();
        Collection<Node> nodes = graph.getNodes();
        String forward, backward, sourceString, targetString;
        Node source, target;
        int edgeIndex = 0, index = 0, value = 0;

        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node current = i.next();
            current.addString("", PATH, ((Integer) value).toString());
            value++;
        }

        for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
            Edge edge = i.next();
            source = edge.getSource();
            target = edge.getTarget();
            sourceString = source.getString(PATH);
            targetString = target.getString(PATH);

            forward = sourceString + "FPP-" + targetString;
            backward = targetString + "FPP-" + sourceString;
            edge.addBoolean("", forward, false);
            edge.addBoolean("", backward, false);

            /** Direct access to the edges */
            edge.addInteger("", "FPPnumber", edgeIndex);
            edgePosition[edgeIndex] = edge;
            edgeIndex++;

            edgesMap.put(forward, edge);
            edgesMap.put(backward, edge);

            edge.addInteger("", sourceString + "FPPnext", -1);
            edge.addInteger("", sourceString + "FPPprevious", -1);
            edge.addInteger("", targetString + "FPPnext", -1);
            edge.addInteger("", targetString + "FPPprevious", -1);

        }

        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node current = i.next();
            Edge edge = null, nextEdge = null;
            String next = "", previous = "";
            /** Determine the order of edges relative to the Node current */
            LinkedList<Node> adjacency = ((LinkedList<Node>) (tGraph
                    .getTestedComponents().get(0)).getAdjacencyList(current));
            Node[] neighbours = adjacency.toArray(new Node[0]);
            for (int j = 0; j < neighbours.length; j++) {
                edge = getEdge(neighbours[j], current);
                next = current.getString(PATH) + "FPPnext";
                previous = current.getString(PATH) + "FPPprevious";
                index = j + 1;
                if (index == neighbours.length) {
                    index = 0;
                }
                nextEdge = getEdge(neighbours[index], current);
                addEdge(edge, nextEdge, next, previous);
            }
        }

    }

    /**
     * @param edge
     *            <code>Edge</code> -
     * @param index
     *            <code>int</code> -
     * 
     * @return index <code>int</code> of a face. Evaluate not visited faces
     *         relative to edge
     */
    private int evaluateFace(Edge edge, int index) {
        Face face;
        face = getFace(edge, true);
        if (face != null) {
            face.setIndex(index);
            faces[index] = face;
            addAttr(face, index);
            index++;
        }
        face = getFace(edge, false);
        if (face != null) {
            face.setIndex(index);
            faces[index] = face;
            addAttr(face, index);
            index++;
        }
        return index;
    }

    /**
     * @param edge
     *            <code>Edge</code> -
     * @param ahead
     *            <code>boolean</code> -
     * 
     * @return a not visited face, otherwise null. The face starts with edge
     *         source or edge target depending from "ahead"
     */
    private Face getFace(Edge edge, boolean ahead) {
        Node source, nextNode, current;
        String forward;
        String next;
        LinkedList<Node> facelistNode = new LinkedList<Node>();
        LinkedList<Edge> facelistEdge = new LinkedList<Edge>();
        Edge visited;
        if (ahead) {
            source = edge.getSource();
            nextNode = edge.getTarget();
        } else {
            nextNode = edge.getSource();
            source = edge.getTarget();
        }

        current = source;
        facelistNode.add(source);
        while (source != nextNode) {
            visited = getEdge(current, nextNode);
            if (isVisited(current, nextNode))
                return null;
            forward = current.getString(PATH) + "FPP-"
                    + nextNode.getString(PATH);
            visited.setBoolean(forward, true);
            facelistNode.add(nextNode);
            facelistEdge.add(visited);
            next = nextNode.getString(PATH) + "FPPnext";
            current = nextNode;
            int edgeIndex = visited.getInteger(next);
            visited = getEdge(edgeIndex);
            nextNode = getOppositeNode(nextNode, visited);
        }
        visited = getEdge(facelistNode.getFirst(), facelistNode.getLast());
        facelistEdge.add(visited);
        Face face = new Face(facelistNode, facelistEdge);
        return face;
    }

    /**
     * Every vertex and edge get the attribute "Face", containing the index of
     * the face as an IntegerObj
     * 
     * @param face
     *            <code>Face</code> -
     * @param index
     *            <code>int</code> -
     * 
     */
    private void addAttr(Face face, int index) {
        Node node;
        Edge edge;
        NodeAttributes nodeAttr;
        EdgeAttributes edgeAttr;
        ObjectAttribute objAttr = null;

        for (Iterator<Node> i = face.getNodelist().iterator(); i.hasNext();) {
            node = i.next();
            try {
                objAttr = (ObjectAttribute) node.getAttribute(FACE);
                nodeAttr = (NodeAttributes) objAttr.getObject();
                nodeAttr.addFace(index);
            } catch (Exception e) {
                nodeAttr = new NodeAttributes();
                nodeAttr.addFace(index);

                objAttr = new ObjectAttribute(FACE);
                objAttr.setObject(nodeAttr);

                node.addAttribute(objAttr, "");
            }
        }

        for (Iterator<Edge> i = face.getEdgelist().iterator(); i.hasNext();) {
            edge = i.next();
            try {
                objAttr = (ObjectAttribute) edge.getAttribute(FACE);
                edgeAttr = (EdgeAttributes) objAttr.getObject();
                edgeAttr.addFace(index);
            } catch (Exception e) {
                edgeAttr = new EdgeAttributes();
                edgeAttr.addFace(index);

                objAttr = new ObjectAttribute(FACE);
                objAttr.setObject(edgeAttr);

                edge.addAttribute(objAttr, "");
            }
        }
    }

    /**
     * @param source
     *            <code>Node</code> -
     * @param target
     *            <code>Node</code> -
     * 
     * @return <code>true</code>, if the <code>Edge</code> of (source, target)
     *         is <code>visited</code>, otherwise <code>false</code>.
     */
    private boolean isVisited(Node source, Node target) {
        String forward = source.getString(PATH) + "FPP-"
                + target.getString(PATH);
        return getEdge(source, target).getBoolean(forward);
    }

    /**
     * @return the index <code>int</code> of the outerface concern faces[]
     *         <code>Face[]</code>
     */
    private int getOuterfaceIndex() {
        int nodeSize = 0;
        for (int i = 0; i < faces.length; i++) {
            if (nodeSize < faces[i].nodeSize()) {
                nodeSize = faces[i].nodeSize();
                outerfaceIndex = i;
            }
        }
        return outerfaceIndex;
    }

    /**
     * Return the source or the target from edge
     * 
     * @param edge
     * @param current
     *            must be the source or the target relative to edge
     * 
     * @return the sourcenode or the targetnode from edge
     */
    protected Node getOppositeNode(Node current, Edge edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();
        if (source == current)
            return target;
        return source;
    }

    /**
     * The Edge <code>addEdge</code> becomes the next edge of current. The
     * element is inserted and becomes the successor of <code>current</code>.
     * 
     * @param current
     *            <code>Edge</code>
     * @param addEdge
     *            <code>Edge</code>
     * 
     * @return true, if <code>addNode</code> is added, otherwise false.
     */
    protected boolean addEdge(Edge current, Edge addEdge, String next,
            String previous) {
        if (current == null || addEdge == null)
            return false;
        int successorIndex = current.getInteger(next);
        int currentIndex = current.getInteger("FPPnumber");
        int addEdgeIndex = addEdge.getInteger("FPPnumber");

        if (successorIndex == -1) // current has not a successor
        {
            current.setInteger(next, addEdgeIndex);
            addEdge.setInteger(previous, currentIndex);
        } else
        // current has exact one successor
        {

            Edge successor = getEdge(successorIndex);
            current.setInteger(next, addEdgeIndex);
            addEdge.setInteger(previous, currentIndex);
            addEdge.setInteger(next, successorIndex);
            successor.setInteger(previous, addEdgeIndex);
        }
        return true;
    }

    /**
     * Remove the Edge "current" from the adjacency list.
     * 
     * @param current
     *            <code>Node</code> will be deleted
     * 
     * @return true, if <code>current</code> is deleted, otherwise false.
     */
    protected boolean removeEdge(Edge current, String next, String previous) {
        if (current == null)
            return false;
        Edge successor, predecessor;
        int succ = current.getInteger(next);
        int pred = current.getInteger(previous);

        if (succ != -1) // current has a sucessor
        {
            if (pred != -1) // current has a successor and a predecessor
            {
                predecessor = getEdge(pred);
                successor = getEdge(succ);
                current.setInteger(next, -1);
                current.setInteger(previous, -1);
                predecessor.setInteger(next, succ);
                successor.setInteger(previous, pred);
                current.setInteger(next, -1);
                current.setInteger(previous, -1);
            } else
            // current has ONLY a sucessor
            {
                successor = getEdge(succ);
                successor.setInteger(previous, -1);
                current.setInteger(next, -1);
            }
        } else
        // current has NOT a successor
        {
            if (pred != -1) // current has ONLY a predecessor
            {
                current.setInteger(previous, -1);
                predecessor = getEdge(pred);
                predecessor.setInteger(next, -1);
            } else
                return false;
        }
        return true;
    }

    /**
     * @param source
     *            <code>Node</code> -
     * @param target
     *            <code>Node</code> -
     * 
     * @return the edge <code>Edge</code> of (source, target).
     */
    protected Edge getEdge(Node source, Node target) {
        String forward = source.getString(PATH) + "FPP-"
                + target.getString(PATH);
        return edgesMap.get(forward);
    }

    /**
     * This method returns a edge relative to the number.
     * 
     * @param number
     *            of the edge
     * 
     * @return Edge
     */
    protected Edge getEdge(int number) {
        return edgePosition[number];
    }

    /**
     * The HashMap contains the edges of the graph. The KEY is a String between
     * two Nodes. Example: source.getString(PATH) + "-" + target.getString(PATH)
     * 
     * @return the HashMap
     */
    protected HashMap<String, Edge> getEdgeMap() {
        return edgesMap;
    }

    /**
     * Euler's theorem |V| + |F| - |E| = 1 <=> |F| = |E| + 1 - |V| V = Vertices,
     * E = Edges, F = inner Faces
     * 
     * @return number of faces <code>int</code>
     */
    protected int getNumberOfFaces() {
        numberOfFaces = graph.getNumberOfEdges() + 2 - graph.getNumberOfNodes();
        return numberOfFaces;
    }

    /** @return face Array <code>Face[]</code> */
    protected Face[] getFaces() {
        return faces;
    }

    /** @return outerfaceindex <code>int</code> */
    protected int getOutIndex() {
        return outerfaceIndex;
    }
}
