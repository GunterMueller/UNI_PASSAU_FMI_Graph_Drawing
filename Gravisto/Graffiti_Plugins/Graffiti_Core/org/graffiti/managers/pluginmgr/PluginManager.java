// =============================================================================
//
//   PluginManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginManager.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.net.URL;
import java.util.Collection;

import org.graffiti.plugin.GenericPlugin;
import org.graffiti.util.ProgressViewer;

/**
 * Defines an interface for a plugin manager handeling the list of plugins.
 */
public interface PluginManager {
    /**
     * Returns <code>true</code>, if the plugin with the given name is in the
     * list of currently installed plugins.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isInstalled(String name);

    /**
     * Sets the <code>loadOnStartup</code> flag of the given object, to the
     * given value.
     * 
     * @param name
     *            the name of the plugin.
     * @param loadOnStartup
     *            <code>true</code>, if the plugin should be loaded at startup.
     */
    public void setLoadOnStartup(String name, Boolean loadOnStartup);

    /**
     * Returns the current list of plugin entries.
     * 
     * @return a <code>Collection</code> containing all the plugin entries.
     */
    public Collection<Entry> getPluginEntries();

    /**
     * Returns the plugin instance of the given plugin name.
     * 
     * @param name
     *            the name of the plugin.
     * 
     * @return the instance of the plugin of the given name.
     */
    public GenericPlugin getPluginInstance(String name);

    /**
     * Adds the given plugin manager listener to the list of listeners.
     * 
     * @param listener
     *            the new listener to add to the list.
     */
    public void addPluginManagerListener(PluginManagerListener listener);

    /**
     * Returns a new instance of the plugin &quot;main&quot; class with the
     * given plugin name.
     * 
     * @param pluginLocation
     *            the URL to the plugin.
     * 
     * @return the instantiated plugin.
     * 
     * @exception PluginManagerException
     *                if an error occured while loading or instantiating the
     *                plugin.
     */
    public GenericPlugin createInstance(URL pluginLocation)
            throws PluginManagerException;

    /**
     * Loads the plugin from the given location. If several plugins should be
     * loaded simultaniously, use <code>loadPlugins</code> instead, since
     * dependencies can then be checked successfully.
     * 
     * @param pluginLocation
     *            the location of the plugin.
     * @param loadOnStartup
     *            <code>true</code>, if the given plugin should be loaded at the
     *            startup.
     * 
     * @exception PluginManagerException
     *                if an error occurs while loading or instantiating the
     *                plugin.
     */
    public void loadPlugin(PluginDescription desc, URL pluginLocation,
            Boolean loadOnStartup) throws PluginManagerException;

    /**
     * Loads the plugins described by the given entries.
     * 
     * @param plugins
     * 
     * @throws PluginManagerException
     */
    public void loadPlugins(Entry[] plugins) throws PluginManagerException;

    /**
     * Loads the plugins which should be loaded on startup.
     * 
     * @exception PluginManagerException
     *                if an error occurred while loading one of the plugins.
     */
    public void loadStartupPlugins() throws PluginManagerException;

    /**
     * Loads the plugins which should be loaded on startup. The progress made is
     * display in progressViewer.
     * 
     * @param progressViewer
     *            A ProgressViewer that display the progress made while loading
     *            the plugins
     * 
     * @exception PluginManagerException
     *                if an error occurred while loading one of the plugins.
     */
    public void loadStartupPlugins(ProgressViewer progressViewer)
            throws PluginManagerException;

    /**
     * Removes the given plugin manager listener from the list of listeners.
     * 
     * @param listener
     *            the listener to remove from the list of listeners.
     */
    public void removePluginManagerListener(PluginManagerListener listener);

    /**
     * Saves the plugin manager's prefs.
     * 
     * @exception PluginManagerException
     *                if an error occurrs while saving the preferences.
     */
    public void savePrefs() throws PluginManagerException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
