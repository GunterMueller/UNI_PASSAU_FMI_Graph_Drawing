// =============================================================================
//
//   CirculantGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CirculantGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * This generator creates a graph with n nodes and connects every node x with
 * the nodes specified as parameters. That means: If the specified nodes are
 * e.g. 2, 4 and 5, this algorithm creates edges from node x to the nodes x + 2,
 * x + 4 and x + 5.
 */
public class CirculantGraphGenerator extends AbstractGenerator {

    /** list with the nodes to connect */
    Collection<Integer> nodesList;

    /** directed graph */
    private BooleanParameter directedParam;

    /** number of nodes */
    private IntegerParameter nodesParam;

    /** nodes to connect */
    private StringParameter connectParam;

    /** number of nodes */
    private int numberOfNodes;

    /** is the graph directed ? */
    private boolean directed;

    /**
     * Constructs a new instance.
     */
    public CirculantGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addFormSelOption();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        connectParam = new StringParameter("1.2.3.", "nodes to connect",
                "2.3. means: there are edges from node x to x + 2 and x + 3");
        directedParam = new BooleanParameter(false, "directed",
                "if the graph is directed, there can be both edges: (a, b) and (b, a)");
        parameterList.addFirst(directedParam);
        parameterList.addFirst(connectParam);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Circulant Graph Generator";
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

        try {
            nodesList = parseConnectParam(connectParam.getString());
        } catch (Exception e) {
            errors
                    .add("The input format of the nodes to connect is not correct, format: <x.>*");
        }

        for (Integer integ : nodesList) {
            if (integ.compareTo(nodesParam.getValue()) >= 0) {
                errors
                        .add("A node to connect must be smaller than the number of nodes");
            }

            if (integ.compareTo(new Integer(0)) <= 0) {
                errors.add("Negative parameters are not allowed");
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
        // add nodes
        numberOfNodes = nodesParam.getValue().intValue();
        directed = directedParam.getBoolean().booleanValue();
        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edges = new LinkedList<Edge>();

        graph.getListenerManager().transactionStarted(this);
        graph.setDirected(directed);

        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(graph.addNode());
        }

        int i = 0;
        for (Node node : nodes) {
            for (Integer integ : nodesList) {
                int x = integ.intValue();

                if (directed) {
                    edges.add(graph.addEdge(node, nodes.get((i + x)
                            % numberOfNodes), true));
                } else {
                    boolean alreadyExisting = false;
                    for (Edge e : nodes.get((i + x) % numberOfNodes)
                            .getUndirectedEdges()) {
                        if (e.getTarget() == node) {
                            alreadyExisting = true;
                        }
                    }

                    if (!alreadyExisting) {
                        edges.add(graph.addEdge(node, nodes.get((i + x)
                                % numberOfNodes), false));
                    }
                }
            }
            i++;
        }

        graph.getListenerManager().transactionFinished(this);

        formGraph(nodes, form.getSelectedValue());

        if (directed) {
            setEdgeArrows(graph);
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
        connectParam.setValue("1.2.3.");
        directedParam.setValue(new Boolean(false));
    }

    /**
     * Parses the input.
     * 
     * @param input
     *            The input to parse.
     * 
     * @return The parsed parameters.
     */
    private Collection<Integer> parseConnectParam(String input) {
        Collection<Integer> connectNodes = new LinkedList<Integer>();

        while (input.length() > 0) {
            int i = 0;
            int splitter = input.indexOf(".");

            if (splitter == -1) {
                splitter = input.length();
            }

            String arg = input.substring(i, splitter);
            connectNodes.add(new Integer(Integer.parseInt(arg)));

            if (splitter <= input.length()) {
                input = input.substring(splitter + 1);
            } else {
                break;
            }
        }

        return connectNodes;
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
     * Returns the nodesList.
     * 
     * @return the nodesList.
     */
    public Collection<Integer> getNodesList() {
        return nodesList;
    }

    /**
     * Sets the nodesList.
     * 
     * @param nodesList
     *            the nodesList to set.
     */
    public void setNodesList(Collection<Integer> nodesList) {
        this.nodesList = nodesList;
    }

    /**
     * Returns the numberOfNodes.
     * 
     * @return the numberOfNodes.
     */
    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    /**
     * Sets the numberOfNodes.
     * 
     * @param numberOfNodes
     *            the numberOfNodes to set.
     */
    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
