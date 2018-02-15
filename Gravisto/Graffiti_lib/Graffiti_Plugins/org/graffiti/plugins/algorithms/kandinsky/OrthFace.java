package org.graffiti.plugins.algorithms.kandinsky;

import java.util.LinkedList;

/**
 * The orthogonal representation of a face.
 */
public class OrthFace {

    /** The name of the face. */
    private String name;

    /** The orthogonal representations of the edges of the face. */
    LinkedList<OrthEdge> edges;

    /**
     * Constructs an orthogonal representation of a face.
     * 
     * @param name
     *            The label of the face.
     */
    public OrthFace(String name) {
        this.name = name;
        edges = new LinkedList<OrthEdge>();
    }

    /**
     * Adds the orthogonal representation of one of its edges to the face.
     * 
     * @param e
     *            the orthogonal representation of the edge
     */
    public void addEdge(OrthEdge e) {
        edges.addFirst(e);
    }

    /**
     * Returns the orthogonal representations of all of the edges of the face.
     * 
     * @return the orthogonal representations of the edges of the face.
     */
    public LinkedList<OrthEdge> getEdges() {
        return edges;
    }

    /**
     * Gets the direction of the last edge of the face.
     * 
     * @return The direction of the last Edge
     */
    public boolean getDirectionOfLastEdge() {
        return edges.getFirst().getDirection();
    }

    /**
     * Gets the end of the last inserted edge of the face.
     * 
     * @return The end node of the last edge.
     */
    public GraphNode getEndOfLastEdge() {
        if (edges.size() == 0)
            return null;
        return edges.getFirst().getStart();
    }

    /**
     * Returns the name of the face.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }
}
