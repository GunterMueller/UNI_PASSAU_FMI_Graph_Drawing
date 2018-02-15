// =============================================================================
//
//   CutCopyPasteListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CutCopyPasteListener.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions.cutcopypaste;

import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;

/**
 * Class represents a listener, who is activated everytime the selection of the
 * graph changes, due to enable or disable the Cut-, Copy- or Paste-Button
 * correctly.
 * 
 * @author MH
 */
public class CutCopyPasteListener implements SelectionListener {

    /** Reference to the SelectionAction, which creates the listener */
    private SelectionAction selectionAction;

    /**
     * Creates a new SelectionActionListener
     * 
     * @param selectionAction
     *            the SelectionAction which creates the Listener
     */
    public CutCopyPasteListener(SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * Cut-, Copy- or Paste-Button has to be enabled or disabled
     * 
     * @param e
     *            not used
     */
    public void selectionChanged(SelectionEvent e) {
        selectionAction.update();
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * Cut-, Copy- or Paste-Button has to be enabled or disabled
     * 
     * @param e
     *            not used
     */
    public void selectionListChanged(SelectionEvent e) {
        selectionAction.update();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
