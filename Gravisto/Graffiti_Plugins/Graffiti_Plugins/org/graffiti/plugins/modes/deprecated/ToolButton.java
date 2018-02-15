// =============================================================================
//
//   ToolButton.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ToolButton.java 1002 2006-01-03 13:21:54Z forster $

package org.graffiti.plugins.modes.deprecated;

import javax.swing.ImageIcon;

import org.graffiti.plugin.gui.GraffitiToggleButton;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: forster $
 * @version $Revision: 1002 $ $Date: 2006-01-03 14:21:54 +0100 (Di, 03 Jan 2006)
 *          $
 * @deprecated
 */
@Deprecated
public class ToolButton extends GraffitiToggleButton implements
        GraffitiToolComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -3197619286808536159L;
    /** The tool this button is identified with. */
    private Tool tool;

    /**
     * Constructor that sets the buttons tool to the given <code>Tool</code>.
     * 
     * @param t
     *            DOCUMENT ME!
     */
    public ToolButton(Tool t) {
        this.tool = t;
    }

    /**
     * Creates a new ToolButton object.
     * 
     * @param t
     *            DOCUMENT ME!
     * @param preferredComponent
     *            DOCUMENT ME!
     */
    public ToolButton(Tool t, String preferredComponent) {
        super(preferredComponent);
        this.tool = t;
    }

    /**
     * Creates a new ToolButton object.
     * 
     * @param t
     *            DOCUMENT ME!
     * @param preferredComponent
     *            DOCUMENT ME!
     * @param icon
     *            DOCUMENT ME!
     */
    public ToolButton(Tool t, String preferredComponent, ImageIcon icon) {
        super(preferredComponent, icon);
        this.tool = t;
    }

    /**
     * Creates a new ToolButton object.
     * 
     * @param t
     *            DOCUMENT ME!
     * @param preferredComponent
     *            DOCUMENT ME!
     * @param text
     *            DOCUMENT ME!
     */
    public ToolButton(Tool t, String preferredComponent, String text) {
        super(text);
        this.tool = t;
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiContainer#getId()
     */
    public String getId() {
        return getClass().getName();
    }

    /**
     * Returns the tool this button is identified with.
     * 
     * @return the tool this button is identified with.
     */
    public Tool getTool() {
        return tool;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
