// =============================================================================
//
//   RevertEdgeAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RevertEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;

/**
 * Processed on all selected edges or all edges, if no edge selected. Reverts
 * the according edge.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class RevertEdgeAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -8171022526436861076L;

    /**
     * Creates a new action.
     * 
     */
    public RevertEdgeAction() {
        super(GraphEditingBundle.getString("menu.revertEdgeAction"),
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

        Collection<Edge> edges = null;

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        if (selection.getEdges().isEmpty()) {
            edges = graph.getEdges();
        } else {
            edges = selection.getEdges();
        }

        graph.getListenerManager().transactionStarted(this);

        for (Edge edge : edges) {
            edge.reverse();
        }

        graph.getListenerManager().transactionFinished(this);
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
