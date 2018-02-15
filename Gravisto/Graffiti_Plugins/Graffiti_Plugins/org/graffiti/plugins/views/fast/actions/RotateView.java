// =============================================================================
//
//   RotateView.java
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
@ActionId("rotateView")
public class RotateView extends FastViewAction {
    @InSlot
    public static final Slot<Double> angleSlot = Slot.create("angle",
            Double.class);

    @InSlot
    public static final Slot<Boolean> snapSlot = Slot.create("snap",
            Boolean.class);

    private Point2D center;

    public RotateView() {
        center = new Point2D.Double();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        if (center == null)
            return;
        view.getViewport().rotate(in.get(angleSlot) / 180.0 * Math.PI, center,
                in.get(snapSlot));
    }

    protected void setCenter(Point2D center) {
        this.center = center;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
