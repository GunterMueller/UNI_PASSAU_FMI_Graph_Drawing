// =============================================================================
//
//   CombinationInfo.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * Holds the information obtained from a call to
 * {@link ContourNodeList#calculateCombination(double, ContourNodeList)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class ContourCombinationInfo {
    /**
     * See {@link #getShift()}.
     */
    // Shift
    private double shift;

    /**
     * See {@link #getComparedHeights()}.
     */
    private int comparedHeights;

    /**
     * See {@link #getConnectionNode()}.
     */
    private BasicContourNodeList.Iterator connectionNode;

    /**
     * Creates a new <code>ContourCombinationInfo</code> object. Must only be
     * called from
     * {@link ContourNodeList#calculateCombination(double, ContourNodeList)}.
     * 
     * @param shift
     *            See {@link #getShift()}.
     * @param comparedHeights
     *            See {@link #getComparedHeights()}.
     * @param connectionNode
     *            See {@link #getConnectionNode()}.
     */
    ContourCombinationInfo(double shift, int comparedHeights,
            BasicContourNodeList.Iterator connectionNode) {
        this.shift = shift;
        this.comparedHeights = comparedHeights;
        this.connectionNode = connectionNode;
    }

    /**
     * Returns how far the right tree has to be translated in order to meet the
     * distance constraints.
     * 
     * @return how far the right tree has to be translated in order to meet the
     *         distance constraints.
     */
    public double getShift() {
        return shift;
    }

    /**
     * Returns which contour line belongs to the higher tree or if both contour
     * lines are equally high.
     * 
     * @return -1 if the right contour of the left tree is higher.<br>
     *         0 if both contours are of equal heigth.<br>
     *         1 if the left contour of the right tree is higher.
     */
    public int getComparedHeights() {
        return comparedHeights;
    }

    /**
     * Returns an iterator pointing to the node to which the shorter contour
     * line is connected to by a call to
     * {@link BasicContourNodeList.Iterator#connectToLeftContour(BasicContourNodeList, double, double)}
     * or
     * {@link BasicContourNodeList.Iterator#connectToLeftContour(BasicContourNodeList, double, double)}
     * .
     * 
     * @return an iterator pointing to the node to which the shorter contour
     *         line is connected to by a call to {@code
     *         BasicContourNodeList.Iterator#connectToLeftContour(BasicContourNodeList
     *         , double, double)} or {@code
     *         BasicContourNodeList.Iterator#connectToLeftContour(BasicContourNodeList
     *         , double, double)} .
     */
    public BasicContourNodeList.Iterator getConnectionNode() {
        return connectionNode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
