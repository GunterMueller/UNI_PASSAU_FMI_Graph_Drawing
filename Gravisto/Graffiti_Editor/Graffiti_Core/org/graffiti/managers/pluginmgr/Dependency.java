// =============================================================================
//
//   Dependency.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Dependency.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

/**
 * Interface for dependency of a plugin.
 * 
 * @version $Revision: 5767 $
 */
public interface Dependency {

    /**
     * Returns the main.
     * 
     * @return String
     */
    public String getMain();

    // /**
    // * Returns <code>true</code>, if the dependency is satisfied.
    // */
    // public abstract boolean isSatisfied();

    /**
     * Returns the name.
     * 
     * @return String
     */
    public String getName();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
