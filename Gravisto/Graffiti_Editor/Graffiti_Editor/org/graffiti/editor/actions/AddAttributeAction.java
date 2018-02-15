// =============================================================================
//
//   AddAttributeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddAttributeAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.SelectionEvent;

/**
 * Represents an action to add an attribute to the selected item.
 * 
 * @author flierl
 * @version $Revision: 5768 $
 */
public class AddAttributeAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 5654063616326959190L;

    /**
     * Constructs a new add attribute action.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public AddAttributeAction(MainFrame mainFrame) {
        super("action.add.attribute", mainFrame);
    }

    /**
     * Returns the help context of this action.
     * 
     * @return the help context of this action.
     */
    @Override
    public HelpContext getHelpContext() {
        return null; // TODO
    }

    /**
     * Returns the name of this action.
     * 
     * @return the name of this action.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        // TODO
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void selectionChanged(SelectionEvent e) {
        // TODO
    }

    /**
     * Returns <code>true</code>, if this action should survive a focus change
     * in the editor.
     * 
     * @return <code>true</code>, if this action should survive a focus chage in
     *         the editor.
     */
    @Override
    public boolean surviveFocusChange() {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
