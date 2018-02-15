/* Copyright (c) 2003 IPK Gatersleben
 * $Id: SelectAllAction.java 5768 2010-05-07 18:42:39Z gleissner $
 */

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;

/**
 * Represents a &quot;select all graph elements&quot; action.
 * 
 * @version $Revision: 5768 $
 */
public class SelectAllAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4069936503649285202L;

    /**
     * Constructs a new copy action.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public SelectAllAction(MainFrame mainFrame) {
        super("edit.selectAll", mainFrame);
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
        // this check can be discarded when the isEnabled-function works
        // correctly
        if (!mainFrame.isSessionActive())
            return;

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        selection.clear();
        selection.addAll(mainFrame.getActiveEditorSession().getGraph()
                .getGraphElements());

        mainFrame.getActiveEditorSession().getSelectionModel()
                .selectionChanged();

    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getGraph()
                    .getGraphElements().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
