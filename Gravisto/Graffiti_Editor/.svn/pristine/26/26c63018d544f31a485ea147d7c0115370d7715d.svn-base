// =============================================================================
//
//   SuperNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.util.ArrayList;

import org.graffiti.graph.Node;

/**
 * Constructs the SuperNode, which uses the Node Class from Gravisto
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */

public class SuperNode {
    /**
     * the original node in the graph.
     */
    private Node node;
    /**
     * the degree of the node.
     */
    private int degree;
    /**
     * the number of Fill-in Edges for building the simplicial node
     */
    private int lackOfEdgeSim;
    /**
     * the SuperNode is finished
     */
    private boolean finish;
    /**
     * the neighbors of the node.
     */
    private ArrayList<SuperNode> neighbors;
    /**
     * the number of Fill-in excluding one neighbor edges for building the
     * simplicial node
     */
    private int lackOfEdgeFaSim;
    /**
     * the number of nodes in a treeWidthNode
     */
    private int NodeSizeInClique;

    /**
     * Constructs a new instance.
     * 
     * @param node
     *            is a node from the original graph.
     */
    public SuperNode(Node node) {
        this.node = node;
        this.lackOfEdgeSim = 0;
        this.finish = false;
        this.neighbors = new ArrayList<SuperNode>();
        this.degree = 0;
        this.lackOfEdgeFaSim = 0;
        this.NodeSizeInClique = 0;
    }

    /**
     * Returns the degree.
     * 
     * @return the degree.
     */

    public int getDegree() {
        return degree;
    }

    /**
     * Sets the degree.
     * 
     * @param degree
     *            the degree to set.
     */
    public void setDegree(int degree) {
        this.degree = degree;
    }

    /**
     * Returns the lackOfEdge.
     * 
     * @return the lackOfEdge.
     */
    public int getLackOfEdgeSim() {
        return lackOfEdgeSim;
    }

    /**
     * Sets the lackOfEdge.
     * 
     * @param lackOfEdgeSim
     *            the lackOfEdge to set.
     */
    public void setLackOfEdgeSim(int lackOfEdgeSim) {
        this.lackOfEdgeSim = lackOfEdgeSim;
    }

    /**
     * Returns the node.
     * 
     * @return the node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Sets the node.
     * 
     * @param node
     *            the node to set.
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Returns whether node is finished.
     * 
     * @return whether node is finished
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * Marks the node as finished.
     * 
     * @param finish
     *            whether node is finished
     */
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    /**
     * Returns the neighbors.
     * 
     * @return the neighbors.
     */
    public ArrayList<SuperNode> getNeighbors() {
        return neighbors;
    }

    /**
     * Sets the neighbors.
     * 
     * @param neighbors
     *            the neighbors to be set.
     */
    public void setNeighbors(ArrayList<SuperNode> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Returns the lackOfEdgeFaSim.
     * 
     * @return the lackOfEdgeFaSim.
     */
    public int getLackOfEdgeFaSim() {
        return lackOfEdgeFaSim;
    }

    /**
     * Sets the lackOfEdgeFaSim.
     * 
     * @param lackOfEdgeFaSim
     *            the lackOfEdgeFaSim to be set.
     */
    public void setLackOfEdgeFaSim(int lackOfEdgeFaSim) {
        this.lackOfEdgeFaSim = lackOfEdgeFaSim;
    }

    /**
     * Returns the nodeSizeInClique.
     * 
     * @return the nodeSizeInClique.
     */
    public int getNodeSizeInClique() {
        return NodeSizeInClique;
    }

    /**
     * Sets the nodeSizeInClique.
     * 
     * @param nodeSizeInClique
     *            the nodeSizeInClique to be set.
     */
    public void setNodeSizeInClique(int nodeSizeInClique) {
        NodeSizeInClique = nodeSizeInClique;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
