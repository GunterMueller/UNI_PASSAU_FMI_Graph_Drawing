// =============================================================================
//
//   Node.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Node.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides an interfaces for a graph node. All directed edges whose source is
 * the current <code>Node</code> are regarded as outgoing edges. Ingoing edges
 * are those directed edges whose target is the current <code>Node</code>.
 * Undirected edges are regarded separately since a <code>Graph</code> can have
 * <b>both</b> directed and indirected edges.
 * 
 * @version $Revision: 5767 $
 * 
 * @see Edge
 * @see GraphElement
 * @see Graph
 */
public interface Node extends GraphElement {

    /**
     * Union of <code>getDirectedInEdges()</code> and
     * <code>getUndirectedEdges() </code>.
     * 
     * @return Collection
     */
    public Collection<Edge> getAllInEdges();

    /**
     * Union of <code>getInNeighbors()</code> and
     * <code>getUndirectedNeighbors() </code>.
     * 
     * @return Collection
     */
    public Collection<Node> getAllInNeighbors();

    /**
     * Union of <code>getDirectedOutEdges()</code> and
     * <code>getUndirectedEdges() </code>.
     * 
     * @return Collection
     */
    public Collection<Edge> getAllOutEdges();

    /**
     * Union of <code>getOutNeighbors()</code> and
     * <code>getUndirectedNeighbors()</code>.
     * 
     * @return Collection
     */
    public Collection<Node> getAllOutNeighbors();

    /**
     * Returns a collection containing the directed ingoing edges of the
     * <code>Node</code>.
     * 
     * @return a collection containing the directed ingoing edges of the
     *         <code>Node</code>.
     */
    public Collection<Edge> getDirectedInEdges();

    /**
     * Returns an iterator containing the directed ingoing edges of the
     * <code>Node</code>.
     * 
     * @return an iterator containing the directed ingoing edges of the
     *         <code>Node</code>.
     */
    public Iterator<Edge> getDirectedInEdgesIterator();

    /**
     * Returns a collection containing the outgoing directed edges of the
     * <code>Node</code>.
     * 
     * @return a collection containing the outgoing directed edges of the
     *         <code>Node</code>.
     */
    public Collection<Edge> getDirectedOutEdges();

    /**
     * Returns an iterator containing the outgoing directed edges of the
     * <code>Node</code>.
     * 
     * @return an iterator containing the outgoing directed edges of the
     *         <code>Node</code>.
     */
    public Iterator<Edge> getDirectedOutEdgesIterator();

    /**
     * Returns a collection containing all the ingoing and outgoing directed and
     * undirected edges of the current <code>Node</code>. Ingoing and outgoing
     * edges will not be separated and there will be no ordering on the
     * collection.
     * 
     * @return a collection containing all ingoing and outgoing directed and
     *         undirected edges of the current <code>Node</code>.
     */
    public Collection<Edge> getEdges();

    /**
     * Returns an iterator containing all the ingoing and outgoing directed and
     * undirected edges of the current <code>Node</code>. Ingoing and outgoing
     * edges will not be separated and there will be no ordering on the
     * collection.
     * 
     * @return an iterator containing all ingoing and outgoing directed and
     *         undirected edges of the current <code>Node</code>.
     */
    public Iterator<Edge> getEdgesIterator();

    /**
     * Returns the in-degree of the current <code>Node</code>. The in-degree is
     * defined as the number of ingoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the in-degree of the current <code>Node</code>.
     */
    public int getInDegree();

    /**
     * Returns a collection containing the neighbor nodes belonging to incoming
     * edges. Note that the number of Elements returned by this function might
     * be less than the number returned by <code>getInDegree()</code>, due to
     * the fact that multiple edges between the same two nodes are possible but
     * the corresponding <code>Node</code> is only inserted once.
     * 
     * @return a collection containing the neighbor nodes belonging to incoming
     *         edges.
     */
    public Collection<Node> getInNeighbors();

    /**
     * Returns an iterator over the neighbor nodes belonging to incoming edges.
     * 
     * @return a Iterator over the neighbor nodes belonging to incoming edges.
     */
    public Iterator<Node> getInNeighborsIterator();

    /**
     * Returns a collection containing all the neighbor nodes of the current
     * <code>Node</code>. A neighbor <code>Node</code> is either the source or
     * the target of either an ingoing or an outgoing or an undirected
     * <code>Edge</code>.
     * 
     * @return a collection containing all the neighbor nodes of the current
     *         <code>Node</code>.
     */
    public Collection<Node> getNeighbors();

    /**
     * Returns an interator over the neighbor nodes of the current
     * <code>Node</code>. A neighbor <code>Node</code> is either the source or
     * the target of either an ingoing or outgoing or undirected
     * <code>Edge</code>.
     * 
     * @return an iterator over the neighbor nodes of the current
     *         <code>Node</code>.
     */
    public Iterator<Node> getNeighborsIterator();

    /**
     * Returns the out-degree of the current <code>Node</code>. The out-degree
     * is defined as the number of outgoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the out-degree of the current <code>Node</code>.
     */
    public int getOutDegree();

    /**
     * Returns a collection containing all the neighbors of the current
     * <code>Node</code> which are connected by an outgoing <code>Edge</code>.
     * Note that the number of elements returned by this function might be less
     * than the number returned by <code>getOutDegree()</code>, due to the fact
     * that multiple edges between the same two nodes are possible but the
     * corresponding <code>Node</code> is only inserted once.
     * 
     * @return a collection containing all the neighbor nodes of the current
     *         <code>Node</code> connected by an outgoing <code>Edge</code>.
     */
    public Collection<Node> getOutNeighbors();

    /**
     * Returns an iterator containing all the neighbors of the current
     * <code>Node</code> which are connected by an outgoing <code>Edge</code>.
     * 
     * @return an iterator containing all the neighbor <code>Node</code>s of the
     *         current <code>Node</code> connected by an outgoing
     *         <code>Edge</code>.
     */
    public Iterator<Node> getOutNeighborsIterator();

    /**
     * Returns a collection containing the undirected ingoing and outgoing edges
     * of the <code>Node</code>.
     * 
     * @return a collection containing the undirected ingoing and outgoing edges
     *         of the <code>Node</code>.
     */
    public Collection<Edge> getUndirectedEdges();

    /**
     * Returns an iterator containing all undirected edges of the
     * <code>Node</code>.
     * 
     * @return an iterator containing all undirected edges of the
     *         <code>Node</code>.
     */
    public Iterator<Edge> getUndirectedEdgesIterator();

    /**
     * Returns a collection containing all the neighbors which are connected to
     * the current <code>Node</code> by an undirected <code>Edge</code>.
     * 
     * @return a collection containing all the neighbors which are connected to
     *         the current <code>Node</code> by an undirected <code>Edge</code>.
     */
    public Collection<Node> getUndirectedNeighbors();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
