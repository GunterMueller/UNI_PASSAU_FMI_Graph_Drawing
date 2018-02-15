// =============================================================================
//
//   KeyPressTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.KeyPressGesture;
import org.graffiti.plugin.view.interactive.UserGesture;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class KeyPressTrigger extends FastViewTrigger {
    public KeyPressTrigger(KeyboardTrigger parent) {
        super(parent, "press");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        return userGesture instanceof KeyPressGesture;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
