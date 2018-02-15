// =============================================================================
//
//   PentaTreeGrid.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.orthTreeDrawingsWinding;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * @author matzeder
 * @version $Revision$ $Date$
 */
public class OrthogonalUpwardDrawing extends AbstractAlgorithm {
    public static final int TORIGHT = 0;

    public static final int TOLEFT = 1;

    /**
     * If exists and is unique then root is the root of the current given tree.
     */
    private Node root = null;

    /**
     * The constructed binary tree.
     */
    private BinTree bt;

    /**
     * Constructs a new instance.
     */
    public OrthogonalUpwardDrawing() {
        super();
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        this.root = GraphChecker.checkTree(this.graph, 2);
        checkVervollstaendigterBinaryTree(this.root);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        this.graph.getListenerManager().transactionStarted(this);

        removeBends(root);
        // creates a new BinTree with the given graph (which is a Binary Tree)
        bt = new BinTree(root);

        calculateLocalHVDrawing(bt, bt.getRoot(), 0, 0, TORIGHT);

        // changes the CoordinateAttributes for the graph object to be drawn
        bt.setCoordinatesToGraph(bt.getRoot());

        this.graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Determines an hv-drawing of the subtree of <code>node</code> with the
     * integer coordinates, where <code>node</code> is positioned at (0,0).
     * 
     * @param node
     *            The given root node of the subtree.
     */
    public void calculateLocalHVDrawing(BinTree bt, BinTreeNode node, int posX,
            int posY, int dir) {

        // determines for each node the number of leaves in its subtree
        bt.setNumberOfLeavesOfTreeNodes(bt.getRoot());

        // in O(n)
        bt.makeRightHeavy(bt.getRoot());

        // calculates the width of each subtree (in O(n))
        calculateSubtreeSize(bt.getRoot());

        // root at position (0,0)
        bt.getRoot().setXPos(0);
        bt.getRoot().setYPos(0);

        // position each node of the subtree of bt.getRoot()
        positionNodes(bt.getRoot());

    }

    /**
     * Method positions the nodes in the subtree of bNode dependent on the width
     * of each subtree.
     * 
     * @param bNode
     */
    private void positionNodes(BinTreeNode bNode) {

        if (bNode.getLeft() != null) {
            bNode.getRight().setXPos(
                    bNode.getXPos()
                            + (bNode.getSubtreeWidth() - bNode.getRight()
                                    .getSubtreeWidth()));

            bNode.getRight().setYPos(bNode.getYPos());

            bNode.getLeft().setXPos(bNode.getXPos());
            bNode.getLeft().setYPos(bNode.getYPos() + 1);

            positionNodes(bNode.getLeft());
            positionNodes(bNode.getRight());

        }
        // System.out.println(bNode);
    }

    /**
     * Calculates recursively the necessary height and width of the subtree of
     * <code>node</code>.
     * 
     * @param node
     *            The given node of the subtree.
     */
    private void calculateSubtreeSize(BinTreeNode node) {

        // no children, local position = 0
        if (node.getLeft() == null) {
            node.setSubtreeWidth(0);
            node.setSubtreeHeight(0);
            return;
        } else {
            // recursive calculation
            calculateSubtreeSize(node.getLeft());
            calculateSubtreeSize(node.getRight());

            // width and height of left and right child
            int leftWidth = node.getLeft().getSubtreeWidth();
            int rightWidth = node.getRight().getSubtreeWidth();

            int leftHeight = node.getLeft().getSubtreeHeight();
            int rightHeight = node.getRight().getSubtreeHeight();

            node.setSubtreeWidth(leftWidth + rightWidth + 1);
            node.setSubtreeHeight(Math.max(leftHeight + 1, rightHeight));

        }
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Orthogonal Planar Drawing - Upward (H:O(logn), W:O(n))";
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /*
     * removes all bends in the tree under root
     */
    private void removeBends(Node root) {

        Iterator<Edge> edgeIt = root.getAllOutEdges().iterator();

        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                    .getAttribute("graphics");
            if (edgeAttr.getNumberOfBends() > 0) {
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                edgeAttr.setBends(bends);
            }

            Node n = e.getTarget();
            removeBends(n);

        }
    }

    /**
     * Checks whether the subtree of node is a binary tree with 2 children or no
     * children (1 child is not allowed).
     * 
     * @param node
     * @throws PreconditionException
     */
    private void checkVervollstaendigterBinaryTree(Node node)
            throws PreconditionException {
        // if node is a leaf, base case
        if (node.getAllOutEdges().isEmpty())
            return;
        else if (node.getAllOutEdges().size() == 2) {
            LinkedHashSet<Node> l = (LinkedHashSet<Node>) node
                    .getAllOutNeighbors();

            // vorausgesetzt vervollständigter Binärbaum
            Iterator<Node> it = l.iterator();
            Node child1 = it.next();
            Node child2 = it.next();

            checkVervollstaendigterBinaryTree(child1);
            checkVervollstaendigterBinaryTree(child2);

        } else {

            PreconditionException errors = new PreconditionException(
                    "Kein vervollständigter Binärbaum !!!");
            throw errors;
        }
    }
}
