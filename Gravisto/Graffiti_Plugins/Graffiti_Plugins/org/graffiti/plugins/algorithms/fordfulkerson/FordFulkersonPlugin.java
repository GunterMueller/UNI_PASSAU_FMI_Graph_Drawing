// =============================================================================
//
//   FordFulkersonPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FordFulkersonPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.fordfulkerson;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a dijkstra algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class FordFulkersonPlugin extends GenericPluginAdapter {

    /**
     * Creates a new FordFulkersonPlugin object.
     */
    public FordFulkersonPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new FordFulkersonAlgorithm();
        // this.algorithms[1] = new RandomEdgeWeightsAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
