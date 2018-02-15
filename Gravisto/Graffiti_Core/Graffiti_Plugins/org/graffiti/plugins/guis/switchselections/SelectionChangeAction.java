// =============================================================================
//
//   SelectionChangeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionChangeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.switchselections;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.selection.Selection;
import org.graffiti.session.EditorSession;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class SelectionChangeAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 5776600348691541324L;

    /** DOCUMENT ME! */
    private EditorSession session;

    /** DOCUMENT ME! */
    private Selection selection;

    /**
     * Creates a new SelectionChangeAction object.
     * 
     * @param sel
     *            DOCUMENT ME!
     * @param sess
     *            DOCUMENT ME!
     */
    public SelectionChangeAction(Selection sel, EditorSession sess) {
        super(sel.getName(), null);
        this.session = sess;
        this.selection = sel;
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return super.enabled;
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            Selection clonedSel = (Selection) selection.clone();
            clonedSel.setName(selection.getName());
            this.session.getSelectionModel().setActiveSelection(clonedSel);
        } catch (CloneNotSupportedException cnse) {
            // it is clonable!
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
