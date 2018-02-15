// =============================================================================
//
//   FRSpringPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FRSpringPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * The plugin class for the variants of spring embedders of Fruchterman and
 * Reingold.
 * 
 * @author matzeder
 */
public class FRSpringPlugin extends GenericPluginAdapter {

    /**
     * Creates a new FRSpringPlugin object.
     */
    public FRSpringPlugin() {
        this.algorithms = new Algorithm[3];

        this.algorithms[0] = new FRSpringAlgorithmStandardPreserveEdgeCrossings();
        this.algorithms[1] = new FRSpringAlgorithmAllParams();
        this.algorithms[2] = new FRSpringAlgorithmStandard();

    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, new Algorithm[0],
                new PluginPathNode[] { new PluginPathNode(
                        "Fruchterman & Reingold", algorithms, null) });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
