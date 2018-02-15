// =============================================================================
//
//   DefaultPluginManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultPluginManager.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.graffiti.plugin.GenericPlugin;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;
import org.graffiti.util.PluginHelper;
import org.graffiti.util.ProgressViewer;
import org.graffiti.util.StringSplitter;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Manages the list of plugins.
 * 
 * @version $Revision: 5779 $
 */
public class DefaultPluginManager implements PluginManager {
    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(DefaultPluginManager.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Maps from a plugin name (<code>String</code>) to a plugin entry (
     * <code>Entry</code>).
     */
    private Hashtable<String, Entry> pluginEntries;

    private List<Entry> pluginEntriesSorted;

    /**
     * Holds the plugin entries of the last search. This avoids researching
     * everytime a dependent plugin is automatically searched.
     */
    private List<Entry> entries;

    /** The list of plugin manager listeners. */
    private List<PluginManagerListener> pluginManagerListeners;

    /** The preferences of the plugin manager. */
    private Preferences prefs;

    /**
     * Constructs a new <code>PluginManger</code> instance.
     * 
     * @param prefs
     *            the preferences, which contain information about what to load
     *            during the instanciation of the plugin manager.
     */
    public DefaultPluginManager(Preferences prefs) {
        this.prefs = prefs;
        this.pluginEntries = new Hashtable<String, Entry>();
        this.pluginEntriesSorted = new LinkedList<Entry>();
        this.pluginManagerListeners = new LinkedList<PluginManagerListener>();
    }

    /**
     * Checks if the plugin is already installed, i.e. if the plugin's name is
     * in the list of plugin entries.
     * 
     * @param name
     *            name of the plugin.
     * 
     * @return <code>true</code> if the plugin's name is in the list of plugin
     *         entries, <code>false</code> otherwise.
     */
    public boolean isInstalled(String name) {
        return pluginEntries.containsKey(name);
    }

    /**
     * Sets the <code>loadOnStartup</code> flag of the given object, to the
     * given value.
     * 
     * @param name
     *            the name of the plugin.
     * @param loadOnStartup
     *            <code>true</code>, if the plugin should be loaded at startup.
     */
    public void setLoadOnStartup(String name, Boolean loadOnStartup) {
        pluginEntries.get(name).setLoadOnStartup(loadOnStartup);
    }

    /**
     * Returns the current list of plugin entries.
     * 
     * @return a <code>Collection</code> containing all the plugin entries.
     */
    public Collection<Entry> getPluginEntries() {
        return pluginEntries.values();
    }

    /**
     * Returns the plugin instance of the given plugin name.
     * 
     * @param name
     *            the name of the plugin.
     * 
     * @return the instance of the plugin of the given name.
     */
    public GenericPlugin getPluginInstance(String name) {
        return pluginEntries.get(name).getPlugin();
    }

    /**
     * Adds the given plugin manager listener to the list of listeners.
     * 
     * @param listener
     *            the new listener to add to the list.
     */
    public void addPluginManagerListener(PluginManagerListener listener) {
        pluginManagerListeners.add(listener);
    }

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
     *                an error occured while loading or instantiating the
     *                plugin.
     */
    public GenericPlugin createInstance(URL pluginLocation)
            throws PluginManagerException {
        return createInstance(pluginLocation, null);
    }

    /**
     * Returns a new instance of the plugin &quot;main&quot; class with the
     * given plugin name. The progress made is displayed with progressViewer.
     * 
     * @param pluginLocation
     *            the URL to the plugin.
     * @param progressViewer
     *            the progress viewer that display the progress made
     * 
     * @return the instantiated plugin.
     * 
     * @exception PluginManagerException
     *                an error occured while loading or instantiating the
     *                plugin.
     */
    public GenericPlugin createInstance(URL pluginLocation,
            ProgressViewer progressViewer) throws PluginManagerException {
        PluginDescription description = PluginHelper
                .readPluginDescription(pluginLocation);

        GenericPlugin pluginInstance = createInstance(description,
                progressViewer);

        // // add the plugin to the list of instanciated plugins.
        // addPlugin(description, pluginInstance, pluginLocation, Boolean.TRUE);
        //
        // // inform all listeners about the new plugin.
        // firePluginAdded(pluginInstance, description);
        return pluginInstance;
    }

    /**
     * Loads the plugin from the given location.
     * 
     * @param description
     *            DOCUMENT ME!
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
    public void loadPlugin(PluginDescription description, URL pluginLocation,
            Boolean loadOnStartup) throws PluginManagerException {
        loadPlugins(new Entry[] { new DefaultPluginEntry(pluginLocation
                .toString(), description) });
    }

    /**
     * Loads the plugin from the given location.
     * 
     * @param plugins
     *            the plugin entries describing the plugins to be loaded
     * 
     * @exception PluginManagerException
     *                if an error occurs while loading or instantiating the
     *                plugin.
     */
    public void loadPlugins(Entry[] plugins) throws PluginManagerException {
        loadPlugins(plugins, null);
    }

    private boolean dependenciesSatisfied(PluginDescription pd) {
        List<Dependency> deps = pd.getDependencies();
        if (deps == null)
            return true;
        for (Dependency dep : deps) {
            if (!isInstalled(dep.getName()))
                return false;
        }
        return true;
    }

    /**
     * Loads the plugin from the given location.The progress made is displayed
     * with progressViewer.
     * 
     * @param plugins
     *            the plugin entries describing the plugins to be loaded
     * @param progressViewer
     *            the progress viewer that display the progress made
     * 
     * @exception PluginManagerException
     *                if an error occurs while loading or instantiating the
     *                plugin.
     */
    public void loadPlugins(Entry[] plugins, ProgressViewer progressViewer)
            throws PluginManagerException {
        loadPlugins(plugins, progressViewer, true);
    }

    public void loadPlugins(Entry[] plugins, ProgressViewer progressViewer,
            boolean topLevel) throws PluginManagerException {
        List<String> messages = new LinkedList<String>();

        for (int i = 0; i < plugins.length; i++) {
            if (plugins[i] == null
                    || isInstalled(plugins[i].getDescription().getName())) {
                continue;
            }
            PluginDescription desc = plugins[i].getDescription();
            List<Dependency> deps = desc.getDependencies();

            // avoid redundant lookup
            if (!dependenciesSatisfied(desc) && entries == null) {
                entries = new ClassPathPluginDescriptionCollector()
                        .collectPluginDescriptions();
            }

            boolean couldLoadDependencies = true;

            for (Dependency dep : deps) {
                if (!isInstalled(dep.getName())) {
                    for (Entry entry : entries) {
                        if (entry.getDescription().getName().equals(
                                dep.getName())) {
                            // successfully found a missing dep
                            // plugin
                            try {
                                loadPlugins(new Entry[] { entry },
                                        progressViewer, false);
                                break;
                            } catch (Exception e) {
                                couldLoadDependencies = false;
                            }
                        }
                    }
                }
            }

            if (couldLoadDependencies) {
                try {
                    addPlugin(desc, new URL(plugins[i].getFileName()),
                            Boolean.TRUE, progressViewer);
                } catch (Exception e) {
                    messages.add("Error during automatic"
                            + "dependency resolving: " + e);
                }
            } else {
                messages.add("Plugin " + desc.getName() + " could not be "
                        + "loaded since one or more dependencies are not "
                        + "satisfied:");

                for (Dependency dep : deps) {
                    if (!isInstalled(dep.getName())) {
                        messages.add("     " + dep.getName() + " ("
                                + dep.getMain() + ")");
                    }
                }
            }
        }

        if (topLevel) {
            savePrefs();
        }

        // build error string and throw exception
        if (!messages.isEmpty()) {
            String msg = "";

            for (String s : messages) {
                msg += s + "\n";
            }

            throw new PluginManagerException("exception.loadStartup\n", msg
                    .trim());
        }
    }

    /**
     * Loads the plugins which should be loaded on startup.
     * 
     * @exception PluginManagerException
     *                if an error occurred while loading one of the plugins.
     */
    public void loadStartupPlugins() throws PluginManagerException {
        loadStartupPlugins(null);
    }

    /**
     * Loads the plugins which should be loaded on startup. The progress made is
     * displayed with progressViewer.
     * 
     * @param progressViewer
     *            the progress viewer that display the progress made
     * 
     * @exception PluginManagerException
     *                if an error occurred while loading one of the plugins.
     */
    public void loadStartupPlugins(ProgressViewer progressViewer)
            throws PluginManagerException {
        // load the user's standard plugins
        int numberOfPlugins = prefs.getInt("numberOfPlugins", 0);

        // If available initialize the progressViewer
        if (progressViewer != null) {
            progressViewer.setMaximum(numberOfPlugins);
        }

        List<String> messages = new LinkedList<String>();

        Entry[] pluginEntries = new Entry[numberOfPlugins];

        int cnt = 0;

        for (int i = 1; i <= numberOfPlugins; i++) {
            String pluginLocation = prefs.get("pluginLocation" + i, null);

            if (pluginLocation != null) {
                try {
                    URL pluginUrl = new URL(pluginLocation);
                    PluginDescription desc = PluginHelper
                            .readPluginDescription(pluginUrl);
                    pluginEntries[cnt++] = new DefaultPluginEntry(
                            pluginLocation, desc);
                } catch (MalformedURLException mue) {
                    System.err.println(mue.getLocalizedMessage());
                    messages.add(mue.getMessage());
                }
            }
        }

        Throwable cause = null;

        try {
            loadPlugins(pluginEntries, progressViewer);
        } catch (PluginManagerException pme) {
            messages.add(pme.getMessage());
            cause = pme;
        }

        // collect info of all exceptions into one exception
        if (!messages.isEmpty()) {
            String msg = "";

            for (String s : messages) {
                msg += s + "\n";
            }

            throw new PluginManagerException("exception.loadStartup\n", msg
                    .trim(), cause);
        }
    }

    /**
     * Removes the given plugin manager listener from the list of listeners.
     * 
     * @param listener
     *            the listener to remove from the list of listeners.
     */
    public void removePluginManagerListener(PluginManagerListener listener) {
        boolean success = pluginManagerListeners.remove(listener);

        if (!success) {
            logger.warning("trying to remove a non existing"
                    + " plugin manager listener");
        }
    }

    /**
     * Saves the plugin manager's prefs.
     * 
     * @exception PluginManagerException
     *                if an error occurrs while saving the preferences.
     */
    public void savePrefs() throws PluginManagerException {
        try {
            // get rid of the old preferences ...
            prefs.clear();

            // search for all plugins, which should be loaded at startup
            // and put their urls into this list
            List<URL> plugins = new LinkedList<URL>();

            for (Entry e : pluginEntriesSorted) {
                if (e.getLoadOnStartup().equals(Boolean.TRUE)) {
                    plugins.add(e.getPluginLocation());
                }
            }

            // and write the new ones
            prefs.putInt("numberOfPlugins", plugins.size());

            int count = 1;

            for (URL url : plugins) {
                prefs.put("pluginLocation" + count, url.toString());
                count++;
            }
        } catch (Exception e) {
            throw new PluginManagerException("exception.SavePrefs", e
                    .getMessage());
        }
    }

    /**
     * Adds the given plugin file to the list of plugins. The progress made is
     * displayed with progressViewer.
     * 
     * @param description
     *            the name of the plugin's main class.
     * @param pluginLocation
     *            the location of the given plugin.
     * @param loadOnStartup
     *            <code>true</code> if the plugin should be loaded on the
     *            startup of the system, <code>false</code> otherwise.
     * @param progressViewer
     *            the progress viewer that display the progress made
     * 
     * @throws PluginManagerException
     *             DOCUMENT ME!
     */
    private void addPlugin(PluginDescription description,
            // GenericPlugin plugin,
            URL pluginLocation, Boolean loadOnStartup,
            ProgressViewer progressViewer) throws PluginManagerException {
        // assert plugin != null;
        assert description != null;

        // create an instance of the plugin's main class
        GenericPlugin plugin = createInstance(description, progressViewer);

        // // add the plugin to the list of instanciated plugins.
        // addPlugin(description, pluginInstance, pluginLocation,
        // loadOnStartup);
        DefaultPluginEntry dpe = new DefaultPluginEntry(description, plugin,
                loadOnStartup, pluginLocation);
        pluginEntriesSorted.add(dpe);
        pluginEntries.put(description.getName(), dpe);

        // inform all listeners about the new plugin.
        firePluginAdded(plugin, description);

        // construct the path for the plugin in the preferences
        // e.g. org.graffiti.plugins.io.graphviz.DOTSerializerPlugin becomes
        // org/graffiti/plugins/io/graphviz/DOTSerializerPlugin
        String[] strings = StringSplitter.split(description.getMain(), ".");
        StringBuffer pluginNode = new StringBuffer();

        for (int i = 0; i < strings.length; i++) {
            pluginNode.append(strings[i]);

            if (i < (strings.length - 1)) {
                pluginNode.append("/");
            }
        }

        Preferences pluginPrefs = prefs.node("pluginPrefs/"
                + pluginNode.toString());

        // configure the plugin's preferences
        if (plugin != null) {
            plugin.configure(pluginPrefs);
        }
    }

    /**
     * Creates an instance of the plugin from its description. The progress made
     * is displayed with progressViewer.
     * 
     * @param description
     *            the description of the plugin to be instantiated
     * @param progressViewer
     *            the progress viewer that display the progress made
     * 
     * @return the instantiated plugin.
     * 
     * @exception PluginManagerException
     *                an error occurrs while loading or instantiating the
     *                plugin.
     */
    private GenericPlugin createInstance(PluginDescription description,
            ProgressViewer progressViewer) throws PluginManagerException {
        // TODO check the plugin's dependencies
        // check, if the given plugin is alread in the list of
        // instanciated plugins
        if (isInstalled(description.getName()))
            // throw new
            // PluginManagerException("exception.pluginAlreadyInstanciated",
            // description.getName());
            return null;

        // If available show statustext to the user
        if (progressViewer != null) {
            progressViewer.setText("Loading " + description.getName() + "...");
        }

        GenericPlugin pluginInstance;

        try { // to instanciate the plugin's main class
            pluginInstance = (GenericPlugin) InstanceLoader
                    .createInstance(description.getMain());
        } catch (InstanceCreationException ice) {
            throw new PluginManagerException("exception.ClassNotFound", ice
                    .getMessage());
        }

        // update status (if available).
        if (progressViewer != null) {
            progressViewer.setText("Loading " + description.getName() + ": OK");
            progressViewer.setValue(progressViewer.getValue() + 1);
        }

        return pluginInstance;
    }

    /**
     * Registers the plugin as a plugin manager listener, if it is of instance
     * <code>PluginManagerListener</code> and calls the <code>pluginAdded</code>
     * in all plugin manager listeners.
     * 
     * @param plugin
     *            the added plugin.
     * @param desc
     *            the description of the added plugin.
     */
    private void firePluginAdded(GenericPlugin plugin, PluginDescription desc) {
        // register the plugin as a plugin manager listener, if needed
        if (plugin instanceof PluginManagerListener) {
            addPluginManagerListener((PluginManagerListener) plugin);
        }

        // Copy this list to prevent concurrent modification exceptions
        List<PluginManagerListener> listeners = new LinkedList<PluginManagerListener>(
                pluginManagerListeners);

        for (PluginManagerListener l : listeners) {
            if (plugin != null) {
                l.pluginAdded(plugin, desc);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
