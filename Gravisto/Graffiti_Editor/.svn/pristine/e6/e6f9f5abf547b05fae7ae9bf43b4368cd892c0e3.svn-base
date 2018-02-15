// =============================================================================
//
//   MousePressOnEdgeBendTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugin.view.interactive.slots.StringSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMousePress;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MousePressOnEdgeBendTrigger extends FastViewTrigger {
    @OutSlot
    public static final StringSlot bend = new StringSlot("bend", FastViewPlugin
            .getString("bendslot.name"), FastViewPlugin
            .getString("bendslot.description"), "");

    public MousePressOnEdgeBendTrigger(MousePressOnEdgeTrigger parent) {
        super(parent, "bend");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMousePress emp = (ExtendedMousePress) userGesture;
        return emp.getBend().length() != 0;
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMousePress emp = (ExtendedMousePress) userGesture;
        out.put(bend, emp.getBend());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
