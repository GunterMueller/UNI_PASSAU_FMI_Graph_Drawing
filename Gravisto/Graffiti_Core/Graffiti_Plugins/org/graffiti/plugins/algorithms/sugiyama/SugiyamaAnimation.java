// =============================================================================
//
//   SugiyamaAnimation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaAnimation.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.AbstractAnimation;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BilayerCrossCounter;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.BigNode;
import org.graffiti.plugins.algorithms.sugiyama.util.ConstraintsUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.DummyNodeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class adds support for animations to the sugiyama-framework.<br>
 * <br>
 * In one step of the animation, one phase-algorithm gets executed. If an
 * algorithm does support animations itself, a "sub-animation" will be executed.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class SugiyamaAnimation extends AbstractAnimation {
    /** The attached <tt>Graph</tt> */
    protected Graph graph;
    /** This array stores the algorithms that are executed */
    protected SugiyamaAlgorithm[] algorithms;
    /** Reference to <tt>SugiyamaData</tt> */
    protected SugiyamaData data;
    /**
     * Reference to an <tt>Animation</tt> returned by a phase-algorithm that
     * does support animations by itself. We have to go through this animation
     * before we continue with "our" next step in the animation.
     */
    protected Animation subAnimation;
    /** The current phase that is executed */
    protected int currentPhase;
    /**
     * <tt>true</tt>, if the current phase-algorithm is finished, <tt>false</tt>
     * if it is still busy computing...
     */
    protected boolean ready = false;
    /** This boolean controls support for sub-animations of the algorithms */
    protected boolean inSubAnimation;
    /** The logger */
    private static final Logger logger = Logger
            .getLogger(SugiyamaAnimation.class.getName());

    /**
     * Default constructor for a <tt>SugiyamaAnimation</tt>. The animation has
     * to access the chosen algorithms, as well as the <tt>SugiyamaData</tt> and
     * the <tt>Graph</tt> itself in order to work!
     * 
     * @param algorithms
     *            The selected <tt>SugiyamaAlgorithm</tt>s for each phase
     * @param data
     *            The <tt>SugiyamaData</tt>-Bean that stores essential data
     * @param graph
     *            The <tt>Graph</tt> that is used in the phases
     *            <tt>Sugiyama</tt>-algorithm. This is needed to reset
     *            <tt>Sugiyama</tt> after the animation is finished.
     */
    public SugiyamaAnimation(SugiyamaAlgorithm[] algorithms, SugiyamaData data,
            Graph graph) {
        super();
        this.algorithms = algorithms;
        this.data = data;
        this.graph = graph;
        this.currentPhase = 0;
        this.inSubAnimation = false;
        this.ready = true;
    }

    /**
     * Execute the next step in the sugiyama-algorithm: Executes the next
     * phase-algorithm
     */
    @Override
    public void nextStep() {
        ready = false;
        try {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo(
                            "Currently: " + algorithms[currentPhase].getName());
        } catch (Exception e) {
            // no gui so do nothing
        }

        executeFirstFourPhases();

        if (!this.inSubAnimation) {
            currentPhase++;
        }

        if (currentPhase == 2) {
            graph.getListenerManager().transactionStarted(this);
            if (data.gridActivated()) {
                CoordinatesUtil.addGrid(graph, data);
            }
            graph.getListenerManager().transactionFinished(this);
        }

        // clean up
        if (currentPhase >= 4) {
            graph.getListenerManager().transactionStarted(this);
            CoordinatesUtil.updateRealCoordinates(graph);
            Iterator<BigNode> bNodes = data.getBigNodes().iterator();
            while (bNodes.hasNext()) {
                bNodes.next().removeDummyElements();
            }
            DummyNodeUtil.removeDummies(data, graph);
            ((DecyclingAlgorithm) algorithms[0]).undo();

            EdgeUtil.addPorts(data);
            EdgeUtil.insertSelfLoops(data);

            graph.getListenerManager().transactionFinished(this);
        }
        ready = true;
    }

    protected void executeFirstFourPhases() {
        if (currentPhase == 0) {
            graph.getListenerManager().transactionStarted(this);
            EdgeUtil.removeBends(data);
            EdgeUtil.removeSelfLoops(data);
            graph.getListenerManager().transactionFinished(this);
        }

        try {
            if (!this.inSubAnimation) {
                algorithms[currentPhase].check();
            }
        } catch (PreconditionException pce) {
            throw new RuntimeException(pce);
        }

        // crossing reduction: count the number of crossings before the
        // algorithm did his work
        if (currentPhase == 2 && !this.inSubAnimation) {
            int crossings = 0;
            for (int l = 0; l < data.getLayers().getNumberOfLayers(); l++) {
                crossings += new BilayerCrossCounter(graph, l, data)
                        .getNumberOfCrossings();
            }
            logger.log(Level.INFO, "Edge crossings before crossing reduction: "
                    + crossings);
        }

        // Access the algorithm's animation, if the algorithm does support
        // animations and call nextStep() until the algorithm does not have
        // any additional steps
        if (algorithms[currentPhase].supportsAnimation()) {
            // the current phase-algorithm is already in its animation-phase
            if (this.inSubAnimation) {
                if (this.subAnimation.hasNextStep()) {
                    this.subAnimation.nextStep();
                } else {
                    this.inSubAnimation = false;
                    this.subAnimation = null;
                }
            } else {
                this.subAnimation = algorithms[currentPhase].getAnimation();
                this.inSubAnimation = true;
                if (this.subAnimation.hasNextStep()) {
                    this.subAnimation.nextStep();
                } else {
                    this.inSubAnimation = false;
                    this.subAnimation = null;
                }
            }
        } else {
            if (currentPhase == 3)
                if (!((LayoutAlgorithm) algorithms[currentPhase])
                        .supportsArbitraryXPos()) {
                    data.getLayers().normalizeLayers();
                }
            algorithms[currentPhase].execute();
        }
        if (currentPhase == 0 && !inSubAnimation) {
            EdgeUtil.insertConstraintsForDeletedEdges(data);
        }

        // crossing reduction: count the number of crossings before the
        // algorithm did his work
        if (currentPhase == 2 && !this.inSubAnimation) {
            int crossings = 0;
            for (int l = 0; l < data.getLayers().getNumberOfLayers(); l++) {
                crossings += new BilayerCrossCounter(graph, l, data)
                        .getNumberOfCrossings();
            }
            logger.log(Level.INFO, "Edge crossings after crossing reduction: "
                    + crossings);
            Integer cInt = data.getCrossingChange();
            if (cInt != null) {
                logger.log(Level.INFO, "Crossing reduction claimed change of "
                        + cInt);
            }
        }

        if (currentPhase == 1 || currentPhase == 2) {
            // update the graph
            graph.getListenerManager().transactionStarted(this);
            if (currentPhase == 1 && !inSubAnimation) {
                EdgeUtil.insertDeletedEdges(data);
            }
            CoordinatesUtil.updateGraph(graph, data);
            graph.getListenerManager().transactionFinished(this);
        }
        if (currentPhase == 1 && !inSubAnimation) {
            ConstraintsUtil.checkVerticalConstraints(data);
            if (data.getDeletedEdges().size() > 0) {
                ConstraintsUtil.removeDeletedEdgesConstraints(graph);
            }
        }
        if (currentPhase == 3 && !inSubAnimation) {
            ConstraintsUtil.checkConstraints(data);
        }

    }

    /**
     * Returns <tt>true</tt> if the next step can be executed, <tt>false</tt>
     * otherwise-
     */
    @Override
    public boolean isReady() {
        if (this.inSubAnimation)
            return this.subAnimation.isReady();
        else
            return ready;
    }

    /**
     * Returns <tt>true</tt>, if there is another available step, <tt>false</tt>
     * otherwise.
     */
    @Override
    public boolean hasNextStep() {
        return this.currentPhase < 4;
    }

    @Override
    public void clear() {
        // don't do anything useful here
    }

    public SugiyamaData getData() {
        return data;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
