// =============================================================================
//
//   DeterministicBFSLevelling.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.chebyshev.MCMCrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.levelling.DummyLevelling;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.tools.benchmark.BenchmarkAttribute;
import org.graffiti.plugins.tools.math.Permutation;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DeterministicBFSLevelling extends DummyLevelling {
    private static class TiebreakerComparator implements Comparator<Integer> {
        private int[] tiebreakers;

        public TiebreakerComparator(int[] tiebreakers) {
            this.tiebreakers = tiebreakers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.valueOf(tiebreakers[o1]).compareTo(tiebreakers[o2]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        int nodesCount = graph.getNumberOfNodes();
        Map<Node, Integer> nodeToIdMap = new HashMap<Node, Integer>();
        Node[] nodes = new Node[nodesCount];
        int[] tiebreakers = new int[nodesCount];
        boolean marked[] = new boolean[nodesCount];
        ArrayList<Integer> currentLayer = new ArrayList<Integer>();

        if (!graph.isEmpty()
                && !graph.getNodes().get(0).containsAttribute(
                        SugiyamaConstants.PATH_LEVEL)) {
            if (!graph.containsAttribute(BenchmarkAttribute.PATH)) {
                Permutation permutation = new Permutation(nodesCount);
                permutation.shuffle(new Random());
                int id = 0;
                for (Node node : graph.getNodes()) {
                    nodeToIdMap.put(node, id);
                    nodes[id] = node;
                    tiebreakers[id] = permutation.get(id);
                    if (node.getInDegree() == 0) {
                        currentLayer.add(id);
                    }
                    id++;
                }
            } else {
                for (Node node : graph.getNodes()) {
                    int id = node.getInteger(BenchmarkAttribute.UID_PATH);
                    nodeToIdMap.put(node, id);
                    nodes[id] = node;
                    tiebreakers[id] = node
                            .getInteger(BenchmarkAttribute.TIEBREAKER_PATH);
                    if (node.getInDegree() == 0) {
                        currentLayer.add(id);
                    }
                }
            }
            TiebreakerComparator comparator = new TiebreakerComparator(
                    tiebreakers);
            int level = 0;
            while (!currentLayer.isEmpty()) {
                Collections.sort(currentLayer, comparator);
                ArrayList<Integer> nextLayer = new ArrayList<Integer>();

                for (Integer nodeIndex : currentLayer) {
                    Node node = nodes[nodeIndex];
                    node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
                    for (Node next : node.getOutNeighbors()) {
                        int nextId = nodeToIdMap.get(next);
                        if (marked[nextId]) {
                            continue;
                        }
                        marked[nextId] = true;
                        nextLayer.add(nextId);
                    }
                }

                currentLayer = nextLayer;
                level++;
            }

        }
        super.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return MCMCrossMinAlgorithm.getString("levelling.name");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
