// =============================================================================
//
//   AuxElement.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.util.EnumMap;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.LabelAttribute;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class Block<NT extends Block<?>> {
    // public static final int IN = 0;
    // public static final int OUT = 1;

    // protected int pi;

    protected Block<?> currentPiPrev;
    protected Block<?> currentPiNext;
    protected int currentPiValue;

    protected Block<?> storedPiPrev;
    protected Block<?> storedPiNext;
    protected int storedPiValue;

    protected Block<?> bestPiPrev;
    protected Block<?> bestPiNext;
    protected int bestPiValue;

    public EnumMap<Direction, NT[]> blocks;
    public EnumMap<Direction, NT[]> sortedBlocks;
    public EnumMap<Direction, int[]> adjacencyIndices;
    public EnumMap<Direction, Integer> blockCount;
    public EnumMap<Direction, Integer> nextSortedBlockIndex;

    protected BlockGraph graph;

    public abstract boolean isNode();

    public abstract boolean isEdge();

    public abstract int getUpperPhi();

    public abstract int getLowerPhi();

    protected abstract void storeIsActive();

    protected abstract void restoreIsActive();

    protected abstract void storeBestIsActive();

    protected abstract void restoreBestIsActive();

    protected abstract String debugToString();

    protected Block(Incubator incubator) {
        blocks = new EnumMap<Direction, NT[]>(Direction.class);
        sortedBlocks = new EnumMap<Direction, NT[]>(Direction.class);
        adjacencyIndices = new EnumMap<Direction, int[]>(Direction.class);
        blockCount = new EnumMap<Direction, Integer>(Direction.class);
        nextSortedBlockIndex = new EnumMap<Direction, Integer>(Direction.class);

        incubator.register(this);
        // this.globalBlockIndex = incubator.register(this);
    }

    public void setBlockGraph(BlockGraph graph) {
        this.graph = graph;
    }

    protected final void storePi() {
        storedPiPrev = currentPiPrev;
        storedPiNext = currentPiNext;
        storedPiValue = currentPiValue;
        bestPiPrev = currentPiPrev;
        bestPiNext = currentPiNext;
        bestPiValue = currentPiValue;
        storeIsActive();
    }

    protected final void restorePi() {
        currentPiPrev = storedPiPrev;
        currentPiNext = storedPiNext;
        currentPiValue = storedPiValue;
        restoreIsActive();
    }

    protected final void storeBestPi() {
        bestPiPrev = currentPiPrev;
        bestPiNext = currentPiNext;
        bestPiValue = currentPiValue;
        storeBestIsActive();
    }

    protected final void restoreBestPi() {
        currentPiPrev = bestPiPrev;
        currentPiNext = bestPiNext;
        currentPiValue = bestPiValue;
        restoreBestIsActive();
    }

    public abstract boolean isActive();

    /**
     * Returns the pi value for the specified level.
     * 
     * @param phi
     *            the level.
     * @return the pi value for the specified level.
     */
    public abstract int getPi(int phi);

    public abstract boolean containsPhi(int phi);

    protected abstract GraphElement getGraphElement();

    protected String getDebugLabel() {
        GraphElement ge = getGraphElement();
        if (ge.containsAttribute("label0")) {
            LabelAttribute la = (LabelAttribute) ge.getAttribute("label0");
            return la.getLabel();
        } else
            return null;
    }

    @Override
    public String toString() {
        String debugLabel = getDebugLabel();
        return debugLabel != null ? debugLabel : super.toString();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
