package org.graffiti.plugins.tools.benchmark.generators;

import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingContext;
import org.graffiti.plugins.tools.math.Permutation;

public class DecyclifiedGraphGenerator extends DeterministicGraphGenerator {
    private static final String SIZE = "size";
    private static final String EDGE_COUNT = "edgeCount";

    @Override
    protected Node[] generate(Graph graph, SamplingContext context) {
        Random random = context.getRandom();
        int size = context.getVariable(SIZE).intValue();
        int edgeCount = context.getVariable(EDGE_COUNT).intValue();

        Permutation permutation = new Permutation(size * (size - 1) / 2);
        permutation.shuffle(random);

        Node[] nodes = new Node[size];

        for (int i = 0; i < size; i++) {
            nodes[i] = graph.addNode();
            for (int j = 0; j < i; j++) {
                boolean hasEdge = permutation.get(i * (i - 1) / 2 + j) < edgeCount;
                if (hasEdge) {
                    graph.addEdge(nodes[j], nodes[i], true);
                }
            }
        }

        return nodes;
    }
}
