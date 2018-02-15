package org.graffiti.plugins.tools.benchmark.generators;

import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingContext;
import org.graffiti.plugins.tools.math.Permutation;

public class TournamentGraphGenerator extends DeterministicGraphGenerator {
    private static final String SIZE = "size";
    private static final String BACK_EDGE_COUNT = "backEdgeCount";

    @Override
    protected Node[] generate(Graph graph, SamplingContext context) {
        Random random = context.getRandom();

        int size = context.getVariable(SIZE).intValue();
        int backEdgeCount = context.getVariable(BACK_EDGE_COUNT).intValue();

        Permutation permutation = new Permutation(size * (size - 1) / 2);
        permutation.shuffle(random);

        Node[] nodes = new Node[size];

        for (int i = 0; i < size; i++) {
            nodes[i] = graph.addNode();
            for (int j = 0; j < i; j++) {
                boolean isBackEdge = permutation.get(i * (i - 1) / 2 + j) < backEdgeCount;
                if (isBackEdge) {
                    graph.addEdge(nodes[i], nodes[j], true);
                } else {
                    graph.addEdge(nodes[j], nodes[i], true);
                }
            }
        }
        return nodes;
    }
}
