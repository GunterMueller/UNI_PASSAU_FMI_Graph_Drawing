// =============================================================================
//
//   GeoThicknessCalculationPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jun 22, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author ma
 * 
 *         Implementation of Algorithmus geometric thickness
 */
public class GeoThicknessCalculationPlugin extends GenericPluginAdapter {
    /**
     * Creates a new GeoThicknessCalculationPlugin object.
     */
    public GeoThicknessCalculationPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new GeoThicknessCalculationAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}
