// =============================================================================
//
//   EditUndoAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditUndoAction.java 5782 2010-05-11 22:08:56Z hanauer $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoManager;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.session.EditorSession;

/**
 * Special class for undo capabilities.
 * 
 * @version $Revision: 5782 $
 */
public class EditUndoAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8471739910845392234L;

    /**
     * Creates a new EditUndoAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public EditUndoAction(MainFrame mainFrame) {
        super("edit.undo", mainFrame);
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
            mainFrame.getActiveEditorSession().getUndoManager().undo();
            try {
                ToolRegistry.get().canceled(
                        (InteractiveView<?>) mainFrame.getActiveEditorSession()
                                .getActiveView());
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            }
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
            setEnabled(um.canUndo());
            putValue(NAME, um.getUndoPresentationName());
            putValue(SHORT_DESCRIPTION, um.getUndoPresentationName());
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
