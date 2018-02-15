// =============================================================================
//
//   InvalidMagneticFieldException.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InvalidMagneticFieldException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Exception if a magnetic field should be created, which does not work
 * 
 * @author matzeder
 */
public class InvalidMagneticFieldException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 8959302670449554674L;

    /**
     * Creates a new InvalidMagneticFieldException
     * 
     */
    public InvalidMagneticFieldException() {
        super();
    }

    /**
     * Creates a new InvalidMagneticFieldException with a message.
     * 
     * @param message
     *            The message of the exception.
     */
    public InvalidMagneticFieldException(String message) {
        super(message);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
