// =============================================================================
//
//   UniversalSiftingAnimation.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.util.Collections;

import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.SugiyamaBenchmarkAdapter;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling.InitialLevelling;
import org.graffiti.plugins.algorithms.sugiyama.levelling.DummyLevelling;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class UniversalSiftingAnimation extends AbstractAnimation {
    private abstract class State {
        protected abstract void execute();
    }

    private final State initialLevellingState = new State() {
        @Override
        public void execute() {
            graph.getGraph().setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED,
                    true);
            Pair<Integer, Integer> pair = initialLevelling.execute();
            data
                    .putObject(SugiyamaData.INITIAL_CROSSING_COUNT, pair
                            .getFirst());
            data.putObject(SugiyamaData.INITIAL_LEVEL_COUNT, pair.getSecond());

            accumulatedTime = 0;

            if (terminalCondition.update(1, data)) {
                state = finishingState;
            } else {
                graph.prepare();

                state = coreState;
            }
        }
    };

    private final State coreState = new State() {
        @Override
        public void execute() {
            // System.out.println("Starting US-round...");
            long startTime = System.nanoTime();

            int delta = graph.siftingRound();

            long stopTime = System.nanoTime();

            accumulatedTime += stopTime - startTime;
            if (delta != 0) {
                accumulatedTimeUntilBreak += stopTime - startTime;
                // System.out.println("Time: " + ((stopTime - startTime) /
                // 1000000000.0) + "s");
            }

            if (terminalCondition.update(delta, data)) {
                state = finishingState;
            }
        }
    };

    private final State finishingState = new State() {
        @Override
        public void execute() {
            createAttributes();
            state = null;
            data.putObject(SugiyamaBenchmarkAdapter.CROSSMIN_TIME_KEY,
                    accumulatedTime);
            data.putObject(
                    SugiyamaBenchmarkAdapter.CROSSMIN_TIME_UNTIL_BREAK_KEY,
                    accumulatedTimeUntilBreak);
        }
    };

    private SugiyamaData data;

    private BlockGraph graph;

    private TerminalCondition terminalCondition;

    private InitialLevelling initialLevelling;

    private State state;

    private DummyLevelling dummyLevelling;

    private long accumulatedTime;

    private long accumulatedTimeUntilBreak;

    protected UniversalSiftingAnimation(SugiyamaData data,
            AlgorithmParameters parameters, boolean isAnimated) {
        this.data = data;
        graph = Incubator.create(data.getGraph(), parameters);
        terminalCondition = parameters.createTerminalCondition(graph, data);
        initialLevelling = parameters.createInitialLevelling(graph, data);
        dummyLevelling = new DummyLevelling();
        dummyLevelling.attach(data.getGraph());
        dummyLevelling.setData(data);
        state = initialLevellingState;
        accumulatedTime = 0;
        accumulatedTimeUntilBreak = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNextStep() {
        return state != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReady() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performNextStep() {
        state.execute();
    }

    private void createAttributes() {
        graph.getGraph().getListenerManager().transactionStarted(this);
        // System.out.println(graph.debugToString());

        graph.exportLevels();

        dummyLevelling.execute();

        graph.exportOrdering();

        NodeLayers levels = data.getLayers();
        int levelCount = levels.getNumberOfLayers();
        for (int level = 0; level < levelCount; level++) {
            Collections.sort(levels.getLayer(level), new XPosComparator());
        }

        levels.normalizeLayers();

        graph.stripOwnAttributes();

        graph.getGraph().getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
