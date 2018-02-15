package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.graffiti.graph.Node;

class Block implements TopoSortable {
    /**
     * stores the backlinks from Node to Block
     */
    public static Map<Node, Block> forNode = null;

    /**
     * stores each node's index
     */
    private Map<Node, Integer> indexMap = null;

    /**
     * Reference to the layering information
     */
    public final Node[][] layers;

    Block(int level, Node[][] layers) {
        this.level = level;
        this.layers = layers;
    }

    /**
     * all nodes of this block (ordered top to bottom)
     */
    Node[] nodes = null;

    /**
     * Links each block to its containing SCC
     */
    SCC parentSCC = null;

    /**
     * The x coordinate of this block's root node
     */
    double x = 0;

    /**
     * The inclination of this block. The x coordinates of this block's nodes
     * are defined as follows:<br>
     * <i>nodes[i].x = this.x + this.inclination * i</i>
     */
    double inclination = 0;

    /**
     * the level of the root node
     */
    final int level;

    /**
     * this block's neighbors to the left
     */
    LinkedHashMap<BlockgraphEdge, BlockgraphEdge> leftNeighbors;

    /**
     * this block's neighbors to the right
     */
    LinkedHashMap<BlockgraphEdge, BlockgraphEdge> rightNeighbors;

    /**
     * Returns the x-coordinate of the given node.<br>
     * If the level is not within this block then the result is NaN.<br>
     * If <code>this.x</code> is undefined then the result is NaN.
     */
    public double getXCoordOf(Node node) {
        if (!indexMap.containsKey(node))
            throw new IllegalStateException("block does not span over level "
                    + level + "!" + " --- shouldn't happen -> Bug!");

        int index = indexMap.get(node);

        return x + inclination * index;
    }

    /**
     * Finds the minimum distance between this block and another one. It is
     * assumed that the other block is on the left side of this block. If this
     * is not true then the "distance" will be negative.
     * <p>
     * Finishes in O(1).
     */
    double getMinDistanceToLeftNeighbor(Block leftNeighbor) {
        return getMinDistance(leftNeighbor, this);
    }

    /**
     * Finds the minimum distance between this block and another one. It is
     * assumed that the other block is on the right side of this block. If this
     * is not true then the "distance" will be negative.
     * <p>
     * Finishes in O(1).
     */
    double getMinDistanceToRightNeighbor(Block rightNeighbor) {
        return getMinDistance(this, rightNeighbor);
    }

    private static double getMinDistance(Block left, Block right) {
        BlockgraphEdge edge = new BlockgraphEdge(left, right);

        if (!left.rightNeighbors.containsKey(edge)
                || !right.leftNeighbors.containsKey(edge))
            throw new IllegalStateException("requested illegal edge");

        edge = left.rightNeighbors.get(edge);
        return edge.length;
    }

    @Override
    public String toString() {
        return "Block (" + nodes.length + " nodes, x=" + x + ", L"
                + leftNeighbors.size() + " R" + rightNeighbors.size()
                + ", root: " + DebugToolkit.getNodeLabel(nodes[0]) + ")";
    }

    /**
     * Sets the <code>length</code> field of all BlockgraphEdges in the graph.
     */
    static void findBlockDistances(Set<Block> blocks, Node[][] layers) {
        // invalidate all edge lengths
        for (Block block : blocks) {
            for (BlockgraphEdge edge : block.leftNeighbors.keySet()) {
                edge.length = Double.POSITIVE_INFINITY;
            }
            for (BlockgraphEdge edge : block.rightNeighbors.keySet()) {
                edge.length = Double.POSITIVE_INFINITY;
            }
        }

        // sweep over the layers and fill the information into the edge lengths
        for (Node[] layer : layers) {
            for (int index = 1; index < layer.length; index++) {
                // get the pair of nodes
                Node leftNode = layer[index - 1];
                Node rightNode = layer[index];

                // get the nodes' blocks
                Block leftBlock = Block.forNode.get(leftNode);
                Block rightBlock = Block.forNode.get(rightNode);

                // get the distance between the blocks
                double distance = rightBlock.getXCoordOf(rightNode)
                        - leftBlock.getXCoordOf(leftNode);

                // store the distance
                BlockgraphEdge edge = new BlockgraphEdge(leftBlock, rightBlock);
                if (leftBlock.rightNeighbors.containsKey(edge)) {
                    edge = leftBlock.rightNeighbors.get(edge);
                    edge.length = min(edge.length, distance);
                }
                if (rightBlock.leftNeighbors.containsKey(edge)) {
                    edge = rightBlock.leftNeighbors.get(edge);
                    edge.length = min(edge.length, distance);
                }
            }
        }

        // check whether all edges have valid lengths
        for (Block block : blocks) {
            for (BlockgraphEdge edge : block.leftNeighbors.keySet())
                if (Double.isInfinite(edge.length))
                    throw new IllegalStateException("length = inf -> Bug!");
            for (BlockgraphEdge edge : block.rightNeighbors.keySet())
                if (Double.isInfinite(edge.length))
                    throw new IllegalStateException("length = inf -> Bug!");
        }
    }

    /**
     * Create the blockgraph's adjacency lists.
     */
    static void findNeighbors(Set<Block> blocks, Node[][] layers) {
        // let the blocks find their neighbors -
        // runs in O(graph.getNodes().size())
        for (Block block : blocks) {
            block.findNeighbors(layers);
        }
    }

