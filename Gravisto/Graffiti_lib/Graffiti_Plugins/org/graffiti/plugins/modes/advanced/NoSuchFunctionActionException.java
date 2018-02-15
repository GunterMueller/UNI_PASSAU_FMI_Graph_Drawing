// =============================================================================
//
//   NoSuchFunctionActionException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NoSuchFunctionActionException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

/**
 * Is thrown if you add a function to a FunctionManager and the FunctionManager
 * can't fetch the Action assigned to the function.
 */
public class NoSuchFunctionActionException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1295044364234097277L;
    /** Name of the function */
    private String functionName;

    /**
     * Creates a new NoSuchFunctionActionException.
     * 
     * @param functionName
     *            name of the function where no Action could be found for
     */
    public NoSuchFunctionActionException(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Returns the name of the function no Action could be found for.
     * 
     * @return name of the function no Action could be found for
     */
    public String getFunction() {
        return functionName;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
