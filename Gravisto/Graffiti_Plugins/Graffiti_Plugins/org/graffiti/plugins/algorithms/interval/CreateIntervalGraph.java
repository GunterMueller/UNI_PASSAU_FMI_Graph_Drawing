// =============================================================================
//
//   CreateIntervalGraph.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;

/**
 * @author struckmeier
 */
public class CreateIntervalGraph extends AbstractAlgorithm {

    private static final Logger logger = Logger.getLogger(ComputeCliques.class
            .getName());

    /** The graph, the algorithm is adapted on. */
    protected Graph graph;

    public CreateIntervalGraph() {

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return "Create Interval Graph";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // The graph is inherited from AbstractAlgorithm.
        this.graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getGraph();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }
        if (graph.getNumberOfNodes() <= 0) {
            errors.add("The graph is empty. Cannot run IntervalRecognition.");
        }

        if (!errors.isEmpty())
            throw errors;

    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
    public void execute() {
        CreateGraph create = new CreateGraph();
        // Graph newGraph =
        create.create(graph);
        logger.log(Level.FINER, "Plugin finished.");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
