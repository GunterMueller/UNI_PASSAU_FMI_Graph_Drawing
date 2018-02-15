// =============================================================================
//
//   CloseViewAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CloseViewAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * Represents a clone view action.
 */
public class CloseViewAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 5290117426629523465L;

    /**
     * Constructs a new close view action.
     */
    public CloseViewAction() {
        super("action.view.close", null); // TODO
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * Returns the help context for this action.
     * 
     * @return the help context for this action.
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * Returns the name of this action.
     * 
     * @return String, the name of this action.
     */
    @Override
    public String getName() {
        return null;
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
