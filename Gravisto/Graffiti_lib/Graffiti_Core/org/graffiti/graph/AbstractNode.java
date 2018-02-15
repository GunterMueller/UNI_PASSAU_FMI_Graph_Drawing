// =============================================================================
//
//   AbstractNode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractNode.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.graffiti.attributes.CollectionAttribute;

/**
 * Abstract class <code>AbstractNode</code> common functionality for
 * <code>Node</code> implementations.
 * 
 * @version $Revision: 5767 $
 * 
 * @see AdjListNode
 */
public abstract class AbstractNode extends AbstractGraphElement implements Node {

    /**
     * Constructs a new <code>AbstractNode</code>. Sets the graph of the new
     * <code>AbstractNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the new <code>AbstractNode</code>
     *            instance shall belong to.
     */
    public AbstractNode(Graph graph) {
        super(graph);
    }

    /**
     * Constructs a new <code>AbstractNode</code>. Sets the graph of the new
     * <code>AbstractNode</code>.
     * 
     * @param graph
     *            the <code>Graph</code> the new <code>AbstractNode</code>
     *            instance shall belong to.
     * @param coll
     *            the <code>CollectionAttribute</code> of the newly created
     *            <code>AbstractNode</code> instance.
     */
    public AbstractNode(Graph graph, CollectionAttribute coll) {
        super(graph, coll);
    }

    /**
     * @see org.graffiti.graph.Node#getAllInEdges()
     */
    public Collection<Edge> getAllInEdges() {
        Collection<Edge> col = getDirectedInEdges();
        col.addAll(getUndirectedEdges());

        return col;
    }

    /**
     * @see org.graffiti.graph.Node#getAllInNeighbors()
     */
    public Collection<Node> getAllInNeighbors() {
        Collection<Node> col = getInNeighbors();
        col.addAll(getUndirectedNeighbors());

        return col;
    }

    /**
     * @see org.graffiti.graph.Node#getAllOutEdges()
     */
    public Collection<Edge> getAllOutEdges() {
        Collection<Edge> col = getDirectedOutEdges();
        col.addAll(getUndirectedEdges());

        return col;
    }

    /**
     * @see org.graffiti.graph.Node#getAllOutNeighbors()
     */
    public Collection<Node> getAllOutNeighbors() {
        Collection<Node> col = getOutNeighbors();
        col.addAll(getUndirectedNeighbors());

        return col;
    }

    /**
     * Returns a collection containing the directed, ingoing edges of the
     * <code>Node</code>.
     * 
     * @return a collection containing the directed, ingoing edges of the
     *         <code>Node</code>.
     */
    public Collection<Edge> getDirectedInEdges() {
        ArrayList<Edge> l = new ArrayList<Edge>();

        for (Iterator<Edge> it = getDirectedInEdgesIterator(); it.hasNext();) {
            l.add((it.next()));
        }

        return l;
    }

    /**
     * Returns a collection containing the directed outgoing edges of the
     * <code>Node</code>.
     * 
     * @return a collection containing the directed outgoing edges of the
     *         <code>Node</code>.
     */
    public Collection<Edge> getDirectedOutEdges() {
        List<Edge> l = new ArrayList<Edge>();

        for (Iterator<Edge> it = getDirectedOutEdgesIterator(); it.hasNext();) {
            l.add((it.next()));
        }

        return l;
    }

    /**
     * Returns a collection containing all the ingoing and outgoing directed and
     * undirected edges of the current <code>Node</code>. Ingoing and outgoing
     * edges will not be separated and there will be no ordering on the
     * collection.
     * 
     * @return a collection containing all ingoing and outgoing directed and
     *         undirected edges of the current <code>Node</code>.
     */
    public Collection<Edge> getEdges() {
        Collection<Edge> c = new LinkedList<Edge>();

        for (Iterator<Edge> itr = getEdgesIterator(); itr.hasNext();) {
            c.add(itr.next());
        }

        return c;
    }

    /**
     * Returns the in-degree of the current <code>Node</code>. The in-degree is
     * defined as the number of ingoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the in-degree of the current <code>Node</code>.
     */
    public int getInDegree() {
        return getDirectedInEdges().size() + getUndirectedEdges().size();
    }

