// =============================================================================
//
//   RestrictFailedExcpetion.java
//
//   Copyright (c) 2001-2011, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

/**
 * @author Manu
 * @version $Revision$ $Date$
 */
public class RestrictFailedExcpetion extends Exception {

    /**
     * 
     */
    public RestrictFailedExcpetion() {
    }

    /**
     * @param message
     */
    public RestrictFailedExcpetion(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public RestrictFailedExcpetion(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RestrictFailedExcpetion(String message, Throwable cause) {
        super(message, cause);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
