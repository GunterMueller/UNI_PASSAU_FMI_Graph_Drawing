// =============================================================================
//
//   Serializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Serializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

/**
 * Defines a generic serializer which provides a set of extensions.
 */
public interface Serializer {

    /**
     * The file extensions the serializer can read or write.
     * 
     * @return DOCUMENT ME!
     */
    public String[] getExtensions();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
