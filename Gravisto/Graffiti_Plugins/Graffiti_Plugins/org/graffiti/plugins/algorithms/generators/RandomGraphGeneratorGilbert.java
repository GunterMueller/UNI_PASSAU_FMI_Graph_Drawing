// =============================================================================
//
//   RandomGraphGeneratorGilbert.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGeneratorGilbert.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * $Id: RandomGraphGeneratorGilbert.java 5766 2010-05-07 18:39:06Z gleissner $
 */

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;

/**
 * This generator creates a graph with n nodes. It creates edges with a specific
 * probability, as described by Mr. Gilbert.
 */
public class RandomGraphGeneratorGilbert extends AbstractGenerator {

    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /** probability parameter */
    private ProbabilityParameter probabilityParam;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGeneratorGilbert() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        probabilityParam = new ProbabilityParameter(0.5, "probability",
                "probability of edge generation");
        parameterList.addFirst(probabilityParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random Graph Generator: Gilbert";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The number of nodes may not be smaller than zero.");
        }

        if (probabilityParam.getValue().compareTo(new Double(1.0)) > 0) {
            errors.add("The probability may not be greater than 1.0.");
        }

        if (probabilityParam.getValue().compareTo(new Double(0.0)) < 0) {
            errors.add("The probability may not be less than 0.0.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // add nodes
        int numberOfNodes = nodesParam.getValue().intValue();

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < numberOfNodes; ++i) {
            nodes.add(graph.addNode());
        }

        // add edges
        double p = probabilityParam.getValue().doubleValue();

        if (p != 0.0) {
            double r = 1 / Math.log(1 - p);
            int v = 1;
            int w = 1;

            while (v < numberOfNodes) {
                w += (1 + ((int) (r * Math.log(Math.random()))));

                while (w > numberOfNodes) {
                    v++;
                    w -= (numberOfNodes - v);
                }

                if (v < numberOfNodes) {
                    edges.add(graph.addEdge(nodes.get(v - 1), nodes.get(w - 1),
                            false));
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);

        formGraph(nodes, form.getSelectedValue());

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            labelNodes(nodes, startNumberParam.getValue().intValue());
        }

        // label the edges
        if (edgeLabelParam.getBoolean().booleanValue()) {
            labelEdges(edges, edgeLabelNameParam.getString(), edgeMin
                    .getValue().intValue(), edgeMax.getValue().intValue());
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        nodesParam.setValue(new Integer(5));
        probabilityParam.setValue(new Double(0.5));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
