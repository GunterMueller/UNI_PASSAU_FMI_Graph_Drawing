// =============================================================================
//
//   STPlugin.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: STPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.stnumbering;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author Diana Lucic
 */
public class STPlugin extends GenericPluginAdapter {
    /**
     * Creates a new STPlugin object.
     */
    public STPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new ST();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
