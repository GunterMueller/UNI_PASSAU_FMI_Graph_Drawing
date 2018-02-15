// =============================================================================
//
//   CompactingPlugin.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CompactorPlugin extends GenericPluginAdapter {

    public CompactorPlugin() {
        algorithms = new Algorithm[] { new CompactorAlgorithm() };
    }
    
    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(SUGIYAMA, algorithms, null);
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
