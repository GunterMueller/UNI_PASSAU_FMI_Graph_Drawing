// =============================================================================
//
//   Face.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Face.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.planarity.faces;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * Representation of a face as a sequence of <code>Dart</code>s (d_1, ..., d_n).
 * Each dart represents an edge of the original graph either in its original or
 * reverse direction.
 * 
 * @see Dart
 */
public class Face {
    /** List of darts defining a face. */
    private List<Dart> darts = new LinkedList<Dart>();

    /** List of nodes defining the face. */
    private List<Node> nodes;

    /**
     * Add a new dart to the list of darts defining this face.
     * 
     * @param dart
     *            Additional dart for this face.
     */
    void addDart(Dart dart) {
        darts.add(dart);
    }

    /**
     * Get a sequence of the darts defining the face.
     * 
     * @return List of darts.
     */
    public List<Dart> getDarts() {
        return Collections.unmodifiableList(darts);
    }

    /**
     * Convenience method returning the nodes defining the face.
     * 
     * @return List of nodes.
     */
    public synchronized List<Node> getNodes() {
        if (nodes != null)
            return nodes;

        nodes = new LinkedList<Node>();

        for (Dart dart : getDarts()) {
            nodes.add(dart.getSource());
        }

        nodes = Collections.unmodifiableList(nodes);
        return nodes;
    }
}
