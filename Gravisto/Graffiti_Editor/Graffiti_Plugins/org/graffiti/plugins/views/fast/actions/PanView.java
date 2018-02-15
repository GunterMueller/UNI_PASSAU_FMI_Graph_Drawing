// =============================================================================
//
//   PanView.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

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
@ActionId("pan")
public class PanView extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> deltaSlot = Slot.create("delta",
            Point2D.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D delta = in.get(deltaSlot);
        view.getViewport().pan(delta.getX(), delta.getY());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
