package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is used to perform a wedge-like cut in a block graph. After the
 * cut, there are no more cycles left in the graph.
 */
class WedgeCutter {
    private Set<BlockgraphEdge> markedEdges = new HashSet<BlockgraphEdge>();
    // private Map<Block, Integer> touchedBlocks = new HashMap<Block,
    // Integer>();
    // private int height;
    private Integer traversedLevels;

    public int winding = 0;

    /**
     * Performs the wedge cut. A random start block is picked and then the graph
     * is cut in the directions left-up and left-down.<br>
     * Removed edges are stored internally.
     */
    void cut(Set<Block> blocks) {
        winding = getWinding(blocks);
        if (traversedLevels != null)
            throw new IllegalStateException(
                    "Call this method only once per instance!");

        // randomly pick a block to start with
        Block startBlock = blocks.iterator().next();

        // remove all incoming edges from the start-block
        for (Block rightNeighbor : startBlock.getRightNeighborBlocks()) {
            markedEdges.add(new BlockgraphEdge(startBlock, rightNeighbor));
        }

        // cut upwards
        // height = startBlock.level;
        recursiveCut(startBlock, true);
        // touchedBlocks.clear();

        // cut downwards
        // height = startBlock.level;
        recursiveCut(startBlock, false);

        // remove the edges that have been cut
        removeAllMarkedEdges(blocks);
    }

    /**
     * Removes all edges that are stored in the field <code>markedEdges</code>
     * from the blocks' adjacency lists.
     */
    private void removeAllMarkedEdges(Set<Block> blocks) {
        for (BlockgraphEdge edge : markedEdges) {
            edge.left.rightNeighbors.remove(edge);
            edge.right.leftNeighbors.remove(edge);
        }
    }

    private void recursiveCut(Block block, boolean up) {

        Block nextBlock;
        if (up && winding > 0) {
            if (block.isLeftmostInSCC(block.nodes[0]))
                return;
            nextBlock = block.getLeftNeighborBlocks().getFirst();
        } else if (up && winding < 0) {
            if (block.isRightmostInSCC(block.nodes[0]))
                return;
            nextBlock = block.getLeftNeighborBlocks().getFirst();

        } else if (!up && winding > 0) {
            if (block.isRightmostInSCC(block.nodes[block.nodes.length - 1]))
                return;
            nextBlock = block.getLeftNeighborBlocks().getLast();
        } else { // !up && winding < 0
            if (block.isLeftmostInSCC(block.nodes[block.nodes.length - 1]))
                return;
            nextBlock = block.getLeftNeighborBlocks().getLast();

        }

        // mark all edges that are above the just-traveled edge
        Iterator<Block> it;
        if (up) {
            it = nextBlock.getRightNeighborBlocks().iterator();
        } else {
            it = nextBlock.getRightNeighborBlocks().descendingIterator();
        }
        while (it.hasNext()) {
            Block rightNeighbor = it.next();
            if (rightNeighbor == block) {
                break;
            }
            markedEdges.add(new BlockgraphEdge(nextBlock, rightNeighbor));
        }

        // continue the wedge cut
        recursiveCut(nextBlock, up);
    }

    /**
     * Returns length of the smallest (i.e. closest to negative infinity) of the
     * removed edges.
     */
    double getSmallestRemovedEdge() {
        /*
         * if (traversedLevels == null) throw new IllegalStateException(
         * "Call the method \"cut\" first!");
         */
        double result = Double.POSITIVE_INFINITY;
        for (BlockgraphEdge re : markedEdges) {
            result = Math.min(result, re.right.x - re.left.x);
        }
        return result;
    }

    /**
     * Returns the number of levels that have been traversed while performing
     * the wedge cut. This number will (obviously?) be the same as the number of
     * levels of the whole graph. The important information delivered by this
     * function resides in the sign of the returned number. It indicates whether
     * all the cycles go "upwards" (negative sign) or "downwards" (positive
     * sign).
     */
    int getTraversedLevels() {
        if (traversedLevels == null)
            throw new IllegalStateException("Call the method \"cut\" first!");

        return traversedLevels;
    }

    /**
     * @param blocks
     */
    public int getWinding(Set<Block> blocks) {
        for (Block b : blocks) {
            if (b.isRightmostInSCC(b.nodes[0]))
                return -b.layers.length;
        }
        return blocks.iterator().next().layers.length;
    }
}
