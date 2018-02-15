// =============================================================================
//
//   RemoveIsolatedNodes.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RemoveIsolatedNodesAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Node;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.util.CoreGraphEditing;

/**
 * Removes all isolated nodes.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt 2009)
 *          $
 */
public class RemoveIsolatedNodesAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -937421938311326504L;

    /**
     * Creates a new Remove Isolated Nodes action.
     * 
     */
    public RemoveIsolatedNodesAction() {
        super(GraphEditingBundle.getString("menu.removeIsolatedNodesAction"),
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
        Selection selection = selectionModel.getActiveSelection();
        Collection<Node> selectedNodes = selection.getNodes();
        Collection<Node> isolatedNodes = CoreGraphEditing.selectIsolatedNodes(
                !selectedNodes.isEmpty() ? selectedNodes : session.getGraph()
                        .getNodes()).getNodes();
        if (!isolatedNodes.isEmpty()) {
            UndoUtil undoUtil = new UndoUtil(session);
            undoUtil
                    .deleteElements(
                            isolatedNodes,
                            GraphEditingBundle
                                    .getString(isolatedNodes.size() == 1 ? "undo.removeIsolatedNode"
                                            : "menu.removeIsolatedNodesAction"));
            selection.clear();
            undoUtil.close();
            selectionModel.selectionChanged();
        }
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
