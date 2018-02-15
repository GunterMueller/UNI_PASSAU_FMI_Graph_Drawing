// =============================================================================
//
//   Entry.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Entry.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.net.URL;

import org.graffiti.plugin.GenericPlugin;

/**
 * An entry in the plugin manager.
 * 
 * @see PluginManager
 */
public interface Entry {

    /**
     * Sets the description.
     * 
     * @param description
     *            the description to be set.
     */
    public void setDescription(PluginDescription description);

    /**
     * Returns the plugin description of this entry.
     * 
     * @return the plugin description of this entry.
     */
    public PluginDescription getDescription();

    /**
     * Returns the file name of the plugin.
     * 
     * @return the file name of the plugin.
     */
    public String getFileName();

    /**
     * Sets the flag indicating whether a plugin should be loaded on the startup
     * of the editor.
     * 
     * @param loadOnStartup
     *            the flag indicating whether a plugin should be loaded on the
     *            startup of the editor.
     */
    public void setLoadOnStartup(Boolean loadOnStartup);

    /**
     * Returns <code>true</code>, if the plugin should be loaded at startup.
     * 
     * @return <code>true</code>, if the plugin should be loaded at startup.
     */
    public Boolean getLoadOnStartup();

    /**
     * Sets the plugin.
     * 
     * @param plugin
     *            the plugin to be set.
     */
    public void setPlugin(GenericPlugin plugin);

    /**
     * Returns the plugin of this entry.
     * 
     * @return the plugin of this entry.
     */
    public GenericPlugin getPlugin();

    /**
     * Sets the plugin location.
     * 
     * @param pluginLocation
     *            plugin location to be set.
     */
    public void setPluginLocation(URL pluginLocation);

    /**
     * Returns the location of the plugin.
     * 
     * @return the location of the plugin.
     */
    public URL getPluginLocation();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
