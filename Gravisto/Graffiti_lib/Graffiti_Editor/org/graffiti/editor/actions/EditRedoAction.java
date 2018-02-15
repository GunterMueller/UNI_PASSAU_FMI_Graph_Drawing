// =============================================================================
//
//   EditRedoAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditRedoAction.java 5782 2010-05-11 22:08:56Z hanauer $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoManager;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.session.EditorSession;

/**
 * Special class for redo capabilities.
 * 
 * @version $Revision: 5782 $
 */
public class EditRedoAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6331846569157657511L;

    /**
     * Creates a new EditRedoAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public EditRedoAction(MainFrame mainFrame) {
        super("edit.redo", mainFrame);
        enabled = false;
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * <b>Implementation Note: </b> The status of the GUIComponents has to be
     * updated after actionPerformed was executed.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        if (this.isEnabled()) {
            mainFrame.getActiveEditorSession().getGraph().getListenerManager().transactionStarted(this);
            mainFrame.getActiveEditorSession().getUndoManager().redo();
            mainFrame.getActiveEditorSession().getGraph().getListenerManager().transactionFinished(this);

            mainFrame.updateActions();
        }
    }

    /**
     * Updates the state of this action.
     */
    @Override
    public void update() {
        if (mainFrame.isSessionActive()) {
            EditorSession session = mainFrame.getActiveEditorSession();
            UndoManager um = session.getUndoManager();
            setEnabled(um.canRedo());
            putValue(NAME, um.getRedoPresentationName());
            putValue(SHORT_DESCRIPTION, um.getRedoPresentationName());
        } else {
            setEnabled(false);
            putValue(NAME, coreBundle.getString("menu." + getName()));
            putValue(SHORT_DESCRIPTION, getName());
        }

        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
