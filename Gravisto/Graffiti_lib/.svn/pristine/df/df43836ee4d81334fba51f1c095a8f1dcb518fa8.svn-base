// =============================================================================
//
//   SetNodeLabelsToNodeDegree.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SetNodeLabelsToNodeDegreeAction.java 1255 2006-05-08 16:17:37Z piorkows $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.EditorGraphEditing;

/**
 * Sets the labels of all selected nodes to an distinct integer
 */
public class SetDistinctIntegerNodeLabelsAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 2925703430161654220L;

    /**
     * Creates a new action.
     * 
     */
    public SetDistinctIntegerNodeLabelsAction() {
        super(GraphEditingBundle
                .getString("menu.setDistinctIntegerNodeLabelsAction"),
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
            EditorGraphEditing.setDistinctIntegerNodeLabels(graph.getNodes());
        } else {
            EditorGraphEditing.setDistinctIntegerNodeLabels(selection
                    .getNodes());
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
