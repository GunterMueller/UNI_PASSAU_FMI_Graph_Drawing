// =============================================================================
//
//   KKSpringPluginRestricted.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KKSpringPluginRestricted.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderrestricted;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5766 $
 */
public class KKSpringPluginRestricted extends GenericPluginAdapter {

    /**
     * Creates a new KKSpringPluginRestricted object.
     */
    public KKSpringPluginRestricted() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new KKSpringRestrictedAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(ARBITRARY, algorithms, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
