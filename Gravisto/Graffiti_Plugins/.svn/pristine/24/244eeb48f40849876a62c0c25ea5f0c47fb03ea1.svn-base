// =============================================================================
//
//   ModifiableUserGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.event.InputEvent;

/**
 * Represents a user gesture that is aware of the state of the modal keys and
 * mouse buttons. It is provided for convenience and its use by
 * {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see InputEvent#getModifiersEx()
 */
public class ModifiableUserGesture implements UserGesture {
    /**
     * The bit set of the modifiers. Like the extended modified mask of
     * InputEvent.
     */
    private int modifierMask;

    /**
     * Constructs a modifiable user gesture.
     * 
     * @param isAltDown
     *            specifies whether the Alt modifier is down on this user
     *            gesture.
     * @param isControlDown
     *            specifies whether the Control modifier is down on this user
     *            gesture.
     * @param isMetaDown
     *            specifies whether the Meta modifier is down on this user
     *            gesture.
     * @param isShiftDown
     *            specifies whether the Shift modifier is down on this user
     *            gesture.
     * @param isLeftMouseButtonDown
     *            specifies whether the left mouse button is down on this user
     *            gesture.
     * @param isMiddleMouseButtonDown
     *            specifies whether the middle mouse button is down on this user
     *            gesture.
     * @param isRightMouseButtonDown
     *            specifies whether the Meta modifier is down on this user
     *            gesture.
     */
    protected ModifiableUserGesture(boolean isAltDown, boolean isControlDown,
            boolean isMetaDown, boolean isShiftDown,
            boolean isLeftMouseButtonDown, boolean isMiddleMouseButtonDown,
            boolean isRightMouseButtonDown) {
        modifierMask = 0;
        if (isAltDown) {
            modifierMask |= InputEvent.ALT_DOWN_MASK;
        }
        if (isControlDown) {
            modifierMask |= InputEvent.CTRL_DOWN_MASK;
        }
        if (isMetaDown) {
            modifierMask |= InputEvent.META_DOWN_MASK;
        }
        if (isShiftDown) {
            modifierMask |= InputEvent.SHIFT_DOWN_MASK;
        }
        if (isLeftMouseButtonDown) {
            modifierMask |= InputEvent.BUTTON1_DOWN_MASK;
        }
        if (isMiddleMouseButtonDown) {
            modifierMask |= InputEvent.BUTTON2_DOWN_MASK;
        }
        if (isRightMouseButtonDown) {
            modifierMask |= InputEvent.BUTTON3_DOWN_MASK;
        }
    }

    /**
     * Constructs a {@code ModifiableUserGesture}.
     * 
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     * @see InputEvent#getModifiersEx()
     */
    protected ModifiableUserGesture(int modifierMask) {
        this.modifierMask = modifierMask;
    }

    /**
     * Returns whether the Alt modifier is down on this user gesture.
     * 
     * @return whether the Alt modifier is down on this user gesture.
     */
    public boolean isAltDown() {
        return (modifierMask & InputEvent.ALT_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the Control modifier is down on this user gesture.
     * 
     * @return whether the Control modifier is down on this user gesture.
     */
    public boolean isControlDown() {
        return (modifierMask & InputEvent.CTRL_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the Meta modifier is down on this user gesture.
     * 
     * @return whether the Meta modifier is down on this user gesture.
     */
    public boolean isMetaDown() {
        return (modifierMask & InputEvent.META_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the Shift modifier is down on this user gesture.
     * 
     * @return whether the Shift modifier is down on this user gesture.
     */
    public boolean isShiftDown() {
        return (modifierMask & InputEvent.SHIFT_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the left mouse button is down on this user gesture.
     * 
     * @return whether the left mouse button is down on this user gesture.
     */
    public boolean isLeftMouseButtonDown() {
        return (modifierMask & InputEvent.BUTTON1_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the middle mouse button is down on this user gesture.
     * 
     * @return whether the middle mouse button is down on this user gesture.
     */
    public boolean isMiddleMouseButtonDown() {
        return (modifierMask & InputEvent.BUTTON2_DOWN_MASK) != 0;
    }

    /**
     * Returns whether the right mouse button is down on this user gesture.
     * 
     * @return whether the right mouse button is down on this user gesture.
     */
    public boolean isRightMouseButtonDown() {
        return (modifierMask & InputEvent.BUTTON3_DOWN_MASK) != 0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
