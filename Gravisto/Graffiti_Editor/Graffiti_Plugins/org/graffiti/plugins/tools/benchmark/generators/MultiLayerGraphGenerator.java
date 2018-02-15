// =============================================================================
//
//   MultiLayerGenerator.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingContext;
import org.graffiti.plugins.tools.math.Permutation;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MultiLayerGraphGenerator extends DeterministicGraphGenerator {
    private static final String LAYER_COUNT = "layerCount";
    private static final String LAYER = "layer";
    private static final String NODE_COUNT = "nodeCount";
    private static final String EDGE_COUNT = "edgeCount";

    private LinkedList<Integer> layerSizes;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node[] generate(Graph graph, SamplingContext context) {
        Random random = context.getRandom();

        int layerCount = context.getVariable(LAYER_COUNT).intValue();

        ArrayList<Node> nodes = new ArrayList<Node>();
        context.setVariable("layerIndex", 0);
        context.setVariable("prevNodeCount", 0);
        SamplingContext subContext = context.createContext(LAYER);
        int prevLayerSize = subContext.getVariable(NODE_COUNT).intValue();

        layerSizes = new LinkedList<Integer>();
        layerSizes.add(prevLayerSize);

        for (int i = 0; i < prevLayerSize; i++) {
            nodes.add(graph.addNode());
        }

        for (int layer = 1; layer < layerCount; layer++) {
            context.setVariable("layerIndex", layer);
            context.setVariable("prevNodeCount", prevLayerSize);
            subContext = context.createContext(LAYER);
            int layerSize = subContext.getVariable(NODE_COUNT).intValue();
            int edgeCount = subContext.getVariable(EDGE_COUNT).intValue();

            layerSizes.add(layerSize);

            for (int i = 0; i < layerSize; i++) {
                nodes.add(graph.addNode());
            }

            int maxEdgeCount = layerSize * prevLayerSize;

            if (maxEdgeCount > 0) {
                Permutation permutation = new Permutation(maxEdgeCount);
                permutation.shuffle(random);

                int secondBase = nodes.size() - layerSize;
                int firstBase = secondBase - prevLayerSize;

                for (int i = 0; i < prevLayerSize; i++) {
                    for (int j = 0; j < layerSize; j++) {
                        if (permutation.get(i * layerSize + j) < edgeCount) {
                            graph.addEdge(nodes.get(firstBase + i), nodes
                                    .get(secondBase + j), true);
                        }
                    }
                }
            }

            prevLayerSize = layerSize;
        }

        return nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assignCoordinates(Node[] nodes, SamplingContext context) {
        int i = 0;
        int y = 0;
        for (int layerSize : layerSizes) {
            for (int x = 0; x < layerSize; x++) {
                nodes[i].setInteger(SugiyamaConstants.PATH_LEVEL, y);
                assignCoordinate(nodes[i], new Point2D.Double(Math
                        .round(x * 100), y * 100));
                i++;
            }
            y++;
        }
        layerSizes = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
