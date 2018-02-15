// =============================================================================
//
//   StartEdgeCreation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Reference;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startEdgeCreation")
public class StartEdgeCreation extends FastViewAction {
    @InSlot
    public static final Slot<Node> nodeSlot = Slot.create("node", Node.class);

    protected LinkedList<Point2D> points;
    protected Reference<Node> startNode;

    public StartEdgeCreation() {
        startNode = new Reference<Node>();
        points = new LinkedList<Point2D>();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Node node = in.get(nodeSlot);
        startNode.set(node);
        points.clear();
        Point2D point = AttributeUtil.getPosition(node);
        points.add(point);
        points.add(new Point2D.Double(point.getX(), point.getY()));
        view.getGestureFeedbackProvider().setDummyEdgePoints(points, true);
    }

    public LinkedList<Point2D> getPoints() {
        return points;
    }

    public Reference<Node> getStartNode() {
        return startNode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
