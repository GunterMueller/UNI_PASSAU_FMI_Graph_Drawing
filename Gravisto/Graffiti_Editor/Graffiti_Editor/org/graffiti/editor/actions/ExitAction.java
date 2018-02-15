// =============================================================================
//
//   ExitAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ExitAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * Exits the editor.
 */
public class ExitAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 6294696024311937132L;

    /**
     * Creates a new ExitAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public ExitAction(MainFrame mainFrame) {
        super("file.exit", mainFrame);
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
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
        mainFrame.dispose(); // TODO "do you really want to exit dialog"
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
