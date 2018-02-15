// =============================================================================
//
//   IntervalPlugin.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

// =============================================================================
//
//   BFSPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSPlugin.java 1010 2006-01-04 09:21:57Z forster $

package org.graffiti.plugins.algorithms.interval;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * A plugin to recognize interval graphs and return an interval representation.
 * 
 */
public class IntervalPlugin extends GenericPluginAdapter {

    /**
     * Creates a new IntervalPlugin object.
     */
    public IntervalPlugin() {
        this.algorithms = new Algorithm[2];
        this.algorithms[0] = new IntervalRecognition();
        this.algorithms[1] = new CreateIntervalGraph();
    }

    @Override
    public String getName() {
        return "Interval";
    }

}
