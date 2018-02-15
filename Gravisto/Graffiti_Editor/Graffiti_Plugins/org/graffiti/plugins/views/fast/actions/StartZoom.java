// =============================================================================
//
//   StartZoom.java
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
@ActionId("startZoom")
public class StartZoom extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> centerSlot = Slot.create("center",
            Point2D.class);

    private ZoomView zoomViewAction;

    public StartZoom(ZoomView zoomViewAction) {
        this.zoomViewAction = zoomViewAction;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        zoomViewAction.setCenter(in.get(centerSlot));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
