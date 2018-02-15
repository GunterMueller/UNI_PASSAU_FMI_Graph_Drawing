// =============================================================================
//
//   AddEdgePoint.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("addEdgePoint")
public class AddEdgePoint extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private LinkedList<Point2D> points;

    public AddEdgePoint(StartEdgeCreation startEdgeCreationAction) {
        points = startEdgeCreationAction.getPoints();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Point2D point = gfp.snapBend(in.get(positionSlot));
        points.getLast().setLocation(point);

        points.add(new Point2D.Double(point.getX(), point.getY()));
        view.getGestureFeedbackProvider().setDummyEdgePoints(points, true);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
