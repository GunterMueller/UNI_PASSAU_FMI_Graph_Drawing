// =============================================================================
//
//   MouseReleaseOnNodeTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseRelease;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseReleaseOnNodeTrigger extends FastViewTrigger {
    @OutSlot
    public static final Slot<GraphElement> element = Slot.create("element",
            GraphElement.class);

    public MouseReleaseOnNodeTrigger(MouseReleaseTrigger parent) {
        super(parent, "node");
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMouseRelease emr = (ExtendedMouseRelease) userGesture;
        out.put(element, emr.getElement());
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMouseRelease emr = (ExtendedMouseRelease) userGesture;
        return emr.getElement() instanceof Node;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
