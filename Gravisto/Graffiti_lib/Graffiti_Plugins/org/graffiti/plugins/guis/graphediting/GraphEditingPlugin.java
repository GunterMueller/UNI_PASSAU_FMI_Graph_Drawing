// =============================================================================
//
//   GraphEditingPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphEditingPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * This plugin creates a new menu in Gravisto editor and provides some graph
 * editing operations.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-04-05 23:01:26 +0200 (Mi, 05 Apr 2006)
 *          $
 */
public class GraphEditingPlugin extends EditorPluginAdapter {

    /**
     * Creates a new Graph Editing Plugin.
     * 
     */
    public GraphEditingPlugin() {
        this.guiComponents = new GraffitiComponent[1];
        guiComponents[0] = new GraphEditingMenu();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
