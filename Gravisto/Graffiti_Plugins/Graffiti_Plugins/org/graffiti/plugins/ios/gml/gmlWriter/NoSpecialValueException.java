// =============================================================================
//
//   NoSpecialValueException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NoSpecialValueException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

/**
 * This exception it thrown whenever an attribute requires special treatment but
 * no special value could be generated.
 * 
 * @author ruediger
 */
public class NoSpecialValueException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 8486613231968947451L;

    /**
     * Constructs a new <code>NoSpecialValueException</code> from the specified
     * message.
     * 
     * @param message
     *            the message for this exception.
     */
    public NoSpecialValueException(String message) {
        super(message);
    }

    /**
     * Constructs a new <code>NoSpecialValueException</code> from the specified
     * message and the specified cause.
     * 
     * @param message
     *            the message for this exception.
     * @param cause
     *            the cause for this exception.
     */
    public NoSpecialValueException(String message, Throwable cause) {
        super(message, cause);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
