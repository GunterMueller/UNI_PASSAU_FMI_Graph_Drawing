// =============================================================================
//
//   MouseMoveGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.geom.Point2D;

/**
 * User gesture that represents moving the mouse while none of the mouse buttons
 * is being pressed. It is provided for convenience and its use by
 * {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see UserGesture
 */
public class MouseMoveGesture extends MouseGesture {
    /**
     * The change of mouse coordinates. The referred coordinate system is left
     * to the {@link InteractiveView} that created this {@code MouseDragGesture}
     * .
     */
    private Point2D delta;

    /**
     * Constructs a {@code MouseMoveGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param delta
     *            the change in position of the mouse cursor. The referred
     *            coordinate system is left to the calling
     *            {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     */
    public MouseMoveGesture(Point2D position, Point2D delta, int modifierMask) {
        super(position, modifierMask);
        this.delta = delta;
    }

    /**
     * Returns the change in position of the mouse cursor.
     * 
     * @return the change in position of the mouse cursor. The referred
     *         coordinate system is left to the calling {@link InteractiveView}.
     */
    public Point2D getDelta() {
        return delta;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
