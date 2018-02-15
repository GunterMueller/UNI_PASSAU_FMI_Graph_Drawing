// =============================================================================
//
//   GraphGeneratorPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomBiconGraphGeneratorPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.circulardrawing.benchmark;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides some graph generator algorithms.
 * 
 * @version $Revision: 5766 $
 */
public class RandomBiconGraphGeneratorPlugin extends GenericPluginAdapter {

    /**
     * Creates a new GraphGeneratorPlugin object.
     */
    public RandomBiconGraphGeneratorPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new RandomBiconGraphGenerator();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
