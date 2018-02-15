// =============================================================================
//
//   RandomGraphGeneratorPreference.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGeneratorPreference.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * This generator creates a random graph with prference. That menas, the
 * probability of creating an edge from or to a node depends on the number of
 * edges this node has. The more edges it has, the bigger is the probability of
 * a new edge to be created.
 */
public class RandomGraphGeneratorPreference extends AbstractGenerator {

    /** probability parameter */
    private IntegerParameter degreeParam;

    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGeneratorPreference() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addEdgeBendingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        degreeParam = new IntegerParameter(new Integer(2), new Integer(0),
                new Integer(100), "starting degree", "starting degree");
        parameterList.addFirst(degreeParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random Graph Generator: Preference";
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

        if (degreeParam.getValue().compareTo(new Integer(0)) < 0) {
            errors.add("The degree may not be smaller than zero.");
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

        int b = degreeParam.getValue().intValue();
        Node[] tempNodes = new Node[2 * numberOfNodes * b];

        int m = 0;

        for (int v = 0; v < numberOfNodes; v++) {
            for (int j = 0; j < b; j++) {
                tempNodes[2 * m] = nodes.get(v);

                int random = (int) (Math.random() * 2 * m);
                Node w = tempNodes[random];
                tempNodes[(2 * m) + 1] = w;
                edges.add(graph.addEdge(nodes.get(v), w, false));
                m++;
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
        degreeParam.setValue(new Integer(2));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
