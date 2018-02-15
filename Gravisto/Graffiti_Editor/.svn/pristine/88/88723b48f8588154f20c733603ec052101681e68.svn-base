// =============================================================================
//
//   UserGestureListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * Classes implementing {@code UserGestureListener} are interested in the
 * occurrence of user gestures. To actually be notified, {@code
 * UserGestureListener}s are usually added to the {@link UserGestureDispatcher}
 * returned by {@link InteractiveView#getUserGestureDispatcher()} of an
 * appropriate view.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface UserGestureListener {
    /**
     * Is called when a user gesture occurs in a view.
     * 
     * @param source
     *            the view the user gesture occurred in.
     * @param gesture
     *            the user gesture that was performed.
     */
    public void gesturePerformed(InteractiveView<?> source, UserGesture gesture);

    /**
     * Is called when the user canceled a sequence of user gestures.
     * 
     * @param source
     *            the currently active view.
     */
    public void canceled(InteractiveView<?> source);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
