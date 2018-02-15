// =============================================================================
//
//   AuxNode.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.In;
import static org.graffiti.plugins.algorithms.sugiyama.gridsifting.Direction.Out;

import java.util.Collection;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class NodeBlock extends Block<EdgeBlock> {
    private Node node;
    protected int phi;
    protected boolean wantsUpwards;

    protected int globalNodeBlockIndex;

    public NodeBlock(Node node, Incubator incubator) {
        super(incubator);

        globalNodeBlockIndex = incubator.register(node, this);
        this.node = node;
        Collection<Edge> outEdges = node.getAllOutEdges();

        int count = outEdges.size();
        blockCount.put(Out, count);
        blocks.put(Out, new EdgeBlock[count]);
        sortedBlocks.put(Out, new EdgeBlock[count]);
        adjacencyIndices.put(Out, new int[count]);

        nextSortedBlockIndex.put(In, 0);
        nextSortedBlockIndex.put(Out, 0);

        int outIndex = 0;

        for (Edge edge : outEdges) {
            Node target = edge.getTarget();
            NodeBlock targetBlock = incubator.get(target);
            EdgeBlock outBlock = new EdgeBlock(edge, this, targetBlock,
                    incubator);
            incubator
                    .addInEdgeBlock(targetBlock.globalNodeBlockIndex, outBlock);

            blocks.get(Out)[outIndex] = outBlock;
            outIndex++;
        }
    }

    public void setInBlocks(EdgeBlock[] inBlocks) {
        int count = inBlocks.length;
        blockCount.put(In, count);
        blocks.put(In, inBlocks);
        sortedBlocks.put(In, new EdgeBlock[count]);
        adjacencyIndices.put(In, new int[count]);
        wantsUpwards = count <= blockCount.get(Out);
    }

    public int addSortedBlock(Direction inOut, EdgeBlock block) {
        int nsbIndex = nextSortedBlockIndex.get(inOut);
        sortedBlocks.get(inOut)[nsbIndex] = block;
        int result = nsbIndex;
        nsbIndex++;
        nextSortedBlockIndex.put(inOut, nsbIndex == blockCount.get(inOut) ? 0
                : nsbIndex);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNode() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEdge() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpperPhi() {
        return phi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLowerPhi() {
        return phi;
    }

    public int calculateMinPossibleLevel() {
        int minLevel = 1;

        for (int i = 0; i < blockCount.get(In); i++) {
            int level = blocks.get(In)[i].source.phi + 1;

            minLevel = level > minLevel ? level : minLevel;
        }

        return minLevel;
    }

    public int calculateMaxPossibleLevel(int levelCount) {
        int maxLevel = levelCount;

        for (int i = 0; i < blockCount.get(Out); i++) {
            int level = blocks.get(Out)[i].target.phi - 1;

            maxLevel = level < maxLevel ? level : maxLevel;
        }

        return maxLevel;
    }

    public void exportLevels() {
        node.setInteger(SugiyamaConstants.PATH_LEVEL, phi / 2 - 1);
    }

    @Override
    protected String debugToString() {
        StringBuilder builder = new StringBuilder();
        builder.append(toString()).append("\n- phi: ").append(phi).append(
                "\n- pi: ").append(currentPiValue).append("\n- in: ");

        for (int i = 0; i < blockCount.get(In); i++) {
            builder.append(blocks.get(In)[i]).append("; ");
        }

        builder.append("\n- out: ");

        for (int i = 0; i < blockCount.get(Out); i++) {
            builder.append(blocks.get(Out)[i]).append("; ");
        }

        return builder.toString();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public int getPi(int phi) {
        return currentPiValue;
    }

    @Override
    public boolean containsPhi(int phi) {
        return this.phi == phi;
    }

    @Override
    protected GraphElement getGraphElement() {
        return node;
    }

    @Override
    public String toString() {
        return super.toString() + "[phi=" + phi + "]";
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * storeBestIsActive()
     */
    @Override
    protected final void storeIsActive() {
        // Nothing to do.
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * restoreBestIsActive()
     */
    @Override
    protected final void restoreIsActive() {
        // Nothing to do.
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * storeBestIsActive()
     */
    @Override
    protected final void storeBestIsActive() {
        // Nothing to do.
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.universalsifting.Block#
     * restoreBestIsActive()
     */
    @Override
    protected final void restoreBestIsActive() {
        // Nothing to do.
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
