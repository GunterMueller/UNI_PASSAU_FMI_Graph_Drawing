// =============================================================================
//
//   ToolPreferences.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The preferences of a {@code Tool}. Delegates to {@link Preferences}. Writes
 * to the preferences tree are automatically flushed. If a {@link Tool} has been
 * deleted, the changes are ignored.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ToolPreferences {
    /**
     * Denotes if the tool has been deleted so write requests are ignored.
     */
    private boolean isDeleted;

    /**
     * The preferences node where the the preferences are stored at.
     */
    private Preferences preferences;

    /**
     * Constructs a {@code ToolPreferences} object wrapping the specified
     * preferences node.
     * 
     * @param preferences
     *            the preferences node that stores the preferences of the tool.
     */
    ToolPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     * See {@link Preferences#put(String, String)}.
     */
    public void put(String key, String value) {
        if (isDeleted)
            return;
        preferences.put(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putBoolean(String, boolean)}.
     */
    public void putBoolean(String key, boolean value) {
        if (isDeleted)
            return;
        preferences.putBoolean(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putByteArray(String, byte[])}.
     */
    public void putByteArray(String key, byte[] value) {
        if (isDeleted)
            return;
        preferences.putByteArray(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putDouble(String, double)}.
     */
    public void putDouble(String key, double value) {
        if (isDeleted)
            return;
        preferences.putDouble(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putFloat(String, float)}.
     */
    public void putFloat(String key, float value) {
        if (isDeleted)
            return;
        preferences.putFloat(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putInt(String, int)}.
     */
    public void putInt(String key, int value) {
        if (isDeleted)
            return;
        preferences.putInt(key, value);
        flush();
    }

    /**
     * See {@link Preferences#putLong(String, long)}.
     */
    public void putLong(String key, long value) {
        if (isDeleted)
            return;
        preferences.putLong(key, value);
        flush();
    }

    /**
     * Is called when the tool, whose preferences are maintained by this object,
     * is deleted.
     */
    void delete() {
        isDeleted = true;
        try {
            preferences.removeNode();
        } catch (BackingStoreException e) {
        }
    }

    /**
     * Flushes the preferences.
     */
    private void flush() {
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
        }
    }

    /**
     * See {@link Preferences#get(String, String)}.
     */
    public String get(String key, String def) {
        return preferences.get(key, def);
    }

    /**
     * See {@link Preferences#getBoolean(String, boolean)}.
     */
    public boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    /**
     * See {@link Preferences#getByteArray(String, byte[])}.
     */
    public byte[] getByteArray(String key, byte[] def) {
        return preferences.getByteArray(key, def);
    }

    /**
     * See {@link Preferences#getDouble(String, double)}.
     */
    public double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }

    /**
     * See {@link Preferences#getFloat(String, float)}.
     */
    public float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }

    /**
     * See {@link Preferences#getInt(String, int)}.
     */
    public int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }

    /**
     * See {@link Preferences#getLong(String, long)}.
     */
    public long getLong(String key, long def) {
        return preferences.getLong(key, def);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