    /**
     * Returns a collection containing the neighbor nodes belonging to incoming
     * edges. The number of Elements returned by this function might be less
     * than the number returned by <code>getInDegree()</code>, due to the fact
     * that multiple edges between the same two nodes are possible but the
     * corresponding <code>Node</code> is only inserted once.
     * 
     * @return a collection containing the neighbor nodes belonging to incoming
     *         edges.
     */
    public Collection<Node> getInNeighbors() {
        Set<Node> s = new LinkedHashSet<Node>(getUndirectedNeighbors());

        for (Iterator<Edge> it = getDirectedInEdgesIterator(); it.hasNext();) {
            s.add((it.next()).getSource());
        }

        return s;
    }

    /**
     * Returns an iterator over the neighbor nodes belonging to incoming edges.
     * 
     * @return an iterator over the neighbor nodes belonging to incoming edges.
     */
    public Iterator<Node> getInNeighborsIterator() {
        return getInNeighbors().iterator();
    }

    /**
     * Returns a collection containing all the neighbor nodes of the current
     * <code>Node</code>. A neighbor <code>Node</code> is either the source or
     * the target of either an ingoing, outgoing or an undirected
     * <code>Edge</code> of this <code>Node</code>.
     * 
     * @return a collection containing all the neighbor nodes of the current
     *         <code>Node</code>.
     */
    public Collection<Node> getNeighbors() {
        Set<Node> s = new LinkedHashSet<Node>();

        for (Iterator<Edge> it = getEdgesIterator(); it.hasNext();) {
            Edge e = (it.next());

            if (this == e.getSource()) {
                s.add(e.getTarget());
            } else {
                s.add(e.getSource());
            }
        }

        return s;
    }

    /**
     * Returns an interator over the neighbor nodes of the current
     * <code>Node</code>. A neighbor nodes is either the source or the target of
     * either an ingoing or outgoing or undirected <code>Edge</code>.
     * 
     * @return an iterator over the neighbor nodes of the current
     *         <code>Node</code>.
     */
    public Iterator<Node> getNeighborsIterator() {
        return getNeighbors().iterator();
    }

    /**
     * Returns the out-degree of the current <code>Node</code>. The out-degree
     * is defined as the number of outgoing, directed edges plus the number of
     * undirected edges.
     * 
     * @return the out-degree of the current <code>Node</code>.
     */
    public int getOutDegree() {
        return getDirectedOutEdges().size() + getUndirectedEdges().size();
    }

    /**
     * Returns a collection conataining all the neighbors of the current
     * <code>Node</code> which are connected by an outgoing <code>Edge</code>.
     * The number of Elements returned by this function might be less than the
     * number returned by <code>getOutDegree()</code>, due to the fact that
     * multiple edges between the same two nodes are possible but the
     * corresponding <code>Node</code> is only inserted once.
     * 
     * @return a collection containing all the neighbor nodes of the current
     *         <code>Node</code> connected by an outgoing <code>Edge</code>.
     */
    public Collection<Node> getOutNeighbors() {
        Set<Node> s = new LinkedHashSet<Node>(getUndirectedNeighbors());

        for (Iterator<Edge> it = getDirectedOutEdgesIterator(); it.hasNext();) {
            s.add((it.next()).getTarget());
        }

        return s;
    }

    /**
     * Returns an iterator conataining all the neighbors of the current
     * <code>Node</code> which are connected by an outgoing <code>Edge</code>.
     * 
     * @return a Iterator containing all the neighbor nodes of the current
     *         <code>Node</code> connected by an outgoing <code>Edge</code>.
     */
    public Iterator<Node> getOutNeighborsIterator() {
        return getOutNeighbors().iterator();
    }

    /**
     * Returns a collection containing the undirected edges of the
     * <code>Node</code>.
     * 
     * @return a collection containing the undirected edges of the
     *         <code>Node</code>.
     */
    public Collection<Edge> getUndirectedEdges() {
        List<Edge> l = new ArrayList<Edge>();

        for (Iterator<Edge> it = getUndirectedEdgesIterator(); it.hasNext();) {
            l.add((it.next()));
        }

        return l;
    }

    /**
     * Returns a collection containing all the neighbors which are connected to
     * the current <code>Node</code> by an undirected <code>Edge</code>.
     * 
     * @return a collection containing all the neighbors which are connected to
     *         the current <code>Node</code> by an undirected <code>Edge</code>.
     */
    public Collection<Node> getUndirectedNeighbors() {
        Set<Node> s = new LinkedHashSet<Node>();

        for (Iterator<Edge> it = getUndirectedEdgesIterator(); it.hasNext();) {
            Edge e = (it.next());

            if (this == e.getSource()) {
                s.add(e.getTarget());
            } else {
                s.add(e.getSource());
            }
        }

        return s;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
