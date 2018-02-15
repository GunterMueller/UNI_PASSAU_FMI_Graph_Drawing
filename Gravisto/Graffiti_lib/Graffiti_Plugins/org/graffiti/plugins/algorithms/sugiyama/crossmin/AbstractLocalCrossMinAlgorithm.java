// =============================================================================
//
//   AbstractLocalCrossMinAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractLocalCrossMinAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class provides all needed methods to implement an algorithm to minimize
 * edge-crossings between two layers of a graph.
 * 
 * An actual implementation has to overwrite the following methods:
 * <ul>
 * <li>check
 * <li>initialize
 * <li>processLayers
 * <li>getName
 * <li>reset
 * <li>setDefaultParameters
 * </ul>
 * 
 * @author Ferdinand H&uuml;bner
 */
public abstract class AbstractLocalCrossMinAlgorithm extends AbstractAlgorithm
        implements CrossMinAlgorithm {
    /** Number of iterations */
    protected int iterations;

    /** The <code>SugiyamaData</code> to store the results */
    protected SugiyamaData data;

    /** The layers of the graph */
    protected NodeLayers layers;

    /** Is animation-support enabled or not */
    protected boolean animated;

    /** Is the animation-process finished or not */
    protected boolean animationFinished;

    private NodeLayers lastLayers;
    private int lastCrossings;

    private boolean debug = false;

    /**
     * Default constructor - sets the number of iterations to 50
     */
    public AbstractLocalCrossMinAlgorithm() {
        iterations = 10;
        this.animated = false;
        this.animationFinished = true;
    }

    /**
     * Execute the algorithm - iterate <i>iterations</i> times. This method
     * should not be overwritten by an actual implementation!
     */
    public void execute() {

        graph.getListenerManager().transactionStarted(this);
        initialize();

        layers = data.getLayers();
        if (this.animated) {
            graph.getListenerManager().transactionFinished(this);
            return;
        }

        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            // if the graph is cyclic, we iterate top-down, starting from a
            // random
            // level of the graph; afterwards we iterate bottom-up, starting
            // from another random layer of the graph
            for (int i = 0; i < iterations; i++) {
                // top-down
                int startingLayer = (int) (Math.floor((Math.random() * (layers
                        .getNumberOfLayers() - 1)) + 0.5d));
                for (int j = startingLayer; j < layers.getNumberOfLayers(); j++) {
                    processLayers(j, true, layers);
                }
                if (startingLayer != 0) {
                    for (int j = 0; j < startingLayer; j++) {
                        processLayers(j, true, layers);
                    }
                }
                // bottom up
                startingLayer = (int) (Math.floor((Math.random() * (layers
                        .getNumberOfLayers() - 1)) + 0.5d));

                for (int j = startingLayer; j >= 0; j--) {
                    processLayers(j, false, layers);
                }
                if (startingLayer != layers.getNumberOfLayers() - 1) {
                    for (int j = layers.getNumberOfLayers() - 1; j > startingLayer; j--) {
                        processLayers(j, false, layers);
                    }
                }
            }
        } else {
            for (int i = 0; i < iterations; i++) {
                // top down
                for (int j = 1; j < layers.getNumberOfLayers(); j++) {
                    processLayers(j, true, layers);
                }
                if (i == iterations - 1) {
                    lastCrossings = computeCrossings();
                    lastLayers = data.getLayers().clone();
                }
                if (debug) {
                    System.out.println("Top-down, crossings: "
                            + computeCrossings());
                }
                // bottom up
                for (int j = layers.getNumberOfLayers() - 2; j >= 0; j--) {
                    processLayers(j, false, layers);
                }
                // Use the old layer if it was a better order
                if (i == iterations - 1 && lastCrossings < computeCrossings()) {
                    data.setLayers(lastLayers);
                }
                if (debug) {
                    System.out.println("Bottom-up, crossings: "
                            + computeCrossings());
                }
            }
        }

        // (re-)write all "sugiyama.xpos" attributes
        for (int layerNum = 0; layerNum < data.getLayers().getNumberOfLayers(); layerNum++) {
            ArrayList<Node> layer = data.getLayers().getLayer(layerNum);
            for (int nodeNum = 0; nodeNum < layer.size(); nodeNum++) {
                layer.get(nodeNum).setDouble(SugiyamaConstants.PATH_XPOS,
                        nodeNum);
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Overwrite this method! Process layer b
     * 
     * @param b
     *            The layer to process
     * @param topDown
     *            If this is <code>true</code>, the iteration is top-down, i.e.
     *            consider layer b-1 fixed, if it's false, consider layer b+1
     *            fixed
     * @param layers
     *            This <code>NodeLayers</code>-Object stores the layers of the
     *            graph
     */
    protected void processLayers(int b, boolean topDown, NodeLayers layers) {
    }

    /**
     * Overwrite this method and initialize the graph if needed (e.g. initialize
     * the first layer)
     */
    protected void initialize() {

    }

    /**
     * Accessor for the SugiyamaData-bean
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * Setter for the SugiyamaData-bean
     */
    public void setData(SugiyamaData theData) {
        this.data = theData;
        layers = data.getLayers();
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter iterations = new IntegerParameter(10, 1, 5000,
                "Iterations", "Iterations of the cross-min-algorithm");
        BooleanParameter isAnimated = new BooleanParameter(false, "Animate",
                "Pause after each iteration (Requires framework-animation)");
        return new Parameter[] { iterations, isAnimated };
    }

    /**
     * Setter-method to store an array of parameters
     * 
     * @param params
     *            The parameters to be stored
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.iterations = ((IntegerParameter) params[0]).getValue();
        this.animated = ((BooleanParameter) params[1]).getValue();
        if (this.animated) {
            this.animationFinished = false;
        }
        if (debug) {
            System.out.println("Set iterations to " + iterations);
        }
    }

    /**
     * Reset the internal structure of this algorithm
     */
    @Override
    public void reset() {
        if (this.animationFinished) {
            this.graph = null;
            this.parameters = null;
        }
    }

    @Override
    public boolean supportsAnimation() {
        return this.animated;
    }

    @Override
    public Animation getAnimation() {
        return new LocalCrossMinAnimation(this.data, this.iterations, this);
    }

    protected void animationFinished() {
        this.animationFinished = true;
    }

    public int getIterations() {
        return this.iterations;
    }

    public void setIterations(int iter) {
        this.iterations = iter;
    }

    /**
     * This method computes the total number of crossing <tt>Edge</tt>s in the
     * <tt>Graph</tt>.
     * 
     * @return Calculates the total number of crossing <tt>Edge</tt>s in the
     *         <tt>Graph</tt> and returns this value.
     */
    protected int computeCrossings() {
        NodeLayers currentLayers = data.getLayers();
        int totalCrossings = 0;
        int localCrossings;
        ArrayList<ArrayList<Integer>> current;
        Iterator<Node> neighbors;
        Node neighbor;

        // compute the number of crossings for each layer
        for (int i = 1; i < currentLayers.getNumberOfLayers(); i++) {
            current = new ArrayList<ArrayList<Integer>>();
            for (int j = 0; j < currentLayers.getLayer(i).size(); j++) {
                // get the indices of the neighbors on the upper level
                neighbors = currentLayers.getLayer(i).get(j)
                        .getNeighborsIterator();
                current.add(new ArrayList<Integer>());
                while (neighbors.hasNext()) {
                    neighbor = neighbors.next();
                    if (currentLayers.getLayer(i - 1).contains(neighbor)) {
                        current.get(j)
                                .add(
                                        currentLayers.getLayer(i - 1).indexOf(
                                                neighbor));
                    }
                }
            }
            // compute the crossings
            localCrossings = 0;
            // for each node on the current level ...
            for (int j = 0; j < current.size(); j++) {
                // ... take each node that is right of the current node ...
                for (int k = j + 1; k < current.size(); k++) {
                    // ... for each edge of the current node ...
                    for (int l = 0; l < current.get(j).size(); l++) {
                        // ... get the index of each edge of the other node ...
                        for (int m = 0; m < current.get(k).size(); m++) {
                            // if the index of the edge is smaller than the
                            // index of the current node's edge, there is a
                            // crossing
                            if (current.get(k).get(m) < current.get(j).get(l)) {
                                localCrossings++;
                            }
                        }
                    }
                }
            }
            totalCrossings += localCrossings;
        }
        return totalCrossings;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
