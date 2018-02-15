// =============================================================================
//
//   MouseCursorProvider.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.Cursor;

import org.graffiti.plugin.view.View;

/**
 * {@code GestureFeedbackProvider}s implementing {@code MouseCursorProvider}
 * support setting the bitmap representation of the mouse cursor.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface MouseCursorProvider extends GestureFeedbackProvider {
    /**
     * Sets the bitmap representation of the mouse cursor that is shown while
     * the mouse cursor is placed above the main component of the
     * {@link InteractiveView} that handed out this {@code
     * GestureFeedbackProvider}.
     * 
     * @param cursor
     *            the specified bitmap representation of the mouse cursor.
     * @see View#getViewComponent()
     */
    public void setCursor(Cursor cursor);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
