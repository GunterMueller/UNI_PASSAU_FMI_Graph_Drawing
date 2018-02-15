package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.Iterator;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class represents a separating triangle of a maximum planar graph. That
 * is a triangle whose removal will destroy the connectivity of the graph. The
 * inheritance from the <code>Face</code> has just been chosen to keep the
 * implementation more simple. CAUTION: a separating triangle of a graph is NOT
 * one of its faces. The class is needed here as Brehm`s algorithm to calculate
 * all realizers also flips separating triangles.
 * 
 * @author hofmeier
 */
public class SeparatingTriangle extends Face {
    /** The faces withi the separating triangle */
    private HashList<Face> innerFaces = new HashList<Face>();

    /** The edges within the separating triangle */
    private HashList<Edge> innerEdges = new HashList<Edge>();

    /**
     * Creates a new instance of the class.
     * 
     * @param n1
     *            the first node.
     * @param n2
     *            the second node.
     * @param n3
     *            the third node.
     * @param e1
     *            the first edge.
     * @param e2
     *            the second edge.
     * @param e3
     *            the third edge.
     * @param a
     *            the drawing algorithm.
     */
    public SeparatingTriangle(Node n1, Node n2, Node n3, Edge e1, Edge e2,
            Edge e3, AbstractDrawingAlgorithm a) {
        super(n1, n2, n3, e1, e2, e3, a);
        this.calculateInnerFacesAndEdges();
    }

    /**
     * Calculates the faces and edges contained within the separating triangle.
     * This is done by separating the faces in two groups. The ones left of an
     * edge of the triangle and the ones right of it. After that the group of
     * faces that does not contain the outer face are the faces within the
     * triangle.
     */
    private void calculateInnerFacesAndEdges() {
        Face neighborFace1 = this.asa.facesByEdges.get(this.edges[0]).get(0);
        Face neighborFace2 = this.asa.facesByEdges.get(this.edges[0]).get(1);
        Face innerNeighbor = neighborFace1;
        // Calculate one group of faces.
        this.innerFaces = this.calculateNeighborFaces(neighborFace1);
        boolean containsOuterFace = false;
        Iterator<Face> faceIt = innerFaces.iterator();
        while (faceIt.hasNext()) {
            Face f = faceIt.next();
            if (f.isOuterFace()) {
                containsOuterFace = true;
                break;
            }
        }
        // If the outer face is contained, calcuate the other group.
        if (containsOuterFace) {
            innerNeighbor = neighborFace2;
            this.innerFaces = this.calculateNeighborFaces(neighborFace2);
        }
        // Get the inner edges.
        faceIt = this.innerFaces.iterator();
        while (faceIt.hasNext()) {
            Face f = faceIt.next();
            for (int i = 0; i < f.getEdges().length; i++) {
                Edge e = f.getEdges()[i];
                if ((!innerEdges.contains(e)) && (!e.equals(this.edges[0]))
                        && (!e.equals(this.edges[1]))
                        && (!e.equals(this.edges[2]))) {
                    this.innerEdges.append(e);
                }
            }
        }
        this.correct(innerNeighbor);
    }

    /**
     * Does the main work during the calculation of the inner faces. It gets
     * iteratively all neighbor faces of a given face, but does not "jump over"
     * one the separating triangle`s edges.
     * 
     * @param neighborFace
     *            the starting face
     * @return all faces within or out of the separating triangle.
     */
    private HashList<Face> calculateNeighborFaces(Face neighborFace) {
        Stack<Face> facesWithUnknownNeighbors = new Stack<Face>();
        HashList<Face> neighborFaces = new HashList<Face>();
        facesWithUnknownNeighbors.push(neighborFace);
        neighborFaces.append(neighborFace);
        while (!facesWithUnknownNeighbors.isEmpty()) {
            Face neighbor = facesWithUnknownNeighbors.pop();
            for (int i = 0; i < neighbor.getEdges().length; i++) {
                Edge e = neighbor.getEdges()[i];
                // Prevent "jumping over" an edge of this separating triangle
                if ((!e.equals(this.edges[0])) && (!e.equals(this.edges[1]))
                        && (!e.equals(this.edges[2]))) {
                    Face neighbor1 = this.asa.facesByEdges.get(e).getFirst();
                    Face neighbor2 = this.asa.facesByEdges.get(e).getLast();
                    if (!neighborFaces.contains(neighbor1)) {
                        neighborFaces.append(neighbor1);
                        facesWithUnknownNeighbors.push(neighbor1);
                    }
                    if (!neighborFaces.contains(neighbor2)) {
                        neighborFaces.append(neighbor2);
                        facesWithUnknownNeighbors.push(neighbor2);
                    }
                }
            }
        }
        return neighborFaces;
    }

    /**
     * As it is not guaranteed that the nodes of the separating triangle are
     * saved in ccw order, this is checked and - if necessary - corrected.
     * 
     * @param innerNeighbor
     *            a face within the separating triangle.
     */
    private void correct(Face innerNeighbor) {
        Edge commonEdge = this.edges[0];
        int positionOfCEInNeighbor = 0;
        for (int i = 0; i < innerNeighbor.getEdges().length; i++) {
            if (innerNeighbor.getEdges()[i].equals(commonEdge)) {
                positionOfCEInNeighbor = i;
            }
        }
        if (!this.nodes[0]
                .equals(innerNeighbor.getNodes()[positionOfCEInNeighbor])) {
            Node tempNode = this.nodes[1];
            this.nodes[1] = this.nodes[2];
            this.nodes[2] = tempNode;
            Edge tempEdge = this.edges[0];
            this.edges[0] = this.edges[2];
            this.edges[2] = tempEdge;
        }
    }

    /**
     * Returns the edges within this separating triangle.
     * 
     * @return the edges within this separating triangle.
     */
    public HashList<Edge> getInnerEdges() {
        return innerEdges;
    }
}
