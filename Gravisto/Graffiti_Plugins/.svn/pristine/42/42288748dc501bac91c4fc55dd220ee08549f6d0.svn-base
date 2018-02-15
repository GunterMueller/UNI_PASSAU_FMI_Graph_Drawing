// =============================================================================
//
//   PlanarFASPlugin.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class PlanarFASPlugin extends GenericPluginAdapter {

    /**
     * Creates a new PlanarFASPlugin object.
     */
    public PlanarFASPlugin() {
        this.algorithms = new Algorithm[3];
        this.algorithms[0] = new PlanarFAS(false, true);
        this.algorithms[1] = new PlanarFAS(true, true);
        this.algorithms[2] = new PlanarFASWithSCC(false, true);
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode("Feedback Arc Set",
                        algorithms, null) });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
