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
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * @author matzeder
 * @version $Revision$ $Date$
 */
public class OrthogonalUpwardDrawingWinding extends AbstractAlgorithm {

    public static final int TORIGHT = 0;

    public static final int TOLEFT = 1;

    /**
     * If exists and is unique then root is the root of the current given tree.
     */

    private Node root = null;

    /**
     * The \"A\" in the algorithm of Chan et al.
     */
    private int areaResolution;

    /**
     * The constructed binary tree.
     */
    private BinTree bt;

    /**
     * Constructs a new instance.
     */
    public OrthogonalUpwardDrawingWinding() {
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

        // creates the BinTree with the given graph (which is a Binary Tree)
        bt = new BinTree(root);

        // determines for each node in the subtree the number of leaves
        bt.setNumberOfLeavesOfTreeNodes(bt.getRoot());

        // in O(n)
        bt.makeRightHeavy(bt.getRoot());

        // this recursive method determines the integer coordinates of the tree
        // nodes according to the winding paradigm of Chan et al.
        recursiveWindingCalculation(bt.getRoot(), 0, 0, TORIGHT);

        // changes the CoordinateAttributes for the graph object to be drawn
        bt.setCoordinatesToGraph(bt.getRoot());

        this.graph.getListenerManager().transactionFinished(this);
    }

    private void recursiveWindingCalculation(BinTreeNode node, int xPos,
            int yPos, int dir) {

        if (node.getNumberOfLeaves() > 1) {

            // separate the tree according to Chan et al.'s separation criteria
            // in
            // the subtree of <code>node</code>
            BinTreeNode nodeVk = findVk(node);

            node.setXPos(xPos);
            node.setYPos(yPos);

            // v_k = v_1
            if (nodeVk.getVk() == 1) {

                // draw T' recursively
                recursiveWindingCalculation(nodeVk.getLeft(), xPos + 1, yPos,
                        getOtherDirection(dir));

                // draw T'' recursively
                recursiveWindingCalculation(nodeVk.getRight(), xPos, yPos
                        + nodeVk.getLeft().getSubtreeHeight() + 1,
                        getOtherDirection(dir));
            }
            // v_k = v_2
            else if (nodeVk.getVk() == 2) {
                // draw subtree of v_1 with height O(logn) and width O(n)
                OrthogonalUpwardDrawing o = new OrthogonalUpwardDrawing();
                o.calculateLocalHVDrawing(bt, node.getLeft(), 1, 0, dir);

                // position the node v_2 below v_1
                nodeVk.getLeft().setXPos(nodeVk.getXPos());
                nodeVk.getLeft().setYPos(
                        nodeVk.getYPos() + nodeVk.getSubtreeHeight() + 1);

                // draw T' recursively
                recursiveWindingCalculation(nodeVk.getLeft().getLeft(), nodeVk
                        .getLeft().getXPos() + 1, nodeVk.getLeft().getYPos(),
                        dir);
                // draw T'' recursively
                recursiveWindingCalculation(nodeVk.getLeft().getRight(), nodeVk
                        .getLeft().getXPos(), nodeVk.getLeft().getYPos()
                        + nodeVk.getLeft().getSubtreeHeight(), dir);

            }
            // v_k mit k > 2
            else {
                // the width of the subtrees of T_1, T_2, ...
                int width = 0;
                int height = 0;
                BinTreeNode currNode = node;
                // for all subtrees T_1, ..., T_{k-2}
                while (currNode.getVk() < nodeVk.getVk() - 1) {
                    OrthogonalUpwardDrawing o = new OrthogonalUpwardDrawing();
                    o.calculateLocalHVDrawing(bt, node.getLeft(), (currNode
                            .getSubtreeWidth()
                            + width + 1), currNode.getYPos(), dir);
                    width = currNode.getSubtreeWidth() + width + 1;

                    // der nächste knoten von v_i ist v_{i+1}
                    currNode = currNode.getRight();

                    if (currNode.getSubtreeHeight() > height) {
                        height = currNode.getSubtreeHeight();
                    }
                }

                // v_{k-1}
                BinTreeNode nodeVKMinusOne = currNode.getRight();

                nodeVKMinusOne.setXPos(node.getXPos() + width + 1);
                nodeVKMinusOne.setYPos(node.getYPos());

                // TODO: noch falsch gezeichnet gehört 90° gedreht und dann
                // gespiegelt
                OrthogonalUpwardDrawing o = new OrthogonalUpwardDrawing();
                o.calculateLocalHVDrawing(bt, nodeVKMinusOne.getLeft(),
                        nodeVKMinusOne.getXPos() + 1, currNode.getYPos(), dir);

                nodeVk.setXPos(nodeVKMinusOne.getXPos());
                nodeVk.setYPos(height + 1);

                recursiveWindingCalculation(nodeVk.getLeft(), -1, 0,
                        getOtherDirection(dir));

                recursiveWindingCalculation(nodeVk.getRight(), 0, nodeVk
                        .getLeft().getSubtreeHeight() + 1,
                        getOtherDirection(dir));

            }
        }
    }

