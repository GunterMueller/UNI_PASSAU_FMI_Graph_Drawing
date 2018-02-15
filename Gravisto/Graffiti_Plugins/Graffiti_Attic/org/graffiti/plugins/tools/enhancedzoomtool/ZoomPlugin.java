// =============================================================================
//
//   ZoomPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ZoomPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.tools.enhancedzoomtool;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * This plugin contains the standard editing tools.
 * 
 * @version $Revision: 5772 $
 */
public class ZoomPlugin extends EditorPluginAdapter {
    /** The button for the zoom tool */
    private GraffitiComponent zoomButton;

    /**
     * Creates a new StandardTools object.
     */
    public ZoomPlugin() {
        zoomButton = new ZoomChangeComponent("defaultToolbar");
        guiComponents = new GraffitiComponent[1];
        guiComponents[0] = zoomButton;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
