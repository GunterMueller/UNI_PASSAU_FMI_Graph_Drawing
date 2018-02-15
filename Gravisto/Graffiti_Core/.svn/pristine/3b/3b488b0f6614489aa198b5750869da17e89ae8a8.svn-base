package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.DebugToolkit.getNodeLabel;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.isDummy;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.isMarked;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.xpos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

class VerticalAlignment {

    /** The logger */
    private static final Logger logger = Logger
            .getLogger(VerticalAlignment.class.getName());

    Set<Block> execute(SugiyamaData data, Node[][] layers,
            boolean splitLongBlocks, boolean balanced) {
        Median median = findMedians(layers);
        if (balanced) {
            calculateAlignBalanced(layers, median);
        } else {
            calculateAlign(layers, median);
        }
        Set<Node> noRoots = findNoRoots();
        calculateRoot(layers, noRoots);
        breakCyclicBlocks(layers);
        if (splitLongBlocks) {
            splitLongBlocks(layers);
        }
        Set<Block> blocks = buildBlockGraph(layers);
        return blocks;
    }

    /**
     * points to the root of each block (-> vertical alignment)
     */
    private Map<Node, Node> root;
    /**
     * links the nodes within a block (-> vertical alignment)
     */
    private Map<Node, Node> align;

    /**
     * Computes the left and right median for each node. We need to precompute
     * the medians in order to retain linear runtime for the following
     * calculation steps.
     */
    static private Median findMedians(Node[][] layers) {
        // counts for each node how much neighbors have already been visited
        HashMap<Node, Integer> count = new HashMap<Node, Integer>();

        // init each node's neighbor-counter to 0
        for (Node[] layer : layers) {
            for (Node node : layer) {
                count.put(node, 0);
            }
        }

        // these Maps store each node's left and right median
        Median median = new Median();

        // for all layers
        for (int l = 0; l < layers.length; l++) {
            // for all nodes of a layer
            for (int n = 0; n < layers[l].length; n++) {
                Node maybeMedian = layers[l][n];

                // for all lower neighbors of a node
                Collection<Node> lowerNeighbors = maybeMedian.getOutNeighbors();
                for (Node lowerNode : lowerNeighbors) {
                    int pos = count.get(lowerNode) + 1;
                    double medianPos = lowerNode.getInDegree() / 2.0 + 0.5;
                    if (pos == Math.round(medianPos - 0.25)) {
                        median.left.put(lowerNode, maybeMedian);
                    }
                    if (pos == Math.round(medianPos + 0.25)) {
                        median.right.put(lowerNode, maybeMedian);
                    }
                    count.put(lowerNode, pos);
                }
            }
        }

        return median;
    }

    /**
     * Computes the "root" field of each node.
     * 
     * @param noRoots
     *            a Set with all nodes that are *NO* root
     */
    private void calculateRoot(Node[][] layers, Set<Node> noRoots) {

        root = new HashMap<Node, Node>();

        // find all roots by inverting the noRoots set
        Set<Node> roots = new HashSet<Node>();
        for (Node[] layer : layers) {
            for (Node node : layer)
                if (!noRoots.contains(node)) {
                    roots.add(node);
                }
        }

        // for all roots
        for (Node currentRoot : roots) {
            // the root points to itself
            root.put(currentRoot, currentRoot);

            // iterate over the current block
            // (the last node's align will point to itself)
            Node node = currentRoot;
            while (align.get(node) != node) {
                node = align.get(node);
                // each node of the block points to the block's root
                root.put(node, currentRoot);
            }
        }
    }

    /**
     * align(lastNode) := lastNode root(all nodes reachable from rootNode) :=
     * rootNode
     */
    private void cutAlignLinkAndSetRoots(Node rootNode, Node lastNode) {
        int segments = 0;

        // cut the align link to mark the end of the block
        align.put(lastNode, lastNode);

        // set the root values of the nodes that are reachable from rootNode
        Node node = rootNode;
        do {
            root.put(node, rootNode);

            // break if this is the last node
            if (align.get(node) == node) {
                break;
            }
            // otherwise go to next node
            node = align.get(node);
            segments++;
        } while (true);

        // die here if lastNode is not reachable from rootNode
        if (node != lastNode)
            throw new IllegalArgumentException();
    }

