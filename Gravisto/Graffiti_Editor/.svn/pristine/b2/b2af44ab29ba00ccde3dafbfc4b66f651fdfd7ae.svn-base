// =============================================================================
//
//   ZoomView.java
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
@ActionId("zoom")
public class ZoomView extends FastViewAction {
    @InSlot
    public static final Slot<Double> factorSlot = Slot.create("factor",
            Double.class);

    @InSlot
    public static final Slot<Boolean> snapSlot = Slot.create("snap",
            Boolean.class);

    private Point2D center;

    protected void setCenter(Point2D center) {
        this.center = center;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        view.getViewport().zoom(in.get(factorSlot), center, in.get(snapSlot));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
