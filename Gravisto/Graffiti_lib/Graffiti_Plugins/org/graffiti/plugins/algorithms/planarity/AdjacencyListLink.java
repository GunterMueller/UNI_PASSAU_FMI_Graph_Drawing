package org.graffiti.plugins.algorithms.planarity;

/**
 * Abstract superclass of <code>ArbitraryNode</code> and <code>HalfEdge</code>
 * which contains the Array <code>link</code> to represent the two entries of
 * the symlist of <code>ArbitraryNode</code> or <code>HalfEdge</code>.
 * 
 * @author Wolfgang Brunner
 */
public abstract class AdjacencyListLink {

    /**
     * The two entries of the symlist.
     */
    public AdjacencyListLink[] link = new AdjacencyListLink[2];
}
