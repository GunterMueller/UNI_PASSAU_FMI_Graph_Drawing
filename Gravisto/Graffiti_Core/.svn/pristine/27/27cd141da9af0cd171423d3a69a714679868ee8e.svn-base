// =============================================================================
//
//   ToolActivationTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.ToolActivationGesture4;
import org.graffiti.plugin.view.interactive.UserGesture;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ToolActivationTrigger extends FastViewTrigger {
    public ToolActivationTrigger(RootTrigger parent) {
        super(parent, "activation");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        return userGesture instanceof ToolActivationGesture4;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
