// =============================================================================
//
//   StartEdgeCreation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.actions.StartEdgeCreation;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startEdgeCreationOnTorus")
public class StartEdgeCreationOnTorus extends StartEdgeCreation {

    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    public StartEdgeCreationOnTorus() {
        super();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Node node = in.get(nodeSlot);
        Point2D point = in.get(positionSlot);
        startNode.set(node);
        points.clear();
        Point2D nodePoint = AttributeUtil.getPosition(node);
        point = ((ToricalFastView) view).roundToNearestCopy(point, nodePoint);
        points.add(point);
        points.add(new Point2D.Double(point.getX(), point.getY()));
        view.getGestureFeedbackProvider().setDummyEdgePoints(points, true);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
