// =============================================================================
//
//   InstanceCreationException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InstanceCreationException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * An exception, which is thrown, iff the instanciation of a given class fails.
 * 
 * @version $Revision: 5767 $
 */
public class InstanceCreationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5052589819208279218L;

    /**
     * Creates a new InstanceCreationException object.
     * 
     * @param msg
     *            DOCUMENT ME!
     */
    public InstanceCreationException(String msg) {
        super(msg);
    }

    /**
     * Creates a new InstanceCreationException object.
     * 
     * @param ex
     *            DOCUMENT ME!
     */
    public InstanceCreationException(Exception ex) {
        super(ex);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
