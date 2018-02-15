// =============================================================================
//
//   Java2DGraphElementFinder.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.geom.Point2D;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.java2d.AbstractEdgeRep;
import org.graffiti.plugins.views.fast.java2d.AbstractNodeRep;
import org.graffiti.plugins.views.fast.java2d.Java2DGraphElementFinder;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ToricalGraphElementFinder extends Java2DGraphElementFinder {

    private ToricalFastView view;

    protected ToricalGraphElementFinder(Map<Node, AbstractNodeRep> nodes,
            Map<Edge, AbstractEdgeRep> edges) {
        super(nodes, edges);
    }

    public void setView(ToricalFastView view) {
        this.view = view;
    }

    @Override
    public Edge getEdgeAt(Point2D position, double tolerance) {
        return super.getEdgeAt(view.transformOnTorus(position), tolerance);
    }

    @Override
    public Node getNodeAt(Point2D position) {
        return super.getNodeAt(view.transformOnTorus(position));
    }

    @Override
    public boolean isOnShapeBorder(Point2D position, Node node, double tolerance) {
        return super.isOnShapeBorder(view.transformOnTorus(position), node,
                tolerance);
    }

    @Override
    public String getBend(Point2D position, Edge edge, double tolerance) {
        return super.getBend(view.transformOnTorus(position), edge, tolerance);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
