// =============================================================================
//
//   AuxGraph.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.ArrayList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.tools.benchmark.BenchmarkAttribute;
import org.graffiti.plugins.tools.math.Permutation;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class AuxGraph {
    private final AuxLayer[] layers;

    private Graph graph;

    private final int layerCount;
    private final int nodeCount;
    private final NodeLayers nodeLayers;

    public AuxGraph(SugiyamaData data, AlgorithmParameters parameters) {
        nodeLayers = data.getLayers();
        layerCount = nodeLayers.getNumberOfLayers();
        layers = new AuxLayer[layerCount];
        graph = data.getGraph();
        nodeCount = graph.getNumberOfNodes();
        Incubator incubator = new Incubator();

        boolean isNormalizing = parameters.isNormalizing();

        for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
            ArrayList<Node> nodes = nodeLayers.getLayer(layerIndex);
            int layerSize = nodes.size();
            AuxNode[] auxNodes = new AuxNode[layerSize];
            for (int i = 0; i < layerSize; i++) {
                auxNodes[i] = incubator.get(nodes.get(i));
            }
            layers[layerIndex] = new AuxLayer(auxNodes, isNormalizing);
        }

        if (!graph.containsAttribute(BenchmarkAttribute.PATH)) {
            Permutation permutation = new Permutation(nodeCount);
            permutation.shuffle(parameters.getRandom());
            int i = 0;
            for (Node node : graph.getNodes()) {
                incubator.get(node).setId(i, permutation.get(i));
                i++;
            }
        }

        for (AuxLayer layer : layers) {
            layer.finishCreation();
        }
    }

    public AuxLayer getLayer(int layerIndex) {
        return layers[layerIndex];
    }

    public int getLayerCount() {
        return layerCount;
    }

    public void apply() {
        graph.getListenerManager().transactionStarted(this);
        for (int i = 0; i < layerCount; i++) {
            layers[i].apply(nodeLayers.getLayer(i));
        }
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
