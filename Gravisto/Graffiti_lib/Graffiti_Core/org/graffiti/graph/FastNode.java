// =============================================================================
//
//   FastNode.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FastNode.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import static org.graffiti.graph.FastEdge.Incidency.DIR_IN;
import static org.graffiti.graph.FastEdge.Incidency.DIR_LOOP;
import static org.graffiti.graph.FastEdge.Incidency.DIR_OUT;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_IN;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_LOOP;
import static org.graffiti.graph.FastEdge.Incidency.UNDIR_OUT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.FastEdge.End;
import org.graffiti.graph.FastEdge.Incidency;
import org.graffiti.util.ConcatCollection;
import org.graffiti.util.MultiLinkNode;
import org.graffiti.util.MultiLinkable;
import org.graffiti.util.MultiLinkedList;

/**
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-07-21 15:49:46 +0200 (Fr, 21 Jul 2006)
 *          $
 */
public class FastNode extends AbstractNode implements
        MultiLinkable<FastNode, Void> {
    Map<Incidency, List<FastEdge>> edges;

    MultiLinkNode<FastNode> link;

    /**
     * @param graph
     */
    public FastNode(Graph graph) {
        this(graph, null);
    }

    /**
     * @param graph
     * @param coll
     */
    public FastNode(Graph graph, CollectionAttribute coll) {
        super(graph, coll);

        edges = new EnumMap<Incidency, List<FastEdge>>(Incidency.class);

        for (Incidency i : Incidency.values()) {
            edges.put(i, new MultiLinkedList<FastEdge, End>(i.getEdgeEnd()));
        }

    }

    public void addEdge(Incidency incidency, FastEdge edge) {
        edges.get(incidency).add(edge);
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getAllInEdges()
     */
    @Override
    public Collection<Edge> getAllInEdges() {
        return getEdges(DIR_IN, DIR_LOOP, UNDIR_IN, UNDIR_OUT, UNDIR_LOOP);
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getAllOutEdges()
     */
    @Override
    public Collection<Edge> getAllOutEdges() {
        return getEdges(DIR_OUT, DIR_LOOP, UNDIR_IN, UNDIR_OUT, UNDIR_LOOP);
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getDirectedInEdges()
     */
    @Override
    public Collection<Edge> getDirectedInEdges() {
        return getEdges(DIR_IN, DIR_LOOP);
    }

    /*
     * @see org.graffiti.graph.Node#getDirectedInEdgesIterator()
     */
    public Iterator<Edge> getDirectedInEdgesIterator() {
        return getEdges(DIR_IN, DIR_LOOP).iterator();
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getDirectedOutEdges()
     */
    @Override
    public Collection<Edge> getDirectedOutEdges() {
        return getEdges(DIR_OUT, DIR_LOOP);
    }

    /*
     * @see org.graffiti.graph.Node#getDirectedOutEdgesIterator()
     */
    public Iterator<Edge> getDirectedOutEdgesIterator() {
        return getEdges(DIR_OUT, DIR_LOOP).iterator();
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getEdges()
     */
    @Override
    public Collection<Edge> getEdges() {
        return getEdges(DIR_IN, DIR_OUT, DIR_LOOP, UNDIR_IN, UNDIR_OUT,
                UNDIR_LOOP);
    }

    private Collection<Edge> getEdges(Incidency... incidencies) {
        Collection<List<FastEdge>> collections = new ArrayList<List<FastEdge>>(
                incidencies.length);

        for (Incidency incidency : incidencies) {
            collections.add(edges.get(incidency));
        }

        return Collections.unmodifiableCollection(new ConcatCollection<Edge>(
                collections));
    }

    /*
     * @see org.graffiti.graph.Node#getEdgesIterator()
     */
    public Iterator<Edge> getEdgesIterator() {
        return getEdges(DIR_IN, DIR_OUT, DIR_LOOP, UNDIR_IN, UNDIR_OUT,
                UNDIR_LOOP).iterator();
    }

    /*
     * @see org.graffiti.util.Linkable#getLink(java.lang.Object)
     */
    public MultiLinkNode<FastNode> getLinkNode(Void v) {
        return link;
    }

    /*
     * @see org.graffiti.graph.AbstractNode#getUndirectedEdges()
     */
    @Override
    public Collection<Edge> getUndirectedEdges() {
        return getEdges(UNDIR_IN, UNDIR_OUT, UNDIR_LOOP);
    }

    /*
     * @see org.graffiti.graph.Node#getUndirectedEdgesIterator()
     */
    public Iterator<Edge> getUndirectedEdgesIterator() {
        return getEdges(UNDIR_IN, UNDIR_OUT, UNDIR_LOOP).iterator();
    }

    public void removeEdge(Incidency incidency, FastEdge edge) {
        edges.get(incidency).remove(edge);
    }

    /*
     * @see org.graffiti.util.Linkable#setLink(org.graffiti.util.ListLink)
     */
    public void setLinkNode(Void v, MultiLinkNode<FastNode> l) {
        link = l;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
