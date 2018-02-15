// =============================================================================
//
//   Java2DPlugin.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugins.tools.scripted.ScriptedToolLoader;

/**
 * @author Wolfgang Brunner
 * @version $Revision$ $Date$
 */
public class ToricalPlugin extends EditorPluginAdapter {
    public ToricalPlugin() {
        views = new String[] { "org.graffiti.plugins.views.fast.torical.ToricalFastView" };
        ToolRegistry.get().registerTools(
                ScriptedToolLoader.loadTools(this,
                        ToricalFastView.TORICAL_FAST_VIEW_FAMILY));

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
