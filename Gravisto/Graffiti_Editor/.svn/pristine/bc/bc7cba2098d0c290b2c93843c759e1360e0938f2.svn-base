package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Hashtable;

import org.graffiti.graph.Edge;

/**
 * Creates a network node for the help nodes.
 */
public class HelpNode extends MCMFNode {
    /** The node for which the HelpNode is constructed. */
    private GraphNode node;

    /** The edge for which the HelpNode is constructed. */
    private Edge edge;

    /** The face where the HelpNode is. */
    private FaceNode face;

    /** The HelpNode on the other side of the edge. */
    private Hashtable<Edge, HelpNode> other;

    /**
     * Creates a network node for the help nodes.
     * 
     * @param label
     *            Label of help node.
     * @param node
     *            The GraphNode for which the HelpNode is constructed.
     * @param edge
     *            The edge for which the HelpNode is constructed.
     * @param id
     *            The id of the node.
     */
    public HelpNode(String label, GraphNode node, Edge edge, int id) {
        super(label, Type.HELP, id);
        this.node = node;
        this.edge = edge;
        this.face = null;
        this.other = new Hashtable<Edge, HelpNode>();
    }

    /**
     * Returns the node.
     * 
     * @return the <code>GraphNode</code>.
     */
    protected GraphNode getNode() {
        return node;
    }

    /**
     * Returns the edge.
     * 
     * @return the <code>Edge</code>.
     */
    protected Edge getEdge() {
        return edge;
    }

    /**
     * Returns the face in which the node is.
     * 
     * @return the <code>FaceNode</code>.
     */
    protected FaceNode getFace() {
        return face;
    }

    /**
     * Sets the face for which the node was constructed.
     * 
     * @param face
     *            the <code>FaceNode</code> to set.
     */
    protected void setFace(FaceNode face) {
        this.face = face;
    }

    /**
     * Returns the HelpNode on the other side of the edge.
     * 
     * @param e
     *            the <code>Edge</code> the HelpNode on the other side id left
     *            to.
     * @return the other HelpNode.
     */
    protected HelpNode getOther(Edge e) {
        return other.get(e);
    }

    /**
     * Sets the HelpNode on the other side of the edge.
     * 
     * @param other
     *            the HelpNode to set.
     * @param e
     *            the <code>Edge</code> the HelpNode on the other side id left
     *            to.
     */
    protected void setOther(HelpNode other, Edge e) {
        this.other.put(e, other);
    }
}
