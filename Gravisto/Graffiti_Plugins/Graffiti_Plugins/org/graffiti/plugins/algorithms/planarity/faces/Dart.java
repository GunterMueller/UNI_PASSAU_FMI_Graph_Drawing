// =============================================================================
//
//   Dart.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Dart.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.planarity.faces;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Internal representation of an edge either in its original or reverse
 * direction. Each original edge is splitted into two darts. Each dart is
 * associated with the face on its right side.
 */
public class Dart {
    /** Reference to original edge. */
    private Edge originalEdge;

    /** The reverse dart. */
    private Dart reverse;

    /** Source of internal edge. */
    private Node source;

    /** Face associated with this dart. */
    private Face face;

    // the following attributes are needed during the calculation of the faces

    /** Internal target vertex. */
    private Vertex targetVertex;

    /** Position of dart in adjacency list of the target node. */
    private int targetPosition;

    /**
     * Create a new dart for the specified edge and use source as the source for
     * the dart.
     * 
     * @param edge
     *            Edge to create dart for.
     * @param source
     *            Source to be used for the dart.
     */
    public Dart(Edge edge, Node source) {
        originalEdge = edge;
        this.source = source;
    }

    /**
     * Check whether dart is an original edge or a reversed one.
     * 
     * @return <code>true</code> if dart is a reversed edge, <code>false</code>
     *         otherwise.
     */
    public boolean isReversed() {
        return originalEdge.getSource() != source;
    }

    /**
     * Get target of dart.
     * 
     * @return Target of dart.
     */
    public Node getTarget() {
        return isReversed() ? originalEdge.getSource() : originalEdge
                .getTarget();
    }

    /**
     * Get source of dart.
     * 
     * @return Source of dart.
     */
    public Node getSource() {
        return source;
    }

    /**
     * Get associated face for the dart.
     * 
     * @return Face at the right side of the dart.
     */
    public Face getFace() {
        return face;
    }

    /**
     * Set associated face for the dart.
     * 
     * @param face
     *            Face at the right side of the dart.
     */
    void setFace(Face face) {
        this.face = face;
    }

    /**
     * Get reverse dart of this dart.
     * 
     * @return Reverse of this dart.
     */
    public Dart getReverse() {
        return reverse;
    }

    /**
     * Set the reverse dart for this dart.
     * 
     * @param reverse
     *            Reverse of this dart.
     */
    void setReverse(Dart reverse) {
        this.reverse = reverse;
    }

    /**
     * Get original edge represented by this dart
     * 
     * @return Original edge of this dart.
     */
    public Edge getEdge() {
        return originalEdge;
    }

    /**
     * Helper method called by a vertex if this dart was added to its adjacency
     * list.
     * 
     * @param vertex
     *            Vertex the dart was added to.
     * @param position
     *            Position of the dart in the adjacency list.
     */
    void added(Vertex vertex, int position) {
        targetVertex = vertex;
        targetPosition = position;
    }

    /**
     * Get next dart around a face.
     * 
     * @return Next dart around a face.
     */
    Dart nextDart() {
        Vertex sourceVertex = reverse.targetVertex;
        int sourcePosition = reverse.targetPosition;
        return sourceVertex.getPred(sourcePosition);
    }
}
