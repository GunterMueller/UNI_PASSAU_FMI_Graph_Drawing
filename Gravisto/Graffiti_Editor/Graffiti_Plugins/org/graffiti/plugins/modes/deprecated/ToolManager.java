// =============================================================================
//
//   ToolManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ToolManager.java 1002 2006-01-03 13:21:54Z forster $

package org.graffiti.plugins.modes.deprecated;

import org.graffiti.managers.pluginmgr.PluginManagerListener;

/**
 * An interface for managing a list of tools.
 * 
 * @version $Revision: 1002 $
 * 
 * @see org.graffiti.managers.pluginmgr.PluginManagerListener
 * @deprecated
 */
@Deprecated
public interface ToolManager extends PluginManagerListener {

    /**
     * Adds the specified tool to the list of tools of this manager.
     * 
     * @param tool
     *            the tool to be added.
     */
    public void addTool(Tool tool);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
