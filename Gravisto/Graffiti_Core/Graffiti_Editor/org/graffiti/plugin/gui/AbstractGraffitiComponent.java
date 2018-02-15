// =============================================================================
//
//   AbstractGraffitiComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraffitiComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import javax.swing.JPanel;

import org.graffiti.editor.MainFrame;

/**
 * Abstract class for default containers.
 * 
 * @version $Revision: 5768 $
 */
public abstract class AbstractGraffitiComponent extends JPanel implements
        GraffitiComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 3884722947525157168L;
    /**
     * The component wherer the current <code>AbstractGraffitiContainer</code>
     * prefers to be inserted.
     */
    protected String preferredComponent;

    /**
     * Constructs a new <code>AbstractGraffitiContainer</code>.
     */
    protected AbstractGraffitiComponent() {
        super();
    }

    /**
     * Constructs a new <code>AbstractGraffitiContainer</code>.
     * 
     * @param prefComp
     *            DOCUMENT ME!
     */
    protected AbstractGraffitiComponent(String prefComp) {
        super();
        this.preferredComponent = prefComp;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#setMainFrame(org.graffiti.editor.MainFrame)
     */
    public void setMainFrame(MainFrame mf) {
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getPreferredComponent() {
        return this.preferredComponent;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
