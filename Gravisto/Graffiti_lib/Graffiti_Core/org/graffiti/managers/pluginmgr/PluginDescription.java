// =============================================================================
//
//   PluginDescription.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginDescription.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Contains a meta data of a plugin.
 * 
 * @version $Revision: 5779 $
 */
public class PluginDescription {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(PluginDescription.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The list of <code>Dependency</code> objects of this plugin. */
    private List<Dependency> dependencies;

    /** The author of the plugin. */
    private String author;

    /**
     * An URL or short description about the location of this plugin in the
     * internet.
     */
    private String available;

    /** A short (american english) description of this plugin. */
    private String description;

    /**
     * The name of the plugin class, which implements the
     * <code>GenericPlugin</code> interface.
     * 
     * @see org.graffiti.plugin.GenericPlugin
     */
    private String main;

    /** The name of the plugin. */
    private String name;

    /** The version of this plugin. */
    private String version;

    /**
     * Constructs an empty plugin description,
     */
    public PluginDescription() {
        dependencies = new LinkedList<Dependency>();
    }

    /**
     * Sets the author.
     * 
     * @param author
     *            The author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the author.
     * 
     * @return String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the available.
     * 
     * @param available
     *            The available to set
     */
    public void setAvailable(String available) {
        this.available = available;
        logger.fine("available set to: " + available);
    }

    /**
     * Returns the available.
     * 
     * @return String
     */
    public String getAvailable() {
        return available;
    }

    /**
     * Sets the dependencies.
     * 
     * @param dependencies
     *            The dependencies to set
     */
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Returns the dependencies.
     * 
     * @return List
     */
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            The description to set
     */
    public void setDescription(String description) {
        this.description = description;
        logger.fine("description set to: " + description);
    }

    /**
     * Returns the description.
     * 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the main.
     * 
     * @param main
     *            The main to set
     */
    public void setMain(String main) {
        this.main = main;
        logger.fine("main set to: " + main);
    }

    /**
     * Returns the main.
     * 
     * @return String
     */
    public String getMain() {
        return main;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    public void setName(String name) {
        this.name = name;
        logger.fine("name set to: " + name);
    }

    /**
     * Returns the name.
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an iterator over the plugin dependency list.
     * 
     * @return an iterator over the plugin dependency list.
     */
    public Iterator<Dependency> getPluginDependenciesIterator() {
        return dependencies.iterator();
    }

    /**
     * Sets the version.
     * 
     * @param version
     *            The version to set
     */
    public void setVersion(String version) {
        this.version = version;
        logger.fine("version set to: " + version);
    }

    /**
     * Returns the version.
     * 
     * @return Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Adds the given dependency to the list of dependencies.
     * 
     * @param dep
     *            the dependency to add to the list of dependent plugins.
     */
    public void addDependency(PluginDependency dep) {
        dependencies.add(dep);
    }

    /**
     * Adds the given plugin dependency to the list of dependencies.
     * 
     * @param dep
     *            the dependency to add to the list.
     */
    public void addPluginDependency(PluginDependency dep) {
        dependencies.add(dep);
    }

    /**
     * Returns a human readable string representation of this object.
     * 
     * @return a human readable string representation of this object.
     */
    @Override
    public String toString() {
        return "[name = " + name + ", version = " + version + ", " + " main = "
                + main + ", number of dependencies = " + dependencies.size()
                + "]";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
