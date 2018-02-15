package org.graffiti.plugins.algorithms.planarity;

/**
 * A <code>HalfEdge</code> object stores a edge of the graph. The edges are part
 * of the adjacency lists of each node.
 * 
 * @author Wolfgang Brunner
 */
public class HalfEdge extends AdjacencyListLink {

    /**
     * The source of the edge
     */
    public ArbitraryNode from;

    /**
     * The target of the edge
     */
    public ArbitraryNode to;

    /**
     * Stores whether the bicomp below the source node of this edge has to be
     * switched
     * 
     * @see ConnectedComponent#buildAdjacencyLists
     */
    public int sign;

    /**
     * Determines whether this edge is a short circuit edge
     */
    public boolean shortCircuitEdge;

    /**
     * The <code>HalfEdge</code> with source and target switched
     */
    public HalfEdge twin;

    /**
     * Constructs a new <code>HalfEdge</code>.
     * 
     * @param from
     *            The source of the edge
     * @param to
     *            The target of the edge
     * @param shortCircuit
     *            Determines whether this edge is a short circuit edge
     */
    public HalfEdge(ArbitraryNode from, ArbitraryNode to, boolean shortCircuit) {
        this.from = from;
        this.to = to;
        sign = 1;
        shortCircuitEdge = shortCircuit;
        twin = null;
    }
}
