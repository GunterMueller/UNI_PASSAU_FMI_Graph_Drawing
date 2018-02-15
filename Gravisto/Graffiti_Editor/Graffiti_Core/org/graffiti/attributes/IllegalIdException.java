// =============================================================================
//
//   IllegalIdException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IllegalIdException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * The <code>IllegalIdException</code> will be thrown if a method tries to add
 * an attribute at a location where another attribute already exists.
 * 
 * @version $Revision: 5767 $
 */
public class IllegalIdException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 8310727638389056412L;

    /**
     * Constructs an <code>IllegalIdException</code> with the specified detail
     * message.
     * 
     * @param msg
     *            The detail message which is saved for later retrieval by the
     *            <code>getMessage()</code> method.
     */
    public IllegalIdException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
