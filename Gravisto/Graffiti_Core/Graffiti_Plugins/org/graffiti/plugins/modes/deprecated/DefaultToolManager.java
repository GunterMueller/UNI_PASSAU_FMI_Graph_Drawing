// =============================================================================
//
//   DefaultToolManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultToolManager.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.util.HashSet;
import java.util.Set;

import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.plugin.GenericPlugin;

/**
 * Manages the list of tools.
 * 
 * @version $Revision: 5766 $
 * @deprecated
 */
@Deprecated
public class DefaultToolManager implements ToolManager {

    /** List of all available tools. */
    private Set<Tool> tools;

    /**
     * Constructs a new tool manager.
     */
    public DefaultToolManager(ModeManager modeManager) {
        tools = new HashSet<Tool>();
    }

    /*
     * @see
     * org.graffiti.managers.ToolManager#addTool(org.graffiti.plugin.tool.Tool)
     */
    public void addTool(Tool tool) {
        tools.add(tool);
    }

    /*
     * @see
     * org.graffiti.managers.pluginmgr.PluginManagerListener#pluginAdded(org
     * .graffiti.plugin.GenericPlugin,
     * org.graffiti.managers.pluginmgr.PluginDescription)
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
