package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

/**
 * Represents one edge in a blockgraph.
 */
class BlockgraphEdge {
    final Block left, right;
    double length = Double.POSITIVE_INFINITY;

    public BlockgraphEdge(Block left, Block right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object other) {
        BlockgraphEdge otherEdge = (BlockgraphEdge) other;
        return left == otherEdge.left && right == otherEdge.right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }
}