    /**
     * Fills the lists of left and right neighbors. The neighbors are sorted:
     * They are in the order in which this block's nodes touch them.<br>
     * Runs in O(this.nodes.length).
     */
    private void findNeighbors(Node[][] layers) {
        leftNeighbors = new LinkedHashMap<BlockgraphEdge, BlockgraphEdge>();
        rightNeighbors = new LinkedHashMap<BlockgraphEdge, BlockgraphEdge>();

        // for all nodes of this block
        int currentLevel = this.level;
        for (Node node : nodes) {
            int xpos = Toolkit.xpos(node);

            // check if the node has a left neighbor...
            if (0 < xpos) {
                // ...yes -> add to list of left neighbors
                Node leftNode = layers[currentLevel][xpos - 1];
                BlockgraphEdge edge = new BlockgraphEdge(forNode.get(leftNode),
                        this);
                leftNeighbors.put(edge, edge);
            }

            // check if the node has a right neighbor...
            if (xpos + 1 < layers[currentLevel].length) {
                // ...yes -> add to list of right neighbors
                Node rightNode = layers[currentLevel][xpos + 1];
                BlockgraphEdge edge = new BlockgraphEdge(this, forNode
                        .get(rightNode));
                rightNeighbors.put(edge, edge);
            }

            currentLevel = (currentLevel + 1) % layers.length;
        }

        // make sure that Key==Value for all entries of all maps
        for (BlockgraphEdge edge : leftNeighbors.keySet()) {
            leftNeighbors.put(edge, edge);
        }
        for (BlockgraphEdge edge : rightNeighbors.keySet()) {
            rightNeighbors.put(edge, edge);
        }
    }

    /**
     * Returns this block's neighbors to the right.
     */
    public Collection<TopoSortable> getTopoSortSuccessors() {
        ArrayList<TopoSortable> result = new ArrayList<TopoSortable>(
                rightNeighbors.size());
        for (BlockgraphEdge edge : rightNeighbors.keySet()) {
            result.add(edge.right);
        }
        return result;
    }

    LinkedList<Block> getLeftNeighborBlocks() {
        LinkedList<Block> result = new LinkedList<Block>();
        for (BlockgraphEdge edge : leftNeighbors.keySet()) {
            result.add(edge.left);
        }
        return result;
    }

    LinkedList<Block> getRightNeighborBlocks() {
        LinkedList<Block> result = new LinkedList<Block>();
        for (BlockgraphEdge edge : rightNeighbors.keySet()) {
            result.add(edge.right);
        }
        return result;
    }

    private double getMinDistanceToTheLeft() {
        double dist = Double.POSITIVE_INFINITY;
        // for all of our left neighbors
        for (BlockgraphEdge edge : leftNeighbors.keySet()) {
            // consider its distance
            dist = min(dist, edge.length);
        }
        return dist;
    }

    private double getMinDistanceToTheRight() {
        double dist = Double.POSITIVE_INFINITY;
        // for all of our right neighbors
        for (BlockgraphEdge edge : rightNeighbors.keySet()) {
            // consider its distance
            dist = min(dist, edge.length);
        }
        return dist;
    }

    void moveLeft() {
        // no predecessors? -> don't move
        if (leftNeighbors.size() == 0)
            return;

        // move
        move(-getMinDistanceToTheLeft() + 1);
    }

    void moveRight() {
        // no successors -> don't move
        if (rightNeighbors.size() == 0)
            return;

        // move
        move(getMinDistanceToTheRight() - 1);
    }

    /**
     * Moves the block <code>delta</code> to the right. Runs in
     * O(this.nodes.length).
     */
    void move(double delta) {
        // change the block's position
        x += delta;

        // update the distances to the neighbors
        for (BlockgraphEdge edge : leftNeighbors.keySet()) {
            if (edge.left == edge.right) {
                continue;
            }

            edge.length += delta;
            edge.left.rightNeighbors.get(edge).length += delta;
        }
        for (BlockgraphEdge edge : rightNeighbors.keySet()) {
            if (edge.left == edge.right) {
                continue;
            }

            edge.length -= delta;
            edge.right.leftNeighbors.get(edge).length -= delta;
        }
    }

    /**
     * Returns the level difference one has to overcome if one travels from this
     * block's root to target's root via direct node adjacencies.
     */
    int getHeightDiff(Block target) {
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            int level = (this.level + i) % layers.length;
            int xpos = Toolkit.xpos(node);
            if (xpos > 0) {
                Node neighbor = layers[level][xpos - 1];
                if (Block.forNode.get(neighbor) == target) {
                    for (int j = 0; j < target.nodes.length; j++)
                        if (neighbor == target.nodes[j])
                            return i - j;
                }
            }
        }
        throw new IllegalArgumentException("Shouldn't happen - Bug!");
    }

    void buildIndexMap() {
        indexMap = new HashMap<Node, Integer>();
        for (int i = 0; i < nodes.length; i++) {
            indexMap.put(nodes[i], i);
        }
    }

    public boolean isRightmostInSCC(Node n) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == n) {
                int level = (this.level + i) % layers.length;
                int xpos = Toolkit.xpos(n);
                Node neighbor = null;
                try {
                    neighbor = layers[level][xpos + 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    return true;
                }
                return (forNode.get(neighbor).parentSCC != this.parentSCC);
            }
        }
        throw new IllegalStateException();
    }

    public boolean isLeftmostInSCC(Node n) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == n) {
                int level = (this.level + i) % layers.length;
                int xpos = Toolkit.xpos(n);
                Node neighbor = null;
                try {
                    neighbor = layers[level][xpos - 1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    return true;
                }
                return (forNode.get(neighbor).parentSCC != this.parentSCC);
            }
        }
        throw new IllegalStateException();
    }
}
