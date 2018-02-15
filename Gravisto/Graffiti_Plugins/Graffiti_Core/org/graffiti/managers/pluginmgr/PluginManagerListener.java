// =============================================================================
//
//   PluginManagerListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginManagerListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import org.graffiti.plugin.GenericPlugin;

/**
 * Represents a listener, which is called, iff a plugin has been added to the
 * plugin manager.
 * 
 * @version $Revision: 5767 $
 */
public interface PluginManagerListener {

    /**
     * Called by the plugin manager, iff a plugin has been added.
     * 
     * @param plugin
     *            the added plugin.
     * @param desc
     *            the description of the new plugin.
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
