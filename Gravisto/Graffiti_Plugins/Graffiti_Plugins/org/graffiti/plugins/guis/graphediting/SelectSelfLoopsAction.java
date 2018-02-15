// =============================================================================
//
//   ClearNodeLabels.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectSelfLoopsAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

/**
 * Selects all self loops of all selected nodes or all nodes of all nodes, if no
 * node is selected.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-05-08 18:17:37 +0200 (Mon, 08 May
 *          2006) $
 */
public class SelectSelfLoopsAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 4997647391994226720L;

    /**
     * Creates a new action.
     * 
     */
    public SelectSelfLoopsAction() {
        super(GraphEditingBundle.getString("menu.selectSelfLoopsAction"),
                GraffitiSingleton.getInstance().getMainFrame());
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();
        Graph graph = mainFrame.getActiveEditorSession().getGraph();

        if (selection.getNodes().isEmpty()) {
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .setActiveSelection(
                            CoreGraphEditing.selectSelfLoops(graph.getNodes()));

        } else {
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .setActiveSelection(
                            CoreGraphEditing.selectSelfLoops(selection
                                    .getNodes()));
        }
    }

    /**
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getGraph().getEdges()
                    .isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
