// =============================================================================
//
//   ConfluentDrawingPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConfluentDrawingPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides a confluent drawing algorithm.
 * 
 * @author Xiaolei Zhang
 * @version $Revision: 5772 $
 */
public class ConfluentDrawingPlugin extends GenericPluginAdapter {

    /**
     * Creates a new ConfluentDrawingPlugin object.
     */
    public ConfluentDrawingPlugin() {
        this.algorithms = new Algorithm[1];
        this.algorithms[0] = new ConfluentDrawingAlgorithm();
    }

    @Override
    public PluginPathNode getPathInformation() {
        return new PluginPathNode(DELETE, algorithms, null);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
