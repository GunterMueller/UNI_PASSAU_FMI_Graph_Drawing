// =============================================================================
//
//   SelectIsolatedNodes.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectIsolatedNodesAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Node;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.util.CoreGraphEditing;

/**
 * Selects all isolated nodes.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class SelectIsolatedNodesAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -7421286212629583947L;

    /**
     * Creates a new Select Isolated Nodes action.
     * 
     */
    public SelectIsolatedNodesAction() {
        super(GraphEditingBundle.getString("menu.selectIsolatedNodesAction"),
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
        EditorSession session = mainFrame.getActiveEditorSession();
        SelectionModel selectionModel = session.getSelectionModel();
        Collection<Node> selectedNodes = selectionModel.getActiveSelection()
                .getNodes();
        Selection isolatedNodes = CoreGraphEditing
                .selectIsolatedNodes(!selectedNodes.isEmpty() ? selectedNodes
                        : session.getGraph().getNodes());
        selectionModel.setActiveSelection(isolatedNodes);
    }

    /**
     * 
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