    /**
     * This method finds cyclic blocks and breaks them at an arbitrary position
     * (current implementation: above the first non-dummy node).<br>
     * Note: The broken cyclic block will still be layouted as a circle because
     * only one segment is removed. It is only necessary so that every block has
     * a root node.
     */
    private void breakCyclicBlocks(Node[][] layers) {
        // iterate over the first layer - a cycle must pass through this layer
        for (Node layerZeroNode : layers[0])
            // if it's a cycle then the root value is not set
            if (!root.containsKey(layerZeroNode)) {
                logger.log(Level.FINE, "Detected cycle in layer 0 at node "
                        + getNodeLabel(layerZeroNode) + ":");

                // find the next non-dummy node
                Node node = layerZeroNode;
                while (isDummy(node)) {
                    node = align.get(node);
                }

                // make this the root node
                Node newRoot = align.get(node);

                logger.log(Level.FINE, "New root: " + getNodeLabel(newRoot));

                cutAlignLinkAndSetRoots(newRoot, node);
            }
    }

    /**
     * This method splits up long blocks. A block is too long and needs to be
     * split if it contains more nodes than there are layers. The class iterates
     * over each block and splits the block at the last possible position(s).
     */
    private void splitLongBlocks(Node[][] layers) {

        // for each block
        for (Node blockRoot : layers[0][0].getGraph().getNodes())
            if (root.get(blockRoot) == blockRoot) {
                // isolated nodes can be ignored
                if (align.get(blockRoot) == blockRoot) {
                    continue;
                }

                Node lastRoot = blockRoot;
                int segmentsSinceLastRoot = 0;

                Node splitMark = null;
                int segmentsSinceSplitMark = 1;

                Node iterator = blockRoot;

                // iterate over the block
                while (align.get(iterator) != iterator) {
                    // if the current segment is no inner segment then the
                    // block can be split here -> mark this segment
                    if (!isDummy(iterator) || !isDummy(align.get(iterator))) {
                        splitMark = iterator;
                        segmentsSinceSplitMark = 0;
                    }

                    // the layering phase should prevent this:
                    if (segmentsSinceSplitMark >= layers.length)
                        throw new IllegalStateException("Found edge that "
                                + "loops more than one time around the graph!");

                    // save this value because a split will overwrite it
                    Node nextIteratorValue = align.get(iterator);

                    // see if we need to split here
                    if (segmentsSinceLastRoot == layers.length - 1) { // split
                                                                      // below
                                                                      // splitMark:

                        // we should have found at least one non-inner segment
                        if (splitMark == null)
                            throw new IllegalStateException("Could not split "
                                    + "block - found no non-inner segment!");

                        // System.out.println("split at node " + splitMark);

                        Node afterSplitMark = align.get(splitMark);
                        cutAlignLinkAndSetRoots(lastRoot, splitMark);

                        // set the new root to the next node after splitMark
                        lastRoot = afterSplitMark;
                        root.put(lastRoot, lastRoot);
                        segmentsSinceLastRoot = segmentsSinceSplitMark - 1;
                    }

                    // investigate the next segment
                    iterator = nextIteratorValue;
                    segmentsSinceLastRoot++;
                    segmentsSinceSplitMark++;
                }
                // iterator now points to the last node of the block

                // fix the last block (from lastRoot to iterator)
                cutAlignLinkAndSetRoots(lastRoot, iterator);
            }
    }

    /**
     * Finds all nodes that are no roots by looking at the align field.
     */
    private Set<Node> findNoRoots() {
        Set<Node> noRoots = new HashSet<Node>();

        // for each alignment that doesn't point to itself
        for (Map.Entry<Node, Node> entry : align.entrySet())
            if (entry.getKey() != entry.getValue()) {
                noRoots.add(entry.getValue());
            }
        return noRoots;
    }

