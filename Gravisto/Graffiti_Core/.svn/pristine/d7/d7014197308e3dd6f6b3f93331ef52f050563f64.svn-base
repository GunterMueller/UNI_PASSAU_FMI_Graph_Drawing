// =============================================================================
//
//   StartBendMoving.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startBendMoving")
public class StartBendMoving extends FastViewAction {
    @InSlot
    public static final Slot<Edge> edgeSlot = Slot.create("edge", Edge.class);

    @InSlot
    public static final Slot<String> bendSlot = Slot.create("bend",
            String.class);

    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private MoveBend moveBendAction;

    public StartBendMoving(MoveBend moveBendAction) {
        this.moveBendAction = moveBendAction;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        moveBendAction.setCurrentData(in.get(edgeSlot), in.get(bendSlot), in
                .get(positionSlot));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
