// =============================================================================
//
//   FirstHDWalk.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class FirstHDWalk {
    private int depth;

    // the distance between two points in the hexagonal grid
    private int unit = HexaConstants.unit;

    // the height of an equilateral triangle with the side length unit
    private double unitHeight = (Math.sqrt(3) / 2) * unit;

    StringSelectionParameter compositionMethod;

    /**
     * 
     * @param depth
     *            The depth of the processed tree
     * @param compositionMethod
     *            Span the tree horizontally, vertically or with alternating
     *            methods
     */
    public FirstHDWalk(int depth, StringSelectionParameter compositionMethod) {
        this.depth = depth;
        this.compositionMethod = compositionMethod;
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself
     * 
     * @param hexaNode
     *            The currently processed node
     * @param level
     *            The curent height level in the tree
     */
    public void firstWalkHD(HexaNode hexaNode, int level) {
        double maxX = 0, maxY = 0;

        // if the current node is a leave the node is centered ot (0, 0)
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {

            // tmpHeight and tmpBreadth represent the point with the maximum y/x
            // component (relative to a rhomb in the hexagonal grid, not the
            // orthogonal coordinates) in the subtree und hexaNode
            MinMaxPosition tmpHeight = new MinMaxPosition(0, 0);
            MinMaxPosition tmpBreadth = new MinMaxPosition(0, 0);

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            // the children of hexaNode
            ArrayList<Object> nodes = hexaNode.getChildren();

            // determines, where the corresponding child is moved to
            int i = 0;

            if (hexaNode.getChildren().size() <= 2) {
                i++;
            }

            Iterator<Object> it = nodes.iterator();

            while (it.hasNext()) {

                HexaNode node = (HexaNode) it.next();

                // recursively visits the childs before moving them in relation
                // to hexaNode itself
                firstWalkHD(node, level + 1);

                // move the subtree to it's destined position
                switch (i) {
                // move the middle subtree one unit to the lower right
                case 0:
                    node.setX(0.5 * unit);
                    node.setY(unitHeight);

                    // update the maximum X and Y positions of the subtree
                    // under node after it is moved
                    node.setMaxYP(new MinMaxPosition(node.getMaxYP().getX()
                            + 0.5 * unit, node.getMaxYP().getY() + unitHeight));

                    node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                            + 0.5 * unit, node.getMaxXP().getY() + unitHeight));

                    // calculate the maximum X and Y poisition in this
                    // subtree
                    // if the node of the maximum X-position lies lower than
                    // the hexaNode, the other subtrees have to be moved
                    // farther to avoid overlaps so the y-component has to
                    // be added
                    maxX = node.getMaxXP().getX() - unit
                            + (node.getMaxXP().getY() - unitHeight)
                            * Math.sqrt(3);
                    maxY = node.getMaxYP().getY() - unitHeight;

                    break;

                // move the right subtree one unit farther to the right than
                // the breadth
                // of the middle subtree
                case 1:
                    // how far has the subtree to be moved
                    double tmpDistance = Math.ceil(maxX / unit) * unit;

                    // move at least one unit to the right if node is a leaf
                    if (node.getChildren().size() == 0) {
                        node.setX(unit);
                        node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                                + unit, node.getMaxXP().getY()));
                    }

                    // if node has children it has to be moved at least two
                    // units to the right so that its left child won't
                    // overlap
                    else if (tmpDistance < unit) {
                        node.setX(2 * unit);
                        node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                                + 2 * unit, node.getMaxXP().getY()));
                    }

                    // if a middle subtree of hexaNode exists move it along
                    // the calculated distance
                    else {
                        node.setX(tmpDistance + 2 * unit);
                        node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                                + tmpDistance + 2 * unit, node.getMaxXP()
                                .getY()));
                    }

                    // if the right subtree is higher than the middle one
                    // update maxY
                    double tmp = node.getMaxYP().getY() - unitHeight;
                    if (tmp > maxY) {
                        maxY = tmp;
                    }
                    break;

                // move the lower left subtree one unit farther than the
                // height of the middle (or right if it is higher than the
                // middle) subtree
                case 2:

                    // // how far down has the subtree to be moved
                    double tmpDistanceHeight = Math.ceil(maxY / unitHeight)
                            * unitHeight;

                    // if the node is a leaf one unit to the lower left is
                    // enough
                    if (node.getChildren().size() == 0) {
                        node.setX(-0.5 * unit);
                        node.setY(unitHeight);

                        node.setMaxYP(new MinMaxPosition(node.getMaxYP().getX()
                                - 0.5 * unit, node.getMaxYP().getY()
                                + unitHeight));

                        node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                                - 0.5 * unit, node.getMaxXP().getY()
                                + (unitHeight)));
                    }

                    else if (tmpDistanceHeight < unitHeight) {
                        node.setX(-unit);
                        node.setY(2 * unitHeight);

                        node
                                .setMaxYP(new MinMaxPosition(node.getMaxYP()
                                        .getX()
                                        - unit, node.getMaxYP().getY() + 2
                                        * unitHeight));

                        node
                                .setMaxXP(new MinMaxPosition(node.getMaxXP()
                                        .getX()
                                        - unit, node.getMaxXP().getY() + 2
                                        * unitHeight));
                    }

                    // move it along the calculated distance
                    else {

                        tmpDistance = (tmpDistanceHeight / (Math.sqrt(3) / 2) + 2 * unit);
                        tmpDistanceHeight = (Math.sqrt(3) / 2) * tmpDistance;

                        node.setX(-0.5 * tmpDistance);
                        node.setY(tmpDistanceHeight);

                        node.setMaxYP(new MinMaxPosition(node.getMaxYP().getX()
                                - 0.5 * tmpDistance, node.getMaxYP().getY()
                                + tmpDistanceHeight));
                        node.setMaxXP(new MinMaxPosition(node.getMaxXP().getX()
                                - 0.5 * tmpDistance, node.getMaxXP().getY()
                                + tmpDistanceHeight));
                    }

                    break;

                }

                // Calculate the maximumX and maximumY out of the 3 subtrees

                // maxY can be calculated straightforward, since it corresponds
                // with its orthogonal y value
                if (node.getMaxYP().getY() > tmpHeight.getY()) {
                    tmpHeight = node.getMaxYP();
                }

                // calculate the maxX along the rhomb of the hexaGrid

                // maxX of node lies to the lower right of the current
                // maximum
                if ((tmpBreadth.getX() < node.getMaxXP().getX())
                        && (tmpBreadth.getY() <= node.getMaxXP().getY())) {
                    tmpBreadth = node.getMaxXP();
                }

                // maxX of node lies to the upper right of the current
                // maximum

                else if ((tmpBreadth.getX() < node.getMaxXP().getX())
                        && (node.getMaxXP().getX() - (tmpBreadth.getX()) * 2
                                / unit > (tmpBreadth.getY() - node.getMaxXP()
                                .getY())
                                / unitHeight)) {
                    tmpBreadth = node.getMaxXP();
                }

                // maxX of node lies to the lower left of the current
                // maximum
                else if (((tmpBreadth.getX() - node.getMaxXP().getX()) * 2
                        / unit < (node.getMaxXP().getY() - tmpBreadth.getY())
                        / unitHeight)) {
                    tmpBreadth = node.getMaxXP();
                }

                i++;

            }

            hexaNode.setMaxYP(tmpHeight);
            hexaNode.setMaxXP(tmpBreadth);

        }
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself
     * 
     * @param hexaNode
     *            the currently processed node
     * @param level
     *            the current height in the tree
     */
    public void firstWalkBends(HexaNode hexaNode, int level) {

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);
        } else {

            // double distance = (Math.pow(2.6, (depth - (level + 1)))) * unit;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            if (compositionMethod.getSelectedValue() == "Horizontally") {

                ArrayList<Object> nodes = hexaNode.getChildren();
                // int i = 0;
                Iterator<Object> it = nodes.iterator();
                HexaNode nodeL = null, nodeM = null, nodeR = null;

                while (it.hasNext()) {

                    HexaNode node = (HexaNode) it.next();
                    firstWalkBends(node, level + 1);

                    // calculate the right (=highest), middle and left (=lowest)
                    // subtree of hexaNode
                    if (nodeR == null) {
                        nodeR = node;
                    } else if (node.getMaxYP().getY() > nodeR.getMaxYP().getY()) {
                        if (hexaNode.getChildren().size() <= 2) {
                            nodeL = nodeR;
                        } else {
                            nodeL = nodeM;
                            nodeM = nodeR;
                        }
                        nodeR = node;
                    } else if (nodeM == null
                            && hexaNode.getChildren().size() <= 2) {
                        nodeL = node;
                    } else if (nodeM == null) {
                        nodeM = node;
                    } else if (node.getMaxYP().getY() > nodeM.getMaxYP().getY()) {
                        if (hexaNode.getChildren().size() <= 2) {
                            nodeL = node;
                        } else {
                            nodeL = nodeM;
                            nodeM = node;
                        }
                    } else {
                        nodeL = node;
                    }
                }

                composeHorizontally(hexaNode, nodeR, nodeM, nodeL);
            }

            else if (compositionMethod.getSelectedValue() == "Vertically") {

                HexaNode nodeU = null, nodeM = null, nodeD = null;

                ArrayList<Object> nodes = hexaNode.getChildren();
                // int i = 0;
                Iterator<Object> it = nodes.iterator();

                while (it.hasNext()) {

                    HexaNode node = (HexaNode) it.next();
                    firstWalkBends(node, level + 1);

                    // calculate the upper (=narrowest), middle and lower
                    // (=broadest) subtree of hexaNode
                    if (nodeD == null) {
                        nodeD = node;
                    } else if (breadth(node.getMaxXP()) > breadth(nodeD
                            .getMaxXP())) {
                        if (hexaNode.getChildren().size() <= 2) {
                            nodeU = nodeD;
                        } else {
                            nodeU = nodeM;
                            nodeM = nodeD;
                        }
                        nodeD = node;
                    } else if (nodeM == null) {
                        nodeM = node;
                    } else if (breadth(node.getMaxXP()) > breadth(nodeM
                            .getMaxXP())) {
                        if (hexaNode.getChildren().size() <= 2) {
                            nodeU = node;
                        } else {
                            nodeU = nodeM;
                            nodeM = node;
                        }
                    } else {
                        nodeU = node;
                    }
                }
                composeVertically(hexaNode, nodeU, nodeM, nodeD);
            }

            else if (compositionMethod.getSelectedValue() == "Alternating") {
                HexaNode nodeU = null, nodeM = null, nodeD = null, nodeL = null, nodeR = null;

                ArrayList<Object> nodes = hexaNode.getChildren();
                // int i = 0;
                Iterator<Object> it = nodes.iterator();

                while (it.hasNext()) {

                    HexaNode node = (HexaNode) it.next();
                    firstWalkBends(node, level + 1);

                    /*
                     * depending on the composition method calculate the upper
                     * (=narrowest), middle and lower (=broadest) subtree of
                     * hexaNode for the vertical composiotion or the right
                     * (=highest), middle and left (=lowest) subtree of hexaNode
                     * for the horizontal composition alternate between the two
                     * methods
                     */

                    if ((depth - level) % 2 == 0) {

                        if (nodeD == null) {
                            nodeD = node;
                        } else if (breadth(node.getMaxXP()) > breadth(nodeD
                                .getMaxXP())) {
                            if (hexaNode.getChildren().size() <= 2) {
                                nodeU = nodeD;
                            } else {
                                nodeU = nodeM;
                                nodeM = nodeD;
                            }
                            nodeD = node;
                        } else if (nodeM == null) {
                            nodeM = node;
                        } else if (breadth(node.getMaxXP()) > breadth(nodeM
                                .getMaxXP())) {
                            if (hexaNode.getChildren().size() <= 2) {
                                nodeU = node;
                            } else {
                                nodeU = nodeM;
                                nodeM = node;
                            }
                        } else {
                            nodeU = node;
                        }
                    } else {

                        if (nodeR == null) {
                            nodeR = node;
                        } else if (node.getMaxYP().getY() > nodeR.getMaxYP()
                                .getY()) {
                            if (hexaNode.getChildren().size() <= 2) {
                                nodeL = nodeR;
                            } else {
                                nodeL = nodeM;
                                nodeM = nodeR;
                            }
                            nodeR = node;
                        } else if (nodeM == null
                                && hexaNode.getChildren().size() <= 2) {
                            nodeL = node;
                        } else if (nodeM == null) {
                            nodeM = node;
                        } else if (node.getMaxYP().getY() > nodeM.getMaxYP()
                                .getY()) {
                            if (hexaNode.getChildren().size() <= 2) {
                                nodeL = node;
                            } else {
                                nodeL = nodeM;
                                nodeM = node;
                            }
                        } else {
                            nodeL = node;
                        }
                    }
                }

                if ((depth - level) % 2 == 0) {
                    composeVertically(hexaNode, nodeU, nodeM, nodeD);
                } else {
                    composeHorizontally(hexaNode, nodeR, nodeM, nodeL);
                }
            }

            ArrayList<Object> nodes = hexaNode.getChildren();
            Iterator<Object> it = nodes.iterator();

            MinMaxPosition tmpHeight = new MinMaxPosition(0, 0);
            MinMaxPosition tmpBreadth = new MinMaxPosition(0, 0);

            // update the maxX and maxY of hexaNode with the maximum of its
            // three subtrees
            while (it.hasNext()) {
                HexaNode node = (HexaNode) it.next();

                // maxY can be calculated straightforward
                if (node.getMaxYP().getY() > tmpHeight.getY()) {
                    tmpHeight = node.getMaxYP();
                }

                // maxX of node lies to the lower right of the current
                // maximum
                if ((tmpBreadth.getX() <= node.getMaxXP().getX())
                        && (tmpBreadth.getY() < node.getMaxXP().getY())) {
                    tmpBreadth = node.getMaxXP();
                }

                // maxX of node lies to the upper right of the current
                // maximum
                else if ((tmpBreadth.getX() < node.getMaxXP().getX())
                        && ((node.getMaxXP().getX() - (tmpBreadth.getX()) * 2)
                                / unit > (tmpBreadth.getY() - node.getMaxXP()
                                .getY())
                                / unitHeight)) {
                    tmpBreadth = node.getMaxXP();
                }

                // maxX of node lies to the lower left of the current
                // maximum

                else if (((tmpBreadth.getX() - node.getMaxXP().getX()) * 2
                        / unit < (node.getMaxXP().getY() - tmpBreadth.getY())
                        / unitHeight)) {
                    tmpBreadth = node.getMaxXP();
                }

            }

            hexaNode.setMaxYP(tmpHeight);
            hexaNode.setMaxXP(tmpBreadth);
        }
    }

    /*
     * calculates the positions for the vertical composition for the children
     * nodeU, nodeM and nodeD of root
     */
    private void composeVertically(HexaNode root, HexaNode nodeU,
            HexaNode nodeM, HexaNode nodeD) {

        double maxY = 0;

        double tmpDistance;

        // move the right subtree two units to the right
        if (nodeU != null) {
            // if it is a leaf, one unit is enough
            if (nodeU.getChildren().size() == 0) {
                nodeU.setX(unit);

                nodeU.setMaxXP(new MinMaxPosition(nodeU.getMaxXP().getX()
                        + unit, nodeU.getMaxXP().getY()));
                nodeU.setMaxYP(new MinMaxPosition(nodeU.getMaxYP().getX()
                        + unit, nodeU.getMaxYP().getY()));

            } else {
                // if there is no middle subtree, one unit is enough
                if (root.getChildren().size() <= 2) {
                    nodeU.setX(unit);

                    nodeU.setMaxXP(new MinMaxPosition(nodeU.getMaxXP().getX()
                            + unit, nodeU.getMaxXP().getY()));
                    nodeU.setMaxYP(new MinMaxPosition(nodeU.getMaxYP().getX()
                            + unit, nodeU.getMaxYP().getY()));
                } else {
                    nodeU.setX(2 * unit);

                    nodeU.setMaxXP(new MinMaxPosition(nodeU.getMaxXP().getX()
                            + 2 * unit, nodeU.getMaxXP().getY()));
                    nodeU.setMaxYP(new MinMaxPosition(nodeU.getMaxYP().getX()
                            + 2 * unit, nodeU.getMaxYP().getY()));

                }
            }

            // calculate the maximum Y position in this subtree
            maxY = nodeU.getMaxYP().getY();

        }

        // move the middle subtree one unit farther down than the height
        // of the right subtree
        if (nodeM != null) {
            // the distance nodeM has to be moved, according to the height of
            // nodeU
            tmpDistance = Math.ceil(maxY / unitHeight) * unitHeight;

            // if nodeM is a leaf, one unit to the lower right is enough
            if (nodeM.getChildren().size() == 0) {
                nodeM.setX(0.5 * unit);
                nodeM.setY(unitHeight);

                nodeM.setMaxYP(new MinMaxPosition(nodeM.getMaxYP().getX() + 0.5
                        * unit, nodeM.getMaxYP().getY() + unitHeight));

                nodeM.setMaxXP(new MinMaxPosition(nodeM.getMaxXP().getX() + 0.5
                        * unit, nodeM.getMaxXP().getY() + unitHeight));

            }

            // move nodeM to it's destined position
            else {
                // a bend is only needed if nodeM is more than one unit to the
                // lower right of root
                if (tmpDistance != 0) {
                    // indicates, that a bend has to be added in the second walk
                    root.setBend(nodeM);
                }

                nodeM.setX(-(tmpDistance) / Math.sqrt(3) + (0.5 * unit));
                nodeM.setY(tmpDistance + unitHeight);

                nodeM.setMaxYP(new MinMaxPosition(nodeM.getMaxYP().getX()
                        - (tmpDistance) / Math.sqrt(3) + (0.5 * unit), nodeM
                        .getMaxYP().getY()
                        + tmpDistance + unitHeight));

                nodeM.setMaxXP(new MinMaxPosition(nodeM.getMaxXP().getX()
                        - (tmpDistance) / Math.sqrt(3) + (0.5 * unit), nodeM
                        .getMaxXP().getY()
                        + tmpDistance + unitHeight));
            }

            // calculate the maximum Y position in this subtree
            maxY = nodeM.getMaxYP().getY();
        }

        // move the left subtree one unit farther down than
        // the middle subtree
        if (nodeD != null) {
            // the distance nodeD has to be moved, according to the height of
            // nodeM
            tmpDistance = Math.ceil(maxY / unitHeight) * unitHeight;

            // if nodeD is a leaf one unit to the lower left is enough
            if (nodeD.getChildren().size() == 0) {
                nodeD.setX(-0.5 * unit);
                nodeD.setY(unitHeight);

                nodeD.setMaxYP(new MinMaxPosition(nodeD.getMaxYP().getX()
                        - (0.5 * unit), nodeD.getMaxYP().getY() + unitHeight));

                nodeD.setMaxXP(new MinMaxPosition(nodeD.getMaxXP().getX()
                        - (0.5 * unit), nodeD.getMaxXP().getY() + unitHeight));
            }

            // move the node to it's calculated position
            else {
                nodeD.setX(-(tmpDistance / Math.sqrt(3)) - (0.5 * unit));
                nodeD.setY(tmpDistance + unitHeight);

                nodeD.setMaxYP(new MinMaxPosition(nodeD.getMaxYP().getX()
                        - (tmpDistance / Math.sqrt(3)) - (0.5 * unit), nodeD
                        .getMaxYP().getY()
                        + tmpDistance + unitHeight));

                nodeD.setMaxXP(new MinMaxPosition(nodeD.getMaxXP().getX()
                        - (tmpDistance / Math.sqrt(3)) - (0.5 * unit), nodeD
                        .getMaxXP().getY()
                        + tmpDistance + unitHeight));
            }

        }

    }

    /*
     * calculates the positions for the horizontal composition for the children
     * nodeR, nodeM and nodeL of root
     */
    private void composeHorizontally(HexaNode root, HexaNode nodeR,
            HexaNode nodeM, HexaNode nodeL) {

        double maxX = 0;

        double tmpDistance;

        if (root.getNumberOfChildren() == 0) {
            root.setX(0);
            root.setY(0);

            nodeL.setMaxXP(new MinMaxPosition(0, 0));
            nodeL.setMaxYP(new MinMaxPosition(0, 0));

        }

        // move the left subtree two units to the lower left
        if (nodeL != null) {
            // calculate the maximum X position in this subtree
            // if the node of the maximum position lies lower than the root, the
            // other subtrees have to be moved farther to avoid overlaps so the
            // y-component has to be added
            maxX = nodeL.getMaxXP().getX()
                    + (((nodeL.getMaxXP().getY()) / unitHeight) * unit) / 2;

            // if nodeL is a leaf, one unit is enough
            if (nodeL.getChildren().size() == 0) {
                nodeL.setX(-0.5 * unit);
                nodeL.setY(unitHeight);

                nodeL.setMaxYP(new MinMaxPosition(nodeL.getMaxYP().getX()
                        - (0.5 * unit), nodeL.getMaxYP().getY() + unitHeight));

                nodeL.setMaxXP(new MinMaxPosition(nodeL.getMaxXP().getX()
                        - (0.5 * unit), nodeL.getMaxXP().getY() + unitHeight));
            }

            else {
                // if there is no middle subtree, one unit is enough
                if (root.getChildren().size() <= 2) {
                    nodeL.setX(-0.5 * unit);
                    nodeL.setY(unitHeight);

                    nodeL.setMaxYP(new MinMaxPosition(nodeL.getMaxYP().getX()
                            - (0.5 * unit), nodeL.getMaxYP().getY()
                            + unitHeight));

                    nodeL.setMaxXP(new MinMaxPosition(nodeL.getMaxXP().getX()
                            - (0.5 * unit), nodeL.getMaxXP().getY()
                            + unitHeight));
                } else {
                    nodeL.setX(-unit);
                    nodeL.setY(2 * unitHeight);

                    nodeL.setMaxYP(new MinMaxPosition(nodeL.getMaxYP().getX()
                            - unit, nodeL.getMaxYP().getY() + 2 * unitHeight));

                    nodeL.setMaxXP(new MinMaxPosition(nodeL.getMaxXP().getX()
                            - unit, nodeL.getMaxXP().getY() + 2 * unitHeight));
                }
            }

        }

        // move the middle subtree one unit farther than the breadth
        // of the left subtree
        if (nodeM != null) {

            // determines the new position according to the breadth of nodeL
            tmpDistance = Math.ceil(maxX / unit) * unit;

            // if nodeM is a leaf, one unit to the lower right is enough
            if (nodeM.getChildren().size() == 0) {
                nodeM.setX(0.5 * unit);
                nodeM.setY(unitHeight);

                nodeM.setMaxYP(new MinMaxPosition(nodeM.getMaxYP().getX()
                        + (0.5 * unit), nodeM.getMaxYP().getY() + unitHeight));

                nodeM.setMaxXP(new MinMaxPosition(nodeM.getMaxXP().getX()
                        + (0.5 * unit), nodeM.getMaxXP().getY() + unitHeight));
            } else {
                // a bend is only needed if nodeM is more than one unit to the
                // lower right of root
                if (tmpDistance != 0) {
                    // indicates, that a bend has to be created in second walk
                    root.setBend(nodeM);
                }

                nodeM.setX(tmpDistance + 0.5 * unit);
                nodeM.setY(unitHeight);

                nodeM.setMaxYP(new MinMaxPosition(nodeM.getMaxYP().getX()
                        + tmpDistance + (0.5 * unit), nodeM.getMaxYP().getY()
                        + unitHeight));

                nodeM.setMaxXP(new MinMaxPosition(nodeM.getMaxXP().getX()
                        + tmpDistance + (0.5 * unit), nodeM.getMaxXP().getY()
                        + unitHeight));
            }

            // calculate the maximum X position in this subtree
            // if the node of the maximum position lies lower than the root, the
            // other subtrees have to be moved farther to avoid overlaps so the
            // y-component has to be added
            maxX = nodeM.getMaxXP().getX()
                    + (((nodeM.getMaxXP().getY()) / unitHeight) * unit) / 2;

        }

        // move the right subtree one unit farther to the left than
        // the middle subtree
        if (nodeR != null) {

            tmpDistance = Math.ceil(maxX / unit) * unit;

            if (nodeR.getChildren().size() == 0) {
                nodeR.setX(unit);

                nodeR.setMaxXP(new MinMaxPosition(nodeR.getMaxXP().getX()
                        + unit, nodeR.getMaxXP().getY()));
                nodeR.setMaxYP(new MinMaxPosition(nodeR.getMaxYP().getX()
                        + unit, nodeR.getMaxYP().getY()));
            } else {
                nodeR.setX(tmpDistance + unit);

                nodeR.setMaxXP(new MinMaxPosition(nodeR.getMaxXP().getX()
                        + tmpDistance + unit, nodeR.getMaxXP().getY()));
                nodeR.setMaxYP(new MinMaxPosition(nodeR.getMaxYP().getX()
                        + tmpDistance + unit, nodeR.getMaxYP().getY()));
            }

        }
    }

    /*
     * if the node of the maximum position lies lower than the root, the other
     * subtrees have to be moved farther to avoid overlaps so the y-component
     * has to be added. this method calculates that offset for a MinMaxPosition
     * p
     */
    private double breadth(MinMaxPosition p) {
        if (p == null)
            return -1;
        return p.getX() + Math.sqrt(3) * p.getY();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
