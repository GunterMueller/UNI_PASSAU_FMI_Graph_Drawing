/**
 * the class MyFace is a face with incident edges and nodes
 * 
 * @author jin
 */

package org.graffiti.plugins.algorithms.upward;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

public class MyFace {
    /**
     * List of edges defining a face.
     */
    private List<Edge> edges;

    /**
     * List of nodes defining the face.
     */
    private List<Node> nodes;

    /**
     * constructor
     */
    public MyFace() {
        this.edges = new LinkedList<Edge>();
        this.nodes = new LinkedList<Node>();
    }

    /**
     * Add a new edge to the list of edges defining this face.
     * 
     * @param edge
     *            Additional edges for this face.
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    /**
     * add a edge to the list of edges
     * 
     * @param index
     *            add the edge to this face in this place.
     * @param edge
     *            add the edge to this face
     */
    public void addEdge(int index, Edge edge) {
        this.edges.add(index, edge);
    }

    /**
     * Get a sequence of the edges defining the face.
     * 
     * @return List of edges.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Add a new node to the list of nodes defining this face.
     * 
     * @param node
     *            Additional nodes for this face.
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Get a sequence of the nodes defining the face.
     * 
     * @return List of nodes.
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * remove the edge
     * 
     * @param index
     *            index of the edges of the face
     */
    public void removeEdge(int index) {
        this.edges.remove(index);
    }

    /**
     * remove the node
     * 
     * @param index
     *            index of the nodes of the face.
     */
    public void removeNode(int index) {
        this.nodes.remove(index);
    }
}
