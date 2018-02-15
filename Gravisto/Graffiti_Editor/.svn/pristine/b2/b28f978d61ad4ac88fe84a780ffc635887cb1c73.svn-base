// =============================================================================
//
//   BinaryTree.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.orthTreeDrawingsWinding;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * @author matzeder
 * @version $Revision$ $Date$
 */
public class BinTree {
    private BinTreeNode r;

    public BinTree(Node root) {

        this.r = new BinTreeNode(root);
        LinkedHashSet<Node> l = (LinkedHashSet<Node>) root.getAllOutNeighbors();

        // vorausgesetzt vervollständigter Binärbaum
        Iterator<Node> it = l.iterator();
        Node child1 = it.next();
        Node child2 = it.next();

        BinTreeNode bn1 = new BinTreeNode(child1);
        BinTreeNode bn2 = new BinTreeNode(child2);

        r.setLeft(bn1);
        r.setRight(bn2);

        createBinTree(bn1);
        createBinTree(bn2);

        System.out.println(this);

    }

    public void setCoordinatesToGraph(BinTreeNode n) {

        CoordinateAttribute ca = ((NodeGraphicAttribute) n
                .getOriginalNodeOfGraph().getAttribute("graphics"))
                .getCoordinate();

        ca.setX(n.getXPos() * 30);
        ca.setY(n.getYPos() * 30);

        if (n.getLeft() != null) {
            setCoordinatesToGraph(n.getLeft());
            setCoordinatesToGraph(n.getRight());
        }
    }

    private void createBinTree(BinTreeNode bNode) {

        if (!bNode.getOriginalNodeOfGraph().getAllOutNeighbors().isEmpty()) {

            LinkedHashSet<Node> l = (LinkedHashSet<Node>) bNode
                    .getOriginalNodeOfGraph().getAllOutNeighbors();

            // vorausgesetzt vervollständigter Binärbaum
            Iterator<Node> it = l.iterator();
            Node child1 = it.next();
            Node child2 = it.next();

            BinTreeNode bn1 = new BinTreeNode(child1);
            BinTreeNode bn2 = new BinTreeNode(child2);

            bNode.setLeft(bn1);
            bNode.setRight(bn2);

            createBinTree(bn1);
            createBinTree(bn2);

        }
    }

    /**
     * Sets the values for the number of leaves in the subtree of
     * <code>node</code>. We assume to have a tree where each node has 0 or 2
     * children.
     * 
     * @param bNode
     *            The given node of the subtree.
     */
    public int setNumberOfLeavesOfTreeNodes(BinTreeNode bNode) {

        // if node is a leaf, base case (OK, because of the assumption above)
        if (bNode.getLeft() == null) {
            bNode.setNumberOfLeaves(1);
            return 1;
        } else {

            setNumberOfLeavesOfTreeNodes(bNode.getLeft());
            setNumberOfLeavesOfTreeNodes(bNode.getRight());

            bNode.setNumberOfLeaves(bNode.getLeft().getNumberOfLeaves()
                    + bNode.getRight().getNumberOfLeaves());

            return bNode.getNumberOfLeaves();

        }
    }

    /**
     * Returns the root.
     * 
     * @return the root.
     */
    public BinTreeNode getRoot() {
        return this.r;
    }

    /**
     * Method changes left child with right child, if left child has more leaves
     * in its subtree than the right child (after applying this method, the
     * binary tree is a right heavy binary tree).
     * 
     * @param node
     */
    public void makeRightHeavy(BinTreeNode node) {
        if (node.getLeft() != null) {

            if (node.getLeft().getNumberOfLeaves() > node.getRight()
                    .getNumberOfLeaves()) {
                // not right heavy
                BinTreeNode rightSon = node.getRight();
                node.setRight(node.getLeft());
                node.setLeft(rightSon);
            }

            if (node.getLeft() != null) {

                makeRightHeavy(node.getLeft());
                makeRightHeavy(node.getRight());

            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
