// =============================================================================
//
// BindingInfo.java
//
//   Copyright (c) 2004 Graffiti Team, Uni Passau
//
// =============================================================================
// $Id: BindingInfo.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.KeyStroke;

/**
 * Helper-class storing information about the key- and mouse-bindings of a
 * Function.
 */
class BindingInfo {
    /** Set of KeyStrokes causing <function> being executed */
    private Set<KeyStroke> keyStrokes = new HashSet<KeyStroke>();

    /** Set of MouseListeners causing <function> being executed */
    private Set<MouseListener> mouseListeners = new HashSet<MouseListener>();

    /** Set of MouseMotionListeners causing <function> being executed */
    private Set<MouseMotionListener> mouseMotionListeners = new HashSet<MouseMotionListener>();

    /**
     * Adds a KeyStroke which causes <function> being executed to this object
     * 
     * @param keyStroke
     *            any KeyStroke
     */
    public void addKeyStroke(KeyStroke keyStroke) {
        keyStrokes.add(keyStroke);
    }

    /**
     * Returns an iterator over all KeyStrokes stored in this class which cause
     * <function> being executed
     * 
     * @return an iterator over KeyStrokes as described
     */
    public Iterator<KeyStroke> getKeyStrokeIterator() {
        return keyStrokes.iterator();
    }

    /**
     * Adds a MouseListener causing <function> to this object.
     * 
     * @param mouseListener
     *            any MouseListener
     */
    public void addMouseListener(MouseListener mouseListener) {
        mouseListeners.add(mouseListener);
    }

    /**
     * Adds a MouseMotionListener causing <function> to this object
     * 
     * @param listener
     *            any MouseListener
     */
    public void addMouseMotionListener(MouseMotionListener listener) {
        mouseMotionListeners.add(listener);
    }
}
