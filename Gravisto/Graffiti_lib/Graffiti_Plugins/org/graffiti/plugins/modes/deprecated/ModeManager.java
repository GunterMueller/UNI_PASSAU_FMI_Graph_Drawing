// =============================================================================
//
//   ModeManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ModeManager.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import org.graffiti.managers.pluginmgr.PluginManagerListener;

/**
 * Provides an interface for a modes manager.
 * 
 * @version $Revision: 5766 $
 * 
 * @see org.graffiti.managers.pluginmgr.PluginManagerListener
 * @deprecated
 */
@Deprecated
public interface ModeManager extends PluginManagerListener {

    /**
     * Returns the specified mode from the list of modes.
     * 
     * @return the specified mode from the list of modes.
     */
    public Mode getMode(String mode);

    /**
     * Adds the specified mode to the list of modes this manager contains.
     * 
     * @param mode
     *            the mode to be added to the list.
     */
    public void addMode(Mode mode);

    /**
     * Removes the specified mode from the list of modes the manager contains.
     * 
     * @param mode
     *            the mode to be removed.
     */
    public void removeMode(Mode mode);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
