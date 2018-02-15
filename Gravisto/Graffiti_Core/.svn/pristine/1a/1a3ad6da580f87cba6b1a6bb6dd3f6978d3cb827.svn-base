// =============================================================================
//
//   AngleCalculationPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * The plugin class for the angle calculation.
 * 
 * @author Mirka Kossak
 */
public class PlanarAngleGraphPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>PlanarAngleGraphPlugin</code>.
     */
    public PlanarAngleGraphPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new PlanarAngleGraphAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(PLANAR, algorithms, null);
    }

}
