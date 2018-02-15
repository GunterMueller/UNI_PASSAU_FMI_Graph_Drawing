// =============================================================================
//
//   ExtendedDFSPlugin.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.extendedDFS;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class ExtendedDFSPlugin extends GenericPluginAdapter{
    
    /**
     * Creates a new ExtendedDFSPlugin object.
     */
    public ExtendedDFSPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new ExtendedDFS();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("DFS", algorithms, null);
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
