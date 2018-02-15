// =============================================================================
//
//   IRPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IRPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.itairodeh;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Diana Lucic
 */
public class IRPlugin extends GenericPluginAdapter {
    /**
     * Creates a new IRPlugin object.
     */
    public IRPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new IR();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(
                TREES,
                new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(MST, algorithms, null) });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
