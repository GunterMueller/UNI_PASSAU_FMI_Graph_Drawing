// =============================================================================
//
//   RandomEdgeLabelingPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomEdgeLabelingPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.randomizedlabeling;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a random labeling algorithm.
 * 
 * @version $Revision: 5772 $
 */
public class RandomEdgeLabelingPlugin extends GenericPluginAdapter {

    /**
     * Creates a new RandomEdgeLabelingPlugin object.
     */
    public RandomEdgeLabelingPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new RandomEdgeLabelingAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
