// =============================================================================
//
//   Vertex.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Vertex.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.planarity.faces;

import java.util.ArrayList;

import org.graffiti.graph.Node;

/**
 * Internal representation of a node.
 * 
 * @see Dart
 * @see Face
 */
class Vertex {
    /**
     * Ordered (could be interpreted as clockwise) list of incoming darts around
     * the vertex.
     */
    private ArrayList<Dart> adjList;

    /**
     * Reference to original node.
     */
    private Node node;

    /**
     * Create a new internal node.
     * 
     * @param node
     *            Node represented by this internal node.
     */
    Vertex(Node node) {
        this.node = node;
        adjList = new ArrayList<Dart>(node.getEdges().size());
    }

    /**
     * Add new incoming dart to the end of the adjacency list of this node.
     * 
     * @param dart
     *            Dart to add to the end of the list.
     */
    void add(Dart dart) {
        // only incoming darts should be added
        assert dart.getTarget() == node;

        adjList.add(dart);
        dart.added(this, adjList.size() - 1);
    }

    /**
     * Get predecessor of a given dart in list of darts.
     * 
     * @param position
     *            Position representing dart to get predecessor for.
     */
    Dart getPred(int position) {
        // make sure position is valid
        assert position >= 0 && position < adjList.size();

        if (position == 0) {
            position = adjList.size();
        }

        return adjList.get(position - 1);
    }
}
