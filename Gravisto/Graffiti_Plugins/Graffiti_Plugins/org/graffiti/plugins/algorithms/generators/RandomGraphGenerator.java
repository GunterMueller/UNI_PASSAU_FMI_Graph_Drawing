// =============================================================================
//
//   RandomGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * This generator creates a graph with n nodes and m edges. The user can choose,
 * if the graph is directed, if the graph can be a multi graph, if self loops
 * are allowed, if the nodes and the edges should be labeled.
 */
public class RandomGraphGenerator extends AbstractGenerator {

    /** should the graph be directed ? */
    private BooleanParameter directedParam;

    /** multigraph allowed ? */
    private BooleanParameter multiParam;

    /** self-loops allowed ? */
    private BooleanParameter selfParam;

    /** number of edges */
    private IntegerParameter edgesParam;

    /** number od nodes */
    private IntegerParameter nodesParam;

    /** is the graph directed */
    private boolean directed;

    /** is a multi graph allowed */
    private boolean multiGraph;

    /** are self loops allowed */
    private boolean selfLoops;

    /** the number of edges */
    private int numOfEdges;

    /** the number of nodes */
    private int numberOfNodes;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addEdgeBendingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate.");
        edgesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of edges",
                "the number of edges to generate.");
        multiParam = new BooleanParameter(false, "permit multi graph",
                "permit duplicate edges.");
        selfParam = new BooleanParameter(false, "permit self-loops",
                "permit edges from a node to itself.");
        directedParam = new BooleanParameter(false, "directed",
                "should the graph be directed ?");
        parameterList.addFirst(directedParam);
        parameterList.addFirst(selfParam);
        parameterList.addFirst(multiParam);
        parameterList.addFirst(edgesParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random Graph Generator";
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        numberOfNodes = nodesParam.getValue().intValue();
        numOfEdges = edgesParam.getValue().intValue();
        selfLoops = selfParam.getBoolean().booleanValue();
        multiGraph = multiParam.getBoolean().booleanValue();
        directed = directedParam.getBoolean().booleanValue();
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

        if (numOfEdges < 0) {
            errors.add("The number of edges may not be smaller than zero.");
        }

        /*
         * Check the number of edges. If there are too many, an infinite loop in
         * in execute() is possible (if a multi graph is not allowed).
         */
        if (!multiGraph) {
            int e = 0;
            String error_output = "The number of edges must be less than ";
            boolean errorAdded = false;
            if (directed && !selfLoops) {
                e = (numberOfNodes * (numberOfNodes - 1));

                if (numOfEdges > e) {
                    errorAdded = true;
                    error_output += "n * (n - 1)";
                }
            } else if (directed && selfLoops) {
                e = numberOfNodes * numberOfNodes;
                if (numOfEdges > e) {
                    errorAdded = true;
                    error_output += "n * n";
                }
            } else if (!directed && !selfLoops) {
                e = (numberOfNodes * (numberOfNodes - 1)) / 2;
                if (numOfEdges > e) {
                    errorAdded = true;
                    error_output += "n * (n - 1) / 2";
                }
            } else if (!directed && selfLoops) {
                e = ((numberOfNodes * (numberOfNodes - 1)) / 2) + numberOfNodes;
                if (numOfEdges > e) {
                    errorAdded = true;
                    error_output += "(n * (n - 1)) / 2 + n";
                }
            }
            if (errorAdded) {
                errors.add(error_output
                        + " (n is the number of nodes).\nIn this case:) " + e);
            }
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

        boolean[][] adjMatrix = new boolean[numberOfNodes][numberOfNodes];

        for (int i = 0; i < numOfEdges; i++) {
            int randomNode1 = getRandom(numberOfNodes);
            int randomNode2 = getRandom(numberOfNodes);

            if (!selfLoops) {
                while (randomNode1 == randomNode2) {
                    randomNode2 = getRandom(numberOfNodes);
                }
            }

            if (!multiGraph) {
                if (adjMatrix[randomNode1][randomNode2]) {
                    i--;

                    continue;
                }
            }

            edges.add(graph.addEdge(nodes.get(randomNode1), nodes
                    .get(randomNode2), directed));
            adjMatrix[randomNode1][randomNode2] = true;

            if (!directed) {
                adjMatrix[randomNode2][randomNode1] = true;
            }
        }

        graph.getListenerManager().transactionFinished(this);

        addGraphicAttributeToNodes(nodes);
        addGraphicAttributeToEdges(edges);

        formGraph(nodes, form.getSelectedValue());

        if (directed) {
            setEdgeArrows(graph);
        }

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
        edgesParam.setValue(new Integer(5));
        multiParam.setValue(new Boolean(false));
        selfParam.setValue(new Boolean(false));
        directedParam.setValue(new Boolean(false));
    }

    /**
     * Returns a random number between zero and the specified upper bound.
     * 
     * @param upperBound
     *            The upper bound of the random number.
     * 
     * @return A random number between zero and the specified upper bound.
     */
    private int getRandom(int upperBound) {
        return (int) (Math.random() * upperBound);
    }

    /**
     * Returns the directed.
     * 
     * @return the directed.
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Sets the directed.
     * 
     * @param directed
     *            the directed to set.
     */
    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    /**
     * Returns the multiGraph.
     * 
     * @return the multiGraph.
     */
    public boolean isMultiGraph() {
        return multiGraph;
    }

    /**
     * Sets the multiGraph.
     * 
     * @param multiGraph
     *            the multiGraph to set.
     */
    public void setMultiGraph(boolean multiGraph) {
        this.multiGraph = multiGraph;
    }

    /**
     * Returns the numOfEdges.
     * 
     * @return the numOfEdges.
     */
    public int getNumOfEdges() {
        return numOfEdges;
    }

    /**
     * Sets the numOfEdges.
     * 
     * @param numOfEdges
     *            the numOfEdges to set.
     */
    public void setNumOfEdges(int numOfEdges) {
        this.numOfEdges = numOfEdges;
    }

    /**
     * Returns the numOfNodes.
     * 
     * @return the numOfNodes.
     */
    public int getNumOfNodes() {
        return numberOfNodes;
    }

    /**
     * Sets the numOfNodes.
     * 
     * @param numOfNodes
     *            the numOfNodes to set.
     */
    public void setNumOfNodes(int numOfNodes) {
        this.numberOfNodes = numOfNodes;
    }

    /**
     * Returns the selfLoops.
     * 
     * @return the selfLoops.
     */
    public boolean isSelfLoops() {
        return selfLoops;
    }

    /**
     * Sets the selfLoops.
     * 
     * @param selfLoops
     *            the selfLoops to set.
     */
    public void setSelfLoops(boolean selfLoops) {
        this.selfLoops = selfLoops;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
