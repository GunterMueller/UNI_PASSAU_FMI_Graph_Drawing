// =============================================================================
//
//   InvalidInputEventException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InvalidInputEventException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

/**
 * Exception which is thrown if one feeds {@link FunctionManager} with an
 * input-event with invalid syntax.
 */
public class InvalidInputEventException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8947356341226623700L;

    /** Input-event which has invalid syntax */
    private String event;

    /** Function someone tried to assign to the event */
    private String function;

    /**
     * Constructs a new InvalidInputEventException using the given information.
     * 
     * @param event
     *            input-event which has invalid syntax
     * @param function
     *            function someone tried to assign to the event
     */
    public InvalidInputEventException(String event, String function) {
        this.event = event;
        this.function = function;
    }

    /**
     * The input-event with invalid syntax.
     * 
     * @return the input-event with invalid syntax.
     */
    public String getEvent() {
        return event;
    }

    /**
     * Function someone tried to assign to the input-event
     * 
     * @return function someone tried to assign to the input-event
     */
    public String getFunction() {
        return function;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
