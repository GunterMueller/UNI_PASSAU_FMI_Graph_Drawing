package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * Creates a network node for the prescribed bends.
 */
public class BendNode extends MCMFNode {
    /** The <code>Edge</code> for which the node is constructed. */
    private Edge edge;

    /**
     * Creates a network node for the prescribed bends.
     * 
     * @param label
     *            Label of bend node.
     * @param edge
     *            <code>Edge</code> for which the node is constructed.
     * @param id
     *            The ID of the MCMFNode.
     */
    public BendNode(String label, Edge edge, int id) {
        super(label, Type.BEND, id);
        this.edge = edge;
    }

    /** Returns the <code>Edge</code> for which the bend is constructed. */
    public Edge getEdge() {
        return edge;
    }
}
