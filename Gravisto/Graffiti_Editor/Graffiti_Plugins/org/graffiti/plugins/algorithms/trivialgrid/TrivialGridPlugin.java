// =============================================================================
//
//   TrivialGridPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TrivialGridPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.trivialgrid;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5766 $
 */
public class TrivialGridPlugin extends GenericPluginAdapter {

    /**
     * Creates a new TrivialGridPlugin object.
     */
    public TrivialGridPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new TrivialGridAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
