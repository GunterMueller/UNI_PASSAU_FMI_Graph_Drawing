// =============================================================================
//
//   AdjacencySorter.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class AdjacencySorter {
    private BlockGraph graph;

    public AdjacencySorter(BlockGraph graph) {
        this.graph = graph;
    }

    public void updatePiValues() {
        Block<?> block = graph.currentPiFirst;

        for (int piValue = 0; block != null; piValue++, block = block.currentPiNext) {
            block.currentPiValue = piValue;
        }
    }

    // O(|E|)
    public void sortAdjacencies() {
        Block<?> block = graph.currentPiFirst;

        while (block != null) {

            if (block.isActive()) {
                for (Direction dir : Direction.values())

                {
                    if (block.isNode()) {
                        NodeBlock nodeBlock = (NodeBlock) block;
                        for (EdgeBlock edgeBlock : nodeBlock.blocks.get(dir)) {
                            if (!edgeBlock.isActive) {
                                NodeBlock adjBlock = edgeBlock.blocks.get(dir)[0];
                                int j = adjBlock.addSortedBlock(dir
                                        .getOpposite(), edgeBlock);

                                if (nodeBlock.currentPiValue < adjBlock.currentPiValue) {
                                    // First traversal of edge.
                                    // Inactive edge so having to store single
                                    // value, so use 0 index.
                                    edgeBlock.cachedAdjacencyIndex = j;
                                } else {
                                    // Second traversal of edge.
                                    // Inactive edge so having to store single
                                    // value, so use 0 index.
                                    int ps = edgeBlock.cachedAdjacencyIndex;
                                    adjBlock.adjacencyIndices.get(dir
                                            .getOpposite())[j] = ps;
                                    nodeBlock.adjacencyIndices.get(dir)[ps] = j;
                                }
                            }
                        }
                    } else {
                        EdgeBlock edgeBlock = (EdgeBlock) block;
                        if (edgeBlock.isActive) {
                            NodeBlock adjBlock = edgeBlock.blocks.get(dir)[0];

                            int j = adjBlock.addSortedBlock(dir.getOpposite(),
                                    edgeBlock);
                            adjBlock.adjacencyIndices.get(dir.getOpposite())[j] = 0;
                            edgeBlock.adjacencyIndices.get(dir)[0] = j;
                        }
                    }
                }
            }

            block = block.currentPiNext;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
