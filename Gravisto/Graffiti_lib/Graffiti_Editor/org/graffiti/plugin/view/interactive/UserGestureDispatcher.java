// =============================================================================
//
//   UserGestureDispatcher.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.LinkedList;

/**
 * A {@code UserGestureDispatcher} receives {@code UserGesture}s and broadcasts
 * them to interested listeners.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class UserGestureDispatcher implements UserGestureListener {
    /**
     * The listeners interested in the user gestures.
     */
    private LinkedList<UserGestureListener> listeners;

    /**
     * Constructs a {@code UserGestureDispatcher}.
     */
    public UserGestureDispatcher() {
        listeners = new LinkedList<UserGestureListener>();
    }

    /**
     * Adds the specified {@code UserGestureListener} listener, which will be
     * notified when a user gesture is performed. If a listener is added
     * multiple times, it will be notified accordingly multiple times in
     * response to each single user gesture.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(UserGestureListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the first occurrence of the specified listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(UserGestureListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc} Calls
     * {@link UserGestureListener#gesturePerformed(InteractiveView, UserGesture)}
     * on each listener that was previously added to this dispatcher. If a
     * listener was added multiple times, it will be notified accordingly
     * multiple times.
     */
    public void gesturePerformed(InteractiveView<?> source, UserGesture gesture) {
        for (UserGestureListener listener : listeners) {
            listener.gesturePerformed(source, gesture);
        }
    }

    /**
     * {@inheritDoc} Calls {@link UserGestureListener#canceled(InteractiveView)}
     * on each listener that was previously added to this dispatcher. If a
     * listener was added multiple times, it will be notified accordingly
     * multiple times.
     */
    public void canceled(InteractiveView<?> source) {
        for (UserGestureListener listener : listeners) {
            listener.canceled(source);
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
