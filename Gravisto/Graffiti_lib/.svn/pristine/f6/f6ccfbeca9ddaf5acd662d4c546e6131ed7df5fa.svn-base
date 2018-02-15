// =============================================================================
//
//   BooleanSlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive.slots;

import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.Slot;

/**
 * Slot of type {@code Boolean}, which can store its value in a preferences
 * tree.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BooleanSlot extends Slot<Boolean> {
    /**
     * The default value of this slot.
     */
    private boolean defaultValue;

    /**
     * Constructs a {@code BooleanSlot}.
     * 
     * @param id
     *            the id of the slot.
     * @param name
     *            the name of the slot as seen by the user when graphically
     *            editing the tools.
     * @param description
     *            the description as seen by the user when graphically editing
     *            the tools.
     * @param defaultValue
     *            the default value.
     */
    public BooleanSlot(String id, String name, String description,
            boolean defaultValue) {
        super(id, name, Boolean.class, description);
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation stores its default value in the preferences tree at
     * the key {@code "value"}.
     */
    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putBoolean("value", defaultValue);
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation loads its value from the preferences tree at the key
     * {@code "value"}.
     */
    @Override
    public Boolean loadValue(Preferences preferences) {
        return preferences.getBoolean("value", defaultValue);
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation stores the specified value in the preferences tree at
     * the key {@code "value"}.
     */
    @Override
    public void saveValue(Preferences preferences, Boolean value) {
        preferences.putBoolean("value", value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
