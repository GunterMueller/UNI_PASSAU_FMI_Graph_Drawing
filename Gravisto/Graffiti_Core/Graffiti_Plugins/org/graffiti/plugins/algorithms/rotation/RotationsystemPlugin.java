// =============================================================================
//
//   RotationsystemPlugin.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.rotation;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class RotationsystemPlugin extends GenericPluginAdapter{
    
    /**
     * Creates a new RotationsystemPlugin object.
     */
    public RotationsystemPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new Rotationsystem();
    }
    
    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode("Rotationsystem", algorithms, null);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
