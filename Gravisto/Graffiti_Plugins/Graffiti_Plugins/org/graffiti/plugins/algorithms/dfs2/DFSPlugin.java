// =============================================================================
//
//   DFSPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DFSPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.dfs2;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Diana Lucic
 * @version $Revision: 5766 $ $Date: 2009-10-19 20:32:39 +0200 (Mo, 19 Okt 2009)
 *          $
 */
public class DFSPlugin extends GenericPluginAdapter {
    /**
     * Creates a new DFSPlugin object.
     */
    public DFSPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new DFS();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(BETA, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
