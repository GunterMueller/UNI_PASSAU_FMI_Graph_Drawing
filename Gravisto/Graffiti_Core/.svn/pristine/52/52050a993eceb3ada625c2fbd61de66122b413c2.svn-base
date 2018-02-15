// =============================================================================
//
//   AuxEdge.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.In;
import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.Out;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class EdgeBlock extends Block<NodeBlock> {
    protected Edge edge;
    protected NodeBlock source;
    protected NodeBlock target;
    protected int globalEdgeBlockIndex;
    protected boolean isActive;
    protected int cachedAdjacencyIndex;
    // protected int cachedAdjacencyIndex; // p[(*, upper(this))]
    // p[(lower(this), *)]

    private boolean storedIsActive;
    private boolean bestIsActive;

    public EdgeBlock(Edge edge, NodeBlock source, NodeBlock target,
            Incubator incubator) {
        super(incubator);

        this.edge = edge;
        this.source = source;
        this.target = target;
        isActive = false;

        for (Direction dir : Direction.values()) {
            blockCount.put(dir, 1);
            blocks.put(dir, new NodeBlock[1]);
            sortedBlocks.put(dir, new NodeBlock[1]);
            adjacencyIndices.put(dir, new int[1]);
            nextSortedBlockIndex.put(dir, 0);
        }

        blocks.get(In)[0] = source;
        blocks.get(Out)[0] = target;
        sortedBlocks.get(In)[0] = source;
        sortedBlocks.get(Out)[0] = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isNode() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEdge() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpperPhi() {
        return graph.incLevel(source.phi, Direction.Out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLowerPhi() {
        return graph.incLevel(target.phi, Direction.In);
    }

    /*
     * public final boolean isActive() { return source.phi + 1 < target.phi; }
     */

    @Override
    protected String debugToString() {
        StringBuilder builder = new StringBuilder();
        builder.append(toString()).append("\n- upper phi: ").append(
                getUpperPhi()).append("\n- lower phi: ").append(getLowerPhi())
                .append("\n- pi: ").append(currentPiValue).append("\n- in: ")
                .append(source.toString()).append("\n- out: ").append(
                        target.toString());

        return builder.toString();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public int getPi(int phi) {
        if (phi == source.phi)
            return source.currentPiValue;
        else if (phi == target.phi)
            return target.currentPiValue;
        else if (isActive)
            return currentPiValue;
        else
            throw new IllegalArgumentException();
    }

    @Override
    public boolean containsPhi(int phi) {
        return source.phi < phi && phi < target.phi;
    }

    @Override
    protected GraphElement getGraphElement() {
        return edge;
    }

    @Override
    public String toString() {
        return super.toString() + "[act=" + (isActive ? "T" : "F") + "]";
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * storeBestIsActive()
     */
    @Override
    protected void storeBestIsActive() {
        bestIsActive = isActive;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * restoreBestIsActive()
     */
    @Override
    protected void restoreBestIsActive() {
        isActive = bestIsActive;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * storeBestIsActive()
     */
    @Override
    protected void storeIsActive() {
        storedIsActive = isActive;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * restoreBestIsActive()
     */
    @Override
    protected void restoreIsActive() {
        isActive = storedIsActive;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
