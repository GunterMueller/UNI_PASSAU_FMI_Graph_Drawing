// =============================================================================
//
//   ModifierHandlingSlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.modes.fast.slots;

import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.ModifierHandling;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ModifierHandlingSlot extends Slot<ModifierHandling> {
    public ModifierHandlingSlot(String id, String name, String description) {
        super(id, name, ModifierHandling.class, description,
                ModifierHandling.IGNORE);
    }

    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putInt("value", ModifierHandling.IGNORE.ordinal());
    }

    @Override
    public ModifierHandling loadValue(Preferences preferences) {
        return ModifierHandling.values()[preferences.getInt("value",
                ModifierHandling.IGNORE.ordinal())];
    }

    @Override
    public void saveValue(Preferences preferences, ModifierHandling value) {
        preferences.putInt("value", value.ordinal());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
