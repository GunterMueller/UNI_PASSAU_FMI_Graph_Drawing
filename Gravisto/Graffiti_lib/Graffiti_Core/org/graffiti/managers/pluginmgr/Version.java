// =============================================================================
//
//   Version.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Version.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

/**
 * Represents an object, which contains a version number.
 * 
 * @author flierl
 * @version $Revision: 5767 $
 */
public class Version implements Comparable<Version> {

    /** The major version of the plugin. */
    String versionMajor;

    /** The minor version of the plugin. */
    String versionMinor;

    /** The release version of the plugin. */
    String versionRelease;

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Version o) {
        return 0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
