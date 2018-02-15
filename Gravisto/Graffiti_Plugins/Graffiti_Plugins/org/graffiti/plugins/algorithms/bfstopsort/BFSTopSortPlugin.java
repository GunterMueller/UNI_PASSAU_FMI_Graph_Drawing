// =============================================================================
//
//   BFSTopSortPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSTopSortPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.bfstopsort;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A simple BFSTopSort plugin.
 * 
 * @version $Revision: 5766 $
 */
public class BFSTopSortPlugin extends GenericPluginAdapter {

    /**
     * Creates a new BFSTopSortPlugin object.
     */
    public BFSTopSortPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new BFSTopSort();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
