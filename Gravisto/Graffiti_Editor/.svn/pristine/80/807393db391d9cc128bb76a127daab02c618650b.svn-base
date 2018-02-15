// =============================================================================
//
//   MCMLayoutAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.ArrayList;

import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MCMLayoutAlgorithm extends MCMAbstractAlgorithm implements
        LayoutAlgorithm {
    private static final String NAME = MCMCrossMinAlgorithm
            .getString("layout.name");
    private static final int CROSS_MIN_PHASE = 2;
    private static final double DEFAULT_LAYER_DISTANCE = 100.0;
    private static final double DEFAULT_NODE_DISTANCE = 100.0;
    private static final double SLIDER_MIN = 5;
    private static final double SLIDER_MAX = 500;

    private double nodeDistance;
    private double layerDistance;

    @Override
    public void check() throws PreconditionException {
        SugiyamaAlgorithm alg = data.getSelectedAlgorithms()[CROSS_MIN_PHASE];
        if (alg instanceof MCMCrossMinAlgorithm) {
            // parameters = ((MMSCrossMinAlgorithm) alg).getMMSParameters();
        } else
            throw new PreconditionException(MCMCrossMinAlgorithm
                    .getString("exception.precondition.notcrossmin"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsArbitraryXPos() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        NodeLayers nodeLayers = data.getLayers();
        int layerCount = nodeLayers.getNumberOfLayers();
        for (int i = 0; i < layerCount; i++) {
            ArrayList<Node> layer = nodeLayers.getLayer(i);
            for (Node node : layer) {
                node.setDouble(GraphicAttributeConstants.COORDY_PATH, i
                        * nodeDistance);
                node.setDouble(GraphicAttributeConstants.COORDX_PATH, node
                        .getDouble(SugiyamaConstants.PATH_XPOS)
                        * layerDistance);
            }
        }
        graph.getListenerManager().transactionFinished(this);
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
                new DoubleParameter(DEFAULT_NODE_DISTANCE,
                        getString("params.nodeDistance.name"),
                        getString("params.nodeDistance.desc"), SLIDER_MIN,
                        SLIDER_MAX),
                new DoubleParameter(DEFAULT_LAYER_DISTANCE,
                        getString("params.layerDistance.name"),
                        getString("params.layerDistance.desc"), SLIDER_MIN,
                        SLIDER_MAX) };
    }

    private static String getString(String key) {
        return MCMCrossMinAlgorithm.getString(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        nodeDistance = ((DoubleParameter) params[0]).getDouble();
        layerDistance = ((DoubleParameter) params[1]).getDouble();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
