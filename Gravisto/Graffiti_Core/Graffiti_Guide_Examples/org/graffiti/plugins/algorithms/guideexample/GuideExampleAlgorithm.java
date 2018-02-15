// =============================================================================
//
//   GuideExampleAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.guideexample;

import java.awt.geom.Point2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.core.Bundle;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class GuideExampleAlgorithm extends AbstractAlgorithm {
    /** The number of nodes. */
    private IntegerParameter nodesParam;

    /** Resource bundle for the algorithm. */
    private Bundle bundle = Bundle.getBundle(getClass());

    public GuideExampleAlgorithm() {
        nodesParam = new IntegerParameter(5, bundle
                .getString("parameter.nodes_cnt.name"), bundle
                .getString("parameter.nodes_cnt.description"), 0, 50, 0,
                Integer.MAX_VALUE);
    }

    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { nodesParam };
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getInteger().compareTo(new Integer(0)) < 0) {
            errors.add(bundle.getString("precondition.nodes_ge_zero"));
        }

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add(bundle.getString("precondition.graph_null"));
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
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

    /*
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return bundle.getString("name");
    }

    // /*
    // * @see org.graffiti.plugin.algorithm.Algorithm#reset()
    // */
    // @Override
    // public void reset()
    // {
    // graph = null;
    // nodesParam.setValue(new Integer(5));
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
