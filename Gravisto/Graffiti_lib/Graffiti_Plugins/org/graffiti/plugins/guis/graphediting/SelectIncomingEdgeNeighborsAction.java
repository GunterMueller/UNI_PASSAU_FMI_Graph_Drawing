// =============================================================================
//
//   SelectInNeighbours.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectIncomingEdgeNeighborsAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

/**
 * Selects all incoming edge neighbors of the selected nodes.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-05-08 18:17:37 +0200 (Mon, 08 May
 *          2006) $
 */
public class SelectIncomingEdgeNeighborsAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -3286611579268881008L;

    /**
     * Creates a new select-incoming-edge-neighbors-action.
     * 
     */
    public SelectIncomingEdgeNeighborsAction() {
        super(GraphEditingBundle
                .getString("menu.selectIncomingEdgeNeighborsAction"),
                GraffitiSingleton.getInstance().getMainFrame());
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /**
     * 
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null; // TODO
    }

    /**
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        // this check can be discarded when the isEnabled-function works
        // correctly
        if (!mainFrame.isSessionActive())
            return;

        /*
         * TODO: Ensure that isEnabled() works correctly and remove the block
         * above.
         */

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        mainFrame.getActiveEditorSession().getSelectionModel()
                .setActiveSelection(
                        CoreGraphEditing
                                .selectAllIncomingEdgeNeighbours(selection
                                        .getNodes()));

    }

    /**
     * 
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().getNodes().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
