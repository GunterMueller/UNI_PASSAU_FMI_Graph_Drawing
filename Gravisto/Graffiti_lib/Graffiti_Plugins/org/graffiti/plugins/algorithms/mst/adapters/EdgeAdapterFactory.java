// =============================================================================
//
//   EdgeFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.mst.adapters;

import org.graffiti.graph.Edge;

/**
 * Factory for edge adapters.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class EdgeAdapterFactory {
    protected final float defaultWeight;

    protected final boolean createColoredEdges;

    /**
     * Creates a factory that will produce edge adapters with a default weight
     * of 1 being uncolored.
     * 
     */
    public EdgeAdapterFactory() {
        this(1f, false);
    }

    /**
     * Creates a factory that will produce edge adapters with the specified
     * default weight being colored or uncolored depending of the value of
     * <tt>createColoredEdges</tt>.
     * 
     * @param defaultWeight
     *            the default weight for edges that are unweighted.
     * @param createColoredEdges
     *            <tt>true</tt> if this factory produces colored edge adapters.
     */
    public EdgeAdapterFactory(float defaultWeight, boolean createColoredEdges) {
        this.defaultWeight = defaultWeight;
        this.createColoredEdges = createColoredEdges;
    }

    /**
     * Creates a new edge adapter for the specified edge.
     * 
     * @param edge
     *            the edge to be adapted.
     * @return an edge adapter for the specified edge.
     */
    public EdgeAdapter createAdapter(Edge edge) {
        return new EdgeAdapter(edge, defaultWeight, createColoredEdges);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
