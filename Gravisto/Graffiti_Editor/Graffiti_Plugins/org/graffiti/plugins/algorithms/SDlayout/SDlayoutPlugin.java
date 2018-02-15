// =============================================================================
//
//   SDlayoutPlugin.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.SDlayout;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class SDlayoutPlugin extends GenericPluginAdapter {

    /**
     * Creates a new SDlayoutPlugin object.
     */
    public SDlayoutPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new SDlayout();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("Planarity", algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
