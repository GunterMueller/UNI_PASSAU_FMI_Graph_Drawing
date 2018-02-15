// =============================================================================
//
//   MouseButtonGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User gesture that involves a mouse button. It is provided for convenience and
 * its use by {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see UserGesture
 */
public class MouseButtonGesture extends MouseGesture {
    /**
     * The mouse button that was pressed or released or that was in pressed
     * state while the mouse was moved.
     */
    private MouseButton button;

    /**
     * Constructs a {@code MouseButtonGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     * @param buttonId
     *            the id of the button that was pressed or released, as used by
     *            {@link MouseEvent#getButton()}.
     */
    protected MouseButtonGesture(Point2D position, int modifierMask,
            int buttonId) {
        super(position, modifierMask);
        switch (buttonId) {
        case MouseEvent.BUTTON1:
            button = MouseButton.LEFT;
            break;
        case MouseEvent.BUTTON2:
            button = MouseButton.MIDDLE;
            break;
        case MouseEvent.BUTTON3:
            button = MouseButton.RIGHT;
            break;
        }
    }

    /**
     * Constructs a {@code MouseButtonGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     * @param button
     *            the mouse button that was pressed or released.
     */
    protected MouseButtonGesture(Point2D position, int modifierMask,
            MouseButton button) {
        super(position, modifierMask);
        this.button = button;
    }

    /**
     * Returns the mouse button that was pressed or released.
     * 
     * @return the mouse button that was pressed or released.
     */
    public MouseButton getButton() {
        return button;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
