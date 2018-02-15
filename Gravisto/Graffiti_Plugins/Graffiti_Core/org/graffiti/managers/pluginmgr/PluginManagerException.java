// =============================================================================
//
//   PluginManagerException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginManagerException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import org.graffiti.core.Bundle;

/**
 * <code>PluginManagerException</code> is thrown, iff an error occured during
 * the loading of a plugin.
 * 
 * @version $Revision: 5767 $
 */
public class PluginManagerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2456307809143961111L;

    /** The <code>Bundle</code> of the exception. */
    protected static final Bundle bundle = Bundle.getCoreBundle();

    /** DOCUMENT ME! */
    private Dependency dependency;

    /**
     * Constructor for PluginManagerException.
     * 
     * @param key
     *            the error message of this exception.
     */
    public PluginManagerException(String key) {
        super(bundle.getString(key));
    }

    /**
     * Constructs a plugin manager exception from the given parameters.
     * 
     * @param key
     *            the property key in the plugin manager's resource bundle.
     * @param message
     *            the additional message of the exception.
     */
    public PluginManagerException(String key, String message) {
        super(bundle.getString(key) + message);
    }

    /**
     * Constructs a plugin manager exception from the given parameters.
     * 
     * @param key
     *            the property key in the plugin manager's resource bundle.
     * @param message
     *            the additional message of the exception.
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link Throwable#getCause()} method). (A null value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public PluginManagerException(String key, String message, Throwable cause) {
        super(bundle.getString(key) + message, cause);
    }

    /**
     * Sets the dependency.
     * 
     * @param dependency
     *            The dependency to set
     */
    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    /**
     * Returns the dependency.
     * 
     * @return Dependency
     */
    public Dependency getDependency() {
        return dependency;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
