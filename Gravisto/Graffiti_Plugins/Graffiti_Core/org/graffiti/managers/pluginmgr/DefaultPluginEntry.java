// =============================================================================
//
//   DefaultPluginEntry.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultPluginEntry.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.net.URL;

import org.graffiti.plugin.GenericPlugin;

/**
 * Represents a plugin entry in the plugin manager.
 */
public class DefaultPluginEntry implements Entry {

    /**
     * <code>true</code> if the plugin should be loaded on startup,
     * <code>false</code> otherwise.
     */
    private Boolean loadOnStartup;

    /** The plugin itself. */
    private GenericPlugin plugin;

    /** The description of the plugin */
    private PluginDescription description;

    /** The file name of the plugin. */
    private String fileName;

    /** The location of the plugin. */
    private URL pluginLocation;

    /**
     * Constructs a new plugin entry.
     * 
     * @param description
     *            the description of this plugin
     * @param plugin
     *            the plugin.
     * @param loadOnStartup
     *            <code>true</code> if the plugin should be loaded at the
     *            startup of the plugin manager.
     * @param pluginLocation
     *            the location of the plugin.
     */
    public DefaultPluginEntry(PluginDescription description,
            GenericPlugin plugin, Boolean loadOnStartup, URL pluginLocation) {
        this.description = description;
        this.plugin = plugin;
        this.pluginLocation = pluginLocation;
        this.loadOnStartup = loadOnStartup;
    }

    /**
     * Constructs a new plugin entry.
     * 
     * @param fileName
     *            the file name of the plugin
     * @param description
     *            the description of this plugin
     */
    public DefaultPluginEntry(String fileName, PluginDescription description) {
        this.fileName = fileName;
        this.description = description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the description to be set.
     */
    public void setDescription(PluginDescription description) {
        this.description = description;
    }

    /**
     * Returns the description of the plugin.
     * 
     * @return the description of the plugin.
     */
    public PluginDescription getDescription() {
        return description;
    }

    /**
     * Returns the file name of the plugin.
     * 
     * @return DOCUMENT ME!
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the flag indicating whether a plugin should be loaded on the startup
     * of the editor.
     * 
     * @param loadOnStartup
     *            flag indicating whether a plugin should be loaded on the
     *            startup of the editor.
     */
    public void setLoadOnStartup(Boolean loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    /**
     * Returns <code>true</code> if the plugin shall be loaded on the startup of
     * the editor, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the plugin shall be loaded on the startup of
     *         the editor, <code>false</code> otherwise.
     */
    public Boolean getLoadOnStartup() {
        return loadOnStartup;
    }

    /**
     * Sets the plugin.
     * 
     * @param plugin
     *            the plugin to be set.
     */
    public void setPlugin(GenericPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the plugin.
     * 
     * @return the plugin.
     */
    public GenericPlugin getPlugin() {
        return plugin;
    }

    /**
     * Sets the plugin location.
     * 
     * @param pluginLocation
     *            plugin location to be set.
     */
    public void setPluginLocation(URL pluginLocation) {
        this.pluginLocation = pluginLocation;
    }

    /**
     * Returns the plugin location.
     * 
     * @return the plugin location.
     */
    public URL getPluginLocation() {
        return pluginLocation;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
