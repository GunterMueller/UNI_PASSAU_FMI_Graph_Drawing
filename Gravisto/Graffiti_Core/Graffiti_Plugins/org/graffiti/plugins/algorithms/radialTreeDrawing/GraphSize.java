// =============================================================================
//
//   GraphSizeInterface.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.graph.Graph;

/**
 * This abstract class generalises all size metrics for graphs.
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public abstract class GraphSize {

    /**
     * Returns the size of the passed graph according to a certain metric.
     * 
     * @param graph
     *            a graph
     * @return the graph size
     */
    public abstract double getGraphSize(Graph graph);
}
