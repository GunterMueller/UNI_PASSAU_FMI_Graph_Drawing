// =============================================================================
//
//   MouseButton.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.Arrays;
import java.util.List;

import org.graffiti.core.Bundle;

/**
 * Enumeration of mouse buttons, which, when used by triggers as a parameter
 * type, is prepared to be used for the graphical configuration of tools.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public enum MouseButton implements SlotEditableEnum {
    /**
     * While not representing a specific mouse button, this value can be used to
     * indicate that a {@link Trigger} should match any button.
     */
    IGNORE("mousebutton.ignore"),

    /**
     * Left mouse button.
     */
    LEFT("mousebutton.left"),

    /**
     * Middle mouse button.
     */
    MIDDLE("mousebutton.middle"),

    /**
     * Right mouse button.
     */
    RIGHT("mousebutton.right");

    /**
     * String used to obtain a localized description of this button.
     */
    private final String bundleString;

    /**
     * Constructs a {@code MouseButton}.
     */
    private MouseButton(String bundleString) {
        this.bundleString = bundleString;
    }

    /**
     * Returns a localized description of this button as obtained from the
     * passed bundle.
     * 
     * @param bundle
     *            the bundle containing the localized description of this
     *            button.
     */
    public String getName(Bundle bundle) {
        return bundle.getString(bundleString);
    }

    /**
     * Returns a {@code List} containing all {@code MouseButton} enum constants.
     * 
     * @return a {@code List} containing all {@code MouseButton} enum constants.
     */
    public List<? extends SlotEditableEnum> getValues() {
        return Arrays.asList(values());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
