// =============================================================================
//
//   PluginManagerEditAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginManagerEditAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.pluginmgr.PluginManager;
import org.graffiti.managers.pluginmgr.PluginManagerDialog;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * Called, if the plugin manager dialog should be shown.
 * 
 * @version $Revision: 5768 $
 */
public class PluginManagerEditAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -931113552125529843L;
    /** DOCUMENT ME! */
    PluginManager pluginmgr;

    /**
     * Creates a new PluginManagerEditAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param pluginmgr
     *            DOCUMENT ME!
     */
    public PluginManagerEditAction(MainFrame mainFrame, PluginManager pluginmgr) {
        super("pluginmgr.edit", mainFrame);
        this.pluginmgr = pluginmgr;
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
        PluginManagerDialog pmd = new PluginManagerDialog(mainFrame, pluginmgr);
        pmd.setVisible(true);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
