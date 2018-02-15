// =============================================================================
//
//   MouseReleaseGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User gesture that represents releasing a mouse button. It is provided for
 * convenience and its use by {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see UserGesture
 */
public class MouseReleaseGesture extends MouseButtonGesture {
    /**
     * Constructs a {@code MouseReleaseGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     * @param buttonId
     *            the id of the button that was released, as used by
     *            {@link MouseEvent#getButton()}.
     */
    public MouseReleaseGesture(Point2D position, int modifierMask, int buttonId) {
        super(position, modifierMask, buttonId);
    }

    /**
     * Constructs a {@code MouseReleaseGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     * @param button
     *            the button that was released, as used by
     *            {@link MouseEvent#getButton()}.
     */
    public MouseReleaseGesture(Point2D position, int modifierMask,
            MouseButton button) {
        super(position, modifierMask, button);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
