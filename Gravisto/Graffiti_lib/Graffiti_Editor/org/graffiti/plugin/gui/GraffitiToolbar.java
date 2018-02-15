// =============================================================================
//
//   GraffitiToolbar.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiToolbar.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import javax.swing.JToolBar;

import org.graffiti.editor.MainFrame;

/**
 * TODO
 */
public class GraffitiToolbar extends JToolBar implements GraffitiContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 5561666032583201708L;

    /** The id of the toolbar. */
    protected String id;

    /** The id of the component the toolbar prefers to be inserted in. */
    protected String preferredComponent;

    /**
     * Standardconstructor for <code>GraffitiToolbar</code>.
     */
    public GraffitiToolbar() {
        this("");
    }

    /**
     * Constructor that sets the id of this <code>GraffitiToolbar</code>.
     * 
     * @param name
     *            DOCUMENT ME!
     */
    public GraffitiToolbar(String name) {
        this.id = name;
        this.preferredComponent = "toolbarPanel";

        // setFloatable(false);
    }

    /**
     * Returns the id of this toolbar.
     * 
     * @return the id of this toolbar.
     */
    public String getId() {
        return this.id;
    }

    /*
     * @see
     * org.graffiti.plugin.gui.GraffitiComponent#setMainFrame(org.graffiti.editor
     * .MainFrame)
     */
    public void setMainFrame(MainFrame mf) {
    }

    /**
     * Returns the id of the component the toolbar prefers to be inserted.
     * 
     * @return the id of the component the toolbar prefers to be inserted.
     */
    public String getPreferredComponent() {
        return this.preferredComponent;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
