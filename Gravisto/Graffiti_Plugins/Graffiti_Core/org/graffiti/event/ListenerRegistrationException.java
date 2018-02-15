// =============================================================================
//
//   ListenerRegistrationException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ListenerRegistrationException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * In general, the exception is used to indicate that a listener could not be
 * registered. Will be thrown, if someone tries to add a strict listener while
 * the same listener is already registered as non strict or vice versa.
 * 
 * @version $Revision: 5767 $
 */
public class ListenerRegistrationException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -4779718430076850252L;

    /**
     * Constructs a <code>ListenerRegistrationException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the detail message for the exception.
     */
    public ListenerRegistrationException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
