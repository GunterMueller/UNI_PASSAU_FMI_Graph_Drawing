// =============================================================================
//
//   TreeWidthPlugin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * This class represents the plugin which contains algorithms to compute the
 * treeWidth and make a tree-decomposition for a graph.
 * 
 * @author wangq
 * @version $Revision: 1000 $
 */
public class TreeWidthPlugin extends GenericPluginAdapter {

    /**
     * Creates a new instance of the class.
     */
    public TreeWidthPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new TreeWidthAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
