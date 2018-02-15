// =============================================================================
//
//   FastZoomPlugin.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.stylemanager;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StyleManagerPlugin extends EditorPluginAdapter {

    public StyleManagerPlugin() {
        guiComponents = new GraffitiComponent[] { new StyleManager() };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
