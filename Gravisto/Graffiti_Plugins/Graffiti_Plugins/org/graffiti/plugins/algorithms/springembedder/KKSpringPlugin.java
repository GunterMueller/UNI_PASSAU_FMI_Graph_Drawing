// =============================================================================
//
//   KKSpringPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KKSpringPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedder;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5766 $
 */
public class KKSpringPlugin extends GenericPluginAdapter {

    /**
     * Creates a new KKSpringPlugin object.
     */
    public KKSpringPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new KKSpringAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, algorithms, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
