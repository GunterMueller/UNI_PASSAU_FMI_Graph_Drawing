// =============================================================================
//
//   ContourNodeList.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import org.graffiti.graph.Node;
import org.graffiti.plugins.tools.debug.DebugImage;

/**
 * Subclasses of <code>ContourNodeList</code> represent contour lines of tree
 * layouts. The contour nodes that constitute this contour cannot be accessed
 * directly but by the use of {@link ContourNodeIterator}. The start of the
 * contour lines and the origin of the contour node coordinates are considered
 * to be the top left corner of the {@link Node} that is the root of the
 * {@link Tree} to which this <code>ContourNodeList</code> belongs.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class ContourNodeList {
    /**
     * Returns an new iterator pointing to the first contour node in this
     * contour.
     * 
     * @return an new iterator pointing to the first contour node in this
     *         contour.
     */
    public abstract ContourNodeIterator getFirst();

    /**
     * Compares two contour lines. This contour line is considered to be the
     * right contour of a (relatively) left tree layout.
     * <code>leftContourOfRightNode</code> is considered to be the left contour
     * of a (relatively) right tree layout. Both contour lines are considered to
     * start at the same coordinate. The {@link ContourCombinationInfo#shift}
     * field of the returned <code>ContourCombinationInfo</code> denotes how far
     * the right tree must be moved horizontally to the right so that both tree
     * layouts contact but do not intersect. <code>extraShift</code> is added to
     * <code>shift</code> in order to reflect the position of the root nodes
     * within their tree layout and to enforce a minimal horizontal distance
     * between the tree layouts. The
     * {@link ContourCombinationInfo#connectionNode} field contains information
     * for the combination of both tree layouts. The
     * {@link ContourCombinationInfo#comparedHeights} field tells if a (or
     * which) tree layout is of greater height.
     * 
     * @param extraShift
     *            is added to the <code>shift</code> field of the returned
     *            <code>ContourCombinationInfo</code>.
     * @param leftContourOfRightNode
     *            the left contour of the right tree layout. <code>this</code>
     *            is interpreted as the right contour of the left tree layout.
     * @return a <code>ContourCombinationInfo</code> with information for the
     *         combination of two tree layouts.
     * @see Tree
     */
    public final ContourCombinationInfo calculateCombination(double extraShift,
            ContourNodeList leftContourOfRightNode) {
        double maxShift = 0.0;
        int comparedHeights = 0;
        BasicContourNodeList.Iterator connectionNode = null;
        ContourNodeIterator iterators[] = new ContourNodeIterator[2];
        iterators[0] = getFirst();
        iterators[1] = leftContourOfRightNode.getFirst();

        // Denote contour line by ('from','to').
        // fromIndex[0] index of the 'from' node in x[0] and y[0]
        // 1 - fromIndex[0] index of the 'to' node in x[0] and y[0]
        // fromIndex[1] index of the 'from' node in x[1] and y[1]
        // 1 - fromIndex[1] index of the 'to' node in x[1] and y[1]
        int[] fromIndex = new int[2];
        fromIndex[0] = 0;
        fromIndex[1] = 0;

        // x[0] leftX; x[1] rightX;
        double x[][] = new double[2][2];
        // y[0] leftY; y[1] leftY;
        double y[][] = new double[2][2];

        // The current line in the left contour is
        // (x[0][fromIndex[0], y[0][fromIndex[0]])
        // to (x[0][1 - fromIndex[0], y[0][1 - fromIndex[0]])
        // The current line in the right contour is
        // (x[1][fromIndex[1], y[1][fromIndex[1]])
        // to (x[1][1 - fromIndex[1], y[1][1 - fromIndex[1]])

        x[0][0] = x[0][1] = iterators[0].getX();
        y[0][0] = y[0][1] = iterators[0].getY();
        x[1][0] = x[1][1] = iterators[1].getX();
        y[1][0] = y[1][1] = iterators[1].getY();
        while (!iterators[0].isEnd() && !iterators[1].isEnd()) {
            int shorterSide = (y[0][1 - fromIndex[0]] < y[1][1 - fromIndex[1]]) ? 0
                    : 1;
            int longerSide = 1 - shorterSide;
            double beta;
            if (y[longerSide][1 - fromIndex[longerSide]] == y[longerSide][fromIndex[longerSide]]) {
                beta = 0;
            } else {
                beta = (y[shorterSide][1 - fromIndex[shorterSide]] - y[longerSide][fromIndex[longerSide]])
                        / (y[longerSide][1 - fromIndex[longerSide]] - y[longerSide][fromIndex[longerSide]]);
            }
            double newX = x[longerSide][fromIndex[longerSide]]
                    + beta
                    * (x[longerSide][1 - fromIndex[longerSide]] - x[longerSide][fromIndex[longerSide]]);
            double newY = y[shorterSide][1 - fromIndex[shorterSide]];
            x[longerSide][fromIndex[longerSide]] = newX;
            y[longerSide][fromIndex[longerSide]] = newY;
            double shift = 0.0;
            if (longerSide == 0) {
                shift = x[0][fromIndex[0]] - x[1][1 - fromIndex[1]];
            } else {
                shift = x[0][1 - fromIndex[0]] - x[1][fromIndex[1]];
            }
            maxShift = Math.max(maxShift, shift);
            fromIndex[shorterSide] = 1 - fromIndex[shorterSide];
            iterators[shorterSide].increment();
            if (!iterators[shorterSide].isEnd()) {
                x[shorterSide][1 - fromIndex[shorterSide]] = iterators[shorterSide]
                        .getX();
                y[shorterSide][1 - fromIndex[shorterSide]] = iterators[shorterSide]
                        .getY();
            } else {
                BasicContourNodeList.Iterator leftBase = iterators[0]
                        .getBaseIterator();
                BasicContourNodeList.Iterator rightBase = iterators[1]
                        .getBaseIterator();
                assert leftBase.isLast() || rightBase.isLast();

                if (leftBase.isLast()) {
                    if (rightBase.isLast()) {
                        if (leftBase.getY() < rightBase.getY()) {
                            comparedHeights = 1;
                        } else if (leftBase.getY() > rightBase.getY()) {
                            comparedHeights = -1;
                        } else {
                            comparedHeights = 0;
                        }
                    } else {
                        comparedHeights = 1;
                    }
                } else {
                    comparedHeights = -1;
                }
                if (comparedHeights == 1) {
                    // Left tree is higher.
                    connectionNode = rightBase;
                } else if (comparedHeights == -1) {
                    // Right tree is higher.
                    connectionNode = leftBase;
                }
            }
        }
        return new ContourCombinationInfo(maxShift + extraShift,
                comparedHeights, connectionNode);
    };

    /**
     * Returns the coordinates of the contour nodes as a <code>String</code>.
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("begin\n");
        for (ContourNodeIterator iterator = getFirst(); !iterator.isEnd(); iterator
                .increment()) {
            buffer.append(String.format("  %f, %f\n", iterator.getX(), iterator
                    .getY()));
        }
        buffer.append("end\n");
        return buffer.toString();
    }

    /**
     * Draws this contour line in a <code>DebugImage</code>.
     * 
     * @param image
     *            the image in which is drawn.
     * @param xOrigin
     *            the x-coordinate in the image where the contour line shall
     *            start.
     * @param yOrigin
     *            the y-coordinate in the image where the contour line shall
     *            start.
     */
    public void writeToDebugImage(DebugImage image, double xOrigin,
            double yOrigin) {
        ContourNodeIterator iter = getFirst();
        double prevX = 0.0;
        double prevY = 0.0;
        while (!iter.isEnd()) {
            image.drawLine(xOrigin + prevX, yOrigin + prevY, xOrigin
                    + iter.getX(), yOrigin + iter.getY());
            prevX = iter.getX();
            prevY = iter.getY();
            iter.increment();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
