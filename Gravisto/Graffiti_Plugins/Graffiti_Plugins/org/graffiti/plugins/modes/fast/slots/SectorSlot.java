// =============================================================================
//
//   SectorHandlingSlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.modes.fast.slots;

import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.Sector;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SectorSlot extends Slot<Sector> {
    public SectorSlot(String id, String name, String description) {
        super(id, name, Sector.class, description, Sector.IGNORE);
    }

    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putInt("value", Sector.IGNORE.ordinal());
    }

    @Override
    public Sector loadValue(Preferences preferences) {
        return Sector.values()[preferences.getInt("value", Sector.IGNORE
                .ordinal())];
    }

    @Override
    public void saveValue(Preferences preferences, Sector value) {
        preferences.putInt("value", value.ordinal());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
