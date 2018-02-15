// =============================================================================
//
//   NumberNodesPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NumberNodesPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.numbernodes;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5772 $
 */
public class NumberNodesPlugin extends GenericPluginAdapter {

    /**
     * Creates a new NumberNodesPlugin object.
     */
    public NumberNodesPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new NumberNodesAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
