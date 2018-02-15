// =============================================================================
//
//   MessageListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MessageListener.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

/**
 * Represents listener which gets messsages for theirs displaying on the GUI
 * components (e.g. status bar).
 * 
 * @version $Revision: 5768 $
 */
public interface MessageListener {

    /** Used for showing a normal status message with a defeault clear-delay */
    public static final int INFO = 0;

    /** Used for showing a status - error */
    public static final int ERROR = 1;

    /**
     * Used for status message that should not disappear after the default delay
     */
    public static final int PERMANENT_INFO = 2;

    // /**
    // * The constants specify GUI components where the messages recieved by
    // this
    // * listener have to be displayed.
    // */
    // public static final String STATUSBAR = "statusBar";
    //
    // /**
    // * Method <code>showMesssage</code> displays message on GUI components
    // * according to the specified type.
    // *
    // * @param message a message string to be displayed
    // * @param type a type of the message (e.g. ERROR)
    // * @param whereto a location for displaying this message
    // */
    // public void showMesssage(String message, int type, String whereto);

    /**
     * Method <code>showMesssage</code> displays a message on GUI components
     * according to the specified type.
     * 
     * @param message
     *            a message string to be displayed
     * @param type
     *            a type of the message (e.g. ERROR)
     */
    public void showMesssage(String message, int type);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
