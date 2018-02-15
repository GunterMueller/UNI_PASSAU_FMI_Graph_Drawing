// =============================================================================
//
//   AuxLayer.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.ArrayList;
import java.util.Arrays;

import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class AuxLayer {
    private boolean isNormalizing;
    private int length;
    private AuxNode[] xNodes;
    private AuxNode[] idNodes;

    public AuxLayer(AuxNode[] nodes, boolean isNormalizing) {
        idNodes = nodes;
        length = nodes.length;
        this.isNormalizing = isNormalizing;
    }

    protected void finishCreation() {
        Arrays.sort(idNodes, IdComparator.get());
        xNodes = idNodes.clone();
        for (int i = 0; i < length; i++) {
            xNodes[i].finishCreation(i);
        }
    }

    public void apply(ArrayList<Node> nodeLayer) {
        for (int i = 0; i < length; i++) {
            nodeLayer.set(i, xNodes[i].apply());
        }
    }

    public void repairDistances() {
        int x = xNodes[0].getX();
        for (int i = 1; i < length; i++) {
            x = Math.max(x + 1, xNodes[i].getX());
            xNodes[i].setX(x);
        }
    }

    public void updateIndices() {
        for (int i = 0; i < length; i++) {
            xNodes[i].setIndex(i);
            if (isNormalizing) {
                xNodes[i].setX(i);
            }
        }
    }

    // Must call updateIndices on changes!
    public AuxNode[] getXNodes() {
        return xNodes;
    }

    public void setXNodes(AuxNode[] xNodes) {
        this.xNodes = xNodes;
        updateIndices();
    }

    public AuxNode[] getIdNodes() {
        return idNodes;
    }

    public int getLength() {
        return length;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
