// =============================================================================
//
//   RandomGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomCoordinatesGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.selection.Selection;

/**
 * This generator creates a graph with n nodes and m edges. The user can choose,
 * if the graph is directed, if the graph can be a multi graph, if self loops
 * are allowed, if the nodes and the edges should be labeled.
 */
public class RandomCoordinatesGenerator extends AbstractGenerator {

    /**
     * Constructs a new instance.
     */
    public RandomCoordinatesGenerator() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random Coordinates Generator";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        String[] options = { DYNAMIC_CIRCLE, STATIC_CIRCLE, RANDOM };
        form = new StringSelectionParameter(options, "form",
                "the graphical form");

        return new Parameter[] { selParam, form };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (graph.getNodes().size() < 1) {
            errors.add("There are no nodes to calculate random positions.");
        }

        if (!errors.isEmpty())
            throw errors;

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        formGraph(graph.getNodes(), form.getSelectedValue());

        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
