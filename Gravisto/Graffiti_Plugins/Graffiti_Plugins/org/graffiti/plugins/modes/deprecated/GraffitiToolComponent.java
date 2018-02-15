// =============================================================================
//
//   GraffitiToolComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiToolComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.awt.event.ActionListener;

import org.graffiti.plugin.gui.GraffitiContainer;

/**
 * @deprecated
 */
@Deprecated
public interface GraffitiToolComponent extends GraffitiContainer {

    /**
     * Returns the tool this button is identified with.
     * 
     * @return the tool this button is identified with.
     */
    public Tool getTool();

    /**
     * DOCUMENT ME!
     * 
     * @param al
     *            DOCUMENT ME!
     */
    public void addActionListener(ActionListener al);

    /**
     * DOCUMENT ME!
     * 
     * @param al
     *            DOCUMENT ME!
     */
    public void removeActionListener(ActionListener al);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
