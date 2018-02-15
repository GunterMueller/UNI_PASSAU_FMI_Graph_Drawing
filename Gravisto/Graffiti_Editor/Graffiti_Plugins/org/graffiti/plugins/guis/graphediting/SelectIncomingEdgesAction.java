// =============================================================================
//
//   SelectIngoingEdgesAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectIncomingEdgesAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

/**
 * Selects all incoming edges of the selected nodes.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class SelectIncomingEdgesAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 6060423284979078911L;

    /**
     * Creates a new select-all-incoming-edges-action.
     * 
     */
    public SelectIncomingEdgesAction() {
        super(GraphEditingBundle.getString("menu.selectIncomingEdgesAction"),
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

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        mainFrame.getActiveEditorSession().getSelectionModel()
                .setActiveSelection(
                        CoreGraphEditing.selectIncomingEdges(selection
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
