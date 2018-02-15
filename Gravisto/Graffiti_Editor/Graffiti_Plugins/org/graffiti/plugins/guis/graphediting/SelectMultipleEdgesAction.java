// =============================================================================
//
//   ClearEdgeLabelsAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectMultipleEdgesAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

/**
 * Selects all multiple edges of the selected nodes or all multiple edges of all
 * nodes, if no node is selected.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class SelectMultipleEdgesAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -1247580530704196692L;

    /**
     * Creates a new action.
     * 
     */
    public SelectMultipleEdgesAction() {
        super(GraphEditingBundle.getString("menu.selectMultipleEdgesAction"),
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
                            CoreGraphEditing.selectMultipleEdges(graph
                                    .getNodes()));

        } else {
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .setActiveSelection(
                            CoreGraphEditing.selectMultipleEdges(selection
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
