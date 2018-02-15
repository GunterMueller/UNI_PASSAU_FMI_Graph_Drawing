// =============================================================================
//
//   BlockMover.java
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
class BlockMover {
    private BlockGraph graph;

    public BlockMover(BlockGraph graph) {
        this.graph = graph;
    }

    public void putAtFirstPosition(Block<?> block) {
        if (block != graph.currentPiFirst) {
            if (block == graph.currentPiLast) {
                graph.currentPiLast = block.currentPiPrev;
                graph.currentPiLast.currentPiNext = null;
            }

            block.currentPiPrev.currentPiNext = block.currentPiNext;

            if (block.currentPiNext != null) {
                block.currentPiNext.currentPiPrev = block.currentPiPrev;
            }

            graph.currentPiFirst.currentPiPrev = block;
            block.currentPiNext = graph.currentPiFirst;
            graph.currentPiFirst = block;
            block.currentPiPrev = null;
        }
    }

    private void remove(Block<?> block) {
        if (block.currentPiPrev != null) {
            block.currentPiPrev.currentPiNext = block.currentPiNext;
        }

        if (block.currentPiNext != null) {
            block.currentPiNext.currentPiPrev = block.currentPiPrev;
        }

        if (block == graph.currentPiFirst) {
            graph.currentPiFirst = block.currentPiNext;
        }

        if (block == graph.currentPiLast) {
            graph.currentPiLast = block.currentPiPrev;
        }
    }

    public void putAfter(Block<?> block, Block<?> otherBlock) {
        assert (block != otherBlock);

        remove(block);

        if (otherBlock.currentPiNext != null) {
            otherBlock.currentPiNext.currentPiPrev = block;
        }

        block.currentPiNext = otherBlock.currentPiNext;
        block.currentPiPrev = otherBlock;
        otherBlock.currentPiNext = block;

        if (graph.currentPiLast == otherBlock) {
            graph.currentPiLast = block;
        }
    }

    public void shiftRight(Block<?> block) {
        assert (block.currentPiNext != null);

        if (block.currentPiPrev != null) {
            block.currentPiPrev.currentPiNext = block.currentPiNext;
        } else {
            graph.currentPiFirst = block.currentPiNext;
        }

        block.currentPiNext.currentPiPrev = block.currentPiPrev;
        block.currentPiPrev = block.currentPiNext;
        block.currentPiNext = block.currentPiNext.currentPiNext;

        if (block.currentPiNext != null) {
            block.currentPiNext.currentPiPrev = block;
        } else {
            graph.currentPiLast = block;
        }

        block.currentPiPrev.currentPiNext = block;

        block.currentPiPrev.currentPiValue--;
        block.currentPiValue++;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
