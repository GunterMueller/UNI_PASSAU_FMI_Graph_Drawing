// =============================================================================
//
//   SelectionSaveAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionSaveAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.switchselections;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.selection.Selection;
import org.graffiti.session.EditorSession;

/**
 * Saves a selection as long as the session lives under some name.
 */
public class SelectionSaveAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4154988989492357844L;

    /**
     * Creates a new SelectionSaveAction object.
     * 
     * @param name
     *            DOCUMENT ME!
     */
    public SelectionSaveAction(String name) {
        super(name, null);
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return super.enabled;
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
        String selName = JOptionPane.showInputDialog(
                "Under which name should the active selection be remembered?",
                "Selection " + System.currentTimeMillis());
        Selection selClone = null;
        EditorSession session = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveEditorSession();

        try {
            selClone = (Selection) session.getSelectionModel()
                    .getActiveSelection().clone();
        } catch (CloneNotSupportedException cnse) {
            // should be cloneable ...
            return;
        }

        selClone.setName(selName);
        session.getSelectionModel().add(selClone);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
