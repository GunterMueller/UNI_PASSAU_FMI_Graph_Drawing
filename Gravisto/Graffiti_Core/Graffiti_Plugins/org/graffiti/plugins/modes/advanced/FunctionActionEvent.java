// =============================================================================
//
//   FunctionActionEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FunctionActionEvent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.awt.event.ActionEvent;

/**
 * Specialised version of ActionEvent used within the function-concept. Unlike
 * ActionEvent, it can store a position linked to the event which caused the
 * FunctionActionEvent.
 */
public class FunctionActionEvent extends ActionEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 5706183419758103461L;
    /** Position linked to the event which caused this FunctionActionEvent. */
    private Point position;

    /**
     * Constructs a new FunctionActionEvent using the given information.
     * 
     * @param source
     *            source which caused the FunctionActionEvent
     * @param id
     *            some id-value
     * @param command
     *            command which caused the FunctionActionEvent
     * @param when
     *            time-stamp
     * @param modifiers
     *            some information about modifiers
     * @param position
     *            position linked to the event which caused this
     *            FunctionActionEvent
     */
    public FunctionActionEvent(Object source, int id, String command,
            long when, int modifiers, Point position) {
        super(source, id, command, when, modifiers);
        this.position = position;
    }

    /**
     * Constructs a new FunctionActionEvent using the given information.
     * 
     * @param source
     *            source which caused the FunctionActionEvent
     * @param id
     *            some id-value
     * @param command
     *            command which caused the FunctionActionEvent
     * @param position
     *            position linked to the event which caused this
     *            FunctionActionEvent
     */
    public FunctionActionEvent(Object source, int id, String command,
            Point position) {
        // Set time-stamp?
        this(source, id, command, 0, 0, position);
    }

    /**
     * Returns the position linked to the event which caused this
     * FunctionActionEvent.
     * 
     * @return position linked to the event which caused this
     *         FunctionActionEvent
     */
    public Point getPosition() {
        return position;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
