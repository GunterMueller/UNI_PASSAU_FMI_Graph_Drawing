// =============================================================================
//
//   TrivialGridRestrictedPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TrivialGridRestrictedPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.trivialgridrestricted;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5772 $
 */
public class TrivialGridRestrictedPlugin extends GenericPluginAdapter {

    /**
     * Creates a new TrivialGridRestrictedPlugin object.
     */
    public TrivialGridRestrictedPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new TrivialGridRestrictedAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
