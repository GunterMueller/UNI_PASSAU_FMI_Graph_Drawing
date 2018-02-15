// =============================================================================
//
//   GraphGeneratorHarary.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphGeneratorHarary.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * This generator creates a k-connected graph with n nodes. That means: You can
 * take away any (k - 1) of the edges, but every node can be reached from any
 * other node anyway.
 */
public class GraphGeneratorHarary extends AbstractGenerator {

    /** connectivity parameter */
    private IntegerParameter connectivityParam;

    /** number of edges to add randomly */
    private IntegerParameter edgesToAddParam;

    /** number od nodes */
    private IntegerParameter nodesParam;

    /** connectivity */
    private int connectivity;

    /** edges to add */
    private int edgesToAdd;

    /** number of nodes */
    private int numberOfNodes;

    /**
     * Constructs a new instance.
     */
    public GraphGeneratorHarary() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        connectivityParam = new IntegerParameter(
                new Integer(3),
                new Integer(0),
                new Integer(100),
                "connectivity degree k",
                "k - 1 nodes can be taken away but every node can be reached from anywhere anyway");
        edgesToAddParam = new IntegerParameter(new Integer(0), new Integer(0),
                new Integer(100), "edges to add",
                "add edges randomly to the graph");
        parameterList.addFirst(edgesToAddParam);
        parameterList.addFirst(connectivityParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Harary";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        numberOfNodes = nodesParam.getValue().intValue();
        connectivity = connectivityParam.getValue().intValue();
        edgesToAdd = edgesToAddParam.getValue().intValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (numberOfNodes < 1) {
            errors.add("The number of nodes may not be smaller than one.");
        }

        if (connectivity >= numberOfNodes) {
            errors
                    .add("The connectivity parameter must be less tha the number of nodes.");
        }

        if (connectivity < 2) {
            errors.add("The connectivity parameter must be greater than one.");
        }

        int numOfHararyEdges = (int) Math
                .ceil((connectivity * numberOfNodes) / 2.0);
        int maxNumOfEdges = ((numberOfNodes * (numberOfNodes - 1)) / 2);
        int maxNumOfEdgesToAdd = maxNumOfEdges - numOfHararyEdges;

        if (edgesToAdd > maxNumOfEdgesToAdd) {
            errors
                    .add("The number of edges to delete is to big.\nMaximal number "
                            + "of edges to add is the number of edges in a \ncomplete "
                            + "graph ( n * (n - 1) / 2 ) less the number of edges in a\nharary graph "
                            + "( ceil(k * n / 2) ).\nIn this case: "
                            + maxNumOfEdgesToAdd);
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
        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < numberOfNodes; ++i) {
            nodes.add(graph.addNode());
        }

        // create edges
        // k even, n any
        if ((connectivity % 2) == 0) {
            for (int i = 0; i < numberOfNodes; i++) {
                for (int j = 0; j <= (connectivity / 2); j++) {
                    if (i != ((i + j) % numberOfNodes)) {
                        edges.add(graph.addEdge(nodes.get(i), nodes.get((i + j)
                                % numberOfNodes), false));
                    }
                }
            }
        }

        // k odd, n even
        else if ((numberOfNodes % 2) == 0) {
            for (int i = 0; i < numberOfNodes; i++) {
                for (int j = 0; j <= (connectivity / 2); j++) {
                    if (i != ((i + j) % numberOfNodes)) {
                        edges.add(graph.addEdge(nodes.get(i), nodes.get((i + j)
                                % numberOfNodes), false));
                    }
                }

                edges
                        .add(graph.addEdge(nodes.get(i),
                                nodes.get((i + (numberOfNodes / 2))
                                        % numberOfNodes), false));
            }
        }

        // k odd, n odd
        else {
            for (int i = 0; i < numberOfNodes; i++) {
                for (int j = 0; j <= ((connectivity - 1) / 2); j++) {
                    if (i != ((i + j) % numberOfNodes)) {
                        edges.add(graph.addEdge(nodes.get(i), nodes.get((i + j)
                                % numberOfNodes), false));
                    }
                }
            }

            edges.add(graph.addEdge(nodes.get(0), nodes
                    .get(((numberOfNodes + 1) / 2) - 1), false));
            edges.add(graph.addEdge(nodes.get(0), nodes
                    .get(((numberOfNodes + 3) / 2) - 1), false));

            for (int i = 1; i < ((numberOfNodes - 1) / 2); i++) {
                edges.add(graph.addEdge(nodes.get(i), nodes.get(i
                        + ((numberOfNodes + 1) / 2)), false));
            }
        }

        for (int i = 0; i < edgesToAddParam.getValue().intValue(); i++) {
            Node node1 = nodes.get((int) (Math.random() * nodes.size()));
            Node node2 = nodes.get((int) (Math.random() * nodes.size()));

            if (node1 == node2) {
                i--;
            } else {
                boolean alreadyExisting = false;
                Iterator<Edge> it = node1.getUndirectedEdgesIterator();

                while (it.hasNext() && !alreadyExisting) {
                    Edge edge = it.next();

                    if (edge.getTarget() == node2) {
                        alreadyExisting = true;
                    }
                }

                it = node2.getUndirectedEdgesIterator();

                while (it.hasNext() && !alreadyExisting) {
                    Edge edge = it.next();

                    if (edge.getTarget() == node1) {
                        alreadyExisting = true;
                    }
                }

                if (!alreadyExisting) {
                    edges.add(graph.addEdge(node1, node2, false));
                } else {
                    i--;
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
        connectivityParam.setValue(new Integer(3));
        edgesToAddParam.setValue(new Integer(0));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
