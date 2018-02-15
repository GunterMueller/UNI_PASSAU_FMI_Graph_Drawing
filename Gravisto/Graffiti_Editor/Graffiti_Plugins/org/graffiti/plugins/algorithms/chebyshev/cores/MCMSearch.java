// =============================================================================
//
//   MCMSearch.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.cores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.graffiti.plugins.algorithms.chebyshev.AuxLayer;
import org.graffiti.plugins.algorithms.chebyshev.AuxNode;
import org.graffiti.plugins.tools.math.BinarySearch;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MCMSearch extends BinarySearch {
    // private AuxLayer fixedLayer;
    private int fixedLength;
    private int midFixedX;

    private AuxLayer activeLayer;
    private int length;
    private AuxNode[] activeIdNodes;

    private int neighborIndex;

    // May be invalid if lockedCount == length.
    private boolean isLocked[];
    private int lockedCount;

    private int currentXs[];
    private int bestXs[];
    private AuxNode[] currentXNodes;
    private AuxNode[] bestXNodes;
    private int[] currentLeftSpace;
    private int[] bestLeftSpace;

    public MCMSearch(AuxLayer fixedLayer, AuxLayer activeLayer,
            int neighborIndex) {
        // this.fixedLayer = fixedLayer;
        fixedLength = fixedLayer.getLength();
        AuxNode[] fixedXNodes = fixedLayer.getXNodes();
        midFixedX = (fixedXNodes[fixedLength - 1].getX() - fixedXNodes[0]
                .getX()) / 2;

        this.activeLayer = activeLayer;
        length = activeLayer.getLength();
        activeIdNodes = activeLayer.getIdNodes();

        isLocked = new boolean[length];
        lockedCount = 0;

        currentXs = new int[length];
        bestXs = new int[length];
        currentXNodes = new AuxNode[length];
        bestXNodes = activeLayer.getXNodes();
        currentLeftSpace = new int[length];
        bestLeftSpace = new int[length];

        this.neighborIndex = neighborIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean test(int value) {
        // Intersect intervals.
        PriorityQueue<Interval> intervals = new PriorityQueue<Interval>(length,
                LowerBoundComparator.get());
        LinkedList<AuxNode> freeNodes = new LinkedList<AuxNode>();
        for (int localId = 0; localId < length; localId++) {
            AuxNode node = activeIdNodes[localId];
            if (isLocked[localId]) {
                int x = bestXs[localId];
                intervals.add(new Interval(x, x, node));
            } else {
                int lower = Integer.MIN_VALUE;
                int upper = Integer.MAX_VALUE;
                AuxNode[] fixedNodes = node.getNeighbors(neighborIndex);
                if (fixedNodes.length == 0) {
                    freeNodes.add(node);
                } else {
                    for (AuxNode fixedNode : fixedNodes) {
                        lower = Math.max(lower, fixedNode.getX() - value);
                        upper = Math.min(upper, fixedNode.getX() + value);
                    }
                    if (lower > upper)
                        return false; // Empty intersection.
                    intervals.add(new Interval(lower, upper, node));
                }
            }
        }

        Iterator<AuxNode> freeNodesIter = freeNodes.iterator();
        int xScan;
        if (intervals.isEmpty()) {
            xScan = midFixedX - (freeNodes.size() - 1) / 2;
        } else {
            xScan = intervals.peek().getLower();
        }

        PriorityQueue<Interval> candidates = new PriorityQueue<Interval>(
                length, UpperBoundComparator.get());

        int index = 0;
        while (!intervals.isEmpty() || !candidates.isEmpty()
                || freeNodesIter.hasNext()) {
            Interval interval;
            while ((interval = intervals.peek()) != null
                    && interval.getLower() <= xScan) {
                candidates.add(intervals.poll());
            }
            interval = candidates.poll();
            if (interval != null) {
                int leftSpace = interval.getUpper() - xScan;
                if (leftSpace < 0)
                    return false;
                else {
                    AuxNode node = interval.getNode();
                    currentXNodes[index] = node;
                    int localId = node.getLocalId();
                    currentXs[localId] = xScan;
                    currentLeftSpace[localId] = leftSpace;
                    xScan++;
                    index++;
                }
            } else {
                if (freeNodesIter.hasNext()) {
                    AuxNode node = freeNodesIter.next();
                    currentXNodes[index] = node;
                    int localId = node.getLocalId();
                    currentXs[localId] = xScan;
                    currentLeftSpace[localId] = Integer.MAX_VALUE;
                    xScan++;
                    index++;
                } else {
                    // Advance
                    xScan = intervals.peek().getLower();
                }
            }
        }

        int[] tmpi = bestXs;
        bestXs = currentXs;
        currentXs = tmpi;
        tmpi = bestLeftSpace;
        bestLeftSpace = currentLeftSpace;
        currentLeftSpace = tmpi;
        AuxNode[] tmpn = bestXNodes;
        bestXNodes = currentXNodes;
        currentXNodes = tmpn;
        return true;
    }

    public void apply() {
        for (int localId = 0; localId < length; localId++) {
            AuxNode node = activeIdNodes[localId];
            node.setX(bestXs[localId]);
        }
        activeLayer.setXNodes(bestXNodes);
    }

    public boolean isCompletelyLocked() {
        return lockedCount == length;
    }

    public void lock(int localId) {
        if (!isLocked[localId]) {
            lockedCount++;
            isLocked[localId] = true;
        }
    }

    public void lockAll() {
        lockedCount = length;
    }

    public ArrayList<AuxNode> getWorstNodes() {
        int worstValue = -1;
        ArrayList<AuxNode> result = new ArrayList<AuxNode>(length);
        for (int localId = 0; localId < length; localId++) {
            if (isLocked[localId]) {
                continue;
            }
            AuxNode node = activeIdNodes[localId];
            int value = node.getWorstEdgeValue(neighborIndex, bestXs[localId]);
            if (value > worstValue) {
                result.clear();
            }
            if (value >= worstValue) {
                result.add(node);
                worstValue = value;
            }
        }
        return result;
    }

    public int[] getLeftSpace() {
        return bestLeftSpace;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
