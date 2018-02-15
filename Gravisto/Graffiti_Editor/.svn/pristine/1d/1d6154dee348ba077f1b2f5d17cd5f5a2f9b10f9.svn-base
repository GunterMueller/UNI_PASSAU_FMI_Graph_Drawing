// =============================================================================
//
//   Java2DEdgeChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.util.Map;
import java.util.SortedSet;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugins.views.fast.EdgeChangeListener;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DEdgeChangeListener extends EdgeChangeListener<Java2DEngine> {
    private Map<Edge, AbstractEdgeRep> edges;
    private SortedSet<AbstractEdgeRep> sortedEdges;
    private Map<Edge, EdgeGraphicAttribute> pendingEdgeShapeChanges;
    private EdgeRepFactory edgeRepFactory;

    protected Java2DEdgeChangeListener(Java2DEngine engine,
            Map<Edge, AbstractEdgeRep> edges,
            SortedSet<AbstractEdgeRep> sortedEdges,
            Map<Edge, EdgeGraphicAttribute> pendingEdgeShapeChanges,
            EdgeRepFactory edgeRepFactory) {
        super(engine);
        this.edges = edges;
        this.sortedEdges = sortedEdges;
        this.pendingEdgeShapeChanges = pendingEdgeShapeChanges;
        this.edgeRepFactory = edgeRepFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReverse(Edge edge) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        edges.remove(edge);
        sortedEdges.remove(edgeRep);
        edgeRep = edgeRepFactory.create(edge);
        edges.put(edge, edgeRep);
        sortedEdges.add(edgeRep);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeDash(org.graffiti
     * .graph.Edge, org.graffiti.graphics.Dash)
     */
    @Override
    public void onChangeDash(Edge edge, Dash dash) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        edgeRep.setDash(dash);
        edgeRep.setLineStroke(engine.acquireStroke(edgeRep.getFrameThickness(),
                dash));
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeDepth(org.
     * graffiti.graph.Edge, double)
     */
    @Override
    public void onChangeDepth(Edge edge, double depth) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        if (depth != edgeRep.getDepth()) {
            sortedEdges.remove(edgeRep);
            edgeRep.setDepth(depth);
            sortedEdges.add(edgeRep);
        }
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeFillColor(
     * org.graffiti.graph.Edge, java.awt.Color)
     */
    @Override
    public void onChangeFillColor(Edge edge, Color color) {
        edges.get(edge).setFillColor(color);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeFrameColor
     * (org.graffiti.graph.Edge, java.awt.Color)
     */
    @Override
    public void onChangeFrameColor(Edge edge, Color color) {
        edges.get(edge).setFrameColor(color);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeFrameThickness
     * (org.graffiti.graph.Edge, double)
     */
    @Override
    public void onChangeFrameThickness(Edge edge, double frameThickness) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        edgeRep.setFrameThickness(frameThickness);
        edgeRep.setArrowStroke(engine.acquireStroke(Math.min(1.0,
                frameThickness), FastViewPlugin.DEFAULT_DASH));
        Dash dash = edgeRep.getDash();
        if (dash != null) {
            edgeRep.setLineStroke(engine.acquireStroke(frameThickness, dash));
        }
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeSelection(
     * org.graffiti.graph.Edge, boolean)
     */
    @Override
    public void onChangeSelection(Edge edge, boolean isSelected) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        // TODO: Temporary fix.
        // onChangeSelection() should only occur after onAdd() notification.
        if (edgeRep == null)
            return;
        edgeRep.setSelected(isSelected);
    }

    @Override
    public void onChangeHover(Edge edge, boolean isHover) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        if (edgeRep == null)
            return;
        edgeRep.setHover(isHover);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeShape(org.
     * graffiti.graph.Edge, org.graffiti.graphics.EdgeGraphicAttribute)
     */
    @Override
    public void onChangeShape(Edge edge, EdgeGraphicAttribute attribute) {
        pendingEdgeShapeChanges.put(edge, attribute);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onChangeThickness(
     * org.graffiti.graph.Edge, double)
     */
    @Override
    public void onChangeThickness(Edge edge, double thickness) {
        edges.get(edge).setThickness(thickness);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onSetShape(org.graffiti
     * .graph.Edge, org.graffiti.plugin.view.EdgeShape)
     */
    @Override
    public void onSetShape(Edge edge, EdgeShape shape) {
        edges.get(edge).setShape(shape);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
