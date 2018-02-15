// =============================================================================
//
//   ViewNewAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ViewNewAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.core.Bundle;
import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * The action for creating a new view.
 */
public class ViewNewAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6272755697902368835L;

    /**
     * Creates a new ViewNewAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param bundle
     *            DOCUMENT ME!
     */
    public ViewNewAction(MainFrame mainFrame, Bundle bundle) {
        super("file.newView", mainFrame);
    }

    /**
     * @see javax.swing.Action#isEnabled()
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
        if (mainFrame.isSessionActive()) {
            String dv = mainFrame.getDefaultView();

            if (dv != null) {
                mainFrame.createInternalFrame(dv, "", false);
            } else {
                mainFrame.showViewChooserDialog(false);
            }
        } else {
            // mainFrame.showError(sBundle.getString("menu.view.new.error"));
            // TODO
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
