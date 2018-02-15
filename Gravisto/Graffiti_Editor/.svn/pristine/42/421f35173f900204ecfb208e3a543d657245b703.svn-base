// =============================================================================
//
//   OpenGLEdgeChangeListener.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugins.views.fast.EdgeChangeListener;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLEdgeChangeListener extends EdgeChangeListener<OpenGLEngine> {
    private Map<Edge, AbstractEdgeRep> edges;
    private ChangeProcessor changeProcessor;

    protected OpenGLEdgeChangeListener(OpenGLEngine engine,
            Map<Edge, AbstractEdgeRep> edges, ChangeProcessor changeProcessor) {
        super(engine);
        this.edges = edges;
        this.changeProcessor = changeProcessor;
    }

    @Override
    public void onChangeDash(Edge edge, Dash dash) {
        changeProcessor.onTesselation(edges.get(edge));
    }

    @Override
    public void onChangeDepth(Edge edge, double depth) {
        changeProcessor.onDepth(edges.get(edge), depth);
    }

    @Override
    public void onChangeFillColor(Edge edge, Color color) {
        changeProcessor.onColor(edges.get(edge));
    }

    @Override
    public void onChangeFrameColor(Edge edge, Color color) {
        changeProcessor.onColor(edges.get(edge));
    }

    @Override
    public void onChangeFrameThickness(Edge edge, double frameThickness) {
        changeProcessor.onTesselation(edges.get(edge));
    }

    @Override
    public void onChangeHover(Edge edge, boolean isHover) {
        changeProcessor.onHover(edges.get(edge), isHover);
    }

    @Override
    public void onChangeSelection(Edge edge, boolean isSelected) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        // TODO: Temporary fix.
        // onChangeSelection() should only occur after onAdd() notification.
        if (edgeRep == null)
            return;
        changeProcessor.onSelection(edgeRep, isSelected);
    }

    @Override
    public void onChangeShape(Edge edge, EdgeGraphicAttribute attribute) {
        changeProcessor.onShape(edges.get(edge));
    }

    @Override
    public void onChangeThickness(Edge edge, double thickness) {
        changeProcessor.onTesselation(edges.get(edge));
    }

    @Override
    public void onSetShape(Edge edge, EdgeShape shape) {
        // Nothing to do.
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.EdgeChangeListener#onReverse(org.graffiti
     * .graph.Edge)
     */
    @Override
    public void onReverse(Edge edge) {
        AbstractEdgeRep edgeRep = edges.get(edge);
        if (edgeRep != null) {
            changeProcessor.onDelete(edgeRep);
        }
        edgeRep = new QualityEdgeRep(edge);
        edges.put(edge, edgeRep);
        changeProcessor.onShape(edgeRep);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
