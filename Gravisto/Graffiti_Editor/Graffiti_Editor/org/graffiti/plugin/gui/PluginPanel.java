// =============================================================================
//
//   PluginPanel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginPanel.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import java.awt.Component;

/**
 * The panel to which plugins can add bigger view-like components.
 * 
 * @version $Revision: 5768 $
 */
public class PluginPanel extends AbstractGraffitiContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 4163512739506314629L;

    /**
     * Creates a new PluginPanel object.
     */
    public PluginPanel() {
        id = "pluginPanel";
        preferredComponent = "";
    }

    /**
     * @see java.awt.Container#addImpl(Component, Object, int)
     */
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        this.setVisible(true);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
