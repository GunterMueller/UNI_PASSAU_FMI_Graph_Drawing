package org.graffiti.plugins.algorithms.SchnyderRealizer;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class represents a face in a maximum planar graph. This means the face
 * must be a triangle.
 * 
 * @author hofmeier
 */
public class Face {

    /** The nodes of the face in ccw order */
    protected Node[] nodes = new Node[3];

    /**
     * The edges of the face in ccw order (edges[0] is from nodes[0] to
     * nodes[1])
     */
    protected Edge[] edges = new Edge[3];

    /**
     * An integer labeling of the inner angles of the face. E.g. in a Schnyder
     * labeling the angles are labeled with 1,2 and 3 with further conditions.
     */
    protected int[] angles = new int[3];

    /**
     * The algorithm, which created the face (necessary to determine if the face
     * is the outer face.
     */
    protected AbstractDrawingAlgorithm asa;

    /**
     * Creates a new face.
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
    public Face(Node n1, Node n2, Node n3, Edge e1, Edge e2, Edge e3,
            AbstractDrawingAlgorithm a) {
        this.nodes[0] = n1;
        this.nodes[1] = n2;
        this.nodes[2] = n3;
        this.edges[0] = e1;
        this.edges[1] = e2;
        this.edges[2] = e3;
        this.asa = a;
    }

    /**
     * Enumerates the angles according to a Schnyder labeling. It starts with a
     * given node (label 1) and enumerates the (in cw-order) following angles
     * with 2 and 3.
     * 
     * @param startFrom
     *            the node whose angle will be labeled with 1.
     */
    public void enumerateAngles(Node startFrom) {
        int i = getPosition(startFrom);
        for (int j = 1; j < 4; j++) {
            this.angles[i % 3] = j;
            i = i + 2;
        }
    }

    /**
     * Returns the position in the <code>nodes</code>array of a given node.
     * 
     * @param node
     *            the nodes whose position will be returned.
     * @return the position of the node.
     */
    public int getPosition(Node node) {
        if (this.nodes[0] == node)
            return 0;
        if (this.nodes[1] == node)
            return 1;
        else
            return 2;
    }

    /**
     * Determines if this is the outer face.
     * 
     * @return true if this is the outer face, else false.
     */
    public boolean isOuterFace() {
        if ((this.nodes[0] == asa.getOuterNodes()[0])
                || (this.nodes[0] == asa.getOuterNodes()[1])
                || (this.nodes[0] == asa.getOuterNodes()[2])) {
            if ((this.nodes[1] == asa.getOuterNodes()[0])
                    || (this.nodes[1] == asa.getOuterNodes()[1])
                    || (this.nodes[1] == asa.getOuterNodes()[2])) {
                if ((this.nodes[2] == asa.getOuterNodes()[0])
                        || (this.nodes[2] == asa.getOuterNodes()[1])
                        || (this.nodes[2] == asa.getOuterNodes()[2]))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the label of an angle at a given position in the
     * <code>angles</code> array.
     * 
     * @param pos
     *            the position in the array.
     * @return the label of the angle.
     */
    public int getAngle(int pos) {
        return this.angles[pos];
    }

    /**
     * Returns the edges of the face.
     * 
     * @return the edges of the face.
     */
    public Edge[] getEdges() {
        return edges;
    }

    /**
     * Returns the nodes of the face.
     * 
     * @return the nodes of the face.
     */
    public Node[] getNodes() {
        return nodes;
    }
}
