// =============================================================================
//
//   AlgorithmTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AlgorithmTest.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.algorithms.test;

import java.awt.geom.Point2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * An implementation of a simple algorithm plugin example which generates a
 * horizontal node chain with a user defined number of nodes.
 * 
 * @author chris
 */
public class AlgorithmTest extends AbstractAlgorithm {

    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /**
     * Constructs a new instance.
     */
    public AlgorithmTest() {
        nodesParam = new IntegerParameter(5, "number of nodes",
                "the number of nodes to generate");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Test Graph Algorithm";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { nodesParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getInteger().compareTo(new Integer(0)) < 0) {
            errors.add("The number of nodes may not be smaller than zero.");
        }

        // The graph is inherited from AbstractAlgorithm.
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
        int n = nodesParam.getInteger().intValue();

        Node[] nodes = new Node[n];

        // start a transaction
        graph.getListenerManager().transactionStarted(this);

        // generate nodes and assign coordinates to them
        for (int i = 0; i < n; ++i) {
            nodes[i] = graph.addNode();

            CoordinateAttribute ca = (CoordinateAttribute) nodes[i]
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            double x = 100 + (i * 100);
            double y = 100;

            ca.setCoordinate(new Point2D.Double(x, y));
        }

        // add edges
        for (int i = 1; i < n; ++i) {
            graph.addEdge(nodes[i - 1], nodes[i], true);
        }

        // stop a transaction
        graph.getListenerManager().transactionFinished(this);

        // add arrows to edges
        graph.setDirected(true, true);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        nodesParam.setValue(new Integer(5));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
