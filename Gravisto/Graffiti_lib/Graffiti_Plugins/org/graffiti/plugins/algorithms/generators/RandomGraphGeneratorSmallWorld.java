// =============================================================================
//
//   RandomGraphGeneratorSmallWorld.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGeneratorSmallWorld.java 5766 2010-05-07 18:39:06Z gleissner $

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
 * This generator creates a graph with n nodes. The nodes and edges build a
 * "Small World".
 */
public class RandomGraphGeneratorSmallWorld extends AbstractGenerator {

    /** neighborhood parameter */
    private IntegerParameter neighborhoodParam;

    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /** probability parameter */
    private ProbabilityParameter probabilityParam;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGeneratorSmallWorld() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addEdgeBendingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        probabilityParam = new ProbabilityParameter(0.5, "change probability",
                "change probability of edge generation");

        neighborhoodParam = new IntegerParameter(new Integer(2),
                new Integer(0), new Integer(100), "neighborhood",
                "neighborhood parameter");
        parameterList.addFirst(neighborhoodParam);
        parameterList.addFirst(probabilityParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random Graph Generator: Small World";
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

        if (neighborhoodParam.getValue().intValue() < 0) {
            errors.add("The neighborhood parameter may not be less than 0.");
        }

        if (neighborhoodParam.getValue().compareTo(
                new Integer((nodesParam.getValue().intValue() - 1) / 2)) > 0) {
            errors
                    .add("The neighbourhood parameter may not be greater than (<number of nodes> - 1) / 2.");
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

        double p = probabilityParam.getValue().doubleValue();

        int r = neighborhoodParam.getValue().intValue();

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < numberOfNodes; ++i) {
            nodes.add(graph.addNode());
        }

        for (int v = 0; v < numberOfNodes; v++) {
            for (int w = v + 1; w <= (v + r); w++) {
                int tempW = w % numberOfNodes;

                double random = Math.random();

                if (((p / 2) <= random) && (random <= (1 - (p / 2)))) {
                    edges.add(graph.addEdge(nodes.get(v), nodes.get(tempW),
                            false));
                } else {
                    if (random < (p / 2)) {
                        int temp = (v + (((int) ((2 * random * (numberOfNodes - 1)) / p)) + 1))
                                % numberOfNodes;
                        edges.add(graph.addEdge(nodes.get(v), nodes.get(temp),
                                false));
                    } else {
                        int temp = (tempW + (((int) ((2 * (1 - random) * (numberOfNodes - 1)) / p)) + 1))
                                % numberOfNodes;
                        edges.add(graph.addEdge(nodes.get(tempW), nodes
                                .get(temp), false));
                    }
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);

        formGraph(nodes, form.getSelectedValue());

        // bend the edges
        if (edgeBendingParam.getBoolean().booleanValue()) {
            bendMultiEdges(edges);
        }

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
        neighborhoodParam.setValue(new Integer(1));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
