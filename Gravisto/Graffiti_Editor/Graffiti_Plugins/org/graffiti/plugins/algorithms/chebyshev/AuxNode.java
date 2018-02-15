// =============================================================================
//
//   AuxNode.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.tools.benchmark.BenchmarkAttribute;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class AuxNode {
    public static final int OUT_NEIGHBORS = 0;
    public static final int IN_NEIGHBORS = 1;

    private AuxNode[][] neighbors;
    private Node node;
    private boolean isDummy;

    // x-position
    private int x;

    // x-index
    private int index;

    // global id
    private int id;

    // id within layer
    private int localId;

    private int tieBreaker;

    public AuxNode(Node node, Incubator incubator) {
        incubator.register(node, this);

        this.node = node;
        isDummy = node.getBoolean(SugiyamaConstants.PATH_DUMMY);

        neighbors = new AuxNode[2][];
        neighbors[0] = createArray(node.getOutNeighbors(), incubator);
        neighbors[1] = createArray(node.getInNeighbors(), incubator);

        if (node.containsAttribute(BenchmarkAttribute.UID_PATH)) {
            id = node.getInteger(BenchmarkAttribute.UID_PATH);
            tieBreaker = node.getInteger(BenchmarkAttribute.TIEBREAKER_PATH);
        }

        localId = -1;
    }

    private AuxNode[] createArray(Collection<Node> nodes, Incubator incubator) {
        int length = nodes.size();
        AuxNode[] result = new AuxNode[length];
        int i = 0;
        for (Iterator<Node> iter = nodes.iterator(); iter.hasNext(); i++) {
            result[i] = incubator.get(iter.next());
        }
        return result;
    }

    public void finishCreation(int i) {
        x = i;
        index = i;
        if (localId != -1)
            throw new IllegalStateException("Local id set for second time.");
        localId = i;
        // System.out.println(node + ": localId = " + localId);
        IdComparator comparator = IdComparator.get();
        Arrays.sort(neighbors[0], comparator);
        Arrays.sort(neighbors[1], comparator);
    }

    public int getX() {
        return x;
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    public int getLocalId() {
        return localId;
    }

    public int getTieBreaker() {
        return tieBreaker;
    }

    public AuxNode[] getNeighbors(int neighborIndex) {
        return neighbors[neighborIndex];
    }

    public Node getNode() {
        return node;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setId(int id, int tieBreaker) {
        this.id = id;
        this.tieBreaker = tieBreaker;
    }

    public Node apply() {
        node.setDouble(SugiyamaConstants.PATH_XPOS, x);
        return node;
    }

    public int getWorstEdgeValue(int neighborIndex, int x) {
        int worstValue = 0;
        for (AuxNode node : neighbors[neighborIndex]) {
            worstValue = Math.max(worstValue, Math.abs(node.getX() - x));
        }
        return worstValue;
    }

    public int getWorstEdgeValue(int neighborIndex) {
        return getWorstEdgeValue(neighborIndex, x);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