    /**
     * Nummeriert die Knoten von v_1, v_2, ..., v_k, v_{k+1} durch und gibt den
     * Knoten v_k zurück.
     * 
     * @param node
     */
    private BinTreeNode findVk(BinTreeNode node) {

        BinTreeNode currNode = node;
        currNode.setVk(1);

        System.out.println(areaResolution);

        int i = 2;
        // gehe solange rechts bis bedingung nicht mehr erfüllt ist
        while (currNode.getRight().getNumberOfLeaves() > areaResolution) {
            System.out.println("i: " + i);
            currNode.getRight().setVk(i);
            currNode = currNode.getRight();
            i++;
        }
        return currNode;
    }

    // /**
    // * Determines an hv-drawing of the subtree of <code>node</code> with the
    // * integer coordinates, where <code>node</code> is positioned at (0,0).
    // *
    // * @param node The given root node of the subtree.
    // */
    // private void calculateLocalHVDrawing(Node node)
    // {
    // determineNumberOfLeaves(node);
    // calculateSubtreeSize(node);
    //
    // CoordinateAttribute caRoot = ((NodeGraphicAttribute)node
    // .getAttribute("graphics")).getCoordinate();
    // caRoot.setX(0);
    // caRoot.setY(0);
    // positionNodes(node);
    //
    // }

    // private void positionNodes(Node nodeOfSubtree)
    // {
    //
    // // node hat genau 2 Söhne, deren Positionen schon berechnet wurden
    // LinkedHashSet<Node> l = (LinkedHashSet<Node>)nodeOfSubtree
    // .getAllOutNeighbors();
    //
    // if (!l.isEmpty())
    // {
    //
    // // vorausgesetzt vervollständigter Binärbaum
    // Iterator<Node> it = l.iterator();
    // Node child1 = it.next();
    // Node child2 = it.next();
    //
    // Integer leaves1 = (Integer)((IntegerAttribute)child1
    // .getAttribute("leavesInSubtree")).getValue();
    // Integer leaves2 = (Integer)((IntegerAttribute)child2
    // .getAttribute("leavesInSubtree")).getValue();
    //
    // Node right;
    // Node left;
    // if (leaves1.compareTo(leaves2) <= 0)
    // {
    // // more leaves in subtree of child2
    // right = child2;
    // left = child1;
    // }
    // else
    // {
    // left = child2;
    // right = child1;
    // }
    //
    // CoordinateAttribute caNodeOfSubtree =
    // ((NodeGraphicAttribute)nodeOfSubtree
    // .getAttribute("graphics")).getCoordinate();
    //
    // CoordinateAttribute caLeft = ((NodeGraphicAttribute)left
    // .getAttribute("graphics")).getCoordinate();
    //
    // CoordinateAttribute caRight = ((NodeGraphicAttribute)right
    // .getAttribute("graphics")).getCoordinate();
    //
    // caRight
    // .setX(caNodeOfSubtree.getX()
    // + ((getLocalXPosition(nodeOfSubtree) - getLocalXPosition(right)) * 30));
    //
    // caRight.setY(caNodeOfSubtree.getY());
    //
    // caLeft.setX(caNodeOfSubtree.getX());
    // caLeft.setY(caNodeOfSubtree.getY() + 30);
    //
    // positionNodes(left);
    // positionNodes(right);
    //
    // }
    // }

