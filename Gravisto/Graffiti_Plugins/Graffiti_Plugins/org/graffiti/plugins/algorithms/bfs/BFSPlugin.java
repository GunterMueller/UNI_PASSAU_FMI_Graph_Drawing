// =============================================================================
//
//   BFSPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.bfs;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A simple BFS plugin.
 * 
 * @version $Revision: 5766 $
 */
public class BFSPlugin extends GenericPluginAdapter {
    /**
     * Creates a new BFSPlugin object.
     */
    public BFSPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new BFSNew(new BFSNumberVisitor());
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}
