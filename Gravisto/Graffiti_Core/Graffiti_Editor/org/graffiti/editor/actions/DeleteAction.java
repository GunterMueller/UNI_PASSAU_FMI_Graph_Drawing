// =============================================================================
//
//   DeleteAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DeleteAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.undo.GraphElementsDeletionEdit;

/**
 * Represents a graph element delete action.
 * 
 * @version $Revision: 5768 $
 */
public class DeleteAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 3253355300988754192L;

    /**
     * Constructs a new copy action.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public DeleteAction(MainFrame mainFrame) {
        super("edit.delete", mainFrame);

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }

    /**
     * Checks the enabled flag, which tells if the CutButton in the mainFrame
     * should be enabled
     * 
     * @return the enabled flag
     */
    @Override
    public boolean isEnabled() {
        // No editor is opened: Button disabled
        if (mainFrame.getActiveEditorSession() == null)
            return false;
        else if (mainFrame.getActiveEditorSession().getSelectionModel()
                .getActiveSelection() == null)
            return false;
        else
            return (!mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().isEmpty());
    }

    /**
     * Returns the help context for the action.
     * 
     * @return HelpContext, the help context for the action
     */
    @Override
    public HelpContext getHelpContext() {
        return null; // TODO
    }

    /**
     * Executes this action.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        if (this.isEnabled()) {
            Selection selection = mainFrame.getActiveEditorSession()
                    .getSelectionModel().getActiveSelection();

            if (!selection.isEmpty()) {
                Graph graph = mainFrame.getActiveEditorSession().getGraph();
                Map<GraphElement, GraphElement> geMap = mainFrame
                        .getActiveEditorSession().getGraphElementsMap();

                UndoableEditSupport undoSupport = mainFrame.getUndoSupport();

                GraphElementsDeletionEdit edit = new GraphElementsDeletionEdit.Builder(
                        geMap, graph, selection.getElements()).build();
                mainFrame.getActiveEditorSession().getSelectionModel()
                        .getActiveSelection().clear();
                edit.execute();
                undoSupport.postEdit(edit);
                mainFrame.getActiveEditorSession().getSelectionModel()
                        .selectionChanged();
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
