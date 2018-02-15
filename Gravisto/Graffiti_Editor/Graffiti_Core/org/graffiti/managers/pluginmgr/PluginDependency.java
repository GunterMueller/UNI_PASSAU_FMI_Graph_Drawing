// =============================================================================
//
//   PluginDependency.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginDependency.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.util.logging.Logger;

import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Models a dependency to another plugin.
 * 
 * @version $Revision: 5779 $
 */
public class PluginDependency implements Dependency {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(PluginDependency.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The author of the plugin. */
    private String author;

    /**
     * An URL or short description about the location of this plugin in the
     * internet.
     */
    private String available;

    /** The name of the main class of the plugin dependency. */
    private String main;

    // /**
    // * Constructs a new plugin dependency.
    // */
    // public PluginDependency() {
    // }

    /** The name of the plugin, this dependency depends on. */
    private String name;

    /** The version of this plugin. */
    private String version;

    /**
     * Sets the author.
     * 
     * @param author
     *            The author to set
     */
    public void setAuthor(String author) {
        this.author = author;
        logger.fine("author set to: " + author);
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
     * @return String
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns a human readable string of this object.
     * 
     * @return a human readable string representation of this object.
     */
    @Override
    public String toString() {
        return "[name = " + name + ",main = " + main + "]";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
