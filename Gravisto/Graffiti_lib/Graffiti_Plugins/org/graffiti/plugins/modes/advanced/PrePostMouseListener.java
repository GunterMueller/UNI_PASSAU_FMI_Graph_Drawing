// =============================================================================
//
//   PrePostMouseListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PrePostMouseListener.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.MouseInputListener;

/**
 * MouseInputListener which stores a set of MouseInputListeners to be notified
 * whenever one mouse-event (like mousePressed) occurs. Performs the calls to
 * the beforeEvent and afterEvent-methods of a given FunctionComponent.
 */
class PrePostMouseListener implements MouseInputListener {

    /**
     * The beforeEvent/afterEvent-methods of this FunctionComponent are called
     * by the mouseXXX-methods of this class
     */
    private FunctionComponent functionComponent;

    /**
     * Set of MouseListeners to be notified by the mouseXXX-methods of this
     * class.
     */
    private Set<MouseListener> mouseListeners = new HashSet<MouseListener>();

    /**
     * Set of MouseMotionListeners to be notified by the mouseXXX-methods of
     * this class.
     */
    private Set<MouseMotionListener> mouseMotionListeners = new HashSet<MouseMotionListener>();

    /**
     * Constructs a new PrePostMouseListeners. Takes the FunctionComponent, on
     * which the beforeEvent/afterEvent-methods should be called, as argument.
     * This is (usually) the FunctionComponent handling the functions the
     * listeners to be added to this object are assigned to.
     * 
     * @param functionComponent
     *            FunctionComponent to be used for the
     *            beforeEvent/afterEvent-calls
     */
    PrePostMouseListener(FunctionComponent functionComponent) {
        this.functionComponent = functionComponent;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseClicked(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseListener l : mouseListeners) {
            l.mouseClicked(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseDragged(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseMotionListener l : mouseMotionListeners) {
            l.mouseDragged(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseEntered(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseListener l : mouseListeners) {
            l.mouseEntered(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseExited(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseListener l : mouseListeners) {
            l.mouseExited(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseMoved(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseMotionListener l : mouseMotionListeners) {
            l.mouseMoved(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mousePressed(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseListener l : mouseListeners) {
            l.mousePressed(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void mouseReleased(MouseEvent e) {
        functionComponent.beforeEvent(e.getPoint());

        for (MouseListener l : mouseListeners) {
            l.mouseReleased(e);
        }

        functionComponent.afterEvent(e.getPoint());
    }

    /**
     * Adds a MouseListener to the set of MouseListeners to be notified whenever
     * this object is notified of a mouseXXX-event.
     * 
     * @param mouseListener
     *            any MouseListener
     */
    void addMouseListener(MouseListener mouseListener) {
        mouseListeners.add(mouseListener);
    }

    /**
     * Adds a MouseMotionListener to the set of MouseMotionListeners to be
     * notified whenever this object is notified of a mouseMoved or
     * mouseDragged-event.
     * 
     * @param mouseMotionListener
     *            any MouseMotionListener
     */
    void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        mouseMotionListeners.add(mouseMotionListener);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
