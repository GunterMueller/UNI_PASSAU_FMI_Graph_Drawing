// =============================================================================
//
//   SelectionAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.actions;

import org.graffiti.editor.MainFrame;

/**
 * Represents an action, which depends on a selection.
 * 
 * @version $Revision: 5768 $
 */
public abstract class SelectionAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 8457633478693180876L;

    /**
     * Constructs a new selection action with the given name.
     * 
     * @param name
     *            DOCUMENT ME!
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public SelectionAction(String name, MainFrame mainFrame) {
        super(name, mainFrame);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return false; // TODO
    }

    /**
     * Returns <code>true</code>, if this action should survive a focus change.
     * 
     * @return <code>true</code>, if this action should survive a focus change.
     */
    public boolean surviveFocusChange() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
