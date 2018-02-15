// =============================================================================
//
//   GraffitiToggleButton.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiToggleButton.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.graffiti.editor.MainFrame;

/**
 * Abstract class for all ToggleButtons that should be used as
 * <code>GraffitiComponents</code>. Provides an implementation for the
 * <code>getPreferredComponent()</code> method.
 * 
 * @version $Revision: 5768 $
 */
public abstract class GraffitiToggleButton extends JToggleButton implements
        GraffitiComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 4158694363909086704L;
    /** The preferred component of this button. */
    protected String preferredComponent;

    /**
     * Creates a new GraffitiToggleButton object.
     */
    public GraffitiToggleButton() {
    }

    /**
     * Creates a new GraffitiToggleButton object.
     * 
     * @param preferredComp
     *            DOCUMENT ME!
     */
    public GraffitiToggleButton(String preferredComp) {
        preferredComponent = preferredComp;
    }

    /**
     * Creates a new GraffitiToggleButton object.
     * 
     * @param preferredComp
     *            DOCUMENT ME!
     * @param text
     *            DOCUMENT ME!
     */
    public GraffitiToggleButton(String preferredComp, String text) {
        super(text);
    }

    /**
     * Creates a new GraffitiToggleButton object.
     * 
     * @param preferredComp
     *            DOCUMENT ME!
     * @param i
     *            DOCUMENT ME!
     */
    public GraffitiToggleButton(String preferredComp, Icon i) {
        super(i);
        preferredComponent = preferredComp;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiComponent#setMainFrame(org.graffiti.editor.MainFrame)
     */
    public void setMainFrame(MainFrame mf) {
    }

    /**
     * Returns the id of the component the button prefers to be inserted in.
     * 
     * @return the id of the component the button prefers to be inserted in.
     */
    public String getPreferredComponent() {
        return this.preferredComponent;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
