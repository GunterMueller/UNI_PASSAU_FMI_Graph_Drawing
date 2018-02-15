// =============================================================================
//
//   ListenerNotFoundException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ListenerNotFoundException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * Will be thrown, if a method tries to deal with a listener that cannot be
 * found in the listener list.
 * 
 * @version $Revision: 5767 $
 */
public class ListenerNotFoundException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -7480920926138456854L;

    /**
     * Constructs a <code>ListenerNotFoundException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message for the exception.
     */
    public ListenerNotFoundException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
