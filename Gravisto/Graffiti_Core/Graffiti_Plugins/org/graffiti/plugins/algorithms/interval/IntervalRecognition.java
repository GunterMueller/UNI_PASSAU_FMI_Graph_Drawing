// =============================================================================
//
//   IntervalNew.java
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
public class IntervalRecognition extends AbstractAlgorithm

{

    private static final Logger logger = Logger
            .getLogger(IntervalRecognition.class.getName());

    private IntervalSets<CliqueSet> cliqueSequence = null;

    /** The graph, the algorithm is adapted on. */
    protected Graph graph;

    /** DOCUMENT ME! */

    public IntervalRecognition() {
    }

    /**
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    public String getName() {
        return "Interval";
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
        RecognizeInterval rec = new RecognizeInterval();
        cliqueSequence = rec.recognize(graph);

        if (!cliqueSequence.getIsInterval()) {
            errors.add("The graph is not an interval graph.");
        }

        if (!errors.isEmpty())
            throw errors;

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        DrawIntervals draw = new DrawIntervals();
        draw.drawIntervals(cliqueSequence, graph);

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
