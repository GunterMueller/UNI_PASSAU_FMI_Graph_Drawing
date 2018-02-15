// =============================================================================
//
//   DefaultModeManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultModeManager.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.plugin.GenericPlugin;

/**
 * Handles the editor's modes.
 * 
 * @version $Revision: 5766 $
 * @deprecated
 */
@Deprecated
public class DefaultModeManager implements ModeManager {
    /** Maps the id of the mode to the corresponding instance of mode. */
    private Map<String, Mode> modes;

    /**
     * Constructs a new mode manager.
     */
    public DefaultModeManager() {
        modes = new HashMap<String, Mode>();
    }

    /*
     * @see org.graffiti.managers.ModeManager#getMode(java.lang.String)
     */
    public Mode getMode(String mode) {
        return modes.get(mode);
    }

    /**
     * Adds the given mode to the list of modes. <code>mode</code> may not be
     * <code>null</code>.
     */
    public void addMode(Mode mode) {
        assert mode != null;
        modes.put(mode.getId(), mode);
    }

    /*
     * @see
     * org.graffiti.managers.ModeManager#removeMode(org.graffiti.plugin.mode
     * .Mode)
     */
    public void removeMode(Mode mode) {
        throw new UnsupportedOperationException();
    }

    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
