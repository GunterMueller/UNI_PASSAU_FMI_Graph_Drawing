// =============================================================================
//
//   ConnectPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConnectPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.connect;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A simple Connect plugin.
 * 
 * @version $Revision: 5772 $
 */
public class ConnectPlugin extends GenericPluginAdapter {

    /**
     * Creates a new ConnectPlugin object.
     */
    public ConnectPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new Connect();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
