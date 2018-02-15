// =============================================================================
//
//   PopupAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PopupAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;

/**
 * Represents an action, which is called, if there should be displayed a popup
 * menu.
 * 
 * @version $Revision: 5768 $
 */
public class PopupAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8463543074773273120L;

    /**
     * Constructs a new popup action.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public PopupAction(MainFrame mainFrame) {
        super("action.popup", mainFrame);
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
     * Returns the name of this action.
     * 
     * @return String, the name
     */
    @Override
    public String getName() {
        return null; // TODO
    }

    /**
     * Executes this action.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
