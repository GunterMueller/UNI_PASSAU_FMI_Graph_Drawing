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

import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class FirstYWalk {
    private int depth;

    // the distance between two points in the hexagonal grid
    private int unit = HexaConstants.unit;

    // the height of an equilateral triangle with the side length unit
    private double unitHeight = (Math.sqrt(3) / 2) * unit;

    StringSelectionParameter useContraction;

    public FirstYWalk(int depth, StringSelectionParameter useContraction) {
        this.depth = depth;
        this.useContraction = useContraction;
    }

    /**
     * Recursively visits every node of a tree and computes the position of its
     * subtrees in relation to itself for the naive algorithm to draw a y layout
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
        // edge
        // length 'length'
        double length = 0, lengthHeight = 0;

        // current node is a leave
        if (hexaNode.getNumberOfChildren() == 0) {
            hexaNode.setX(0);
            hexaNode.setY(0);

        } else {
            ArrayList<Object> nodes = hexaNode.getChildren();
            int i = position;
            i++;
            Iterator<Object> it = nodes.iterator();

            // The minimal length of the edges in a complete balanced binary
            // tree corresponds with the fibonacci sequence
            length = fib(depth - level) * unit;

            lengthHeight = (Math.sqrt(3) / 2) * length;

            // move the root of the tree so that all nodes are on grid points
            if (level == 1) {
                hexaNode.setX(0.5 * unit);
            }

            while (it.hasNext()) {

                i = i % 6;

                // recursively visits all the child nodes
                HexaNode currentChild = (HexaNode) it.next();
                firstWalk(currentChild, i, level + 1);
                currentChild.setPosition(i);

                // move the subtree to it's destined position starting from the
                // position of it's father
                switch (i) {
                // move to the left
                case 0:
                    currentChild.setX(currentChild.getX() - length);
                    updateBoundaries(currentChild, -length, 0);

                    break;
                // move to the upper left
                case 1:
                    currentChild.setX(currentChild.getX() - 0.5 * length);
                    currentChild.setY(currentChild.getY() - lengthHeight);

                    updateBoundaries(currentChild, -0.5 * length, -lengthHeight);

                    break;
                // move to the upper right
                case 2:
                    currentChild.setX(currentChild.getX() + 0.5 * length);
                    currentChild.setY(currentChild.getY() - lengthHeight);

                    updateBoundaries(currentChild, 0.5 * length, -lengthHeight);

                    break;
                // move to the lower left
                case 5:
                    currentChild.setX(currentChild.getX() - 0.5 * length);
                    currentChild.setY(currentChild.getY() + lengthHeight);

                    updateBoundaries(currentChild, -0.5 * length, lengthHeight);

                    break;
                // move to the lower right
                case 4:
                    currentChild.setX(currentChild.getX() + 0.5 * length);
                    currentChild.setY(currentChild.getY() + lengthHeight);

                    updateBoundaries(currentChild, 0.5 * length, lengthHeight);

                    break;
                // move to the right
                case 3:
                    currentChild.setX(currentChild.getX() + length);

                    updateBoundaries(currentChild, length, 0);
                    break;

                }

                // Draws the nodes closer to their father (if possible)
                if (useContraction.getSelectedValue() == "WITH_CONTRACTION") {
                    contract(i, hexaNode, currentChild);
                }

                // calculate all the min and max position for the father node
                // out of the mins and maxs of it's childern
                updateFather(hexaNode, position);

                // set i to the next possible position in the y layout
                if (position == 0) {
                    i = 5;
                } else {
                    i = position - 1;
                }
            }

        }
    }

    /**
     * Updates all the minimum and maximum boundaries of the node when after the
     * node itself has been moved
     * 
     * @param currentChild
     *            The HexaNode which is to be updated
     * @param x
     *            the x offset
     */
    private void updateBoundaries(HexaNode currentChild, double x, double y) {
        // The following two statements update the horizontal boundary
        currentChild.setMaxYP(new MinMaxPosition(currentChild.getMaxYP().getX()
                + x, currentChild.getMaxYP().getY() + y));
        currentChild.setMinYP(new MinMaxPosition(currentChild.getMinYP().getX()
                + x, currentChild.getMinYP().getY() + y));

        // The following two statements update the boundaries along the upward
        // diagonal
        currentChild.setMaxUpDiag(new MinMaxPosition(currentChild
                .getMaxUpDiag().getX()
                + x, currentChild.getMaxUpDiag().getY() + y));
        currentChild.setMinUpDiag(new MinMaxPosition(currentChild
                .getMinUpDiag().getX()
                + x, currentChild.getMinUpDiag().getY() + y));

        // The following two statements update the boundaries along the downward
        // diagonal
        currentChild.setMaxDownDiag(new MinMaxPosition(currentChild
                .getMaxDownDiag().getX()
                + x, currentChild.getMaxDownDiag().getY() + y));
        currentChild.setMinDownDiag(new MinMaxPosition(currentChild
                .getMinDownDiag().getX()
                + x, currentChild.getMinDownDiag().getY() + y));
    }

    /**
     * Calculates the numberth item in the Fibonacci sequence according to
     * Binet's formula
     * 
     * @param number
     * @return The 'number'th item of the Fibonacci sequence
     */
    private double fib(int number) {

        if (number / 2 - 1.0 * number / 2 == 0) // number is even

            return Math.floor(1
                    / Math.sqrt(5)
                    * (Math.pow((1 + Math.sqrt(5)) / 2, number) - Math.pow(
                            (Math.sqrt(5) - 1) / 2, number)));
        else

            // number is uneven

            return Math.floor(1
                    / Math.sqrt(5)
                    * (Math.pow((1 + Math.sqrt(5)) / 2, number) + Math.pow(
                            (Math.sqrt(5) - 1) / 2, number)));
    }

    /**
     * The method moves the subtree under child closer to it's father. It's
     * pulled as far as possible without entering the 'sector' of another
     * subtree
     * 
     * @param position
     *            The direction, in which the subtree has been moved (and so the
     *            alignment of the subtree)
     * @param hexaNode
     *            The father of the subtree that has to be moved
     * @param child
     *            The node, that has to be moved
     */
    private void contract(int position, HexaNode hexaNode, HexaNode child) {

        // The position of the leftmost node in the sector
        MinMaxPosition leftPos = determineMaxLeft(child, position);

        // The position of the rightmost node in the sector
        MinMaxPosition rightPos = determineMaxRight(child, position);
        double leftX = leftPos.getX();
        double leftY = leftPos.getY();
        double rightX = rightPos.getX();
        double rightY = rightPos.getY();

        // hexaNode hasn't been moved yet so staays at (0, 0)
        double parentX = 0;
        double parentY = 0;

        boolean stop = true;

        // The space on the left/right side of the sector to spare
        int spaceLeft = 0;
        int spaceRight = 0;

        // How is the subtree aligned?
        switch (position) {
        // left
        case 0:
            stop = true;
            while (stop) {

                /*
                 * As long as the leftmost node doesn't cross the boundaries of
                 * the sector left max is updated as if the whole subtree was
                 * moved one unit to the right. Once it crosses the boundary the
                 * loop stops
                 */

                if (Math.abs(((parentX - leftX) / (unit / 2))
                        - (leftY - parentY) / unitHeight) < 0.1) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX += unit;
                }
            }
            stop = true;

            /*
             * As long as the rightmost node doesn't cross the boundaries of the
             * sector the right max is updated as if the whole subtree was moved
             * one unit to the right. Once it crosses the boundary the loop
             * stops
             */

            while (stop) {
                if (Math.abs(((parentX - rightX) / (unit / 2))
                        - (parentY - rightY) / unitHeight) < 0.1) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX += unit;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the right
             */
            int min = Math.max(0, Math.min(spaceLeft, spaceRight) - 1);
            child.setX(child.getX() + (min * unit));

            updateBoundaries(child, min * unit, 0);

            break;

        // upper left
        case 1:
            stop = true;

            /*
             * As long as the leftmost node doesn't cross the boundaries of the
             * sector the left max is updated as if the whole subtree was moved
             * one unit to the lower right. Once it crosses the boundary the
             * loop stops
             */
            while (stop) {
                if (Math.abs(parentY - leftY) < 0.000001) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX += 0.5 * unit;
                    leftY += unitHeight;
                }
            }
            stop = true;

            /*
             * As long as the rightmost node doesn't cross the boundaries of the
             * sector the right max is updated as if the whole subtree was moved
             * one unit to the lower right. Once it crosses the boundary the
             * loop stops
             */
            while (stop) {
                if (Math.abs(((rightX - parentX) / (unit / 2))
                        - (parentY - rightY) / unitHeight) < 0.00001) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX += 0.5 * unit;
                    rightY += unitHeight;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the lower right
             */
            min = Math.min(spaceLeft, spaceRight) - 1;
            child.setX(child.getX() + (min * (unit / 2)));
            child.setY(child.getY() + min * unitHeight);

            updateBoundaries(child, min * (unit / 2), min * unitHeight);

            break;

        // upper right
        case 2:
            stop = true;

            /*
             * As long as the leftmost node doesn't cross the boundaries of the
             * sector the left max is updated as if the whole subtree was moved
             * one unit to the lower left. Once it crosses the boundary the loop
             * stops
             */
            while (stop) {
                if (Math.abs(((parentX - leftX) / (unit / 2))
                        - (parentY - leftY) / unitHeight) < 0.00001) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX -= 0.5 * unit;
                    leftY += unitHeight;
                }
            }
            stop = true;

            /*
             * As long as the rightmost node doesn't cross the boundaries of the
             * sector the right max is updated as if the whole subtree was moved
             * one unit to the lower left. Once it crosses the boundary the loop
             * stops
             */
            while (stop) {
                if (Math.abs(rightY - parentY) < 0.000001) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX -= 0.5 * unit;
                    rightY += unitHeight;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the lower left
             */

            min = Math.min(spaceLeft, spaceRight) - 1;
            child.setX(child.getX() - (min * (unit / 2)));
            child.setY(child.getY() + min * unitHeight);

            updateBoundaries(child, -min * (unit / 2), min * unitHeight);

            break;

        // right
        case 3:
            stop = true;

            /*
             * As long as the leftmost node doesn't cross the boundaries of the
             * sector the left max is updated as if the whole subtree was moved
             * one unit to the left. Once it crosses the boundary the loop stops
             */
            while (stop) {
                if (Math.abs(((leftX - parentX) / (unit / 2))
                        - (parentY - leftY) / unitHeight) < 0.1) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX -= unit;
                }
            }
            stop = true;

            /*
             * As long as the rightmost node doesn't cross the boundaries of the
             * sector the right max is updated as if the whole subtree was moved
             * one unit to the left. Once it crosses the boundary the loop stops
             */
            while (stop) {
                if (Math.abs(((rightX - parentX) / (unit / 2))
                        - (rightY - parentY) / unitHeight) < 0.1) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX -= unit;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the left
             */

            min = Math.max(0, Math.min(spaceLeft, spaceRight) - 1);
            child.setX(child.getX() - (min * unit));

            updateBoundaries(child, -min * unit, 0);

            break;

        /*
         * As long as the leftmost node doesn't cross the boundaries of the
         * sector the left max is updated as if the whole subtree was moved one
         * unit to the upper left. Once it crosses the boundary the loop stops
         */

        // lower right
        case 4:
            stop = true;
            while (stop) {
                if (Math.abs(((parentX - rightX) / (unit / 2))
                        - (rightY - parentY) / unitHeight) < 0.00001) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX -= 0.5 * unit;
                    rightY -= unitHeight;
                }
            }
            stop = true;

            /*
             * As long as the rightmost node doesn't cross the boundaries of the
             * sector the right max is updated as if the whole subtree was moved
             * one unit to the upper left. Once it crosses the boundary the loop
             * stops
             */
            while (stop) {
                if (Math.abs(leftY - parentY) < 0.000001) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX -= 0.5 * unit;
                    leftY -= unitHeight;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the upper left
             */

            min = Math.min(spaceLeft, spaceRight) - 1;
            child.setX(child.getX() - (min * (unit / 2)));
            child.setY(child.getY() - min * unitHeight);

            updateBoundaries(child, -min * (unit / 2), -min * unitHeight);

            break;

        // lower left
        case 5:
            stop = true;

            /*
             * As long as the leftmost node doesn't cross the boundaries of the
             * sector the left max is updated as if the whole subtree was moved
             * one unit to the upper right. Once it crosses the boundary the
             * loop stops
             */
            while (stop) {
                if (Math.abs(((leftX - parentX) / (unit / 2))
                        - (leftY - parentY) / unitHeight) < 0.1) {
                    stop = false;
                } else {
                    spaceLeft++;
                    leftX += 0.5 * unit;
                    leftY -= unitHeight;
                }
            }
            stop = true;

            /*
             * As long as the leftmost node doesn't cross the boundaries of the
             * sector the left max is updated as if the whole subtree was moved
             * one unit to the upper right. Once it crosses the boundary the
             * loop stops
             */
            while (stop) {
                if (Math.abs(rightY - parentY) < 0.000001) {
                    stop = false;
                } else {
                    spaceRight++;
                    rightX += 0.5 * unit;
                    rightY -= unitHeight;
                }
            }

            /*
             * determine the minimum of the left and right maxima and move the
             * subtree that far to the upper right
             */

            min = Math.min(spaceLeft, spaceRight) - 1;
            child.setX(child.getX() + (min * (unit / 2)));
            child.setY(child.getY() - min * unitHeight);

            updateBoundaries(child, min * (unit / 2), -min * unitHeight);

            break;
        }

    }

    /**
     * Out of the 6 possible directions choose the one that is the farthest to
     * the right according to the alignment of the subtree
     * 
     * @param child
     *            The root of the subtree
     * @param position
     *            The alignment of the subtree
     * @return The position of the rightmost node in the sector of the subtree
     */
    private MinMaxPosition determineMaxRight(HexaNode child, int position) {
        switch (position) {
        // left
        case 0:
            return child.getMaxDownDiag();
            // upper left
        case 1:
            return child.getMaxUpDiag();

            // upper right
        case 2:
            return child.getMaxYP();
            // right
        case 3:
            return child.getMinDownDiag();
            // lower right
        case 4:
            return child.getMinUpDiag();
            // lower left
        case 5:
            return child.getMinYP();
        }
        return null;
    }

    /**
     * Out of the 6 possible directions choose the one that is the farthest to
     * the left according to the alignment of the subtree
     * 
     * @param child
     *            The root of the subtree
     * @param position
     *            The alignment of the subtree
     * @return The position of the leftmost node in the sector of the subtree
     */
    private MinMaxPosition determineMaxLeft(HexaNode child, int position) {
        switch (position) {
        // left
        case 0:
            return child.getMaxUpDiag();
            // upper left
        case 1:
            return child.getMaxYP();
            // upper right
        case 2:
            return child.getMinDownDiag();
            // right
        case 3:
            return child.getMinUpDiag();
            // lower right
        case 4:
            return child.getMinYP();
            // lower left
        case 5:
            return child.getMaxDownDiag();
        }
        return null;
    }

    /**
     * Determines the maximum and minimum position along the six axis of the
     * father out of the minima and maxima of it's children
     * 
     * @param node
     *            The current node
     * @param position
     *            The alignment of the subtree under node
     */
    private void updateFather(HexaNode node, int position) {
        ArrayList<Object> nodes = node.getChildren();
        Iterator<Object> it = nodes.iterator();

        MinMaxPosition origin = new MinMaxPosition(0, 0);

        // if the node has no cildren there is nothing to update
        if (nodes.size() == 0) {
            node.setMaxYP(origin);
            node.setMinYP(origin);
            node.setMaxDownDiag(origin);
            node.setMaxUpDiag(origin);
            node.setMinDownDiag(origin);
            node.setMinUpDiag(origin);
        }

        else {
            while (it.hasNext()) {
                HexaNode firstChild = (HexaNode) it.next();
                // HexaNode secondChild2 = (HexaNode)it.next();

                // Calculate the maximum position along the horizontal axis
                // amongst
                // the two children
                if (firstChild.getMaxYP().getY() > node.getMaxYP().getY()) {
                    node.setMaxYP(firstChild.getMaxYP());
                }

                // Calculate the minimum position along the horizontal axis
                // amongst
                // the two children
                if (firstChild.getMinYP().getY() < node.getMinYP().getY()) {
                    node.setMinYP(firstChild.getMinYP());
                }

                // Calculate the maximum position along the upward diagonal
                // amongst
                // the two children
                if ((firstChild.getMaxUpDiag().getX() >= node.getMaxUpDiag()
                        .getX())
                        && (firstChild.getMaxUpDiag().getY() >= node
                                .getMaxUpDiag().getY())) {

                    node.setMaxUpDiag(firstChild.getMaxUpDiag());
                } else if ((firstChild.getMaxUpDiag().getX() >= node
                        .getMaxUpDiag().getX())
                        && (firstChild.getMaxUpDiag().getX() - node
                                .getMaxUpDiag().getX())
                                / (unit / 2) > (node.getMaxUpDiag().getY() - firstChild
                                .getMaxUpDiag().getY())
                                / unitHeight) {
                    node.setMaxUpDiag(firstChild.getMaxUpDiag());
                } else if ((node.getMaxUpDiag().getX() - firstChild
                        .getMaxUpDiag().getX())
                        / (unit / 2) < (firstChild.getMaxUpDiag().getY() - node
                        .getMaxUpDiag().getY())
                        / unitHeight) {
                    node.setMaxUpDiag(firstChild.getMaxUpDiag());
                }

                // Calculate the minimum position along the upward diagonal
                // amongst
                // the two children
                if ((firstChild.getMinUpDiag().getX() <= node.getMinUpDiag()
                        .getX())
                        && (firstChild.getMinUpDiag().getY() <= node
                                .getMinUpDiag().getY())) {
                    node.setMinUpDiag(firstChild.getMinUpDiag());
                } else if ((firstChild.getMinUpDiag().getX() <= node
                        .getMinUpDiag().getX())
                        && (node.getMinUpDiag().getX() - firstChild
                                .getMinUpDiag().getX())
                                / (unit / 2) > (firstChild.getMinUpDiag()
                                .getY() - node.getMinUpDiag().getY())
                                / unitHeight) {
                    node.setMinUpDiag(firstChild.getMinUpDiag());
                } else if ((firstChild.getMinUpDiag().getX() - node
                        .getMinUpDiag().getX())
                        / (unit / 2) < (node.getMinUpDiag().getY() - firstChild
                        .getMinUpDiag().getY())
                        / unitHeight) {
                    node.setMinUpDiag(firstChild.getMinUpDiag());
                }

                // Calculate the maximum position along the downward diagonal
                // amongst the two children
                if ((firstChild.getMaxDownDiag().getX() >= node
                        .getMaxDownDiag().getX())
                        && (firstChild.getMaxDownDiag().getY() <= node
                                .getMaxDownDiag().getY())) {
                    node.setMaxDownDiag(firstChild.getMaxDownDiag());
                } else if ((firstChild.getMaxDownDiag().getX() >= node
                        .getMaxDownDiag().getX())
                        && (firstChild.getMaxDownDiag().getX() - node
                                .getMaxDownDiag().getX())
                                / (unit / 2) > (firstChild.getMaxDownDiag()
                                .getY() - node.getMaxDownDiag().getY())
                                / unitHeight) {
                    node.setMaxDownDiag(firstChild.getMaxDownDiag());
                } else if ((node.getMaxDownDiag().getX() - firstChild
                        .getMaxDownDiag().getX())
                        / (unit / 2) < (node.getMaxDownDiag().getY() - firstChild
                        .getMaxDownDiag().getY())
                        / unitHeight) {
                    node.setMaxDownDiag(firstChild.getMaxDownDiag());
                }

                // Calculate the minimum position along the downward diagonal
                // amongst the two children
                if ((firstChild.getMinDownDiag().getX() <= node
                        .getMinDownDiag().getX())
                        && (firstChild.getMinDownDiag().getY() >= node
                                .getMinDownDiag().getY())) {
                    node.setMinDownDiag(firstChild.getMinDownDiag());
                } else if ((firstChild.getMinDownDiag().getX() <= node
                        .getMinDownDiag().getX())
                        && (node.getMinDownDiag().getX() - firstChild
                                .getMinDownDiag().getX())
                                / (unit / 2) < (node.getMinDownDiag().getY() - firstChild
                                .getMinDownDiag().getY())
                                / unitHeight) {
                    node.setMinDownDiag(firstChild.getMinDownDiag());
                } else if ((firstChild.getMinDownDiag().getX() - node
                        .getMinDownDiag().getX())
                        / (unit / 2) < (firstChild.getMinDownDiag().getY() - node
                        .getMinDownDiag().getY())
                        / unitHeight) {
                    node.setMinDownDiag(firstChild.getMinDownDiag());
                }
            }

        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
