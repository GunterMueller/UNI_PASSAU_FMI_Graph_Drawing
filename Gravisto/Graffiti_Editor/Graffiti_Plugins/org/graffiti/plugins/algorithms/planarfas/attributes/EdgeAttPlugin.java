// =============================================================================
//
//   ReverseEdgePlugin.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas.attributes;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * @author ecklbarb
 * @version $Revision$ $Date$
 */
public class EdgeAttPlugin  extends GenericPluginAdapter {
    
    public EdgeAttPlugin()
    {
        this.attributes = new Class[1];
        this.attributes[0] = EdgeAtt.class;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
