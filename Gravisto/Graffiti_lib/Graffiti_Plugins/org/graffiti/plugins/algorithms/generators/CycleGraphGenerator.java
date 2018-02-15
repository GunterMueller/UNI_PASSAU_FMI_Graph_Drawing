// =============================================================================
//
//   CycleGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CycleGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * This generator creates a graph with n nodes. The nodes and edges build
 * graphically a cycle.
 */
public class CycleGraphGenerator extends AbstractGenerator {

    /** number od nodes */
    private IntegerParameter nodesParam;

    /**
     * Constructs a new instance.
     */
    public CycleGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Cycle";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().compareTo(new Integer(1)) < 0) {
            errors.add("The number of nodes may not be smaller than one.");
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
        int numberOfNodes = nodesParam.getValue().intValue();

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < numberOfNodes; ++i) {
            nodes.add(graph.addNode());

            if (i > 0) {
                edges.add(graph.addEdge(nodes.get(i - 1), nodes.get(i), false));
            }
        }

        edges.add(graph.addEdge(nodes.get(numberOfNodes - 1), nodes.get(0),
                false));

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
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
