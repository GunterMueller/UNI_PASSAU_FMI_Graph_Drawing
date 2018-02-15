// =============================================================================
//
//   AbstractCyclicLeveling.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.selection.Selection;

/**
 * @author Gerg� Lov�sz
 * @version $Revision$ $Date$
 */
public abstract class AbstractCyclicLeveling extends
        AbstractCyclicLevelingAlgorithm {

    /** the x coordinate of the center of the graph */
    protected DoubleParameter centerX;

    /** the y coordinate of the center of the graph */
    protected DoubleParameter centerY;

    /** the nodes of each level */
    protected HashSet<Node>[] levels;

    /** the distance between 2 nodes */
    protected IntegerParameter minDistance;

    /** number of levels */
    protected IntegerParameter numberOfLevels;

    /** the source node */
    protected Node sourceNode = null;

    /** maximum number of nodes for a level */
    protected IntegerParameter width;

    /** the selection */
    protected Selection selection;

    /**
     * Assigns a level to each node
     * 
     * @return the duration of the computation
     */
    public abstract long computeLevels();

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
        sLevel = e.getSource().getInteger("level");
        tLevel = e.getTarget().getInteger("level");

        if (tLevel > sLevel) {
            edgeLength = tLevel - sLevel;
        } else {
            edgeLength = levels.length - (sLevel - tLevel);
        }
        return edgeLength;
    }

    protected void levelNodes() {
        computeLevels();
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @SuppressWarnings("unchecked")
    public void execute() {

        levelNodes();

        Draw draw = new Draw(graph, levels);

        draw.computePosition(centerX, centerY, minDistance);
        draw.drawEdges();

        draw.printSumOfEdges(getName());
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
            edgeLength = numberOfLevels.getInteger() - (sLevel - tLevel);
        }

        return edgeLength;
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
