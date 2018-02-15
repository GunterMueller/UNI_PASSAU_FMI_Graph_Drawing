// =============================================================================
//
//   SiftingLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.Arrays;
import java.util.HashSet;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SimpleGraph;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SiftingLevelling extends AbstractLevellingAlgorithm implements
        LevellingAlgorithm {
    private static final String NAME = "Sifting levelling";

    private SugiyamaData data;

    private int nodeCount;
    private int[] levelByNode;
    private int[] nodeByLevel;
    private int currentNodeCount;

    private SimpleGraph simpleGraph;

    @Override
    public SugiyamaData getData() {
        return data;
    }

    @Override
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    @Override
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    @Override
    public boolean supportsBigNodes() {
        return false;
    }

    @Override
    public boolean supportsConstraints() {
        return false;
    }

    @Override
    public void execute() {
        System.out.println("Start level sifting");
        simpleGraph = new SimpleGraph(graph);

        nodeCount = simpleGraph.getNodeCount();

        levelByNode = new int[nodeCount];
        Arrays.fill(levelByNode, -1);
        nodeByLevel = new int[nodeCount];

        currentNodeCount = 0;

        int[] sortedNodes = topoSort();

        for (int i = 0; i < nodeCount; i++) {
            int node = sortedNodes[i];

            nodeByLevel[currentNodeCount] = node;
            levelByNode[node] = currentNodeCount;
            currentNodeCount++;

            siftingStep(node);
        }

        int delta;

        int roundCount = 80;

        for (int round = 0; round < roundCount; round++) {
            delta = 0;

            for (int node = 0; node < nodeCount; node++) {
                delta += siftingStep(node);
            }

            if (delta == 0) {
                break;
            }
        }

        System.out.println("exporting...");

        export();

        System.out.println("done.");
    }

    private int[] topoSort() {
        int[] result = new int[nodeCount];
        int[] inDegrees = new int[nodeCount];

        int nextInsertIndex = 0;

        for (int node = 0; node < nodeCount; node++) {
            int inDegree = simpleGraph.getInNeighbors(node).length;
            inDegrees[node] = inDegree;
            if (inDegree == 0) {
                result[nextInsertIndex] = node;
                nextInsertIndex++;
            }
        }

        int nextReadIndex = 0;

        while (nextReadIndex < nodeCount) {
            int node = result[nextReadIndex];

            int[] outNeighbors = simpleGraph.getOutNeighbors(node);
            int outDegree = outNeighbors.length;

            for (int i = 0; i < outDegree; i++) {
                int outNeighbor = outNeighbors[i];
                inDegrees[outNeighbor]--;
                if (inDegrees[outNeighbor] == 0) {
                    result[nextInsertIndex] = outNeighbor;
                    nextInsertIndex++;
                }
            }

            nextReadIndex++;
        }

        return result;
    }

    private int siftingStep(int node) {
        int minLevel = Math.max(0, calculateMaxLevel(simpleGraph
                .getInNeighbors(node)) + 1);
        int originalLevel = levelByNode[node];

        moveLeft(originalLevel, minLevel);

        int maxLevel = Math.min(currentNodeCount - 1,
                calculateMinLevel(simpleGraph.getOutNeighbors(node)) - 1);

        int chi = 0;
        int bestChi = 0;
        int bestLevel = minLevel;
        int inDegree = simpleGraph.getInNeighbors(node).length;
        int outDegree = simpleGraph.getOutNeighbors(node).length;
        int chiNull = 0;

        for (int i = minLevel; i < maxLevel; i++) {
            // Swap i with i + 1
            int otherNode = nodeByLevel[i + 1];
            int delta = inDegree
                    + simpleGraph.getOutNeighbors(otherNode).length - outDegree
                    - simpleGraph.getInNeighbors(otherNode).length;
            chi += delta;
            if (chi < bestChi) {
                bestChi = chi;
                bestLevel = i + 1;
            }

            if (i + 1 == originalLevel) {
                chiNull = chi;
            }

            nodeByLevel[i] = otherNode;
            nodeByLevel[i + 1] = node;
            levelByNode[node]++;
            levelByNode[otherNode]--;
        }

        if (bestLevel < maxLevel) {
            moveLeft(maxLevel, bestLevel);
        }

        return chi - chiNull;
    }

    private void moveLeft(int fromLevel, int toLevel) {
        int node = nodeByLevel[fromLevel];

        for (int i = fromLevel; i > toLevel; i--) {
            int nn = nodeByLevel[i - 1];
            nodeByLevel[i] = nn;
            levelByNode[nn]++;
        }

        nodeByLevel[toLevel] = node;
        levelByNode[node] = toLevel;
    }

    private void export() {
        graph.getListenerManager().transactionStarted(this);

        NodeLayers layers = data.getLayers();
        for (int level = 0; level < nodeCount; level++) {
            layers.addLayer();
            int nn = nodeByLevel[level];
            Node node = simpleGraph.getNode(nn);
            layers.getLayer(level).add(node);
            // node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
        }

        HashSet<Node> dummies;
        dummies = addDummies(data);
        data.setDummyNodes(dummies);

        for (int level = 0; level < nodeCount; level++) {
            for (Node node : layers.getLayer(level)) {
                node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private int calculateMinLevel(int[] nodes) {
        int len = nodes.length;
        int result = Integer.MAX_VALUE;

        for (int i = 0; i < len; i++) {
            int level = levelByNode[nodes[i]];
            if (level != -1 && level < result) {
                result = level;
            }
        }

        return result;
    }

    private int calculateMaxLevel(int[] nodes) {
        int len = nodes.length;
        int result = Integer.MIN_VALUE;

        for (int i = 0; i < len; i++) {
            int level = levelByNode[nodes[i]];
            if (level != -1 && level > result) {
                result = level;
            }
        }

        return result;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
