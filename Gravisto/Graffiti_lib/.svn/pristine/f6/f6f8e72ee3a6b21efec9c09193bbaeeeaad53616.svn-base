package org.graffiti.plugins.algorithms.kandinsky;

import java.util.LinkedList;

import org.graffiti.graph.Edge;

/**
 * Orthogonal representation of an <code>Edge</code>.
 */
public class OrthEdge {

    /*
     * Format: Kante, Kantenknicke, Winkel zur n�chsten Kante der Einbettung [e;
     * 0101; 3]
     */

    /** Start node of the edge. */
    private GraphNode start;

    /** End node of the edge. */
    private GraphNode end;

    /** The edge of the face. */
    private Edge edge;

    /** The bends of the edge. */
    private LinkedList<Boolean> bends = new LinkedList<Boolean>();

    /** The value of the angle to the next edge of the embedding. */
    private int angle;

    /** Is true, if the edge is in the direction. */
    private boolean direction = true;

    /**
     * Representation of the edge of the graph in the orthogonal representation
     * of a face.
     * 
     * @param start
     *            The starting point of the edge.
     * @param end
     *            The ending point of the edge.
     * @param e
     *            The edge of the face.
     * @param bends
     *            The bends of the edge.
     * @param angle
     *            The value of the angle to the next edge of the embedding.
     * @param direction
     *            True, if it is the direction of the edge.
     */
    public OrthEdge(GraphNode start, GraphNode end, Edge e,
            LinkedList<Boolean> bends, int angle, boolean direction) {
        this.edge = e;
        this.bends = bends;
        this.angle = angle;
        this.direction = direction;
        this.start = start;
        this.end = end;
    }

    /**
     * Gets the corresponding edge of the graph.
     * 
     * @return <code>Edge</code>
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Gets the bends of the edge.
     * 
     * @return <code>LinkedList</code><Boolean>
     */
    public LinkedList<Boolean> getBends() {
        return bends;
    }

    /**
     * Gets the to the next edge of the embedding.
     * 
     * @return int the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Gets the direction of the edge.
     * 
     * @return boolean The direction.
     */
    public boolean getDirection() {
        return direction;
    }

    /**
     * Returns the starting point of the edge.
     * 
     * @return the <code>GraphNode</code>.
     */
    public GraphNode getStart() {
        return start;
    }

    /**
     * Returns the target point of the edge.
     * 
     * @return the <code>GraphNode</code>.
     */
    public GraphNode getEnd() {
        return end;
    }
}
