// =============================================================================
//
//   AbstractSubAlgorithm.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AbstractSubAlgorithm {
    protected BlockGraph graph;
    protected AlgorithmParameters parameters;
    protected SugiyamaData sugiyamaData;

    public final void setGraphAndParameters(BlockGraph graph,
            AlgorithmParameters parameters, SugiyamaData sugiyamaData) {
        this.graph = graph;
        this.parameters = parameters;
        this.sugiyamaData = sugiyamaData;
    }

    protected void init() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
