// =============================================================================
//
//   FileSaveAllAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileSaveAllAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.IOManager;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.session.EditorSession;

/**
 * The action for saving all open graphs.
 * 
 * @version $Revision: 5768 $
 */
public class FileSaveAllAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -53357153860385957L;

    /**
     * Creates a new FileSaveAllAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param ioManager
     *            DOCUMENT ME!
     */
    public FileSaveAllAction(MainFrame mainFrame, IOManager ioManager) {
        super("file.saveAll", mainFrame);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return false;
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
        String dv = mainFrame.getDefaultView();

        if (dv != null) {
            mainFrame.createInternalFrame(dv, "", false);
        } else {
            mainFrame.showViewChooserDialog(new EditorSession(), false);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
