// =============================================================================
//
//   DummyLayout.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DummyLayout.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.layout;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class is a dummy-implementation. It only updates the graph (aligns all
 * nodes to a grid).
 * 
 * @author Ferdinand H&uuml;bner
 */
public class DummyLayout extends AbstractAlgorithm implements LayoutAlgorithm {
    private final String ALGORITHM_NAME = "Dummy-Layout (Only update the graph)";
    private SugiyamaData data;

    public boolean supportsArbitraryXPos() {
        return false;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        CoordinatesUtil.updateGraph(this.graph, data);
        graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
