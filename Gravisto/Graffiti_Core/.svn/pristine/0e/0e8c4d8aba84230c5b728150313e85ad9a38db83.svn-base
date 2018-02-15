// =============================================================================
//
//   ContourNode.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import org.graffiti.graph.Node;

/**
 * The node of a {@link BasicContourNodeList}. Outside of
 * <code>BasicContourNodeList</code>, <code>ContourNode</code> is not directly
 * used but rather referenced by {@link BasicContourNodeList.Iterator}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class ContourNode {
    /**
     * The next node in the contour list.
     * 
     * @see BasicContourNodeList
     */
    private ContourNode next;

    /**
     * The x-position of this node relative to the position of its predecessor
     * in the contour list. The first <code>ContourNode</code> in a contour list
     * always has a x-position of 0 and is considered as being placed on the top
     * left corner of the contour list's tree's root {@link Node}.
     */
    private double dx;

    /**
     * The y-position of this node relative to the position of its predecessor
     * in the contour list. The first <code>ContourNode</code> in a contour list
     * always has a y-position of 0 and is considered as being placed on the top
     * left corner of the contour list's tree's root {@link Node}.
     */
    private double dy;

    /**
     * Returns the next node in the contour list.
     * 
     * @return the next node in the contour list.
     */
    ContourNode getNext() {
        return next;
    }

    /**
     * Returns the x-position of this node relative to the position of its
     * predecessor in the contour list.
     * 
     * @return the x-position of this node relative to the position of its
     *         predecessor in the contour list.
     * @see #dx
     */
    double getDx() {
        return dx;
    }

    /**
     * Returns the y-position of this node relative to the position of its
     * predecessor in the contour list.
     * 
     * @return the y-position of this node relative to the position of its
     *         predecessor in the contour list.
     * @see #dy
     */
    double getDy() {
        return dy;
    }

    /**
     * Sets the x-position of this node relative to the position of its
     * predecessor in the contour list. This method must only be called by
     * ContourNodeList. The absolute x-coordinates held and returned by
     * {@link ContourNodeIterator}s that have been obtained from the
     * {@link BasicContourNodeList} containing this <code>ContourNode</code>
     * before must be updated manually.
     * 
     * @param dx
     *            the new relative x-coordinate.
     */
    void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Sets the y-position of this node relative to the position of its
     * predecessor in the contour list. This method must only be called by
     * ContourNodeList. The absolute y-coordinates held and returned by
     * {@link ContourNodeIterator}s that have been obtained from the
     * {@link BasicContourNodeList} containing this <code>ContourNode</code>
     * before must be updated manually.
     * 
     * @param dy
     *            the new relative y-coordinate.
     */
    void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Sets the successor <code>ContourNode</code> of this
     * <code>ContourNode</code> in its {@link BasicContourNodeList}.
     * 
     * @param next
     *            the <code>ContourNode</code> that becomes the successor of
     *            this <code>ContourNode</code> in its
     *            <code>BasicContourNodeList</code>.
     */
    void setNext(ContourNode next) {
        this.next = next;
    }

    /**
     * Creates a new ContourNode. Must only be called from
     * {@link BasicContourNodeList} or {@link BasicContourNodeList.Iterator}.
     * 
     * @param dx
     *            x-coordinate of the ContourNode relative to its precedessor.
     * @param dy
     *            y-coordinate of the ContourNode relative to its precedessor.
     */
    ContourNode(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
