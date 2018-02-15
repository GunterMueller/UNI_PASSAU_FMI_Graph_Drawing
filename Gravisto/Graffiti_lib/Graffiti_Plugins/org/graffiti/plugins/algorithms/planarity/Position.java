package org.graffiti.plugins.algorithms.planarity;

/**
 * Stores a position on the boundary of a bicomp while searching a Kuratowski
 * subgraph.
 * 
 * @author Wolfgang Brunner
 */
public class Position {

    /**
     * The node the current <code>Position</code> represents
     */
    public ArbitraryNode pos;

    /**
     * The direction the current <code>Position</code> represents. Possible
     * values are 0 and 1. The node <code>pos.link[direction]</code> was the
     * last node visited.
     */
    public int direction;

    /**
     * Constructs a new <code>Position</code>
     * 
     * @param pos
     *            The node of the <code>Position</code>
     * @param direction
     *            The direction of the <code>Position</code>
     */
    public Position(ArbitraryNode pos, int direction) {
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "(" + pos + "," + direction + ")";
    }

}
