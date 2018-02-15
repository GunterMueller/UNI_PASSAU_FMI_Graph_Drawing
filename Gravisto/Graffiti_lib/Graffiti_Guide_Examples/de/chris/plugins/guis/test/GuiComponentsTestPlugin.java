// =============================================================================
//
//   GuiComponentsTestPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GuiComponentsTestPlugin.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * This is a simple example for a GUI component plugin for Graffiti.
 * 
 * @author chris
 */
public class GuiComponentsTestPlugin extends EditorPluginAdapter {

    /**
     * Creates a new GuiComponentsPlugin object.
     */
    public GuiComponentsTestPlugin() {
        this.guiComponents = new GraffitiComponent[4];

        // menu example
        this.guiComponents[0] = new TestMenu();
        this.guiComponents[1] = new TestItem();

        // toolbar example
        this.guiComponents[2] = new TestToolbar();
        this.guiComponents[3] = new TestButton();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
