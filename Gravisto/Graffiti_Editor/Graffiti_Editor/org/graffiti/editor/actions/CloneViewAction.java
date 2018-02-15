// =============================================================================
//
//   CloneViewAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CloneViewAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * Represents an action, which can clone the current view.
 * 
 * @version $Revision: 5768 $
 */
public class CloneViewAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5357908122813820750L;

    /**
     * Constructs a new clone view action.
     */
    public CloneViewAction() {
        super("action.view.clone", null); // TODO
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
        return null; // TODO
    }

    /**
     * Returns the name of this action.
     * 
     * @return String, the name of this action.
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
