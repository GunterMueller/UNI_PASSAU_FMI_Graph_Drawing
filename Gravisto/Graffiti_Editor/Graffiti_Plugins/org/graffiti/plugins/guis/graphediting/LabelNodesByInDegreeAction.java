// =============================================================================
//
//   SetNodeLabelsToInDegree.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LabelNodesByInDegreeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.EditorGraphEditing;

/**
 * Sets the labels of all selected nodes to their in degree. Or sets the lables
 * of all nodes to their in degree, if no node is selected.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class LabelNodesByInDegreeAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 319115916013894390L;

    /**
     * Creates a new action.
     * 
     */
    public LabelNodesByInDegreeAction() {
        super(GraphEditingBundle.getString("menu.labelNodesByInDegreeAction"),
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
            EditorGraphEditing.labelNodesByInDegree(graph.getNodes());
        } else {
            EditorGraphEditing.labelNodesByInDegree(selection.getNodes());
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
