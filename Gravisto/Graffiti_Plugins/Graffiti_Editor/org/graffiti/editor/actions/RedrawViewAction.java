// =============================================================================
//
//   RedrawViewAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RedrawViewAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.MainFrame;
import org.graffiti.event.GraphEvent;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.View;
import org.graffiti.session.EditorSession;

/**
 * The action for a new graph.
 * 
 * @version $Revision: 5768 $
 */
public class RedrawViewAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 6939141137836275872L;

    /**
     * Creates a new RedrawViewAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     */
    public RedrawViewAction(MainFrame mainFrame) {
        super("edit.redraw", mainFrame);
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        // did not find out how can switch from disabled to enabled
        // nobody asks isEnabled any more?!
        return true;

        // EditorSession dv = mainFrame.getActiveEditorSession();
        // if (dv == null) return false;
        //		
        // List views = dv.getViews();
        // return !views.isEmpty();
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
        EditorSession dv = mainFrame.getActiveEditorSession();

        // hack till i find out how to do the enabling correctly
        if (dv == null)
            return;

        for (View view : dv.getViews()) {
            Grid g = view.getGrid();
            view.postGraphCleared(new GraphEvent(dv.getGraph()));
            view.setGraph(dv.getGraph());
            view.setGrid(g);
            mainFrame.fireSessionChanged(dv);
        }

        mainFrame.updateActions();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
