// =============================================================================
//
//   ModularDecompositionNodeException.java
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
public class ModularDecompositionNodeException extends Exception {

    /**
     * 
     */
    public ModularDecompositionNodeException() {
       

    }

    /**
     * @param message
     */
    public ModularDecompositionNodeException(String message) {        
        super(message);

    }

    /**
     * @param cause
     */
    public ModularDecompositionNodeException(Throwable cause) {
        super(cause);

    }

    /**
     * @param message
     * @param cause
     */
    public ModularDecompositionNodeException(String message, Throwable cause) {
        super(message, cause);

    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
