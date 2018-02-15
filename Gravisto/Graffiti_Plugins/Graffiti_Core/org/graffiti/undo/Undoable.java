// =============================================================================
//
//   Undoable.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Undoable.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.undo;

import javax.swing.undo.UndoableEditSupport;

/**
 * This interface should be implemented by all classes that provide
 * <code>UndoableEdit</code>s for their actions.
 * 
 * @version $Revision: 5767 $
 */
public interface Undoable {

    /**
     * Sets the undo support object this object uses. The undo support object
     * handles the <code>UndoableEditListeners</code>.
     * 
     * @param us
     *            the undo support object this object uses.
     */
    public void setUndoSupport(UndoableEditSupport us);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
