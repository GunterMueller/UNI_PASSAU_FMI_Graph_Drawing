// =============================================================================
//
//   PositionInfo.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PositionInfo.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;

/**
 * This class is used in conjunction with a FunctionComponent and stores
 * position-information (mouse, mouse when popup-menu was opened, possibly other
 * things as well in future). This way, the FunctionActions can be feeded with
 * position-information (via the FunctionActionEvents).
 */
public class PositionInfo {

    /** Position of the mouse when the popup-menu was opened last time */
    private Point lastPopupPosition = null;

    /** Last mouse-position */
    private Point mousePosition = null;

    /**
     * Sets the position the mouse had when the popup-menu was opened the last
     * time.
     * 
     * @param lastPopupPosition
     *            the position the mouse had when the popup-menu was opened the
     *            last time, may be null if no such position is available
     */
    public void setLastPopupPosition(Point lastPopupPosition) {
        this.lastPopupPosition = lastPopupPosition;
    }

    /**
     * Returns the position the mouse had when the popup-menu was opened the
     * last time.
     * 
     * @return the position the mouse had when the popup-menu was opened the
     *         last time, null if no such position is available
     */
    public Point getLastPopupPosition() {
        return lastPopupPosition;
    }

    /**
     * Sets the last mouse-position.
     * 
     * @param mousePosition
     *            the last mouse-position, may be null if no mouse-position is
     *            available
     */
    public void setMousePosition(Point mousePosition) {
        this.mousePosition = mousePosition;
    }

    /**
     * Returns the last mouse-position.
     * 
     * @return the last mouse-position, null if presently no mouse-position is
     *         available
     */
    public Point getMousePosition() {
        return mousePosition;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
