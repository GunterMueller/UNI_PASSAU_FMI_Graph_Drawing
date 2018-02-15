package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a strongly connected component within the block graph.
 */
class SCC implements TopoSortable {

    /** The logger */
    private static final Logger logger = Logger.getLogger(SCC.class.getName());

    /** all the blocks that form this SCC */
    Set<Block> blocks = new HashSet<Block>();

    /** the left/right neighbors of a SCC */
    SCC[] leftNeighbors, rightNeighbors;

    /**
     * Fills the fields <code>leftNeighbors</code> and
     * <code>rightNeighbors</code> from the contained blocks' neighbors.
     */
    void findNeighbors() {
        Set<SCC> neighbors = new HashSet<SCC>();

        // for all blocks in this SCC
        for (Block currentBlock : blocks) {
            // for all left neighbors of those blocks
            for (Block leftNeighborBlock : currentBlock.getLeftNeighborBlocks())
                // if the block's left neighbor is not in this SCC
                if (!blocks.contains(leftNeighborBlock)) {
                    // then it is in a neighboring SCC -> add to neighbor list
                    neighbors.add(leftNeighborBlock.parentSCC);
                }
        }

        leftNeighbors = neighbors.toArray(new SCC[0]);

        neighbors.clear();

        // for all blocks in this SCC
        for (Block currentBlock : blocks) {
            // for all right neighbors of those blocks
            for (Block rightNeighborBlock : currentBlock
                    .getRightNeighborBlocks())
                // if the block's right neighbor is not in this SCC
                if (!blocks.contains(rightNeighborBlock)) {
                    // then it is in a neighboring SCC -> add to neighbor list
                    neighbors.add(rightNeighborBlock.parentSCC);
                }
        }

        rightNeighbors = neighbors.toArray(new SCC[0]);
    }

    /**
     * Returns the minimum distance of this SCC to the left.
     */
    private double getMinDistanceToTheLeft() {
        double dist = Double.POSITIVE_INFINITY;
        // for all of our blocks
        for (Block block : blocks) {
            // for all of their left neighbors
            for (Block leftNeighborBlock : block.getLeftNeighborBlocks())
                // if the neighbor is not part of this SCC
                if (!blocks.contains(leftNeighborBlock)) {
                    // then consider its distance
                    dist = min(dist, block
                            .getMinDistanceToLeftNeighbor(leftNeighborBlock));
                }
        }
        return dist;
    }

    /**
     * Returns the minimum distance of this SCC to the right.
     */
    private double getMinDistanceToTheRight() {
        double dist = Double.POSITIVE_INFINITY;
        // for all of our blocks
        for (Block block : blocks) {
            // for all of their right neighbors
            for (Block rightNeighborBlock : block.getRightNeighborBlocks())
                // if the neighbor is not part of this SCC
                if (!blocks.contains(rightNeighborBlock)) {
                    // then consider its distance
                    dist = min(dist, block
                            .getMinDistanceToRightNeighbor(rightNeighborBlock));
                }
        }
        return dist;
    }

    /**
     * Compactifies this SCC, i.e. moves the blocks together as close as
     * possible.
     */
    void compactify() {
        // perform a topological sort on the blocks
        Block[] topoSortedBlocks = new Block[blocks.size()];
        TopologicalSort.topoSort(new ArrayList<TopoSortable>(blocks),
                topoSortedBlocks);

        // from left to right: move blocks to the left
        for (int index = 0; index < topoSortedBlocks.length; index++) {
            topoSortedBlocks[index].moveLeft();
        }

        // from right to left: move blocks to the right
        for (int index = topoSortedBlocks.length - 1; index >= 0; index--) {
            topoSortedBlocks[index].moveRight();
        }
    }

    /**
     * Assigns each block in this SCC an inclination. If the SCC contains only
     * one block and this block does not loop to itself then that block will
     * simply remain vertical.
     * <p>
     * Otherwise the block(s) will be arranged in a way so they do not interfere
     * with each other, yet retain their stored adjacencies. The block(s) will
     * be non-vertical.
     * <p>
     * Destroys the block adjacencies. These need to be rebuilt afterwards.
     */
    void calculateInclination() {
        limitBlockNeighborsToThisSCC();

        if (blocks.size() == 1) {
            Block block = blocks.iterator().next();
            BlockgraphEdge edge = new BlockgraphEdge(block, block);
            if (block.leftNeighbors.containsKey(edge)) {
                doComplexLayout();
            }
        } else {
            doComplexLayout();
        }
    }

    private void doComplexLayout() {
        // cut all cycles
        WedgeCutter wc = new WedgeCutter();
        wc.cut(blocks);
        int levels = wc.winding;

        // compactify the now cycle-free blocks
        compactify();

        // calculate the inclination
        double smallestEdge = wc.getSmallestRemovedEdge();
        double inclination = (-smallestEdge + 1) / levels;
        logger.log(Level.FINE, "traversedLevels=" + levels + " smallestEdge="
                + smallestEdge + " inclination=" + inclination);

        // apply the inclination to all blocks
        for (Block block : blocks) {
            block.inclination = inclination;
        }
    }

    /**
     * For each block: Remove all neighbors that are not in this SCC.
     */
    private void limitBlockNeighborsToThisSCC() {
        Iterator<BlockgraphEdge> it;
        for (Block block : blocks) {
            it = block.leftNeighbors.keySet().iterator();
            while (it.hasNext())
                if (!blocks.contains(it.next().left)) {
                    it.remove();
                }
            it = block.rightNeighbors.keySet().iterator();
            while (it.hasNext())
                if (!blocks.contains(it.next().right)) {
                    it.remove();
                }
        }
    }

    /**
     * Moves the SCC as far as possible to the left.
     */
    void moveLeft() {
        // no predecessors -> don't move
        if (leftNeighbors.length == 0)
            return;

        // move
        move(-getMinDistanceToTheLeft() + 1);
    }

    /**
     * Moves the SCC as far as possible to the right.
     */
    void moveRight() {
        // no successors -> don't move
        if (rightNeighbors.length == 0)
            return;

        // move
        move(getMinDistanceToTheRight() - 1);
    }

    /**
     * Adds <code>delta</code> to the x-coord. of each block in this SCC.
     */
    void move(double delta) {
        // change all x coordinates (+delta)
        for (Block block : blocks) {
            block.move(delta);
        }

    }

    public Collection<TopoSortable> getTopoSortSuccessors() {
        return java.util.Arrays.asList((TopoSortable[]) rightNeighbors);
    }
}
