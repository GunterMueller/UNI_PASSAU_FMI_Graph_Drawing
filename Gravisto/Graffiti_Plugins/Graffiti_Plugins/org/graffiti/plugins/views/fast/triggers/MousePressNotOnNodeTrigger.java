// =============================================================================
//
//   MousePressNotOnNodeTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMousePress;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MousePressNotOnNodeTrigger extends FastViewTrigger {
    public MousePressNotOnNodeTrigger(MousePressTrigger parent) {
        super(parent, "nonode");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMousePress emp = (ExtendedMousePress) userGesture;
        return !(emp.getElement() instanceof Node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
