// =============================================================================
//
//   Swapper.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.In;
import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.Out;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class Swapper {
    private BlockGraph graph;
    private BlockMover blockMover;

    private int[] workingLevels;
    private Direction[] workingDirections;
    private int workingCount;

    private Block<?>[] edgeBlockContainerA;
    private Block<?>[] edgeBlockContainerB;

    public Swapper(BlockGraph graph, BlockMover blockMover) {
        this.graph = graph;
        this.blockMover = blockMover;
        workingLevels = new int[4];
        workingDirections = new Direction[4];
        edgeBlockContainerA = new Block<?>[1];
        edgeBlockContainerB = new Block<?>[1];
    }

    public int siftingSwap(Block<?> blockA, int[] crossCountingLevels,
            int[] crossCountDelta) {
        Block<?> blockB = blockA.currentPiNext;
        int bigDelta = 0;

        assert (blockB != null);

        workingCount = 0;

        if (blockB.isActive()) {
            int upperA = blockA.getUpperPhi();
            int lowerA = blockA.getLowerPhi();
            int upperB = blockB.getUpperPhi();
            int lowerB = blockB.getLowerPhi();

            // upperA in levels(B)
            if (upperB <= upperA && upperA <= lowerB) {
                workingLevels[workingCount] = upperA;
                workingDirections[workingCount] = In;
                workingCount++;
            }

            // upperB in levels(A)
            if (upperA <= upperB
                    && upperB <= lowerA
                    && (workingCount == 0 || workingLevels[workingCount - 1] != upperB)) {
                workingLevels[workingCount] = upperB;
                workingDirections[workingCount] = In;
                workingCount++;
            }

            // lowerA in levels(B)
            if (upperB <= lowerA && lowerA <= lowerB) {
                workingLevels[workingCount] = lowerA;
                workingDirections[workingCount] = Out;
                workingCount++;
            }

            if (upperA <= lowerB
                    && lowerB <= lowerA
                    && (workingCount == 0
                            || workingLevels[workingCount - 1] != lowerB || workingDirections[workingCount - 1] != Out)) {
                workingLevels[workingCount] = lowerB;
                workingDirections[workingCount] = Out;
                workingCount++;
            }

            for (int i = 0; i < workingCount; i++) {
                Direction direction = workingDirections[i];

                int level = graph.incLevel(workingLevels[i], direction);

                Block<?>[] aBlocks;

                if (blockA.isNode()) {
                    aBlocks = blockA.sortedBlocks.get(direction);
                } else {
                    aBlocks = edgeBlockContainerA;
                    aBlocks[0] = blockA;
                }

                Block<?>[] bBlocks;

                if (blockB.isNode()) {
                    bBlocks = blockB.sortedBlocks.get(direction);
                } else {
                    bBlocks = edgeBlockContainerB;
                    bBlocks[0] = blockB;
                }

                int delta = uswap(aBlocks, bBlocks, level);

                // The level where the crossing count changed.
                // int affectedLevel = direction == In ? level : level - 1;
                int affectedLevel = Math.min(workingLevels[i], level);

                for (int levelIndex = 0; levelIndex < crossCountingLevels.length; levelIndex++) {
                    if (affectedLevel == crossCountingLevels[levelIndex]) {
                        // The crossings changed in a level we are interested
                        // in.
                        crossCountDelta[levelIndex] += delta;
                    }
                }

                bigDelta += delta;
            }

            int level = graph.incLevel(blockA.getUpperPhi(), In);
            if (level == graph.incLevel(blockB.getUpperPhi(), In)) {
                updateAdjacency(blockA, blockB, Direction.In, level);
            }

            level = graph.incLevel(blockA.getLowerPhi(), Out);
            if (level == graph.incLevel(blockB.getLowerPhi(), Out)) {
                updateAdjacency(blockA, blockB, Direction.Out, level);
            }
        }

        blockMover.shiftRight(blockA);
        return bigDelta;
    }

    public int uswap(Block<?>[] x, Block<?>[] y, int level) {
        int r = x.length;
        int s = y.length;

        int c = 0;
        int i = 0;
        int j = 0;

        while (i < r && j < s) {
            int piXi = x[i].getPi(level);
            int piYj = y[j].getPi(level);

            // if (piXi <= piYj)
            // {
            // c += s - j;
            // i++;
            // }
            //            
            // if (piXi >= piYj)
            // {
            // c -= r - i;
            // j++;
            // }

            if (piXi < piYj) {
                c += s - j;
                i++;
            } else if (piXi > piYj) {
                c -= r - i;
                j++;
            } else {
                c += (s - j) - (r - i);
                i++;
                j++;
            }
        }

        return c;
    }

    private void updateAdjacency(Block<?> blockA, Block<?> blockB,
            Direction direction, int level) {
        Block<?>[] aBlocks = blockA.sortedBlocks.get(direction);
        Block<?>[] bBlocks = blockB.sortedBlocks.get(direction);

        int[] aAdjacencyIndices = blockA.adjacencyIndices.get(direction);
        int[] bAdjacencyIndices = blockB.adjacencyIndices.get(direction);

        int r = aBlocks.length;
        int s = bBlocks.length;

        int i = 0;
        int j = 0;

        while (i < r && j < s) {
            int piXi = aBlocks[i].getPi(level);
            int piYj = bBlocks[j].getPi(level);

            if (piXi < piYj) {
                i++;
            } else if (piXi > piYj) {
                j++;
            } else {
                Block<?> z = aBlocks[i];

                if (!z.isActive()) {
                    z = z.blocks.get(direction)[0];
                }

                Direction oppDir = direction.getOpposite();

                Block<?>[] zSortedBlocks = z.sortedBlocks.get(oppDir);
                int[] zAdjacencyIndices = z.adjacencyIndices.get(oppDir);

                int aAIi = aAdjacencyIndices[i];

                Block<?> aRep = zSortedBlocks[aAIi];
                Block<?> bRep = zSortedBlocks[aAIi + 1];

                // Swapping in N^{-d}(z)
                zSortedBlocks[aAIi] = bRep;
                zSortedBlocks[aAIi + 1] = aRep;

                // Swapping in I^{-d}(z)
                int swappingIndex = zAdjacencyIndices[aAIi];
                zAdjacencyIndices[aAIi] = zAdjacencyIndices[aAIi + 1];
                zAdjacencyIndices[aAIi + 1] = swappingIndex;

                //
                aAdjacencyIndices[i]++;
                bAdjacencyIndices[j]--;

                i++;
                j++;
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
