// =============================================================================
//
//   FileNewAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileNewAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.ViewManager;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * The action for a new graph.
 * 
 * @version $Revision: 5768 $
 */
public class FileNewAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5482805688657702432L;
    /** DOCUMENT ME! */
    private ViewManager viewManager;

    /**
     * Creates a new FileNewAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param viewManager
     *            DOCUMENT ME!
     */
    public FileNewAction(MainFrame mainFrame, ViewManager viewManager) {
        super("file.new", mainFrame);
        this.viewManager = viewManager;

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return viewManager.hasViews();
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
        mainFrame.addNewSession();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
