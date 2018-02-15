// =============================================================================
//
//   FirstPentaWalk.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class FirstPentaWalk {
    // the depth of the processed tree
    private int depth;

    // the distance between two points in the hexagonal grid
    private int unit = HexaConstants.unit;

    // the height of an equilateral triangle with the side length unit
    // private double unitHeight = (Math.sqrt(3) / 2) * unit;

    public FirstPentaWalk(int depth) {
        this.depth = depth;
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself for the naive algorithm to draw a
     * pentatree
     * 
     * @param hexaNode
     *            root node of the current subtree
     * @param position
     *            indicates the position of the subtree in relation to its
     *            father (left, right, upper right, lower right, lower left or
     *            upper left)
     * @param level
     *            current height level
     */
    public void firstPentaWalk(HexaNode hexaNode, int position, int level) {
        hexaNode.setLevel(level);

        // lengthHeight is the height of one of the triangles on the grid with
        // edge
        // length 'lenth'
        double length = 0, lengthHeight = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {
            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = 0;
            Iterator<Object> it = nodes.iterator();

            // in the naive algorithm in each step the edge length is tripled,
            // so that a complete tree of degreee 5 can be drawn without
            // overlaps
            length = (Math.pow(3, (depth - (level + 1)))) * unit;

            lengthHeight = (Math.sqrt(3) / 2) * length;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            while (it.hasNext()) {
                // If the position of the node would block the connection of its
                // father to it's grandfather then use another position
                if (i == (5 - position)) {
                    i++;
                }

                HexaNode node = (HexaNode) it.next();
                firstPentaWalk(node, i, level + 1);

                // move the subtree to it's destined position starting from the
                // position of it's father
                switch (i) {
                // left
                case 0:
                    node.setX(-length);
                    break;
                // upper left
                case 1:
                    node.setX(-0.5 * length);
                    node.setY(-lengthHeight);
                    break;
                // upper right
                case 2:
                    node.setX(0.5 * length);
                    node.setY(-lengthHeight);
                    break;
                // lower left
                case 3:
                    node.setX(-0.5 * length);
                    node.setY(lengthHeight);
                    break;
                // lower right
                case 4:
                    node.setX(0.5 * length);
                    node.setY(lengthHeight);
                    break;
                // right
                case 5:
                    node.setX(length);
                    break;

                }

                i++;

            }

        }
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself for the modified algorithm to draw a
     * pentatree
     * 
     * @param hexaNode
     * @param position
     *            The alignment of the subtree
     * @param level
     *            The level of hexaNode, counting from the root
     */
    public void firstPentaWalkMod(HexaNode hexaNode, int position, int level) {
        // lengthHeight is the height of an equilateral triangle with the side
        // length length
        double length = 0, lengthHeight = 0;

        // length2 is a shortened to save space
        double length2 = 0, length2Height = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {

            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = 0;
            Iterator<Object> it = nodes.iterator();

            int neighbours = nodes.size();

            // the naive length which is tripled in every step
            length = (Math.pow(3, (depth - (level + 1)))) * unit;
            lengthHeight = (Math.sqrt(3) / 2) * length;

            // length2 sets the moved nodes to the grid point next to the middle
            // of length
            length2 = (Math.ceil(length / (2 * unit))) * unit;
            length2Height = (Math.sqrt(3) / 2) * length2;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            while (it.hasNext()) {
                // if hexaNode has three sons at most spread them out
                if (neighbours <= 3 && i == 1) {
                    i = 4;
                }

                // If the position of the node blocks the connection of its
                // father to it's grandfather then use another position
                if (i == (5 - position)) {
                    i++;
                }

                HexaNode node = (HexaNode) it.next();
                firstPentaWalkMod(node, i, level + 1);

                // move the subtree to it's destined position starting
                // from the position of it's father; for the 2 subtrees next
                // to the incoming edge and the opposite subtree use the
                // shortened length
                switch (i) {
                // left
                case 0:
                    node.setX(-length2);
                    break;
                // upper left
                case 1:
                    if (position == 5) {
                        node.setX(-0.5 * length2);
                        node.setY(-length2Height);
                    } else {
                        node.setX(-0.5 * length);
                        node.setY(-lengthHeight);
                    }

                    break;
                // upper right
                case 2:
                    if (position == 0) {
                        node.setX(0.5 * length2);
                        node.setY(-length2Height);
                    } else {
                        node.setX(0.5 * length);
                        node.setY(-lengthHeight);
                    }

                    break;
                // lower left
                case 3:
                    if (position == 5) {
                        node.setX(-0.5 * length2);
                        node.setY(length2Height);
                    } else {
                        node.setX(-0.5 * length);
                        node.setY(lengthHeight);
                    }

                    break;
                // lower right
                case 4:
                    if (position == 0) {
                        node.setX(0.5 * length2);
                        node.setY(length2Height);
                    } else {
                        node.setX(0.5 * length);
                        node.setY(lengthHeight);
                    }

                    break;
                // right
                case 5:

                    node.setX(length2);

                    break;

                }

                if (neighbours <= 3 && i == 4) {
                    i = 1;
                }

                i++;

            }

        }
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself for the modified algorithm to draw a
     * pentatree
     * 
     * @param hexaNode
     * @param position
     * @param level
     */
    public void firstPentaWalkMod2(HexaNode hexaNode, int position, int level) {
        // lengthHeight is the height of an equilateral triangle with the side
        // length length
        double length = 0, lengthHeight = 0;

        // length2 is a shortened to save space
        double length2 = 0, length2Height = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {

            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = 0;
            Iterator<Object> it = nodes.iterator();

            int neighbours = nodes.size();

            // the naive length which is tripled in every step
            length = (Math.pow(3, (depth - (level + 1)))) * unit;
            lengthHeight = (Math.sqrt(3) / 2) * length;

            // length2 sets the moved nodes to the grid point next to the middle
            // of length
            length2 = (Math.ceil(length / (2 * unit))) * unit;
            length2Height = (Math.sqrt(3) / 2) * length2;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            while (it.hasNext()) {
                // if hexaNode has three sons at most spread them out
                if (neighbours <= 3 && i == 1) {
                    i = 4;
                }

                // If the position of the node blocks the connection of its
                // father to it's grandfather then use another position
                if (i == (5 - position)) {
                    i++;
                }

                HexaNode node = (HexaNode) it.next();
                firstPentaWalkMod2(node, i, level + 1);

                // move the subtree to it's destined position starting
                // from the position of it's father; for the 2 subtrees next
                // to the incoming edge and the opposite subtree use the
                // shortened length
                switch (i) {
                // left
                case 0:
                    if ((position == 0) | (position == 2) | (position == 4)
                            && (length > unit)) {
                        node.setX(-length2);
                    } else {
                        node.setX(-length);
                    }
                    break;
                // upper left
                case 1:
                    if ((position == 1) | (position == 3) | (position == 5)
                            && (length > unit)) {
                        node.setX(-0.5 * length2);
                        node.setY(-length2Height);
                    } else {
                        node.setX(-0.5 * length);
                        node.setY(-lengthHeight);
                    }
                    break;
                // upper right
                case 2:
                    if ((position == 0) | (position == 2) | (position == 4)
                            && (length > unit)) {
                        node.setX(0.5 * length2);
                        node.setY(-length2Height);
                    } else {
                        node.setX(0.5 * length);
                        node.setY(-lengthHeight);
                    }
                    break;
                // lower left
                case 3:
                    if ((position == 1) | (position == 3) | (position == 5)
                            && (length > unit)) {
                        node.setX(-0.5 * length2);
                        node.setY(length2Height);
                    } else {
                        node.setX(-0.5 * length);
                        node.setY(lengthHeight);
                    }
                    break;
                // lower right
                case 4:
                    if ((position == 0) | (position == 2) | (position == 4)
                            && (length > unit)) {
                        node.setX(0.5 * length2);
                        node.setY(length2Height);
                    } else {
                        node.setX(0.5 * length);
                        node.setY(lengthHeight);
                    }
                    break;
                // right
                case 5:
                    if ((position == 1) | (position == 3) | (position == 5)
                            && (length > unit)) {
                        node.setX(length2);
                    } else {
                        node.setX(length);
                    }
                    break;

                }

                if (neighbours <= 3 && i == 4) {
                    i = 1;
                }

                i++;

            }

        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
