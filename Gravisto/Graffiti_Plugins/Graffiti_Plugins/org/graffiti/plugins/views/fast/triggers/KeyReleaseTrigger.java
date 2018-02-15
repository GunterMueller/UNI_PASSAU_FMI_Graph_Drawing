// =============================================================================
//
//   KeyReleaseTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.KeyReleaseGesture;
import org.graffiti.plugin.view.interactive.UserGesture;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class KeyReleaseTrigger extends FastViewTrigger {
    public KeyReleaseTrigger(KeyboardTrigger parent) {
        super(parent, "release");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        return userGesture instanceof KeyReleaseGesture;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
