// =============================================================================
//
//   RadialDrawingPlugin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class RadialTreeDrawingPlugin extends GenericPluginAdapter {

    public RadialTreeDrawingPlugin() {

        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new AdvancedRadialDrawing();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(TREES, algorithms, null);
    }

}
