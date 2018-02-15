// =============================================================================
//
//   AbstractEdgeRep.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractEdgeRep extends AbstractOpenGLRep {
    protected Edge edge;
    protected EdgeShape shape;

    protected AbstractEdgeRep(Edge edge) {
        super();
        this.edge = edge;
    }

    protected Edge getEdge() {
        return edge;
    }

    EdgeShape getShape(OpenGLEngine engine) {
        if (shape == null) {
            shape = engine.retrieveShape(edge);
        }
        return shape;
    }

    @Override
    protected void retrieveShape(Map<Node, AbstractNodeRep> nodes) {
        shape = AttributeUtil.getShape(edge, nodes.get(edge.getSource())
                .getShape(), nodes.get(edge.getTarget()).getShape());
    }

    @Override
    protected void setAsHovered() {
        setPrimarySelectionColor(FastViewPlugin.HOVER_COLOR);
        setPrimarySelectionDepth(depth);
        setSecondarySelectionDepth(2.0);
    }

    @Override
    protected void setAsSelected() {
        setPrimarySelectionColor(FastViewPlugin.SELECTION_COLOR);
        setPrimarySelectionDepth(depth);
        setSecondarySelectionDepth(depth);
    }

    @Override
    protected void setAsUnselected() {
        setPrimarySelectionDepth(2.0);
        setSecondarySelectionDepth(2.0);
    }

    protected abstract void setPrimarySelectionColor(Color color);

    protected abstract void setPrimarySelectionDepth(double depth);

    protected abstract void setSecondarySelectionDepth(double depth);

    @Override
    protected void updatePosition() {
        // Nothing to do.
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
