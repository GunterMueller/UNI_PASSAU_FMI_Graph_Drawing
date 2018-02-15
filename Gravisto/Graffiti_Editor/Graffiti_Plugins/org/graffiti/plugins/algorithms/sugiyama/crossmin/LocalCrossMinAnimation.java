// =============================================================================
//
//   LocalCrossMinAnimation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LocalCrossMinAnimation.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a <tt>Animation</tt> for the local cross-minimization
 * in the <tt>Sugiyama</tt>-algorithm.
 * <p>
 * In each step of the <tt>Animation</tt>, an iteration of the
 * <tt>CrossMinAlgorithm</tt> is executed.
 * 
 * @author Ferdinand H�bner
 */
public class LocalCrossMinAnimation extends AbstractAnimation {
    /** SugiyamaData for data-transfer with other phases */
    private SugiyamaData data;
    /** The number of iterations */
    private int iterations;
    /** Current iteration */
    private int currentIteration;
    /**
     * A reference to the calling algorithm - needed to call the method
     * <i>animationFinished()</i> to notify the caller
     */
    private AbstractLocalCrossMinAlgorithm caller;
    /** Controls if the animation is ready to execute its next step */
    private boolean ready = false;
    /** top-down or bottom-up */
    private boolean topDown = true;
    /**
     * in the last iteration, the last permutation of the nodes on the layers is
     * saved. If the top-down-pass creates a permutation with less crossings
     * than the bottom-up-pass, the result from the top-down-pass is used
     */
    private NodeLayers temporalLayer;
    /** Saves the last number of crossing edges */
    private int lastCrossings;

    /**
     * Default constructor for a <tt>LocalCrossMinAnimation</tt>.
     * 
     * @param data
     *            Essential <tt>SugiyamaData</tt>-Bean, that stores all the
     *            needed data for the algorithm
     * @param iterations
     *            How many iterations (steps) are configured
     * @param caller
     *            A reference to the calling instance of an implementation of
     *            <tt>AbstractLocalCrossMinAlgorithm</tt>, i.e.
     *            <tt>BaryCenter</tt>. This is needed, to be able to run an
     *            iteration
     */
    public LocalCrossMinAnimation(SugiyamaData data, int iterations,
            AbstractLocalCrossMinAlgorithm caller) {
        this.data = data;
        this.iterations = iterations;
        this.caller = caller;
        this.currentIteration = 0;
        this.ready = true;
    }

    /**
     * Returns <tt>true</tt>, if there are still iterations left, <tt>false</tt>
     * otherwise.
     */
    @Override
    public boolean hasNextStep() {
        return this.currentIteration < this.iterations;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    /**
     * Execute the next step in the <tt>CrossMinAlgorithm</tt> - One top-down
     * and bottom-up iteration.
     */
    @Override
    public void nextStep() {
        this.ready = false;

        // if the graph is cyclic we iterate in just one direction (topdown)
        // note: the implementation of processLayers(...) must support this!
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo(
                            caller.getName() + ": Iteration "
                                    + (this.currentIteration + 1) + " of "
                                    + this.iterations + " (top-down)");
            for (int j = 0; j < this.data.getLayers().getNumberOfLayers(); j++) {
                caller.processLayers(j, true, this.data.getLayers());
            }
            System.out.println("Top-down, crossings: "
                    + caller.computeCrossings());
            this.currentIteration++;
        } else if (topDown) {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo(
                            caller.getName() + ": Iteration "
                                    + (this.currentIteration + 1) + " of "
                                    + this.iterations + " (top-down)");
            for (int j = 1; j < this.data.getLayers().getNumberOfLayers(); j++) {
                caller.processLayers(j, true, this.data.getLayers());
            }
            System.out.println("Crossings: " + caller.computeCrossings());

            if (currentIteration == iterations - 1) {
                lastCrossings = caller.computeCrossings();
                temporalLayer = data.getLayers().clone();
            }
            topDown = false;
        } else {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo(
                            caller.getName() + ": Iteration "
                                    + (this.currentIteration + 1) + " of "
                                    + this.iterations + " (bottom-up)");
            for (int j = this.data.getLayers().getNumberOfLayers() - 2; j >= 0; j--) {
                caller.processLayers(j, false, this.data.getLayers());
            }

            System.out.println("Crossings: " + caller.computeCrossings());
            topDown = true;
            // Use the old Node-Layer, if it was a better one
            if (currentIteration == iterations - 1) {
                if (lastCrossings < caller.computeCrossings()) {
                    System.out.println("Using old layer instead of this one");
                    data.setLayers(temporalLayer);
                }
            }
            this.currentIteration++;
        }

        if (this.currentIteration == this.iterations) {
            this.caller.animationFinished();
        }

        this.ready = true;
    }

    /**
     * Doesn't do anything
     */
    @Override
    public void clear() {

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
