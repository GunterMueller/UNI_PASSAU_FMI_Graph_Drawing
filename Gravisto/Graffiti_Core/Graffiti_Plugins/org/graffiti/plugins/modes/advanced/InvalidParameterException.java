// =============================================================================
//
//   InvalidParameterException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InvalidParameterException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

/**
 * Thrown, (1) if you try to set a function-parameter and no parameter with that
 * name exists, or (2) if you try to set a function-parameter to a non-existent
 * value.
 */
public class InvalidParameterException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 9119112825776723450L;
    /** Detailed information on what went wrong */
    private String message;

    /**
     * Constructs a new InvalidParameterException with the given message.
     * 
     * @param message
     *            detailed information on what went wrong
     */
    public InvalidParameterException(String message) {
        this.message = message;
    }

    /**
     * Returns detailed information on what went wrong.
     * 
     * @return detailed information on what went wrong
     */
    @Override
    public String getMessage() {
        return message;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