    /**
     * Defines the align value for each node.
     */
    private void calculateAlignBalanced(Node[][] layers, Median median) {
        Graph graph = layers[0][0].getGraph();

        // initialize the align attribute of each node to point to itself
        align = new HashMap<Node, Node>();
        for (Node[] layer : layers) {
            for (Node node : layer) {
                align.put(node, node);
            }
        }

        // for all layers (including first and last)
        for (int layer = 0; layer < layers.length; layer++) {

            // minimal x of the upper node of a newly added edge
            int maxX = -1;
            int minX = Integer.MAX_VALUE;

            int startX = 0;
            if (layers[layer].length % 2 == 1) {
                startX = (layers[layer].length - 1) / 2;
            } else {
                if (Math.random() > 0.5) {
                    startX = (layers[layer].length) / 2;
                } else {
                    startX = (layers[layer].length) / 2 - 1;
                }
            }

            List<Node> m = new LinkedList<Node>();
            m.add(layers[layer][startX]);

            // for all nodes (from left to right)
            for (Node node : m) {

                // get all medians (if any...)
                LinkedList<Node> medians = new LinkedList<Node>();
                if (median.left.get(node) != null) {
                    medians.add(median.left.get(node));
                }
                if (median.right.get(node) != null) {
                    medians.add(median.right.get(node));
                }
                if (medians.size() == 2
                        && medians.getFirst() == medians.getLast()) {
                    medians.removeLast();
                }

                if (!medians.isEmpty() && Math.random() > 0.5) {
                    Node n = medians.removeFirst();
                    medians.addLast(n);
                }

                // for all medians (from left to right)
                for (Node medianNode : medians) {

                    // get the edge between this node and its median
                    Edge edge = graph.getEdges(medianNode, node).iterator()
                            .next();

                    // the edge mustn't be marked
                    if (isMarked(edge)) {
                        continue;
                    }

                    // align the median to the node
                    align.put(medianNode, node);
                    // root.put(node, root(median));
                    // align.put(node, root(node));
                    minX = xpos(medianNode);
                    maxX = xpos(medianNode);

                    // node has been aligned -> leave the loop
                    break;
                }
            }

            // for all nodes (from left to right)
            for (int x = startX + 1; x < layers[layer].length; x++) {
                Node node = layers[layer][x];
                // get all medians (if any...)
                LinkedList<Node> medians = new LinkedList<Node>();
                if (median.left.get(node) != null) {
                    medians.add(median.left.get(node));
                }
                if (median.right.get(node) != null) {
                    medians.add(median.right.get(node));
                }
                if (medians.size() == 2
                        && medians.getFirst() == medians.getLast()) {
                    medians.removeLast();
                }

                // for all medians (from left to right)
                for (Node medianNode : medians) {

                    // test for an edge crossing
                    if (maxX >= xpos(medianNode)) {
                        continue;
                    }

                    // get the edge between this node and its median
                    Edge edge = graph.getEdges(medianNode, node).iterator()
                            .next();

                    // the edge mustn't be marked
                    if (isMarked(edge)) {
                        continue;
                    }

                    // align the median to the node
                    align.put(medianNode, node);
                    // root.put(node, root(median));
                    // align.put(node, root(node));
                    maxX = xpos(medianNode);
                    if (minX == Integer.MAX_VALUE) {
                        minX = xpos(medianNode);
                    }

                    // node has been aligned -> leave the loop
                    break;
                }
            }

            // for all nodes (from left to right)
            for (int x = startX - 1; x >= 0; x--) {
                Node node = layers[layer][x];
                // get all medians (if any...)
                LinkedList<Node> medians = new LinkedList<Node>();
                if (median.left.get(node) != null) {
                    medians.add(median.left.get(node));
                }
                if (median.right.get(node) != null) {
                    medians.add(median.right.get(node));
                }
                if (medians.size() == 2
                        && medians.getFirst() == medians.getLast()) {
                    medians.removeLast();
                }

                if (!medians.isEmpty()) {
                    Node n = medians.removeFirst();
                    medians.addLast(n);
                }

                // for all medians (from left to right)
                for (Node medianNode : medians) {

                    // test for an edge crossing
                    if (minX <= xpos(medianNode)) {
                        continue;
                    }

                    // get the edge between this node and its median
                    Edge edge = graph.getEdges(medianNode, node).iterator()
                            .next();

                    // the edge mustn't be marked
                    if (isMarked(edge)) {
                        continue;
                    }

                    // align the median to the node
                    align.put(medianNode, node);
                    // root.put(node, root(median));
                    // align.put(node, root(node));
                    minX = xpos(medianNode);
                    if (maxX == -1) {
                        maxX = xpos(medianNode);
                    }

                    // node has been aligned -> leave the loop
                    break;
                }
            }
        }
    }

