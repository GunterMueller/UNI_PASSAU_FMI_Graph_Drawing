// =============================================================================
//
//   GraffitiAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.actions;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.graffiti.core.Bundle;
import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;

/**
 * Represents the basic action in the graffiti system.
 * 
 * @version $Revision: 5768 $
 */
public abstract class GraffitiAction extends AbstractAction {
    /**
     * 
     */
    private static final long serialVersionUID = -5901734111853572523L;

    /** The <code>Bundle</code> instance. */
    protected static final Bundle coreBundle = Bundle.getCoreBundle();

    /** The main frame. */
    protected MainFrame mainFrame;

    /** The abstract name of the action. */
    protected String name;

    /**
     * Constructs a new GraffitiAction from the given name.
     * 
     * @param name
     *            the name for the action
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public GraffitiAction(String name, MainFrame mainFrame) {
        super(name);
        this.name = name;
        this.mainFrame = mainFrame;
    }

    /**
     * Returns <code>true</code>, if this action is enabled.
     * 
     * @return <code>true</code>, if this action is enabled.
     */
    @Override
    public abstract boolean isEnabled();

    /**
     * Returns the help context for this action.
     * 
     * @return the help context for this action.
     */
    public abstract HelpContext getHelpContext();

    /**
     * Basically very strange. But it helps getting around the problem that
     * buttons are not activated after the corresponding action has been
     * installed and activated.
     * 
     * @see javax.swing.AbstractAction#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean newValue) {
        if (this.enabled && !newValue) {
            super.setEnabled(false);
        } else if (newValue) {
            super.setEnabled(false);
            super.setEnabled(true);
        }
    }

    /**
     * Returns the abstract name of the action.
     * 
     * @return the abstract name of the action.
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the state of the action. Calls:
     * <code>setEnabled(isEnabled());</code>.
     */
    public void update() {
        setEnabled(isEnabled());
    }

    /**
     * Shows an error in a modal dialog box.
     * 
     * @param msg
     *            the message to be shown.
     */
    protected void showError(String msg) {
        JOptionPane.showMessageDialog(mainFrame, msg, coreBundle
                .getString("message.dialog.title"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a warning in a modal dialog box.
     * 
     * @param msg
     *            the message to be shown.
     */
    protected void showWarning(String msg) {
        JOptionPane
                .showMessageDialog(mainFrame, msg, coreBundle
                        .getString("message.dialog.title"),
                        JOptionPane.WARNING_MESSAGE);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
