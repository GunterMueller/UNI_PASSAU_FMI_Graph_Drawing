// =============================================================================
//
//   EdgeBlockActivator.java
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
public class EdgeBlockActivator {
    private BlockGraph blockGraph;
    private final NodeBlock[] nodeBlocks;
    private final EdgeBlock[] edgeBlocks;
    private final int blockCount;
    private final int size;

    private Block<?>[] firstInBucket;
    private Block<?>[] lastInBucket;

    public EdgeBlockActivator(BlockGraph blockGraph) {
        this.blockGraph = blockGraph;
        nodeBlocks = blockGraph.nodeBlocks;
        edgeBlocks = blockGraph.edgeBlocks;
        blockCount = blockGraph.blockCount;
        size = 2 * blockCount - 1;

        firstInBucket = new Block<?>[size];
        lastInBucket = new Block<?>[size];
    }

    private void addBlockToBucket(Block<?> block, int index) {
        if (firstInBucket[index] == null) {
            firstInBucket[index] = block;
            lastInBucket[index] = block;
            block.currentPiPrev = null;
            block.currentPiNext = null;
        } else {
            lastInBucket[index].currentPiNext = block;
            block.currentPiPrev = lastInBucket[index];
            lastInBucket[index] = block;
        }
    }

    // hotPhi level of the nodeBlock currently globally sifted. May be -1 if
    // no nodeBlock is currently sifted (i.e. no node blocks at odd levels).
    public void updateEdgeBlocksActivation(int hotPhi) {
        // No need to clear blockBuckets as updatesEdgeBlocksActivation cleans
        // up that array during the last for loop.

        for (NodeBlock nodeBlock : nodeBlocks) {
            addBlockToBucket(nodeBlock, 2 * nodeBlock.currentPiValue);
        }

        // [level - 1; level + 1] were considered inactive, place them between
        // their adjacent node blocks.
        for (EdgeBlock edgeBlock : edgeBlocks) {
            int upperPhi = edgeBlock.getUpperPhi();
            int lowerPhi = edgeBlock.getLowerPhi();

            if (edgeBlock.isActive) {
                addBlockToBucket(edgeBlock, 2 * edgeBlock.currentPiValue);
            } else {
                addBlockToBucket(edgeBlock, edgeBlock.source.currentPiValue
                        + edgeBlock.target.currentPiValue);
            }

            edgeBlock.isActive = upperPhi <= lowerPhi;
            // edgeBlock.isActive = upperPhi < lowerPhi || upperPhi == hotPhi &&
            // lowerPhi == hotPhi;
        }

        Block<?> lastBlock = null;

        for (int i = 0; i < size; i++) {
            Block<?> first = firstInBucket[i];

            if (first != null) {
                if (lastBlock == null) {
                    blockGraph.currentPiFirst = first;
                } else {
                    lastBlock.currentPiNext = first;
                    first.currentPiPrev = lastBlock;
                }

                lastBlock = lastInBucket[i];

                firstInBucket[i] = null;
                lastInBucket[i] = null;
            }
        }

        blockGraph.currentPiLast = lastBlock;

        lastBlock = blockGraph.currentPiFirst;
        for (int pi = 0; lastBlock != null; lastBlock = lastBlock.currentPiNext, pi++) {
            lastBlock.currentPiValue = pi;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
