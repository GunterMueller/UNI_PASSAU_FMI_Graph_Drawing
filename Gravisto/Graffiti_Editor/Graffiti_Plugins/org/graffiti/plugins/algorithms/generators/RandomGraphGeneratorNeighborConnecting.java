// =============================================================================
//
//   RandomGraphGeneratorNeighborConnecting.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomGraphGeneratorNeighborConnecting.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.Collection;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
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
 * This implements a graph producer which places nodes randomly and connects a
 * pair of nodes if their distance is less than the specified threshold. This
 * results in locally restricted connections. There is an option that all nodes
 * are connected to produce a single graph.
 * 
 * @author Henning Schw�bbermeyer
 */
public class RandomGraphGeneratorNeighborConnecting extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private static final double GLOBALX = 1000;

    /** DOCUMENT ME! */
    private static final double GLOBALY = 1000;

    /** DOCUMENT ME! */
    private static final String COORDSTR = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.COORDINATE;

    // private BooleanParamter connectedGraphParameter;

    /** DOCUMENT ME! */
    private BooleanParameter connectedGraphParameter;

    /** DOCUMENT ME! */
    private BooleanParameter directedEdgesParameter;

    /** DOCUMENT ME! */
    private DoubleParameter edgeLengthParam;

    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /**
     * Constructs a new instance.
     */
    public RandomGraphGeneratorNeighborConnecting() {
        nodesParam = new IntegerParameter(new Integer(10), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        edgeLengthParam = new DoubleParameter(0.2, "edge length threshold",
                "maximum length of edges connecting nodes "
                        + "(fraction of maximal edge length)", new Double(0),
                new Double(1000));

        directedEdgesParameter = new BooleanParameter(true, "directed Edges",
                "produce Graph with directed Edges");

        connectedGraphParameter = new BooleanParameter(true, "connected Graph",
                "produce one connected graph");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Neighbor Connecting";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { nodesParam, edgeLengthParam,
                directedEdgesParameter, connectedGraphParameter };
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

        if ((edgeLengthParam.getValue().compareTo(new Double(0f)) < 0)
                || (edgeLengthParam.getValue().compareTo(new Double(100f)) > 0)) {
            errors
                    .add("The maximum length of edges may not be smaller than 0 and not be greater than 1.");
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
        int n = nodesParam.getValue().intValue();

        Node[] nodes = new Node[n];

        graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < n; ++i) {
            nodes[i] = graph.addNode();

            CoordinateAttribute ca = (CoordinateAttribute) nodes[i]
                    .getAttribute(COORDSTR);

            double X = Math.random() * GLOBALX;

            ca.setX(X);

            double Y = Math.random() * GLOBALY;

            ca.setY(Y);
        }

        double[][] distances = new double[n][n];

        for (int i = 0; i < n; ++i) {
            CoordinateAttribute ca1 = (CoordinateAttribute) nodes[i]
                    .getAttribute(COORDSTR);

            for (int j = i + 1; j < n; ++j) {
                CoordinateAttribute ca2 = (CoordinateAttribute) nodes[j]
                        .getAttribute(COORDSTR);

                distances[i][j] = ca1.getCoordinate().distance(
                        ca2.getCoordinate());
                distances[j][i] = ca1.getCoordinate().distance(
                        ca2.getCoordinate());
            }
        }

        double d = edgeLengthParam.getValue().doubleValue();

        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                if (distances[i][j] <= (Math.sqrt((GLOBALX * GLOBALX)
                        + (GLOBALY * GLOBALY)) * d)) {
                    // undirected edges
                    addEdge(nodes[i], nodes[j], directedEdgesParameter
                            .getBoolean().booleanValue());
                }
            }
        }

        boolean connectGraph = connectedGraphParameter.getBoolean()
                .booleanValue();

        if (connectGraph == true) {
            int[] groups = new int[n];

            for (int i = 0; i < n; i++) {
                groups[i] = i;
            }

            for (int i = 0; i < n; ++i) {
                for (int j = i + 1; j < n; ++j) {
                    Collection<Edge> ce = graph.getEdges(nodes[i], nodes[j]);

                    if (!ce.isEmpty() && (groups[i] != groups[j])) {
                        for (int k = 0; k < n; k++) {
                            if (groups[k] == groups[j]) {
                                groups[k] = groups[i];
                            }
                        }
                    }
                }
            }

            while (connectGraph) {
                int firstGroup = groups[0];

                connectGraph = false;

                for (int i = 1; (i < n) && (connectGraph == false); i++) {
                    if (firstGroup != groups[i]) {
                        connectGraph = true;
                    }
                }

                if (connectGraph == true) {
                    // sets minDistance to the maximum distance to start with
                    double minDistance = Math.sqrt((GLOBALX * GLOBALX)
                            + (GLOBALY * GLOBALY));
                    int firstNode = -1;
                    int secondNode = -1;

                    for (int i = 0; i < n; ++i) {
                        if (groups[i] == firstGroup) {
                            for (int j = 0; j < n; j++) {
                                if (j != i) {
                                    if (groups[j] != firstGroup) {
                                        if (distances[i][j] < minDistance) {
                                            minDistance = distances[i][j];
                                            firstNode = i;
                                            secondNode = j;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    addEdge(nodes[firstNode], nodes[secondNode],
                            directedEdgesParameter.getBoolean().booleanValue());

                    for (int i = 0; i < n; ++i) {
                        if (groups[i] == groups[secondNode]) {
                            groups[i] = groups[firstNode];
                        }
                    }
                }
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

        // nodesParam.setValue(new Integer(10));
        // edgeLengthParam.setValue(new Double(0.2));
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
