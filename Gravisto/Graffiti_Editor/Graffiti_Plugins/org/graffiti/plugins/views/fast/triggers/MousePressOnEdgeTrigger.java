// =============================================================================
//
//   MousePressOnEdgeTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMousePress;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MousePressOnEdgeTrigger extends FastViewTrigger {
    @OutSlot
    public static final Slot<GraphElement> element = Slot.create("element",
            GraphElement.class);

    public MousePressOnEdgeTrigger(MousePressTrigger parent) {
        super(parent, "edge");
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMousePress emp = (ExtendedMousePress) userGesture;
        out.put(element, emp.getElement());
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMousePress emp = (ExtendedMousePress) userGesture;
        return emp.getElement() instanceof Edge;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
