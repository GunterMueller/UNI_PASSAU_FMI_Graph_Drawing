// =============================================================================
//
//   MouseGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.geom.Point2D;

/**
 * User gesture involving the mouse. It is provided for convenience and its use
 * by {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseGesture extends ModifiableUserGesture {
    /**
     * The position of the mouse cursor. The referred coordinate system is left
     * to the {@link InteractiveView} that created this {@code MouseDragGesture}
     * .
     */
    private Point2D position;

    /**
     * Constructs a {@code MouseGesture}.
     * 
     * @param position
     *            the position of the mouse cursor. The referred coordinate
     *            system is left to the calling {@link InteractiveView}.
     * @param modifierMask
     *            a bit set representing the state of the modifiers. It is
     *            interpreted like the extended modifier mask of {@code
     *            InputEvent}.
     */
    protected MouseGesture(Point2D position, int modifierMask) {
        super(modifierMask);
        this.position = position;
    }

    /**
     * Returns the position of the mouse cursor.
     * 
     * @return the position of the mouse cursor. The referred coordinate system
     *         is left to the calling {@link InteractiveView}.
     */
    public Point2D getPosition() {
        return position;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
