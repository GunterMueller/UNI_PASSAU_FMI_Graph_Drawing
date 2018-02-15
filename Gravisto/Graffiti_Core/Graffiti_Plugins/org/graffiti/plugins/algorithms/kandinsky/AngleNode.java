package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * Creates a network node for the prescribed angles.
 */
public class AngleNode extends MCMFNode {

    /** The face to which the angle node belongs. */
    private FaceNode face;

    /** The node to which the angle node belongs. */
    private GraphNode node;

    /** The edge, which angle is to be changed by the AngleConstraint. */
    private Edge edge;

    /**
     * Creates a network node for the prescribed angles.
     * 
     * @param label
     *            Label of angle node.
     * @param node
     *            Node of the angle node.
     * @param face
     *            Face of the angle node.
     * @param edge
     *            Edge, which is changed by the angle node.
     * @param id
     *            The ID of the MCMFNode.
     */
    public AngleNode(String label, GraphNode node, FaceNode face, Edge edge,
            int id) {
        super(label, Type.ANGLE, id);
        this.face = face;
        this.node = node;
        this.edge = edge;
    }

    /**
     * Returns the face to which the angle node belongs.
     * 
     * @return the FaceNode.
     */
    public FaceNode getFace() {
        return face;
    }

    /**
     * Returns the node to which the angle node belongs.
     * 
     * @return the GraphNode.
     */
    public GraphNode getNode() {
        return node;
    }

    /**
     * Returns the edge, which is changed by the angle node.
     * 
     * @return the <code>Edge</code>.
     */
    public Edge getEdge() {
        return edge;
    }
}
