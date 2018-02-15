// =============================================================================
//
//   SelectionListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.selection;

/**
 * Interfaces a listener, which wants to be informed about a change in the
 * selection model.
 * 
 * @version $Revision: 5767 $
 */
public interface SelectionListener {

    /**
     * Is called, if something in the selection model changed.
     */
    public void selectionChanged(SelectionEvent e);

    /**
     * Is called, if a named selection is added or removed.
     */
    public void selectionListChanged(SelectionEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
