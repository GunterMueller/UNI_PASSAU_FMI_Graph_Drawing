// =============================================================================
//
//   RemoveNodeLabelsAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RemoveNodeLabelsAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.EditorGraphEditing;

/**
 * Removes all node labels of all selected nodes or all node labels of all
 * nodes, if no node is selected.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-05-08 18:17:37 +0200 (Mon, 08 May
 *          2006) $
 */
public class RemoveNodeLabelsAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 6291485683432310277L;

    /**
     * Creates a new action.
     * 
     */
    public RemoveNodeLabelsAction() {
        super(GraphEditingBundle.getString("menu.removeNodeLabelsAction"),
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
        Graph graph = mainFrame.getActiveEditorSession().getGraph();

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        if (selection.getNodes().isEmpty()) {
            EditorGraphEditing.removeNodeLabels(graph.getNodes());
        } else {
            EditorGraphEditing.removeNodeLabels(selection.getNodes());
        }

    }

    /**
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getGraph().getNodes()
                    .isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
