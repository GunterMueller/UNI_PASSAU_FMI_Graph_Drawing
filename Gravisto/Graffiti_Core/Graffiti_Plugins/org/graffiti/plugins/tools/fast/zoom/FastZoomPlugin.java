// =============================================================================
//
//   FastZoomPlugin.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.fast.zoom;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastZoomPlugin extends EditorPluginAdapter {
    private FastZoomTool zoomTool;

    public FastZoomPlugin() {
        zoomTool = new FastZoomTool();
        guiComponents = new GraffitiComponent[] { zoomTool };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
