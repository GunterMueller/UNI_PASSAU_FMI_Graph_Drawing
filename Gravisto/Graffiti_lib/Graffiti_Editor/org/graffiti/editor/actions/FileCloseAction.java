// =============================================================================
//
//   FileCloseAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileCloseAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * The action for closing a graph.
 * 
 * @version $Revision: 5768 $
 */
public class FileCloseAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1753053200351459126L;

    /**
     * Creates a new FileCloseAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public FileCloseAction(MainFrame mainFrame) {
        super("file.close", mainFrame);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return mainFrame.isSessionActive();
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        mainFrame.removeSession(mainFrame.getActiveSession());
        mainFrame.updateActions();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
