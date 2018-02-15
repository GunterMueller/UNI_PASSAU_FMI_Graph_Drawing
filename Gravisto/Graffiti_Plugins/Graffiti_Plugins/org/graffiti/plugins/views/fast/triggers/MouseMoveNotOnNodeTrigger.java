// =============================================================================
//
//   MouseMoveNotOnNodeTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseMove;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseMoveNotOnNodeTrigger extends FastViewTrigger {
    public MouseMoveNotOnNodeTrigger(MouseMoveTrigger parent) {
        super(parent, "nonode");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMouseMove emm = (ExtendedMouseMove) userGesture;
        return !(emm.getElement() instanceof Node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
