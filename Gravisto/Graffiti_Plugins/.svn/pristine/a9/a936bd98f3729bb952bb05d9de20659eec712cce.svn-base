// =============================================================================
//
//   MouseButtonSlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.modes.fast.slots;

import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.MouseButton;
import org.graffiti.plugin.view.interactive.Slot;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseButtonSlot extends Slot<MouseButton> {
    public MouseButtonSlot(String id, String name, String description,
            MouseButton defaultValue) {
        super(id, name, MouseButton.class, description, defaultValue);
    }

    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putInt("value", defaultValue.ordinal());
    }

    @Override
    public MouseButton loadValue(Preferences preferences) {
        return MouseButton.values()[preferences.getInt("value", defaultValue
                .ordinal())];
    }

    @Override
    public void saveValue(Preferences preferences, MouseButton value) {
        preferences.putInt("value", value.ordinal());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
