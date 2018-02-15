// =============================================================================
//
//   GetHubPosition.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("getHubPosition")
public class GetHubPosition extends FastViewAction {
    @OutSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D position = view.getGestureFeedbackProvider().getHubPosition();
        if (position == null) {
            position = new Point2D.Double();
        }
        out.put(positionSlot, position);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
