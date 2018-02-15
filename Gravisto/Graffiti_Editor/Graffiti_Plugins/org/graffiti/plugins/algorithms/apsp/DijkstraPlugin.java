// =============================================================================
//
//   DijkstraPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DijkstraPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.apsp;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a dijkstra algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class DijkstraPlugin extends GenericPluginAdapter {

    /**
     * Creates a new DijkstraPlugin object.
     */
    public DijkstraPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new DijkstraAlgorithm();
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
