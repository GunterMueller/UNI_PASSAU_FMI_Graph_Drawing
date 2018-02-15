// =============================================================================
//
//   MCMCrossMinAnimation.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugins.algorithms.chebyshev.cores.AlgorithmCore;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class MCMCrossMinAnimation extends AbstractAnimation {
    private AuxGraph graph;
    private Iterator<Step> iterator;
    private boolean isAnimated;
    private AlgorithmCore core;

    protected MCMCrossMinAnimation(SugiyamaData data,
            AlgorithmParameters parameters, boolean isAnimated) {
        graph = new AuxGraph(data, parameters);
        LinkedList<Step> steps = parameters.createSteps(graph);
        if (parameters.isDebug()) {
            steps.addFirst(new HelloStep());
        }
        // System.out.println("Expected steps: " + steps.size());
        iterator = steps.iterator();
        this.isAnimated = isAnimated;
        core = parameters.createCore(graph);
    }

    @Override
    public boolean hasNextStep() {
        return iterator.hasNext();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    protected void performNextStep() {
        iterator.next().execute(core);

        if (isAnimated || !iterator.hasNext()) {
            graph.apply();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
