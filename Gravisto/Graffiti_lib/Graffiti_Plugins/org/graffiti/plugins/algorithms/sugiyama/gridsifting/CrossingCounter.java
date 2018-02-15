// =============================================================================
//
//   CrossingCounter.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.Out;

import java.util.Arrays;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class CrossingCounter {
    private BlockGraph graph;
    private int blockCount;

    private int[] lowerPi;
    private int lineCount;

    private boolean[] hit;
    private int[] piToIndex;
    private int indexCount;

    private int[] tree;

    public CrossingCounter(BlockGraph graph) {
        this.graph = graph;

        blockCount = graph.blockCount;

        lowerPi = new int[blockCount];
        lineCount = 0;

        hit = new boolean[blockCount];
        piToIndex = new int[blockCount];
        indexCount = 0;

        tree = new int[4 * blockCount];
    }

    /**
     * Precondition: No intermediate level between upperLevel and lowerLevel.
     * Correct pi values.
     * 
     * @param upperLevel
     * @param lowerLevel
     */
    public int countCrossings(int upperLevel, int lowerLevel) {
        if (upperLevel > lowerLevel) {
            int chg = upperLevel;
            upperLevel = lowerLevel;
            lowerLevel = chg;
        }

        lineCount = 0;

        Block<?> block = graph.currentPiFirst;

        while (block != null) {
            if (block.isActive() && block.containsPhi(upperLevel)) {
                if (block.isNode()) {
                    for (Block<?> adjBlock : block.sortedBlocks.get(Out)) {
                        addLowerPi(adjBlock.getPi(lowerLevel));
                    }
                } else {
                    addLowerPi(block.getPi(lowerLevel));
                }
            }

            block = block.currentPiNext;
        }

        indexCount = 0;

        for (int piValue = 0; piValue < blockCount; piValue++) {
            if (hit[piValue]) {
                piToIndex[piValue] = indexCount;
                indexCount++;
                hit[piValue] = false;
            }
        }

        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            lowerPi[lineIndex] = piToIndex[lowerPi[lineIndex]];
        }

        return countCrossings();
    }

    private void addLowerPi(int pi) {
        lowerPi[lineCount] = pi;
        lineCount++;
        hit[pi] = true;
    }

    private int countCrossings() {
        int firstIndex = 1;

        while (firstIndex < indexCount) {
            firstIndex <<= 1;
        }

        int treeSize = 2 * firstIndex - 1;
        firstIndex--;

        Arrays.fill(tree, 0, treeSize, 0);

        int crossCount = 0;

        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            int index = lowerPi[lineIndex] + firstIndex;
            tree[index]++;

            while (index > 0) {
                if ((index & 1) != 0) {
                    crossCount += tree[index + 1];
                }
                index = (index - 1) / 2;
                tree[index]++;
            }
        }

        return crossCount;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
