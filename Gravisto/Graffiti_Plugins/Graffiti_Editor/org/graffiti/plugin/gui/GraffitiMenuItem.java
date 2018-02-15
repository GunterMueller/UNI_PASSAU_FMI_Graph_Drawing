// =============================================================================
//
//   GraffitiMenuItem.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiMenuItem.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.graffiti.editor.MainFrame;

/**
 * TODO
 */
public class GraffitiMenuItem extends JMenuItem implements GraffitiComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 8938169395007853640L;
    /** The id of the component the menu item prefers to be inserted in. */
    protected String preferredComponent;

    /**
     * Constructs a new <code>GraffitiMenuItem</code>.
     * 
     * @param prefComp
     *            DOCUMENT ME!
     * @param a
     *            DOCUMENT ME!
     */
    public GraffitiMenuItem(String prefComp, Action a) {
        super(a);
        this.preferredComponent = prefComp;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#setMainFrame(org.graffiti.editor.MainFrame)
     */
    public void setMainFrame(MainFrame mf) {
    }

    /**
     * Returns the id of the component the menu item prefers to be inserted in.
     * 
     * @return the id of the component the menu item prefers to be inserted in.
     */
    public String getPreferredComponent() {
        return this.preferredComponent;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
