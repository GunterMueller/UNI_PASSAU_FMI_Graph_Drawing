// =============================================================================
//
//   CyclicLevelingPlugin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Lovasz
 * @version $Revision$ $Date$
 */
public class CyclicLevelingPlugin extends GenericPluginAdapter {

    public CyclicLevelingPlugin() {
        this.algorithms = new Algorithm[4];
        this.algorithms[0] = new CyclicBFSLeveling();
        this.algorithms[1] = new OptimalLeveling();
        this.algorithms[2] = new CyclicSELeveling();
        this.algorithms[3] = new CyclicMSTLeveling();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Cyclic Leveling",
                        algorithms, null) });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
