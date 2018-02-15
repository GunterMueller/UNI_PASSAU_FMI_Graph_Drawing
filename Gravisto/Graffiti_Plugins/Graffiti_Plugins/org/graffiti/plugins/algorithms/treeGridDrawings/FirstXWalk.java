// =============================================================================
//
//   FirstYWalk.java
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
public class FirstXWalk {
    private int depth;

    // the distance between two points in the hexagonal grid
    private int unit = HexaConstants.unit;

    // the height of an equilateral triangle with the side length unit
    // private double unitHeight = (Math.sqrt(3) / 2) * unit;

    public FirstXWalk(int depth) {
        this.depth = depth;
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself for the naive algorithm to draw an x
     * layout
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
    public void firstWalk(HexaNode hexaNode, int position, int level) {
        hexaNode.setLevel(level);

        // lengthHeight is the height of one of the triangles on the grid with
        // edge length 'length'
        double length = 0, lengthHeight = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {
            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = 0;

            Iterator<Object> it = nodes.iterator();

            length = (Math.pow(3, (depth - (level + 1)))) * unit;

            lengthHeight = (Math.sqrt(3) / 2) * length;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            while (it.hasNext()) {
                i = i % 6;

                // if i would set the tree to one of the two taboo positions
                // (the one of the incoming edge or the one on the opposite
                // side) move on to the next position
                if (i == position || i == (5 - position)) {
                    i++;
                }
                if (i == position || i == (5 - position)) {
                    i++;
                }

                HexaNode node = (HexaNode) it.next();
                firstWalk(node, i, level + 1);

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
     * subtrees in relation to itself for the naive algorithm to draw an x
     * layout
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
    public void firstWalkMod(HexaNode hexaNode, int position, int level) {
        hexaNode.setLevel(level);

        // lengthHeight is the height of one of the triangles on the grid with
        // edge length 'length'
        double length = 0, lengthHeight = 0;
        double length2 = 0, length2Height = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {
            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = 0;

            Iterator<Object> it = nodes.iterator();

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
                i = i % 6;

                // if i would set the tree to one of the two taboo positions
                // (the one of the incoming edge or the one on the opposite
                // side) move on to the next position
                if (i == position || i == (5 - position)) {
                    i++;
                }
                if (i == position || i == (5 - position)) {
                    i++;
                }

                HexaNode node = (HexaNode) it.next();
                firstWalkMod(node, i, level + 1);

                // move the subtree to it's destined position starting from the
                // position of it's father
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
                i++;
            }

        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
