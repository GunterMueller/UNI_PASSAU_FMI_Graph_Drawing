// =============================================================================
//
//   DefaultView.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.graffiti.core.Bundle;
import org.graffiti.graphics.Dash;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugins.tools.scripted.ScriptedToolLoader;
import org.graffiti.plugins.views.fast.dialog.FastViewOptionsMenu;
import org.graffiti.plugins.views.fast.label.CommandListFactory;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastViewPlugin extends EditorPluginAdapter {
    public static final Color SELECTION_COLOR = Color.RED;
    public static final Color HOVER_COLOR = new Color(255, 200, 0);
    public static final Color CONTROL_POINT_COLOR = new Color(124, 0, 0);
    public static final Color ROTATION_HUB_COLOR = Color.YELLOW;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final Color DEFAULT_GRID_COLOR = new Color(192, 192, 192);
    public static final Stroke DEFAULT_GRID_STROKE = new BasicStroke(1.0f);
    public static final int NODE_HANDLE_SIZE = 5;
    public static final int EDGE_BEND_SIZE = 5;
    public static final int EDGE_CONTROLPOINT_SIZE = 3;
    public static final Dash DEFAULT_DASH = new Dash();

    private static final Bundle bundle = Bundle.getBundle(FastViewPlugin.class);

    public static String getString(String key) {
        return bundle.getString(key);
    }

    public FastViewPlugin() {
        super();
        CommandListFactory.createSharedContext();

        ToolRegistry.get().registerTools(
                ScriptedToolLoader.loadTools(this, FastView.FAST_VIEW_FAMILY));

        guiComponents = new GraffitiComponent[] { new FastViewOptionsMenu() };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
