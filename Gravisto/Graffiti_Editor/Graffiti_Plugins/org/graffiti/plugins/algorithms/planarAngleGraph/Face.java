// =============================================================================
//
//   Face.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * A <code>Face</code> object is a cycle of vertices and edges.
 * 
 * @author Mirka Kossak
 */
public class Face {
    private int numberOfNodes;

    private LinkedList<Node> nodes;

    private LinkedList<Edge> edges;

    private int numberOfAngles;

    private LinkedList<Angle> angles;

    private int index;

    /**
     * Creates a new <code>Face</code> without nodes and index index.
     */
    public Face() {
        this.numberOfNodes = 0;
        this.nodes = new LinkedList<Node>();
        this.edges = new LinkedList<Edge>();
        this.numberOfAngles = 0;
        this.angles = new LinkedList<Angle>();
    }

    /**
     * Adds an angle to the face.
     * 
     * @param angle
     */
    public void addAngle(Angle angle) {
        this.angles.add(angle);
        this.numberOfAngles++;
    }

    /**
     * Adds a node to the face
     * 
     * @param node
     */
    public void addNode(Node node) {
        this.nodes.add(node);
        this.numberOfNodes++;
    }

    /**
     * Adds an edge to the face
     * 
     * @param edge
     */
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    /**
     * Returns the number of nodes in the face
     * 
     * @return number of nodes
     */
    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }

    /**
     * Returns the nodes of the face
     * 
     * @return linkedList of the nodes
     */
    public LinkedList<Node> getNodes() {
        return this.nodes;
    }

    /**
     * Returns the index of the face
     * 
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Sets the index of the face
     * 
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return Returns the numberOfAngles.
     */
    public int getNumberOfAngles() {
        return numberOfAngles;
    }

    /**
     * @param numberOfAngles
     *            The numberOfAngles to set.
     */
    public void setNumberOfAngles(int numberOfAngles) {
        this.numberOfAngles = numberOfAngles;
    }

    /**
     * @return Returns the angles.
     */
    public LinkedList<Angle> getAngles() {
        return angles;
    }

    /**
     * @param angles
     *            The angles to set.
     */
    public void setAngles(LinkedList<Angle> angles) {
        this.angles = angles;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
