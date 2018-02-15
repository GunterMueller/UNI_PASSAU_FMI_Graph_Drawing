package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * This arc is used for connecting two faces. It stores the <code>Edge</code>,
 * which the two faces share.
 */
public class MCMFArc_FF extends MCMFArc {

    /**
     * MCMFArc for connecting two faces.
     */

    /** The edge which the two faces are sharing. */
    private Edge edge;

    /**
     * Constructs a MCMFArc for connecting two faces.
     * 
     * @param label
     *            The label of the arc.
     * @param start
     *            The starting point of the edge, which is a face.
     * @param end
     *            The target point of the edge, which is a face.
     * @param edge
     *            The edge which the two faces are sharing.
     */
    public MCMFArc_FF(String label, MCMFNode start, MCMFNode end, Edge edge) {
        super(label, start, end, Integer.MAX_VALUE, 1);
        this.edge = edge;
    }

    /**
     * @return Returns the edge.
     */
    @Override
    public Edge getEdge() {
        return edge;
    }
}
