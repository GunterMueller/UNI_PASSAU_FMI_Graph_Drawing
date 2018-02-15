// =============================================================================
//
//   KeyboardGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * User gesture that represents a key press or release. It is provided for
 * convenience and its use by {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see UserGesture
 * @see InputEvent#getModifiersEx()
 * @see KeyEvent#getKeyCode()
 */
public class KeyboardGesture extends ModifiableUserGesture {
    /**
     * The key code.
     */
    private int keyCode;

    /**
     * Constructs a {@code KeyboardGesture}.
     * 
     * @param keyCode
     *            the key code.
     * @param modifierMask
     *            a bit set representing the state of modal keys and mouse
     *            buttons.
     * @see InputEvent#getModifiersEx()
     */
    protected KeyboardGesture(int keyCode, int modifierMask) {
        super(modifierMask);
        this.keyCode = keyCode;
    }

    /**
     * Returns the key code.
     * 
     * @return the key code.
     * @see KeyEvent#getKeyCode()
     */
    public int getKeyCode() {
        return keyCode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
