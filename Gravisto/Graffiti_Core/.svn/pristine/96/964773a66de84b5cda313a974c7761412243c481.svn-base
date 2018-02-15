// =============================================================================
//
//   KeySlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.modes.fast.slots;

import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.Slot;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class KeyCodeSlot extends Slot<Integer> {
    public KeyCodeSlot(String id, String name, String description,
            int defaultValue) {
        super(id, name, Integer.class, description, null, defaultValue, false);
    }

    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putInt("value", defaultValue);
    }

    @Override
    public Integer loadValue(Preferences preferences) {
        return preferences.getInt("value", defaultValue);
    }

    @Override
    public void saveValue(Preferences preferences, Integer value) {
        preferences.putInt("value", value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
