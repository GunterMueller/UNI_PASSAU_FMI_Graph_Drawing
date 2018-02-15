// =============================================================================
//
//   IsomorphismPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides different algorithms to calculate graph isomorphism.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class IsomorphismPlugin extends GenericPluginAdapter {

    /**
     * Creates a new <code>IsomorphismPlugin</code> object.
     */
    public IsomorphismPlugin() {
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new BabaiKucera();
        this.algorithms[1] = new VF2();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ISOMORPHISM, algorithms, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
