// =============================================================================
//
//   SelectGraphElementActionListener.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectGraphElementActionListener.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-04-05 23:01:26 +0200 (Mi, 05 Apr 2006)
 *          $
 */
public class SelectGraphElementActionListener implements SelectionListener {

    /** Reference to the SelectionAction, which creates the listener * */
    private SelectionAction selectionAction;

    /**
     * Creates a new SelectGraphElementActionListener
     * 
     * @param selectionAction
     *            the SelectGraphElementActionListener which creates the
     *            Listener
     */
    public SelectGraphElementActionListener(SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * select button has to be enabled or disabled
     * 
     * @param e
     *            not needed here.
     */
    public void selectionChanged(SelectionEvent e) {
        selectionAction.update();
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * select button has to be enabled or disabled.
     * 
     * @param e
     *            not needed here.
     */
    public void selectionListChanged(SelectionEvent e) {
        selectionAction.update();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
