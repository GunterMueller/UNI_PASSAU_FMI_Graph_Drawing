// =============================================================================
//
//   MouseMoveTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import java.awt.geom.Point2D;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseMove;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseMoveTrigger extends FastViewTrigger {
    @OutSlot
    public static final Slot<Point2D> rawPosition = Slot.create("rawPosition",
            Point2D.class);

    @OutSlot
    public static final Slot<Point2D> delta = Slot.create("delta",
            Point2D.class);

    public MouseMoveTrigger(MouseTrigger parent) {
        super(parent, "move");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        return userGesture instanceof ExtendedMouseMove;
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMouseMove emm = (ExtendedMouseMove) userGesture;
        out.put(rawPosition, emm.getRawPosition());
        out.put(delta, emm.getDelta());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
