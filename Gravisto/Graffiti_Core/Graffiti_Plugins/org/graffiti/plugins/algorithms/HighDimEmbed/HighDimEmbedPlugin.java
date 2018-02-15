// =============================================================================
//
// HighDimEmbedPlugin.java
//
// Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
//$Id: HighDimEmbedPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.HighDimEmbed;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a high dimensional embeddeder drawing algorithm according to Yehuda
 * Koren.
 * 
 * @version $Revision: 5766 $
 */
public class HighDimEmbedPlugin extends GenericPluginAdapter {

    /**
     * Creates a new HighDimEmbedPlugin object.
     */
    public HighDimEmbedPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new HighDimEmbedAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
