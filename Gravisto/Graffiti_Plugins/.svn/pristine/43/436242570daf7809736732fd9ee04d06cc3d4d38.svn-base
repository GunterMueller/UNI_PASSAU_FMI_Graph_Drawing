// =============================================================================
//
//   StackQueueLayoutPlugin.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.gui.GraffitiComponent;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueLayoutPlugin extends EditorPluginAdapter {
    private static final Bundle BUNDLE = Bundle
            .getBundle(StackQueueLayoutPlugin.class);

    private StackQueueToolbar toolbar;

    /**
     * Creates a new <code>StackQueueLayoutPlugin</code> object.
     */
    public StackQueueLayoutPlugin() {
        toolbar = new StackQueueToolbar();

        algorithms = new Algorithm[] { new StackQueueLayoutAlgorithm() };
        guiComponents = new GraffitiComponent[] { toolbar };
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, algorithms, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
