// =============================================================================
//
//   PermutationGraphPlugin.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;


/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */

public class PermutationGraphPlugin extends GenericPluginAdapter {
    public PermutationGraphPlugin() {
        //
        algorithms = new Algorithm[] {
                new PermutationGraphAlgorithm()
        };
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
