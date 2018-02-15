// =============================================================================
//
//   GraphicAttributeAssurer.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.util;

import java.awt.geom.Point2D;
import java.util.Random;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.tools.benchmark.SeedableAlgorithm;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class GraphicAttributeAssurer extends AbstractAlgorithm implements
        SeedableAlgorithm {
    private static final String NAME = "Graphic Attribute Ensurer";
    private static final double SPREAD = 200.0;

    private long seed;
    private boolean randomizeCoordinates;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Random random = new Random(seed);

        for (Node node : graph.getNodes()) {
            if (!node.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
                NodeGraphicAttribute nga = new NodeGraphicAttribute();
                if (randomizeCoordinates) {
                    nga.getCoordinate().setCoordinate(
                            new Point2D.Double(random.nextDouble() * SPREAD,
                                    random.nextDouble() * SPREAD));
                }
                node.addAttribute(nga, "");
            }
        }

        for (Edge edge : graph.getEdges()) {
            if (!edge.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
                EdgeGraphicAttribute ega = new EdgeGraphicAttribute();
                edge.addAttribute(ega, "");
            }
        }

        if (!graph.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            graph.addAttribute(new GraphGraphicAttribute(), "");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] {
                new StringParameter(String.valueOf((new Random().nextLong())),
                        "seed", ""),
                new BooleanParameter(true, "randomizeCoordinates", "") };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        seed = Long.parseLong(((StringParameter) params[0]).getString());
        randomizeCoordinates = ((BooleanParameter) params[1]).getBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringParameter getSeedParameteer(Parameter<?>[] parameters) {
        return (StringParameter) parameters[0];
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
