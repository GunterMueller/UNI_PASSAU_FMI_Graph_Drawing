package org.graffiti.plugins.tools.benchmark.generators;

import java.awt.Point;
import java.util.Random;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.tools.benchmark.sampler.SamplingContext;
import org.graffiti.plugins.tools.math.Permutation;

public class TwoLayerGraphGenerator extends DeterministicGraphGenerator {
    private static final String FIRST_SIZE = "firstSize";
    private static final String SECOND_SIZE = "secondSize";
    private static final String EDGE_COUNT = "edgeCount";

    @Override
    protected Node[] generate(Graph graph, SamplingContext context) {
        Random random = context.getRandom();

        int firstSize = context.getVariable(FIRST_SIZE).intValue();
        int secondSize = context.getVariable(SECOND_SIZE).intValue();
        int edgeCount = context.getVariable(EDGE_COUNT).intValue();

        Permutation permutation = new Permutation(firstSize * secondSize);
        permutation.shuffle(random);

        Node[] nodes = new Node[firstSize + secondSize];
        for (int j = 0; j < secondSize; j++) {
            nodes[j + firstSize] = graph.addNode();
        }
        for (int i = 0; i < firstSize; i++) {
            nodes[i] = graph.addNode();
            for (int j = 0; j < secondSize; j++) {
                boolean hasEdge = permutation.get(i * secondSize + j) < edgeCount;
                if (hasEdge) {
                    graph.addEdge(nodes[i], nodes[j + firstSize], true);
                }
            }
        }
        return nodes;
    }

    @Override
    protected void assignCoordinates(Node[] nodes, SamplingContext context) {
        int firstSize = context.getVariable(FIRST_SIZE).intValue();
        int secondSize = context.getVariable(SECOND_SIZE).intValue();

        for (int i = 0; i < firstSize; i++) {
            assignCoordinate(nodes[i], new Point(i * 100, 0));
            nodes[i].setInteger(SugiyamaConstants.PATH_LEVEL, 0);
        }

        for (int j = 0; j < secondSize; j++) {
            assignCoordinate(nodes[j + firstSize], new Point(j * 100, 100));
            nodes[j + firstSize].setInteger(SugiyamaConstants.PATH_LEVEL, 1);
        }
    }
}
