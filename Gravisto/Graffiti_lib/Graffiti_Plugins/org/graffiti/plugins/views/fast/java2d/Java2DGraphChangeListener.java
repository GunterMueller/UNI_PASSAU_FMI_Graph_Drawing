// =============================================================================
//
//   Java2DGraphChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Stroke;
import java.util.Map;
import java.util.SortedSet;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.plugins.views.fast.GraphChangeListener;
import org.graffiti.util.Pair;

/**
 * {@code GraphChangeListener} for the Java2D graphics engine.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DGraphChangeListener extends
        GraphChangeListener<Java2DEngine> {
    /**
     * A map from nodes to their representatives.
     */
    private Map<Node, AbstractNodeRep> nodes;

    /**
     * Node representatives sorted by their depth value.
     */
    private SortedSet<AbstractNodeRep> sortedNodes;

    /**
     * A map from edges to their representatives.
     */
    private Map<Edge, AbstractEdgeRep> edges;

    /**
     * Edge representatives sorted by their depth value.
     */
    private SortedSet<AbstractEdgeRep> sortedEdges;

    /**
     * A map from pairs of stroke thickness and dashes to the respective
     * strokes.
     */
    private Map<Pair<Double, Dash>, Stroke> strokes;

    private NodeRepFactory nodeRepFactory;
    private EdgeRepFactory edgeRepFactory;

    /**
     * Constructs a {@code Java2DGraphChangeListener}.
     * 
     * @param engine
     *            the graphics engine.
     * @param nodes
     *            a map from nodes to their representatives.
     * @param sortedNodes
     *            node representatives sorted by their depth value.
     * @param edges
     *            a map from edges to their representatives.
     * @param sortedEdges
     *            edge representatives sorted by their depth value.
     * @param strokes
     *            a map from pairs of stroke thickness and dashes to the
     *            respective strokes.
     */
    protected Java2DGraphChangeListener(Java2DEngine engine,
            Map<Node, AbstractNodeRep> nodes,
            SortedSet<AbstractNodeRep> sortedNodes,
            Map<Edge, AbstractEdgeRep> edges,
            SortedSet<AbstractEdgeRep> sortedEdges,
            Map<Pair<Double, Dash>, Stroke> strokes,
            NodeRepFactory nodeRepFactory, EdgeRepFactory edgeRepFactory) {
        super(engine);
        this.nodes = nodes;
        this.sortedNodes = sortedNodes;
        this.edges = edges;
        this.sortedEdges = sortedEdges;
        this.strokes = strokes;
        this.nodeRepFactory = nodeRepFactory;
        this.edgeRepFactory = edgeRepFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAdd(Node node) {
        AbstractNodeRep nodeRep = nodeRepFactory.create(node);
        nodes.put(node, nodeRep);
        sortedNodes.add(nodeRep);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAdd(Edge edge) {
        AbstractEdgeRep edgeRep = edgeRepFactory.create(edge);
        edges.put(edge, edgeRep);
        sortedEdges.add(edgeRep);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClear() {
        nodes.clear();
        sortedNodes.clear();
        edges.clear();
        sortedEdges.clear();
        strokes.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRemove(Node node) {
        AbstractNodeRep nodeRep = nodes.remove(node);
        sortedNodes.remove(nodeRep);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRemove(Edge edge) {
        AbstractEdgeRep edgeRep = edges.remove(edge);
        sortedEdges.remove(edgeRep);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
