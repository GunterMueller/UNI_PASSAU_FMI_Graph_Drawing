// =============================================================================
//
//   OnePlanarPlugin.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class OnePlanarPlugin extends GenericPluginAdapter {

    /**
     * Adds the algorithm to create and analyze random 1-planar graphs
     */
    public OnePlanarPlugin() {
        this.algorithms = new Algorithm[]{new AnalyzeRandomOnePlanar()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("1-planar", algorithms, null);
    }
    
    

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