    // /**
    // * Calculates recursively the necessary height and width of the subtree of
    // * <code>node</code>.
    // *
    // * @param node The given node of the subtree.
    // */
    // private void calculateSubtreeSize(Node node)
    // {
    //
    // // reset x-position
    // if (node.getAttributes().containsAttribute("localXPosition"))
    // {
    // node.getAttributes().remove("localXPosition");
    // }
    // // reset y-position
    // if (node.getAttributes().containsAttribute("localYPosition"))
    // {
    // node.getAttributes().remove("localYPosition");
    // }
    //
    // // no children, local position = 0
    // if (node.getAllOutNeighbors().isEmpty())
    // {
    // IntegerAttribute iaX = new IntegerAttribute("localXPosition",
    // new Integer(0));
    // node.getAttributes().add(iaX);
    //
    // IntegerAttribute iaY = new IntegerAttribute("localYPosition",
    // new Integer(0));
    // node.getAttributes().add(iaY);
    // return;
    // }
    // else
    // {
    // // node has exactly 2 children with calculated width and height
    // LinkedHashSet<Node> l = (LinkedHashSet<Node>)node
    // .getAllOutNeighbors();
    //
    // Iterator<Node> it = l.iterator();
    // Node child1 = it.next();
    // Node child2 = it.next();
    //
    // Integer leaves1 = (Integer)((IntegerAttribute)child1
    // .getAttribute("leavesInSubtree")).getValue();
    // Integer leaves2 = (Integer)((IntegerAttribute)child2
    // .getAttribute("leavesInSubtree")).getValue();
    //
    // Node right;
    // Node left;
    // if (leaves1.compareTo(leaves2) <= 0)
    // {
    // // more leaves or equal in subtree of child2
    // right = child2;
    // left = child1;
    // }
    // else
    // {
    // left = child2;
    // right = child1;
    // }
    //
    // calculateSubtreeSize(left);
    // calculateSubtreeSize(right);
    //
    // // widths of left and right child
    // Integer leftWidth = (Integer)((IntegerAttribute)left
    // .getAttribute("localXPosition")).getValue();
    // Integer rightWidth = (Integer)((IntegerAttribute)right
    // .getAttribute("localXPosition")).getValue();
    //
    // // height of the left child
    // Integer leftHeight = (Integer)((IntegerAttribute)left
    // .getAttribute("localYPosition")).getValue();
    //
    // // the width of the subtree of node
    // IntegerAttribute widthAttr = new IntegerAttribute("localXPosition",
    // leftWidth + rightWidth + 1);
    // node.getAttributes().add(widthAttr);
    //
    // // the height of the subtree of node
    // IntegerAttribute heightAttr = new IntegerAttribute(
    // "localYPosition", leftHeight + 1);
    // node.getAttributes().add(heightAttr);
    // }
    // }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Orthogonal Planar Drawing - Upward & Winding";
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {

        areaResolution = ((IntegerParameter) params[0]).getInteger().intValue();

        this.parameters = params;
    }

    /**
     * Returns an array with the parameters of this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter areaResolutionParam = new IntegerParameter(
                areaResolution, "The A in Chans algorithm",
                "Sets the A in Chans algorithm", 1, 100, 1, 1000);

        return new Parameter[] { areaResolutionParam };
    }

    // /**
    // * Recursive method determines the number of leaves in the subtree of node
    // * and saves the value as an attribute to the given node.
    // *
    // * @param node The given node.
    // * @return Number of the leaves in the subtree of node.
    // */
    // private Integer determineNumberOfLeaves(Node node)
    // {
    // // if node is a leaf, base case
    // if (node.getAllOutEdges().isEmpty())
    // {
    // Integer numberOfLeaves = new Integer(1);
    //
    // setLeaves(node, numberOfLeaves);
    // return 1;
    // }
    // else
    // {
    //
    // LinkedHashSet<Node> l = (LinkedHashSet<Node>)node
    // .getAllOutNeighbors();
    //
    // // vorausgesetzt vervollständigter Binärbaum
    // Iterator<Node> it = l.iterator();
    // Node child1 = it.next();
    // Node child2 = it.next();
    //
    // Integer numberOfLeaves = determineNumberOfLeaves(child1)
    // + determineNumberOfLeaves(child2);
    //
    // setLeaves(node, numberOfLeaves);
    //
    // return (numberOfLeaves);
    // }
    //
    // }

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

    // /**
    // * Maps each node the number of leaves in its subtree and saves as an
    // * attribute "leavesInSubtree".
    // *
    // * @param node
    // * @param leaves
    // */
    // private void setLeaves(Node node, Integer leaves)
    // {
    // if (node.getAttributes().containsAttribute("leavesInSubtree"))
    // {
    // node.getAttributes().remove("leavesInSubtree");
    // }
    //
    // IntegerAttribute la = new IntegerAttribute("leavesInSubtree", leaves);
    // node.getAttributes().add(la);
    //
    // }

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

    // /**
    // * The value of the local x position
    // *
    // * @param node
    // * @return
    // */
    // private Integer getLocalXPosition(Node node)
    // {
    //
    // return (Integer)((IntegerAttribute)node.getAttribute("localXPosition"))
    // .getValue();
    //
    // }

    // /**
    // * The value of the local y position
    // *
    // * @param node
    // * @return
    // */
    // private Integer getLocalYPosition(Node node)
    // {
    //
    // return (Integer)((IntegerAttribute)node.getAttribute("localYPosition"))
    // .getValue();
    //
    // }

    private int getOtherDirection(int dir) {
        if (dir == TORIGHT)
            return TOLEFT;
        else
            return TORIGHT;
    }

}
