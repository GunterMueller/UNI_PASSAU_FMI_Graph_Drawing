// =============================================================================
//
//   BilayerCrossCounter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BilayerCrossCounter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * <p>
 * This class implements bilayer cross counting in O(|e|log|v_small|) where e is
 * the number of edges between the two layers and v_small the smaller number of
 * nodes on either the upper or lower level.
 * </p>
 * <p>
 * The algorithm is based upon the O(e log e) Algorithm described by Waddle and
 * Malhotra in their paper "An E log E line crossing algorithm for levelled
 * graphs" and was first described by Barth, Juenger and Mutzel in their paper
 * "Simple and efficient bilayer cross counting"
 * </p>
 */
public class BilayerCrossCounter {
    /** SugiyamaData bean */
    private SugiyamaData sData;

    /** Index of the lower level */
    private int lowerLevelIndex;

    /** Nodes on the upper level */
    private ArrayList<Node> upperLevel;

    /** Nodes on the lower level */
    private ArrayList<Node> lowerLevel;

    /** Edges between the two levels */
    private ArrayList<Edge> edges;

    /** An integer array representing the accumulator tree */
    private int[] accumulatorTree;

    /** height of the accumulator tree */
    int treeHeight;

    /** Positions of the end nodes */
    private int[] endNodePositions;

    /**
     * Default constructor for the BilayerCrossCounter
     * 
     * @param g
     *            The <code>Graph</code> to be processed
     * @param upperLevelIndex
     *            The index of the upper level
     * @param data
     *            The <code>SugiyamaData</code> bean
     */
    public BilayerCrossCounter(Graph g, int upperLevelIndex, SugiyamaData data) {
        this.sData = data;

        // compute the index of the level below the upper level
        NodeLayers layers = sData.getLayers();
        if (layers.getNumberOfLayers() - 1 == upperLevelIndex) {
            lowerLevelIndex = 0;
        } else {
            lowerLevelIndex = upperLevelIndex + 1;
        }

        upperLevel = layers.getLayer(upperLevelIndex);
        lowerLevel = layers.getLayer(lowerLevelIndex);
    }

    public BilayerCrossCounter(int upperLevelIndex, NodeLayers layers) {
        if (layers.getNumberOfLayers() - 1 == upperLevelIndex) {
            lowerLevelIndex = 0;
        } else {
            lowerLevelIndex = upperLevelIndex + 1;
        }

        upperLevel = layers.getLayer(upperLevelIndex);
        lowerLevel = layers.getLayer(lowerLevelIndex);
    }

    /**
     * This method computes the number of edge intersections between the two
     * levels <i>upperLevel</i> and <i>lowerLevel</i>.
     * 
     * @return Returns the number of edge intersections between
     *         <i>upperLevel</i> and <i>lowerLevel</i>.
     */
    public int getNumberOfCrossings() {
        this.initialize();
        int crossings = 0;
        int treeIndex;

        for (int i = 0; i < endNodePositions.length; i++) {
            treeIndex = endNodePositions[i];
            while (treeIndex != 0) {
                accumulatorTree[treeIndex]++;
                if (isLeftChild(treeIndex)) {
                    crossings += accumulatorTree[getRightSibling(treeIndex)];
                }
                treeIndex = getParent(treeIndex);
            }
        }
        return crossings;
    }

    /**
     * Return the crossings if the node at position left is swapped with the
     * node on position right
     */
    public int getCrossingDifference(int left, int right) {
        // swap the two nodes and compute the new number of crossings
        Node tmpNode = lowerLevel.remove(left);
        lowerLevel.add(right, tmpNode);
        int newCrossings = this.getNumberOfCrossings();
        // restore the layout of the lower layer (swap the nodes back)
        tmpNode = lowerLevel.remove(left);
        lowerLevel.add(right, tmpNode);
        return newCrossings;// - oldCrossings;

    }

    /**
     * <p>
     * This method initializes all helper structures needed for computation of
     * the edge intersections between the two levels:
     * </p>
     * <ul>
     * <li>Create an array containing all edges between the two levels</li>
     * <li>Sort the edges lexicographically (called a <i>radix sort</i>)</li>
     * <li>Initialize the accumulator tree</li>
     * <li>Build the array containing the positions of the end nodes according
     * to the order of the edges between the two levels</i>
     */
    private void initialize() {
        Iterator<Edge> edgeIterator;
        edges = new ArrayList<Edge>();

        // build the array containing the edges
        for (int i = 0; i < lowerLevel.size(); i++) {
            edgeIterator = lowerLevel.get(i).getDirectedInEdgesIterator();
            while (edgeIterator.hasNext()) {
                edges.add(edgeIterator.next());
            }
        }
        // do a radix sort on the edges
        Collections.sort(edges, new RadixSortComparator());
        // initialize the accumulator tree
        /** @todo */
        // treeHeight = Integer.toBinaryString(edges.size()).length();
        treeHeight = Integer.toBinaryString(edges.size()).length() + 1;

        accumulatorTree = new int[(int) Math.pow(2, (treeHeight + 1)) - 1];

        int leafOffset = (int) Math.pow(2, treeHeight) - 1;

        // System.out.println("Tree Height: " + treeHeight + ", edges: " +
        // edges.size() + ", leafOffset: " + leafOffset);
        // build the endNodePosition array
        endNodePositions = new int[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            endNodePositions[i] = lowerLevel.indexOf(edges.get(i).getTarget())
                    + leafOffset;
        }

    }

    /**
     * Simple method to determine if a node in the accumulator tree is a left
     * child
     * 
     * @param index
     *            The index of the node
     * @return Returns <code>true</code> if the node at index <i>index</i> is a
     *         left child in the tree, <code>false</code> otherwise.
     */
    private boolean isLeftChild(int index) {
        return index % 2 == 1;
    }

    /**
     * Simple method to access the index of the parent of the node at position
     * <i>index</i>.
     * 
     * @param index
     *            The index of the node
     * @return Returns the index of the parent node
     */
    private int getParent(int index) {
        return (int) Math.floor((index - 1) / 2);
    }

    /**
     * Simple method to access the right sibling of the current node
     * 
     * @param index
     *            The index of the current node in the accumulator tree
     * @return Returns the index of this node's right sibling in the accumulator
     *         tree.
     */
    private int getRightSibling(int index) {
        return index + 1;
    }

    /**
     * This class implements a so-called <i>RadixSortComparator</i> which is
     * basically a lexicographical comparator.
     * 
     * @author Ferdinand Huebner
     */
    private class RadixSortComparator implements Comparator<Edge> {
        public int compare(Edge a, Edge b) {
            int indexUpperA, indexUpperB;
            int indexLowerA, indexLowerB;
            indexUpperA = upperLevel.indexOf(a.getSource());
            indexLowerA = lowerLevel.indexOf(a.getTarget());
            indexUpperB = upperLevel.indexOf(b.getSource());
            indexLowerB = lowerLevel.indexOf(b.getTarget());

            if (indexUpperA < indexUpperB
                    || (indexUpperA == indexUpperB && indexLowerA < indexLowerB))
                return -1;
            else if (indexUpperA == indexUpperB && indexLowerA == indexLowerB)
                return 0;
            else
                return 1;

        }
    }

}
