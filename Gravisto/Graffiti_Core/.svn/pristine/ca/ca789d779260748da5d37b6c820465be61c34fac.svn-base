// =============================================================================
//
//   SocialBrandesKoepf.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SocialBrandesKoepf.java 2276 2008-03-11 16:45:02Z brunner $

package org.graffiti.plugins.algorithms.sugiyama.layout;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This is an implementation of the Brandes/Koepf-Algorithm for the
 * Sugiyama-Framework.<br>
 * It additionally supports cyclic graphs.
 * <p>
 * For details see Alg. 1, 2, and 3 of the following publication:<br>
 * 
 * U. Brandes, B. K&ouml;pf, Fast and Simple Horizontal Coordinate Assignment.
 * In: P. Mutzel, M. J&uuml;nger, and S. Leipert (Eds.): GD 2001, LNCS 2265, pp.
 * 31-44, 2002, Springer 2002.
 * 
 * @author Raymund F&uuml;l&ouml;p
 */
public class CyclicBrandesKoepf extends AbstractAlgorithm implements
        LayoutAlgorithm {
    /** The name of the algorithm */
    private final String ALGORITHM_NAME = "Brandes/Koepf for cyclic graphs (Blob)";
    /** The <code>SugiyamaData</code>-Bean */
    private SugiyamaData data;

    /** Distance from the center (0/0) to the innermost node */
    private int RADIUS_OFFSET = 50;
    /** Minimal distance between two nodes on a layer */
    private int NODE_DELTA = 50;
    private int DIRECTION = 4;

    /** The dummy-nodes in the graph */
    private Set<Node> dummies;
    /** stores marked segments */
    private Set<Edge> marked;
    /**
     * all nodes, arranged within layers meaning of the indices: layers[layerNr,
     * nodeNr]
     */
    private Node[][] layers;
    /** The computed relative coordinates from Brandes/Koepf */
    // private Map<Node, Integer> coordinates;
    /** The position of each node within its layer (starts with 0) */
    private Map<Node, Integer> pos;
    /** The layer of each node (starts with 0) */
    private Map<Node, Integer> level;
    /** The x-coordinates for each node */
    private Map<Node, Double> x;
    /** The x-coordinates for each node for all 4 directions */
    @SuppressWarnings("unchecked")
    private Map<Node, Double>[] xs = new Map[4];

    /** the current direction */
    private int direction = LEFT_UP;

    /**
     * constants for direction
     */
    private static final int LEFT_UP = 0;
    private static final int LEFT_DOWN = 1;
    private static final int RIGHT_UP = 2;
    private static final int RIGHT_DOWN = 3;

    public boolean supportsArbitraryXPos() {
        return false;
    }

    private void someDebug() {
        if (direction == DIRECTION) {
            paintRootNodes();
            paintAlignedEdges();
        }
        // System.out.print("direction=" + direction + ": ");
        // for (Node node : layers[4]) {
        // if (isDummy(node)) continue;
        // String label = node.getString("label.label");
        // System.out.print(label + ":" + pos.get(node) + " ");
        // }
        // for (Node[] layer : layers) {
        // System.out.print(layer.length + " ");
        // }
        // for (Node node : graph.getNodes()) {
        // if (isDummy(node)) continue;
        // String label = node.getString("label.label");
        // System.out.print(label + ":" + pos.get(node) + " ");
        // }
        // System.out.println();
    }

    /**
     * This method executes the algorithm - Initialize the data-structures, call
     * the Brandes/Koepf-Implementation and update the coordinates of the nodes
     * according to the relative coordinates returned from the
     * Brandes/Koepf-Implemenatation and user-defined offsets
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        copyFromSugiyamaData();
        markType1Conflicts();
        // paintMarkedEdges();

        direction = LEFT_UP;
        verticalAlignment();
        // someDebug();
        // if (1 == 1) throw new RuntimeException("Crash here...");
        horizontalCompaction();
        xs[direction] = x;
        someDebug();
        x = null;

        direction = LEFT_DOWN;
        flipVertical();
        verticalAlignment();
        horizontalCompaction();
        flipVertical();
        xs[direction] = x;
        someDebug();
        x = null;

        direction = RIGHT_UP;
        flipHorizontal();
        verticalAlignment();
        horizontalCompaction();
        flipHorizontal();
        xs[direction] = x;
        someDebug();
        x = null;

        direction = RIGHT_DOWN;
        flipHorizontal();
        flipVertical();
        verticalAlignment();
        horizontalCompaction();
        flipVertical();
        flipHorizontal();
        xs[direction] = x;
        someDebug();
        x = null;

        // undo debug markings
        if (DIRECTION == 4) {
            balance();
        }
        updateGraph();
        graph.getListenerManager().transactionFinished(this);
        // if (1 == 1) throw new RuntimeException("Crash here...");
    }

    /**
     * Returns the index of the layer below layer <code>layer</code>. The next
     * layer below the last layer is the first layer.
     * 
     * @param layer
     * @return the index of the layer below layer <code>layer</code>
     */
    private int nextLayer(int layer) {
        return (layer + 1) % layers.length;
    }

    /**
     * Convenience function for better readability.
     */
    private boolean isDummy(Node node) {
        return dummies.contains(node);
    }

    /**
     * Convenience function for better readability.
     */
    private int pos(Node node) {
        return pos.get(node);
    }

    /**
     * Convenience function for better readability.
     */
    private int level(Node node) {
        return level.get(node);
    }

    /**
     * Marks type 1 conflicts. See Algorithm 1 of publication.
     */
    private void markType1Conflicts() {

        marked = new HashSet<Edge>();

        // for each pair of layers
        for (int upperLayer = 0; upperLayer < layers.length - 1; upperLayer++) {
            int lowerLayer = nextLayer(upperLayer);

            // - inspect all non-inner segments between two inner segments
            // - start from the left

            // left inner segment
            int innerSegmentUL = -1;
            int innerSegmentLL = -1;
            // right inner segment
            int innerSegmentUR = 0;
            int innerSegmentLR = 0;

            do {

                // advance to next inner segment (or end of lower layer)
                do {
                    // next node in lower layer
                    innerSegmentLR++;

                    // end of layer?
                    if (innerSegmentLR >= layers[lowerLayer].length) {
                        // move the right inner segment fully to the right
                        innerSegmentUR = layers[upperLayer].length;
                        // innerSegmentUR = layers[upperLayer].length - 1;
                        // innerSegmentLR--;
                        break;
                    }

                    // found a new dummy node?
                    Node maybeDummy = layers[lowerLayer][innerSegmentLR];
                    if (!isDummy(maybeDummy)) {
                        continue;
                    }

                    // it's upper neighbor must be a dummy, too
                    Node n = maybeDummy.getInNeighbors().iterator().next();
                    if (!isDummy(n)) {
                        continue;
                    }

                    // we found a new inner segment

                    assert n.getInDegree() == 1;
                    assert n.getOutDegree() == 1;
                    assert maybeDummy.getInDegree() == 1;
                    assert maybeDummy.getOutDegree() == 1;

                    // get the pos of the upper neighbor
                    innerSegmentUR = pos(n);
                    break;

                } while (true);

                // inspect all edges whose target nodes lie between the
                // currently selected inner segments

                // for all nodes between innerSegmentLL and innerSegmentLR
                for (int n = innerSegmentLL + 1; n < innerSegmentLR; n++) {
                    Node inspectedNodeL = layers[lowerLayer][n];

                    // for all in-edges of the current node
                    for (Edge inspectedEdge : inspectedNodeL
                            .getDirectedInEdges()) {
                        int posU = pos(inspectedEdge.getSource());

                        // type 1 conflict?
                        if (posU < innerSegmentUL || innerSegmentUR < posU) {
                            // handle the type 1 conflict by marking the
                            // non-inner edge
                            marked.add(inspectedEdge);
                        }
                    }
                }

                // move the left inner segment to the right inner segment
                // (the edges in between have just been tested)
                innerSegmentUL = innerSegmentUR;
                innerSegmentLL = innerSegmentLR;

                // stop at the end of the lower layer
            } while (!(innerSegmentLR >= layers[lowerLayer].length - 1));

            // finished with this pair of layers
        }

        // System.out.println("BK: Found " + marked.size() +
        // " type 1 conflicts.");
    }

    /**
     * Saves the original colors of edges when they are colored during an
     * animation.
     */
    private Map<GraphElement, Color> originalColors = new HashMap<GraphElement, Color>();

    // /**
    // * Paints all "marked" edges red. Saves the original colors.
    // */
    // private void paintMarkedEdges()
    // {
    // ColorAttribute edgeColor;
    // for (Edge edge : marked)
    // {
    // edgeColor = (ColorAttribute)edge.getAttribute("graphics.framecolor");
    // if (!originalColors.containsKey(edge))
    // originalColors.put(edge, edgeColor.getColor());
    // edgeColor.setColor(Color.RED);
    // }
    // }
    //
    // /**
    // * Restores the original color of edges and nodes that have been colored.
    // */
    // private void unColor()
    // {
    // for (Map.Entry<GraphElement, Color> entry : originalColors.entrySet())
    // {
    // ((ColorAttribute)entry.getKey().getAttribute("graphics.framecolor"))
    // .setColor(entry.getValue());
    // }
    // originalColors.clear();
    // }

    /**
     * each node's median(s) are stored here (-> vertical alignment)
     */
    private Map<Node, Node> leftMedian;
    private Map<Node, Node> rightMedian;

    /**
     * Convenience function for better readability.
     */
    private Node leftMedian(Node node) {
        return leftMedian.get(node);
    }

    private Node rightMedian(Node node) {
        return rightMedian.get(node);
    }

    /**
     * Computes the left and right median for each node.
     */
    private void findMedians() {
        // counts for each node how much neighbors have already been visited
        HashMap<Node, Integer> count = new HashMap<Node, Integer>();

        // init each node's neighbor-counter to 0
        for (Node node : graph.getNodes()) {
            count.put(node, 0);
        }

        // these Maps store each node's left and right median
        leftMedian = new HashMap<Node, Node>();
        rightMedian = new HashMap<Node, Node>();

        // for all layers
        for (int l = 0; l < layers.length; l++) {
            // for all nodes of a layer
            for (int n = 0; n < layers[l].length; n++) {
                Node maybeMedian = layers[l][n];

                // for all lower neighbors of a node
                Collection<Node> lowerNeighbors = maybeMedian.getOutNeighbors();
                for (Node lowerNode : lowerNeighbors) {
                    int pos = count.get(lowerNode) + 1;
                    double median = lowerNode.getInDegree() / 2.0 + 0.5;
                    if (pos == Math.round(median - 0.25)) {
                        leftMedian.put(lowerNode, maybeMedian);
                    }
                    if (pos == Math.round(median + 0.25)) {
                        rightMedian.put(lowerNode, maybeMedian);
                    }
                    count.put(lowerNode, pos);
                }
            }
        }
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
     * See Algorithm 2 of publication: aligns edges to left median of upper
     * neighbors
     */
    private void verticalAlignment() {

        // we need to precompute the medians in order to retain linear runtime
        findMedians();

        // fill the align value of each node
        calculateAlign();

        // find all nodes that are not a root
        Set<Node> noRoots = findNoRoots();
        // System.out.println("vA().noRoots.size() == " + noRoots.size());

        // find all roots by inverting the noRoots set
        Set<Node> roots = new HashSet<Node>();
        for (Node node : graph.getNodes())
            if (!noRoots.contains(node)) {
                roots.add(node);
                // System.out.println("vA().roots.size() == " + roots.size());
            }

        root = new HashMap<Node, Node>();

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

        // System.out.println("vA().findNoRoots().size() == " +
        // findNoRoots().size());

        // int rootCount = 0;
        // for (Map.Entry<Node, Node> entry : root.entrySet())
        // if (entry.getKey() == entry.getValue())
        // rootCount++;
        // System.out.println("vA().rootCount == " + rootCount);
        // System.out.println("vA().root.size() == " + root.size());

        // we don't need these two sets any more
        roots = null;
        noRoots = null;

        // find cyclic blocks and break them
        breakCyclicBlocks();

        // find blocks that loop more than one time around and split them
        // splitLongBlocks();

        // checkVerticalAlignment();

        // for (Node node : graph.getNodes()) {
        // String nodelabel;
        // if (isDummy(node))
        // nodelabel = "Dummy (L" + level(node) + ")";
        // else
        // nodelabel = "Node " + node.getString("label.label");
        // System.out.print("align(" + nodelabel);
        // if (isDummy(node))
        // nodelabel = "Dummy (L" + level(align.get(node)) + ")";
        // else
        // nodelabel = "Node " + align.get(node).getString("label.label");
        // System.out.println(") = " + nodelabel);
        // }
        // for (Node node : graph.getNodes()) {
        // String nodelabel;
        // if (isDummy(node))
        // nodelabel = "Dummy (L" + level(node) + ")";
        // else
        // nodelabel = "Node " + node.getString("label.label");
        // System.out.print("root(" + nodelabel);
        // if (isDummy(node))
        // nodelabel = "Dummy (L" + level(root.get(node)) + ")";
        // else
        // nodelabel = "Node " + root.get(node).getString("label.label");
        // System.out.println(") = " + nodelabel);
        // }
    }

    // /**
    // * Performs various tests on the data structures that the vertical
    // alignment
    // * step created.
    // */
    // private void checkVerticalAlignment()
    // {
    // System.out.println("graph.getNumberOfNodes() == " +
    // graph.getNumberOfNodes());
    // System.out.println("root.size() == " + root.size());
    // System.out.println("align.size() == " + align.size());
    //
    // if (root.size() != graph.getNumberOfNodes())
    // System.err.println("root.size() != graph.getNumberOfNodes()");
    // if (align.size() != graph.getNumberOfNodes())
    // System.err.println("align.size() != graph.getNumberOfNodes()");
    //
    // // find all nodes that are not a root
    // Set<Node> noRoots = findNoRoots();
    // System.out.println("noRoots.size() == " + noRoots.size());
    //
    // if (noRoots.size() >= graph.getNumberOfNodes())
    // System.err.println("noRoots.size() >= graph.getNumberOfNodes()");
    //
    // // find all roots by inverting the noRoots set
    // Set<Node> roots = new HashSet<Node>();
    // for (Node node : graph.getNodes())
    // if (!noRoots.contains(node))
    // roots.add(node);
    //
    // System.out.println("roots.size() == " + roots.size());
    //
    // if (roots.size() >= graph.getNumberOfNodes())
    // System.err.println("roots.size() >= graph.getNumberOfNodes()");
    //
    // if (roots.size() + noRoots.size() != graph.getNumberOfNodes())
    // System.err.println("roots.size() + noRoots.size() != graph.getNumberOfNodes()");
    //
    // // each node in roots must be a root
    // for (Node node : roots)
    // {
    // if (!root.containsKey(node))
    // System.err.println("Node " + node +
    // " : roots -> !root.containsKey(...)");
    // if (root.get(node) != node)
    // System.err.println("Node " + node + " : roots -> root.get(...) != ...");
    // }
    //
    // // for each assignment in align
    // for (Map.Entry<Node, Node> entry : align.entrySet())
    // {
    // // points to itself? -> can't say anything
    // if (entry.getKey() == entry.getValue()) continue;
    //
    // int keyLevel = level(entry.getKey());
    // int valueLevel = level(entry.getValue());
    // if (nextLayer(keyLevel) == valueLevel)
    // { // "regular" alignment (-> must not point to a root node)
    // if (root.get(entry.getValue()) == entry.getValue())
    // System.err.println("align(" + entry.getKey() + ") == " + entry.getValue()
    // + " is \"regular\", but root(target) == target");
    // }
    // else // no regular alignment (-> error)
    // {
    // System.err.println("align(" + entry.getKey() + ") == " + entry.getValue()
    // + " is not \"regular\" (keyLevel=" + keyLevel + ",valueLevel=" +
    // valueLevel + ")");
    // }
    // }
    //
    // // for each assignment in root
    // for (Map.Entry<Node, Node> entry : root.entrySet())
    // {
    // if (entry.getKey() == entry.getValue())
    // { // a root
    // if (!roots.contains(entry.getKey()))
    // System.err.println("root(" + entry.getKey() + ") == " + entry.getValue()
    // + " -> !roots.contains(...)");
    // if (noRoots.contains(entry.getKey()))
    // System.err.println("root(" + entry.getKey() + ") == " + entry.getValue()
    // + " -> noRoots.contains(...)");
    // }
    // else
    // { // no root
    // if (roots.contains(entry.getKey()))
    // System.err.println("root(" + entry.getKey() + ") != " + entry.getValue()
    // + " -> roots.contains(...)");
    // if (!noRoots.contains(entry.getKey()))
    // System.err.println("root(" + entry.getKey() + ") != " + entry.getValue()
    // + " -> !noRoots.contains(...)");
    // }
    // }
    //
    // int rootCount = 0;
    // for (Map.Entry<Node, Node> entry : root.entrySet())
    // if (entry.getKey() == entry.getValue())
    // rootCount++;
    // System.out.println("rootCount == " + rootCount);
    // if (rootCount != roots.size())
    // System.err.println("rootCount != roots.size()");
    //
    // // for each block
    // for (Node rootNode : roots)
    // {
    // int segments = 0;
    // Node node = rootNode;
    // while (align.get(node) != node)
    // {
    // node = align.get(node);
    // segments++;
    // }
    // if (segments >= layers.length)
    // System.err.println("Too long block (" + segments +
    // " segments) starting at " + rootNode);
    // }
    // }

    // /**
    // * If the graph's layout is cyclic, then it is necessary to split up
    // * long blocks. A block is too long and needs to be split if it contains
    // * more nodes than there are layers. The block would otherwise intersect
    // * itself.
    // */
    // private void splitLongBlocks()
    // {
    //
    // System.out.println("splitLongBlocks()");
    //
    // // for each block
    // for (Node blockRoot : graph.getNodes())
    // if (root.get(blockRoot) == blockRoot)
    // {
    // // isolated nodes can be ignored
    // if (align.get(blockRoot) == blockRoot) continue;
    //
    // // System.out.println("Now checking block at " +
    // getNodeLabel(blockRoot));
    //
    // Node lastRoot = blockRoot;
    // int segmentsSinceLastRoot = 0;
    //
    // Node splitMark = null;
    // int segmentsSinceSplitMark = 1;
    //
    // Node iterator = blockRoot;
    //
    // // iterate over the block
    // while (align.get(iterator) != iterator)
    // {
    // // if the current segment is no inner segment then the
    // // block can be split here -> mark this segment
    // if (!isDummy(iterator) || !isDummy(align.get(iterator)))
    // {
    // splitMark = iterator;
    // segmentsSinceSplitMark = 0;
    // }
    //
    // // the layering phase should prevent this:
    // if (segmentsSinceSplitMark >= layers.length)
    // throw new IllegalStateException("Found edge that " +
    // "loops more than one time around the graph!");
    //
    // // save this value because a split will overwrite it
    // Node nextIteratorValue = align.get(iterator);
    //
    // // see if we need to split here
    // if (segmentsSinceLastRoot == layers.length - 1)
    // { // split below splitMark:
    //
    // // we should have found at least one non-inner segment
    // if (splitMark == null)
    // throw new IllegalStateException("Could not split " +
    // "block - found no non-inner segment!");
    //
    // System.out.println("split at node " + splitMark);
    //                        
    // Node afterSplitMark = align.get(splitMark);
    // cutAlignLinkAndSetRoots(lastRoot, splitMark);
    //
    // // set the new root to the next node after splitMark
    // lastRoot = afterSplitMark;
    // root.put(lastRoot, lastRoot);
    // segmentsSinceLastRoot = segmentsSinceSplitMark - 1;
    // }
    //
    // // investigate the next segment
    // iterator = nextIteratorValue;
    // segmentsSinceLastRoot++;
    // segmentsSinceSplitMark++;
    // }
    // // iterator now points to the last node of the block
    //
    // // fix the last block (from lastRoot to iterator)
    // cutAlignLinkAndSetRoots(lastRoot, iterator);
    //
    // //System.out.println("blocklength: " + block.size());
    // }
    // }

    /**
     * align(lastNode) := lastNode root(all nodes reachable from rootNode) :=
     * rootNode
     */
    private void cutAlignLinkAndSetRoots(Node rootNode, Node lastNode) {
        int segments = 0;
        // System.out.println("split:");

        // cut the align link to mark the end of the block
        align.put(lastNode, lastNode);

        // set the root values of the nodes that are reachable from rootNode
        Node node = rootNode;
        do {
            root.put(node, rootNode);
            // System.out.println("root(" + getNodeLabel(node) + ") := " +
            // getNodeLabel(rootNode));

            // break if this is the last node
            if (align.get(node) == node) {
                break;
            }
            // otherwise go to next node
            node = align.get(node);
            segments++;
        } while (true);

        // System.out.println("segments = " + segments);

        // die here if lastNode is not reachable from rootNode
        if (node != lastNode)
            throw new IllegalArgumentException();
    }

    /**
     * This method finds cyclic blocks and breaks them at an arbitrary position
     * (current implementation: above the first non-dummy node).<br>
     * Note: The broken cyclic block will still be layouted as a circle because
     * only one segment is removed.
     */
    private void breakCyclicBlocks() {
        // System.out.println("breakCyclicBlocks()");

        // iterate over the first layer - a cycle must pass through this layer
        for (Node layerZeroNode : layers[0])
            // if it's a cycle then the root value is not set
            if (!root.containsKey(layerZeroNode)) {
                System.out.println("Detected cycle in layer 0 at node "
                        + getNodeLabel(layerZeroNode) + ":");

                // find the next non-dummy node
                Node node = layerZeroNode;
                while (isDummy(node)) {
                    node = align.get(node);
                }

                // make this the root node
                Node newRoot = align.get(node);

                System.out.println("New root: " + getNodeLabel(newRoot));

                cutAlignLinkAndSetRoots(newRoot, node);
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
                int keyLevel = level(entry.getKey());
                int valueLevel = level(entry.getValue());
                if (nextLayer(keyLevel) == valueLevel) {
                    noRoots.add(entry.getValue());
                } else
                    throw new IllegalStateException(
                            "falignment between non-adjacent levels");
            }
        return noRoots;
    }

    /**
     * Defines the align value for each node.
     */
    private void calculateAlign() {
        // initialize the align attribute of each node to point to itself
        align = new HashMap<Node, Node>();
        for (Node node : graph.getNodes()) {
            align.put(node, node);
        }

        // for all layers (including first and last)
        for (int layer = 1; layer < layers.length; layer++) {
            // System.out.println("Layer " + layer + ":");

            // minimal x of the upper node of a newly added edge
            int minX = -1;

            // for all nodes (from left to right)
            for (Node node : layers[layer]) {
                // System.out.println("(Lower) Node at pos " + pos(node) + ":");

                // get all medians (if any...)
                LinkedList<Node> medians = new LinkedList<Node>();
                if (leftMedian(node) != null) {
                    medians.add(leftMedian(node));
                }
                if (rightMedian(node) != null) {
                    medians.add(rightMedian(node));
                }
                if (medians.size() == 2
                        && leftMedian(node) == rightMedian(node)) {
                    medians.removeLast();
                }

                // for all medians (from left to right)
                for (Node median : medians) {
                    // String nodelabel;
                    // if (isDummy(median))
                    // nodelabel = "Dummy (L" + level(median) + ")";
                    // else
                    // nodelabel = "Node " + median.getString("label.label");
                    // System.out.print("Median " + nodelabel + ": ");

                    // test for an edge crossing
                    if (minX >= pos(median)) {
                        // System.out.println(" edge crossing");
                        continue;
                    }

                    // get the edge between this node and its median
                    Edge edge = graph.getEdges(median, node).iterator().next();

                    // the edge mustn't be marked
                    if (marked.contains(edge)) {
                        // System.out.println(" edge is marked");
                        continue;
                    }

                    // // see if the lower node is already part of a block
                    // if (align.get(node) != node) {
                    // System.out.print(" (merging:)");
                    // }

                    // align the median to the node
                    align.put(median, node);
                    // root.put(node, root.get(median));
                    // align.put(node, root.get(node));
                    minX = pos(median);

                    // if (isDummy(node))
                    // nodelabel = "Dummy (L" + level(node) + ")";
                    // else
                    // nodelabel = "Node " + node.getString("label.label");
                    // System.out.println(" now aligned to " + nodelabel);

                    // node has been aligned -> leave the loop
                    break;
                    // System.out.print(minX + " ");
                }
            }
        }
    }

    private String getNodeLabel(Node node) {
        if (isDummy(node))
            return "Dummy (L" + level(node) + ")";
        else
            return "Node " + node.getString("label.label");
    }

    /**
     * Paints all edges from the field "align" green. Saves the original colors.
     */
    private void paintAlignedEdges() {
        ColorAttribute edgeColor;
        for (Node node1 : align.keySet()) {
            if (align.get(node1) == node1) {
                continue; // points to itself
            }

            Node node2 = align.get(node1);
            Edge edge = graph.getEdges(node1, node2).iterator().next();
            edgeColor = (ColorAttribute) edge
                    .getAttribute("graphics.framecolor");
            if (!originalColors.containsKey(edge)) {
                originalColors.put(edge, edgeColor.getColor());
            }
            edgeColor.setColor(Color.GREEN);
        }
    }

    /**
     * Paints all nodes from the field "root" green. Saves the original colors.
     */
    private void paintRootNodes() {
        ColorAttribute color;
        for (Node node : root.values()) {
            color = (ColorAttribute) node.getAttribute("graphics.framecolor");
            if (!originalColors.containsKey(node)) {
                originalColors.put(node, color.getColor());
            }
            color.setColor(Color.GREEN);
        }
    }

    // /**
    // * Paints all dummy nodes blue. Saves the original colors.
    // */
    // private void paintDummies()
    // {
    // ColorAttribute color;
    // for (Node node : dummies)
    // {
    // color = (ColorAttribute)node.getAttribute("graphics.framecolor");
    // if (!originalColors.containsKey(node))
    // originalColors.put(node, color.getColor());
    // color.setColor(Color.BLUE);
    // }
    // }
    //
    // /**
    // * Paints all edges from the fields "leftMedian" and "rightMedian". Saves
    // * the original colors.
    // */
    // private void paintMedians()
    // {
    // ColorAttribute edgeColor;
    // for (Node node : leftMedian.keySet())
    // {
    // Node median = leftMedian.get(node);
    // Edge edge = graph.getEdges(node, median).iterator().next();
    // edgeColor = (ColorAttribute)edge.getAttribute("graphics.framecolor");
    // if (!originalColors.containsKey(edge))
    // originalColors.put(edge, edgeColor.getColor());
    // edge.changeInteger("graphics.framecolor.green", 254);
    // }
    // for (Node node : rightMedian.keySet())
    // {
    // Node median = rightMedian.get(node);
    // Edge edge = graph.getEdges(node, median).iterator().next();
    // edgeColor = (ColorAttribute)edge.getAttribute("graphics.framecolor");
    // if (!originalColors.containsKey(edge))
    // originalColors.put(edge, edgeColor.getColor());
    // edge.changeInteger("graphics.framecolor.blue", 254);
    // }
    // }

    // /** the sink of each class */
    // private Map<Node, Node> sink;
    //
    // /** the shift of each node */
    // private Map<Node, Integer> shift;
    //
    // /** each node's left neighbor */
    // private Map<Node, Node> pred;

    /**
     * See Algorithm 3 of publication: compute relative x-coordinates for all
     * roots
     */
    private void horizontalCompaction() {
        // init
        x = new HashMap<Node, Double>();
        // sink = new HashMap<Node, Node>();
        // shift = new HashMap<Node, Integer>();
        // for (Node node : graph.getNodes())
        // {
        // sink.put(node, node);
        // shift.put(node, Integer.MAX_VALUE);
        // }
        // pred = new HashMap<Node, Node>();
        // for (Node[] layer : layers)
        // for (int n = 1; n < layer.length; n++)
        // pred.put(layer[n], layer[n - 1]);
        //
        // // for all nodes of the graph
        // for (Node v : this.graph.getNodes()) {
        //
        // // compute root coordinates relative to sink
        // if (this.root.get(v).equals(v)) {
        // placeBlock(v);
        // }
        // }

        buildBlockGraph();
        tarjan();
        buildSCCGraph();
        topoSortSCCGraph();
        rectifyBlocks();
        calculateSCCSpacing();
        moveSCCsLeft();
        calculateSCCSpacing();
        moveSCCsRight();

        // make the coordinates absolute
        // for (Node v : graph.getNodes())
        // {
        // x.put(v, x.get(root.get(v)));
        // int currentShift = shift.get(sink.get(root.get(v)));
        // if (currentShift < Integer.MAX_VALUE)
        // x.put(v, x.get(v) + currentShift);
        // }
    }

    // /**
    // * See Algorithm 3 of publication: computes a relative x-coordinate for
    // the
    // * current node and if necessary a shift to the next block
    // *
    // * @param rootNode some block's root node
    // */
    // private void placeBlock(Node rootNode) {
    //
    // // x-coordinate not yet set?
    // if (x.get(rootNode) == null) {
    //
    // // initialize x-coordinate with 0
    // x.put(rootNode, 0.0);
    // Node w = rootNode;
    //
    // do {
    //
    // if (pos(w) > 0) {
    //
    // // get left neighbor and its root
    // Node leftRoot = root.get(pred.get(w));
    //
    // // recursive method call
    // placeBlock(leftRoot);
    //
    // // set sink
    // if (sink.get(rootNode) == rootNode) {
    // sink.put(rootNode, sink.get(leftRoot));
    // }
    //
    // // if sink was already set - compute the shift
    // // else compute x-coordinate
    // if (sink.get(rootNode) != sink.get(leftRoot)) {
    // shift.put(sink.get(leftRoot),
    // (int)Math.min(
    // shift.get(sink.get(leftRoot)),
    // x.get(rootNode) - x.get(leftRoot) - 1));
    // } else {
    // x.put(rootNode, Math.max(x.get(rootNode), x.get(leftRoot) + 1));
    // }
    // }
    //
    // // break if this was the last node of the block
    // if (align.get(w) == w) break;
    //                
    // // get node to align under w
    // w = align.get(w);
    // } while (true);
    // }
    // }

    /**
     * Stores each block's left neighbors
     */
    private Map<Node, Set<Node>> blockNeighbors;

    private void buildBlockGraph() {
        blockNeighbors = new HashMap<Node, Set<Node>>();

        // for all root nodes (i.e. blocks)
        for (Node node : graph.getNodes())
            if (root.get(node).equals(node)) {
                // init the adjacency list
                blockNeighbors.put(node, new HashSet<Node>());
            }

        // for each pair of neighboring nodes (left, right)...
        for (Node[] layer : layers) {
            for (int i = 1; i < layer.length; i++) {
                Node right = root.get(layer[i]);
                Node left = root.get(layer[i - 1]);
                // ...add the edge (left <- right) to the blockgraph
                blockNeighbors.get(right).add(left);
            }
        }
    }

    private int tarjanDfsNum;
    private Map<Node, Integer> tarjanIndex;
    private Map<Node, Integer> tarjanLowlink;
    private Stack<Node> tarjanStack;

    /**
     * Implements the <a href="http://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten"
     * > Algorithm of Tarjan</a> to find strongly connected components in the
     * block graph.
     */
    private void tarjan() {
        sccs = new HashSet<SCC>();
        parentSCC = new HashMap<Node, SCC>();

        // init the data structures of the Tarjan algorithm
        tarjanDfsNum = 0;
        tarjanIndex = new HashMap<Node, Integer>();
        tarjanLowlink = new HashMap<Node, Integer>();
        tarjanStack = new Stack<Node>();

        // for all root nodes (i.e. blocks)
        for (Node node : graph.getNodes())
            if (root.get(node).equals(node))
                // if unvisited then visit
                if (!tarjanIndex.containsKey(node)) {
                    tarjan(node);
                }
    }

    private void tarjan(Node node) {
        // check if it's a block root
        assert node.equals(root.get(node));

        tarjanIndex.put(node, tarjanDfsNum);
        tarjanLowlink.put(node, tarjanDfsNum);
        tarjanDfsNum++;
        tarjanStack.push(node);
        for (Node neighbor : blockNeighbors.get(node)) {
            if (!tarjanIndex.containsKey(neighbor)) {
                tarjan(neighbor);
                tarjanLowlink.put(node, Math.min(tarjanLowlink.get(node),
                        tarjanLowlink.get(neighbor)));
            } else if (tarjanStack.contains(neighbor)) {
                tarjanLowlink.put(node, Math.min(tarjanLowlink.get(node),
                        tarjanIndex.get(neighbor)));
            }
        }
        if (tarjanLowlink.get(node) == tarjanIndex.get(node)) {
            // create new SCC
            SCC scc = new SCC();
            System.out.println("New SCC (" + tarjanStack.search(node) + "):");
            Node loopNode;
            do {
                loopNode = tarjanStack.pop();
                // add loopNode to the current SCC
                scc.blockroots.add(loopNode);
                parentSCC.put(loopNode, scc);
                System.out.println(loopNode);
            } while (loopNode != node);
            sccs.add(scc);
        }
    }

    /**
     * Represents a strongly connected component within the block graph.
     */
    class SCC {
        /** all the roots of the blocks that form this SCC */
        Set<Node> blockroots;
        /** the left/right neighbors of a SCC */
        Set<SCC> pred, succ;
        /** the number of incoming links (for topological sorting) */
        int incomingLinks;
        /** stores the minimum distance to a neighboring SCC */
        Map<SCC, Double> distToPred, distToSucc;

        /**
         * Init all fields with empty Sets/Maps.
         */
        SCC() {
            blockroots = new HashSet<Node>();
            pred = new HashSet<SCC>();
            succ = new HashSet<SCC>();
            distToPred = new HashMap<SCC, Double>();
            distToSucc = new HashMap<SCC, Double>();
        }

        void rectify() {
            if (blockroots.size() == 1) {
                rectifyBlock(blockroots.iterator().next(), 0);
            } else
                throw new NotImplementedException();
        }

        /**
         * Set the x coordinates of the whole block represented by
         * <code>node</code> to the value of <code>xPos</code>.
         */
        private void rectifyBlock(Node node, double xPos) {
            x.put(node, xPos);
            while (align.get(node) != node) {
                node = align.get(node);
                x.put(node, xPos);
            }
        }

        /**
         * Moves the SCC as far as possible to the left.
         */
        void moveLeft() {
            // no predecessors -> don't move
            if (pred.size() == 0)
                return;

            // find the minimum spacing to the neighbors
            double delta = Double.POSITIVE_INFINITY;
            for (double distance : distToPred.values()) {
                delta = min(delta, distance);
            }

            // move the SCC
            changePos(delta + 1);
        }

        /**
         * Moves the SCC as far as possible to the right.
         */
        void moveRight() {
            // no successors -> don't move
            if (succ.size() == 0)
                return;

            // find the minimum spacing to the neighbors
            double delta = Double.POSITIVE_INFINITY;
            for (double distance : distToSucc.values()) {
                delta = min(delta, distance);
            }

            // move the SCC
            changePos(-delta - 1);
        }

        /**
         * Adds <code>delta</code> to the x-Coord. of each node in this SCC.
         */
        void changePos(double delta) {
            // change all x coordinates (+delta)
            for (Node blockRoot : blockroots) {
                Node node = blockRoot;
                x.put(node, x.get(node) + delta);
                while (align.get(node) != node) {
                    node = align.get(node);
                    x.put(node, x.get(node) + delta);
                }

            }

            // update the distances to the neighbors
            for (Map.Entry<SCC, Double> entry : distToPred.entrySet()) {
                entry.setValue(entry.getValue() + delta);
            }
            for (Map.Entry<SCC, Double> entry : distToSucc.entrySet()) {
                entry.setValue(entry.getValue() - delta);
            }
            for (SCC neighbor : pred) {
                double dist = neighbor.distToSucc.get(this);
                neighbor.distToSucc.put(this, dist + delta);
            }
            for (SCC neighbor : succ) {
                double dist = neighbor.distToPred.get(this);
                neighbor.distToPred.put(this, dist - delta);
            }
        }
    }

    /**
     * all strongly connected component within the block graph
     */
    private Set<SCC> sccs;

    /**
     * Links each block to its containing SCC
     */
    private Map<Node, SCC> parentSCC;

    /**
     * Fills the SCCs' adjacency lists.
     */
    private void buildSCCGraph() {
        // for each SCC
        for (SCC scc : sccs) {
            // for all blocks of the current SCC
            for (Node blockroot : scc.blockroots) {
                // for all potential neighbor blocks
                for (Node neighborBlock : blockNeighbors.get(blockroot)) {
                    // get the neighbor block's surrounding SCC
                    SCC neighborSCC = parentSCC.get(neighborBlock);

                    // add it if it's not the same as the current SCC
                    if (scc != neighborSCC) {
                        scc.pred.add(neighborSCC);
                    }
                }
            }
        }

        // fill SCC.succ from SCC.pred
        for (SCC scc : sccs) {
            for (SCC leftNeighbor : scc.pred) {
                leftNeighbor.succ.add(scc);
            }
        }
    }

    private SCC[] topoSortedSCCs;

    private void topoSortSCCGraph() {
        List<SCC> sources = new ArrayList<SCC>();

        // set each SCCs link count
        for (SCC scc : sccs) {
            scc.incomingLinks = scc.succ.size();
        }

        // find the first sources
        for (SCC scc : sccs)
            if (scc.incomingLinks == 0) {
                sources.add(scc);
            }

        // remove the sources and look for new sources
        int index = 0;
        while (sources.size() < sccs.size()) {
            for (SCC pred : sources.get(index).pred) {
                pred.incomingLinks--;
                if (pred.incomingLinks == 0) {
                    sources.add(pred);
                }
            }
            index++;
        }

        topoSortedSCCs = sources.toArray(new SCC[0]);
    }

    private void rectifyBlocks() {
        for (SCC scc : sccs) {
            scc.rectify();
        }
    }

    /**
     * Calculates the current minimum spacing between each neighboring pair of
     * SCCs. The results can be negative.
     */
    private void calculateSCCSpacing() {
        // for each Node
        for (Node[] layer : layers) {
            for (int n = 1; n < layer.length; n++) {
                SCC sccLeft = parentSCC.get(root.get(layer[n - 1]));
                SCC sccRight = parentSCC.get(root.get(layer[n]));

                if (sccLeft == sccRight) {
                    continue;
                }

                double dist = x.get(layer[n]) - x.get(layer[n - 1]);

                Double value = sccLeft.distToSucc.get(sccRight);
                if (value == null) {
                    value = Double.POSITIVE_INFINITY;
                }
                sccLeft.distToSucc.put(sccRight, min(dist, value));

                value = sccRight.distToPred.get(sccLeft);
                if (value == null) {
                    value = Double.POSITIVE_INFINITY;
                }
                sccRight.distToPred.put(sccLeft, min(dist, value));
            }
        }
    }

    private void moveSCCsLeft() {
        for (int index = topoSortedSCCs.length - 1; index >= 0; index--) {
            topoSortedSCCs[index].moveLeft();
        }
    }

    private void moveSCCsRight() {
        for (int index = 0; index < topoSortedSCCs.length; index++) {
            topoSortedSCCs[index].moveRight();
        }
    }

    /**
     * flips the whole graph horizontally
     */
    private void flipHorizontal() {

        if (x != null) {
            // find the rightmost x-coordinate ...
            double maxX = 0;
            for (double val : x.values()) {
                maxX = Math.max(maxX, val);
            }

            // ... and flip all x-coordinates
            for (Map.Entry<Node, Double> entry : x.entrySet()) {
                entry.setValue(maxX - entry.getValue());
            }
        }

        // for all layers
        Node[][] result = new Node[layers.length][];
        for (int layer = 0; layer < layers.length; layer++) {

            // flip this layer horizontally
            Node[] oldArray = layers[layer];
            Node[] newArray = new Node[oldArray.length];
            result[layer] = newArray;
            for (int n = 0; n < newArray.length; n++) {
                // copy one node
                int index = oldArray.length - 1 - n;
                Node node = oldArray[n];
                newArray[index] = node;

                // update the node's x-coordinate
                pos.put(node, index);
            }
        }
        layers = result;
    }

    /**
     * flips the whole graph vertically
     */
    private void flipVertical() {

        // flip all layers vertically
        Node[][] result = new Node[layers.length][];
        for (int layer = 0; layer < layers.length; layer++) {
            result[layer] = layers[layers.length - 1 - layer];
        }
        layers = result;

        // flip each node's layer number
        for (Map.Entry<Node, Integer> entry : level.entrySet()) {
            entry.setValue(layers.length - 1 - entry.getValue());
        }

        // reverse all edges
        for (Edge edge : graph.getEdges()) {
            edge.reverse();
        }
    }

    /**
     * Aligns the assignments to the one with smallest width and computes the
     * average median. The resulting coordinates are stored in the field "x".
     */
    private void balance() {

        x = new HashMap<Node, Double>();

        double minWidth = Double.MAX_VALUE;
        int smallestWidthLayout = 0;
        double[] min = new double[4];
        double[] max = new double[4];

        // get the layout with smallest width and set minimum and maximum value
        // for each direction
        for (int dir = 0; dir <= 3; ++dir) {
            min[dir] = Integer.MAX_VALUE;
            max[dir] = 0;
            for (double x : xs[dir].values()) {
                if (x < min[dir]) {
                    min[dir] = x;
                }
                if (x > max[dir]) {
                    max[dir] = x;
                }
            }
            double width = max[dir] - min[dir];
            if (width < minWidth) {
                minWidth = width;
                smallestWidthLayout = dir;
            }
        }

        // align the layouts to the one with smallest width
        for (int dir = 0; dir <= 3; ++dir) {

            if (dir == smallestWidthLayout) {
                continue;
            }

            double diff;
            if (dir == LEFT_UP || dir == LEFT_DOWN) {
                // align the left to right layouts to the left border of the
                // smallest layout
                diff = min[smallestWidthLayout] - min[dir];
            } else {
                // align the right to left layouts to the right border of
                // the smallest layout
                diff = max[smallestWidthLayout] - max[dir];
            }

            if (diff == 0) {
                continue;
            }

            for (Node n : xs[dir].keySet()) {
                xs[dir].put(n, xs[dir].get(n) + diff);
            }
        }

        // get the average median of each coordinate
        for (Node n : this.graph.getNodes()) {
            double[] values = new double[4];
            for (int dir = 0; dir < 4; dir++) {
                values[dir] = xs[dir].get(n);
            }
            Arrays.sort(values); // values.length == 4 -> no runtime problem
            // for (int i = 0; i < 4; i++)
            // System.out.print(values[i] + " ");
            double finalCoordinate = (values[1] + values[2]) / 2.0;
            if (DIRECTION == 4) {
                x.put(n, finalCoordinate);
            } else {
                x.put(n, xs[DIRECTION].get(n));
            }
        }

        // move all coordinates to the left (so that leftmost x == 0)
        double minValue = Double.MAX_VALUE;
        for (double c : x.values())
            if (c < minValue) {
                minValue = c;
            }
        if (minValue != 0) {
            for (Node n : x.keySet()) {
                x.put(n, x.get(n) - minValue);
            }
        }
    }

    /**
     * This method reads the fields <code>x</code> and <code>level</code>,
     * calculates each node's coordinates and stores those coordinates in each
     * node's "sugiyama.coordinate" attribute.
     * <p>
     * Horizontal and cyclic layout are distinguished.
     */
    private void updateGraph() {
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            updateGraphCyclic();
        }
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
            updateGraphHorizontal();
        }
    }

    /**
     * This method creates regular (horizontal) coordinates.
     */
    private void updateGraphHorizontal() {
        // for each layer
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            ArrayList<Node> layer = data.getLayers().getLayer(i);

            // for each node of current layer
            for (int j = 0; j < layer.size(); j++) {
                Node node = layer.get(j);
                CoordinateAttribute ca = new CoordinateAttribute("coordinate",
                        x.get(node) * NODE_DELTA, level(node) * RADIUS_OFFSET);
                node.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                // ca = new CoordinateAttribute(
                // "coordinate",
                // x.get(node) * NODE_DELTA,
                // level(node) * RADIUS_OFFSET);
                // node.removeAttribute("graphics.coordinate");
                // node.addAttribute(ca, "graphics");
            }
        }
    }

    /**
     * This method creates cyclic coordinates.
     */
    private void updateGraphCyclic() {

        double angleBetweenLayers = -(2 * PI)
                / data.getLayers().getNumberOfLayers();

        // for each layer
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            ArrayList<Node> layer = data.getLayers().getLayer(i);

            // for each node of current layer
            for (int j = 0; j < layer.size(); j++) {
                Node node = layer.get(j);
                if (x.containsKey(node)) {
                    double radius = RADIUS_OFFSET + (x.get(node) * NODE_DELTA);
                    CoordinateAttribute ca = new CoordinateAttribute(
                            "coordinate", cos(i * angleBetweenLayers) * radius,
                            sin(i * angleBetweenLayers) * radius);
                    node.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                }
            }
        }
    }

    /**
     * Copies all initially needed information from the SugiyamaData object.
     */
    private void copyFromSugiyamaData() {
        dummies = data.getDummyNodes();

        // x-coordinate of each node
        pos = new HashMap<Node, Integer>();
        // y-coordinate of each node
        level = new HashMap<Node, Integer>();

        // copy references to all nodes into a 2-dimensional array
        layers = new Node[data.getLayers().getNumberOfLayers()][];
        for (int layerNr = 0; layerNr < layers.length; layerNr++) {
            ArrayList<Node> nodeLayer = data.getLayers().getLayer(layerNr);
            layers[layerNr] = new Node[nodeLayer.size()];
            nodeLayer.toArray(layers[layerNr]);
            for (int node = 0; node < layers[layerNr].length; node++) {
                // store each node's coordinates
                pos.put(layers[layerNr][node], node);
                level.put(layers[layerNr][node], layerNr);
            }
        }

        // HashMap<Edge, Integer> offset = new HashMap<Edge, Integer>();
        // Iterator<Edge> edgeIter = graph.getEdgesIterator();
        // while (edgeIter.hasNext())
        // offset.put(edgeIter.next(), new Integer(0));

        // do some checks on our data structures
        String prefix = this.getClass().getSimpleName()
                + ": input data is corrupt: ";

        int nodes = 0;
        for (Node[] layer : layers) {
            nodes += layer.length;
        }
        if (nodes != graph.getNumberOfNodes())
            throw new IllegalStateException(prefix
                    + "Node# in layers[][] != graph.getNumberOfNodes()");

        if (pos.size() != graph.getNumberOfNodes())
            throw new IllegalStateException(prefix
                    + "pos.size() != graph.getNumberOfNodes()");

        if (level.size() != graph.getNumberOfNodes())
            throw new IllegalStateException(prefix
                    + "level.size() != graph.getNumberOfNodes()");
    }

    @Override
    public void reset() {
        // we don't do much here as each algorithm initializes its own data
        // structures
        originalColors.clear();
        super.reset();
    }

    /**
     * Return the supported parameters of this algorithm. The user can define
     * the minimal distance to the center and the spacing between adjacent
     * nodes.
     * 
     * @return Returns a <code>Parameter[]</code> of supported parameters
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter p1 = new IntegerParameter(RADIUS_OFFSET, 1, 1000,
                "Distance to center",
                "Set the minimal distance of a node to the center.");
        IntegerParameter p2 = new IntegerParameter(NODE_DELTA, 1, 1000,
                "Node spacing",
                "Set the minimal distance between two nodes on the same level.");
        IntegerParameter p3 = new IntegerParameter(DIRECTION, 0, 4,
                "Direction",
                "Use only direction 0,1,2 or 3 (4 -> all 4 directions).");

        this.parameters = new Parameter[] { p1, p2, p3 };
        return this.parameters;
    }

    /**
     * Apply the configured parameters to the algorithm
     * 
     * @param params
     *            Parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        RADIUS_OFFSET = ((IntegerParameter) params[0]).getValue();
        NODE_DELTA = ((IntegerParameter) params[1]).getValue();
        DIRECTION = ((IntegerParameter) params[2]).getValue();
        data.setCyclicLayoutRadiusOffset(RADIUS_OFFSET);
        data.setCyclicLayoutRadiusDelta(NODE_DELTA);
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData theData) {
        this.data = theData;
    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
