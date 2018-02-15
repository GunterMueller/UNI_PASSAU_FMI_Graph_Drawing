// =============================================================================
//
//   SugiyamaPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.sugiyama.incremental.IncrementalSugiyama;

/**
 * This class acts as a plugin-adapter for the sugiyama-framework
 * 
 * @author Ferdinand H�bner
 */
public class SugiyamaPlugin extends GenericPluginAdapter {

    /**
     * Default constructor - creates a new Sugiyama-Algorithm
     */
    public SugiyamaPlugin() {
        this.algorithms = new Algorithm[3];
        this.algorithms[0] = new Sugiyama();
        this.algorithms[1] = new IncrementalSugiyama();
        this.algorithms[2] = new SugiyamaAttributesCreator();
        ((SugiyamaAlgorithm) this.algorithms[2])
                .setData(((SugiyamaAlgorithm) this.algorithms[0]).getData());
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(SUGIYAMA, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
