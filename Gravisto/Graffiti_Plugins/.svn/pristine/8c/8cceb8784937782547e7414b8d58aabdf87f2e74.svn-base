// =============================================================================
//
//   BinaryTreeNode.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.orthTreeDrawingsWinding;

import org.graffiti.graph.Node;

/**
 * @author matzeder
 * @version $Revision$ $Date$
 */
public class BinTreeNode {

    private Node originalNodeOfGraph;

    private BinTreeNode left;

    private BinTreeNode right;

    private int subtreeWidth;

    private int subtreeHeight;

    private int xPos;

    private int yPos;

    private int vk;

    // private OrderedBinaryTreeNode father;

    int numberOfLeaves = -1;

    /**
     * Constructor of the BinaryTreeNode object.
     * 
     * @param originalNode
     */
    public BinTreeNode(Node originalNode) {
        this.originalNodeOfGraph = originalNode;

    }

    /**
     * Returns the numberOfLeaves.
     * 
     * @return the numberOfLeaves.
     */
    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    /**
     * Sets the numberOfLeaves.
     * 
     * @param numberOfLeaves
     *            the numberOfLeaves to set.
     */
    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }

    // /**
    // * Returns the father.
    // *
    // * @return the father.
    // */
    // public OrderedBinaryTreeNode getFather()
    // {
    // return father;
    // }

    /**
     * Returns the left.
     * 
     * @return the left.
     */
    public BinTreeNode getLeft() {
        return left;
    }

    /**
     * Returns the right.
     * 
     * @return the right.
     */
    public BinTreeNode getRight() {
        return right;
    }

    /**
     * Sets the left.
     * 
     * @param left
     *            the left to set.
     */
    public void setLeft(BinTreeNode left) {
        this.left = left;
    }

    /**
     * Sets the right.
     * 
     * @param right
     *            the right to set.
     */
    public void setRight(BinTreeNode right) {
        this.right = right;
    }

    /**
     * Returns the originalNodeOfGraph.
     * 
     * @return the originalNodeOfGraph.
     */
    public Node getOriginalNodeOfGraph() {
        return originalNodeOfGraph;
    }

    /**
     * Returns the xPos.
     * 
     * @return the xPos.
     */
    public int getSubtreeWidth() {
        return subtreeWidth;
    }

    /**
     * Sets the xPos.
     * 
     * @param pos
     *            the xPos to set.
     */
    public void setSubtreeWidth(int pos) {
        subtreeWidth = pos;
    }

    /**
     * Returns the yPos.
     * 
     * @return the yPos.
     */
    public int getSubtreeHeight() {
        return subtreeHeight;
    }

    /**
     * Sets the yPos.
     * 
     * @param pos
     *            the yPos to set.
     */
    public void setSubtreeHeight(int pos) {
        subtreeHeight = pos;
    }

    @Override
    public String toString() {
        return ("Subtree-Dimension: (" + this.getSubtreeWidth() + ", "
                + this.getSubtreeHeight() + ") \t" + "Position: ("
                + this.getXPos() + ", " + this.getYPos() + ")");
    }

    /**
     * Returns the xPos.
     * 
     * @return the xPos.
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Sets the xPos.
     * 
     * @param pos
     *            the xPos to set.
     */
    public void setXPos(int pos) {
        xPos = pos;
    }

    /**
     * Returns the yPos.
     * 
     * @return the yPos.
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * Sets the yPos.
     * 
     * @param pos
     *            the yPos to set.
     */
    public void setYPos(int pos) {
        yPos = pos;
    }

    /**
     * Returns the vk.
     * 
     * @return the vk.
     */
    public int getVk() {
        return vk;
    }

    /**
     * Sets the vk.
     * 
     * @param vk
     *            the vk to set.
     */
    public void setVk(int vk) {
        this.vk = vk;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
