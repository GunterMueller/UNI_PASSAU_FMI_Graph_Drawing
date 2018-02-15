// =============================================================================
//
//   ConnectSpecialPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConnectSpecialPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.connectspecial;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A simple ConnectSpecial plugin.
 * 
 * @version $Revision: 5772 $
 */
public class ConnectSpecialPlugin extends GenericPluginAdapter {

    /**
     * Creates a new ConnectSpecialPlugin object.
     */
    public ConnectSpecialPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new ConnectSpecial();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
