// =============================================================================
//
//   RandomGraphGeneratorNodeDensityBased.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGeneratorNodeDensityBased.java 5766 2010-05-07 18:39:06Z gleissner $

/* Copyright (c) 2003 IPK Gatersleben
 * $Id: RandomGraphGeneratorNodeDensityBased.java 5766 2010-05-07 18:39:06Z gleissner $
 */

package org.graffiti.plugins.algorithms.generators;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * DOCUMENT ME!
 * 
 * @author Henning implementation of the RandomGeneratorAlgorithm
 */
public class RandomGraphGeneratorNodeDensityBased extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private boolean directedEdges = true;

    /** DOCUMENT ME! */
    private double avgNumberEdges = 0.0;

    /** DOCUMENT ME! */
    private int numberOfEdges = 0;

    /** DOCUMENT ME! */
    private int numberOfNodes = 0;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGeneratorNodeDensityBased() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Density Based";
    }

    /**
     * DOCUMENT ME!
     * 
     * @param params
     *            DOCUMENT ME!
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        numberOfNodes = ((IntegerParameter) params[0]).getValue().intValue();

        numberOfEdges = ((IntegerParameter) params[1]).getValue().intValue();

        avgNumberEdges = ((DoubleParameter) params[2]).getValue().doubleValue();

        directedEdges = ((BooleanParameter) params[3]).getBoolean()
                .booleanValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter nodesParam = new IntegerParameter(new Integer(10),
                new Integer(0), new Integer(100), "number of nodes",
                "the number of nodes to generate");

        IntegerParameter numberOfEdgesParam = new IntegerParameter(new Integer(
                10), new Integer(0), new Integer(100), "number of edges",
                "the number of edges to generate");

        DoubleParameter edgesPerNodeParam = new DoubleParameter(0d,
                "edges per node", "the average number of edges per node",
                new Double(0), new Double(100));

        BooleanParameter directedEdgesParameter = new BooleanParameter(true,
                "directed Edges", "produce Graph with directed Edges");

        return new Parameter[] { nodesParam, numberOfEdgesParam,
                edgesPerNodeParam, directedEdgesParameter };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (numberOfNodes < 0) {
            errors.add("The number of nodes may not be smaller than zero.");
        }

        if (numberOfEdges == 0) {
            if (avgNumberEdges < 2.0) {
                errors
                        .add("The number of edges per node may not be smaller than 2.0");
            }
        } else {
            if (avgNumberEdges != 0.0) {
                errors.add("Please specify either the total number of edges"
                        + " or the average number of edges per node .");
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
        Node[] nodes = new Node[numberOfNodes];

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < numberOfNodes; ++i) {
            nodes[i] = graph.addNode();

            CoordinateAttribute ca = (CoordinateAttribute) nodes[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            double X = Math.random() * 1000;

            ca.setX(X);

            double Y = Math.random() * 1000;

            ca.setY(Y);
        }

        if (graph.getNumberOfNodes() > 1) {
            addEdge(nodes[0], nodes[1], directedEdges);

            for (int i = 2; i < numberOfNodes; ++i) {
                // grows the connected graph by conneting a new node to a node
                // of the graph
                int connectToNode = (int) Math.floor(Math.random() * i);

                addEdge(nodes[i], nodes[connectToNode], directedEdges);
            }

            if (numberOfEdges == 0) {
                numberOfEdges = (int) ((numberOfNodes * avgNumberEdges) / 2.0);
            }

            int maxNumberOfEdges = (int) ((numberOfNodes * (numberOfNodes - 1)) / 2.0);

            numberOfEdges = Math.min(numberOfEdges, maxNumberOfEdges);

            while (graph.getNumberOfEdges() < numberOfEdges) {
                boolean newEdgeAdded = false;

                do {
                    int n1 = (int) Math.floor(Math.random()
                            * graph.getNumberOfNodes());
                    int n2 = (int) Math.floor(Math.random()
                            * graph.getNumberOfNodes());

                    if ((n1 != n2)
                            && graph.getEdges(nodes[n1], nodes[n2]).isEmpty()) {
                        addEdge(nodes[n1], nodes[n2], directedEdges);
                        newEdgeAdded = true;
                    }
                } while (newEdgeAdded == false);
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node_0
     *            DOCUMENT ME!
     * @param node_1
     *            DOCUMENT ME!
     * @param directed
     *            DOCUMENT ME!
     */
    private void addEdge(Node node_0, Node node_1, boolean directed) {
        if (Math.random() < 0.5) {
            graph.addEdge(node_0, node_1, directed);
        } else {
            graph.addEdge(node_1, node_0, directed);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
