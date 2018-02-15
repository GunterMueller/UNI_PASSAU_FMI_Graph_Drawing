// =============================================================================
//
//   AbstractCyclicLeveling.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.sugiyama;

import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.selection.Selection;

/**
 * @author Gerg� Lov�sz
 * @version $Revision$ $Date$
 */
public abstract class AbstractCyclicLeveling extends
        AbstractCyclicLevelingAlgorithm implements LevellingAlgorithm {

    /** the nodes of each level */
    protected HashSet<Node>[] levels;

    /** the source node */
    protected Node sourceNode = null;

    /** the selection */
    protected Selection selection;

    /** The width of a level */
    protected int width;

    /**
     * Computes the length of all edges
     * 
     * @return length
     */
    public int lengthOfEdges() {
        int sum = 0;
        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            sum += lengthOfEdge(edge);
        }
        return sum;
    }

    /**
     * Computes the length of an Edges
     */
    protected int lengthOfEdge(Edge e) {
        int sLevel, tLevel, edgeLength;
        sLevel = getNodeLevel(e.getSource());
        tLevel = getNodeLevel(e.getTarget());

        if (tLevel > sLevel) {
            edgeLength = tLevel - sLevel;
        } else {
            edgeLength = levels.length - (sLevel - tLevel);
        }
        return edgeLength;
    }

    /**
     * Returns the sourceNode.
     * 
     * @return the sourceNode.
     */
    public Node getSourceNode() {
        return sourceNode;
    }

    /**
     * @return the source's inDegree
     */
    public int getSourceInDegree() {
        if (sourceNode != null)
            return sourceNode.getInDegree();
        else
            return 0;
    }

    /**
     * @return the source's outDegree
     */
    public int getSourceOutDegree() {
        if (sourceNode != null)
            return sourceNode.getOutDegree();
        else
            return 0;
    }

    /**
     * computes the length of an edge between sLevel and tLevel
     * 
     * @param sLevel
     *            tLevel
     * @return the length of an edge (sLevel, tLevel)
     */
    protected int length(int sLevel, int tLevel) {
        int edgeLength = 0;
        if (tLevel > sLevel) {
            edgeLength = tLevel - sLevel;
        } else {
            edgeLength = numberOfLevels - (sLevel - tLevel);
        }

        return edgeLength;
    }

    /**
     * Sets the sourceNode.
     * 
     * @param sourceNode
     *            the sourceNode to set.
     */
    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