    /**
     * Defines the align value for each node.
     */
    private void calculateAlign(Node[][] layers, Median median) {
        Graph graph = layers[0][0].getGraph();

        // initialize the align attribute of each node to point to itself
        align = new HashMap<Node, Node>();
        for (Node[] layer : layers) {
            for (Node node : layer) {
                align.put(node, node);
            }
        }

        // for all layers (including first and last)
        for (int layer = 0; layer < layers.length; layer++) {

            // minimal x of the upper node of a newly added edge
            int minX = -1;

            // for all nodes (from left to right)
            for (Node node : layers[layer]) {

                // get all medians (if any...)
                LinkedList<Node> medians = new LinkedList<Node>();
                if (median.left.get(node) != null) {
                    medians.add(median.left.get(node));
                }
                if (median.right.get(node) != null) {
                    medians.add(median.right.get(node));
                }
                if (medians.size() == 2
                        && medians.getFirst() == medians.getLast()) {
                    medians.removeLast();
                }

                // for all medians (from left to right)
                for (Node medianNode : medians) {

                    // test for an edge crossing
                    if (minX >= xpos(medianNode)) {
                        continue;
                    }

                    // get the edge between this node and its median
                    Edge edge = graph.getEdges(medianNode, node).iterator()
                            .next();

                    // the edge mustn't be marked
                    if (isMarked(edge)) {
                        continue;
                    }

                    // align the median to the node
                    align.put(medianNode, node);
                    // root.put(node, root(median));
                    // align.put(node, root(node));
                    minX = xpos(medianNode);

                    // node has been aligned -> leave the loop
                    break;
                }
            }
        }
    }

    private Set<Block> buildBlockGraph(Node[][] layers) {
        // assemble the nodes into blocks
        Set<Block> blocks = compileBlocks(layers);

        // let the blocks find their neighbors
        Block.findNeighbors(blocks, layers);

        // determine the length of the edges in the blockgraph
        Block.findBlockDistances(blocks, layers);

        return blocks;
    }

    private Set<Block> compileBlocks(Node[][] layers) {
        Set<Block> blocks = new HashSet<Block>();
        int numberOfNodes = layers[0][0].getGraph().getNumberOfNodes();
        Block.forNode = new HashMap<Node, Block>(numberOfNodes);

        // for all root nodes...
        for (int level = 0; level < layers.length; level++) {
            for (Node rootNode : layers[level])
                if (root(rootNode).equals(rootNode)) {
                    // ...create a block
                    Block block = new Block(level, layers);

                    // iterate over the align links and fill a list
                    LinkedList<Node> nodelist = new LinkedList<Node>();

                    Node node = rootNode;
                    nodelist.add(node);
                    Block.forNode.put(node, block);
                    while (align.get(node) != node) {
                        node = align.get(node);
                        nodelist.add(node);
                        Block.forNode.put(node, block);
                    }

                    // copy the node list into the block
                    block.nodes = nodelist.toArray(new Node[0]);
                    block.buildIndexMap();
                    blocks.add(block);
                }
        }

        return blocks;
    }

    /**
     * Convenience function for better readability.
     */
    private Node root(Node node) {
        return root.get(node);
    }
}
