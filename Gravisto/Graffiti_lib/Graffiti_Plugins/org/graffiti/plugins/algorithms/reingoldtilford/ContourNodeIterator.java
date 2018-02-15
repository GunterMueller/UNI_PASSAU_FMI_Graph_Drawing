// =============================================================================
//
//   ContourNodeIterator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.NoSuchElementException;

import org.graffiti.graph.Node;

/**
 * Defines an interface for classes that act as iterators pointing to a contour
 * node in a {@link ContourNodeList} or to <code>null</code>.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface ContourNodeIterator extends Cloneable {
    /**
     * Returns the absolute x-coordinate of the contour node this iterator is
     * pointing to. The origin of the coordinate system is considered to be the
     * top left corner of the {@link Node} that is the root of the tree to which
     * the contour node's {@link ContourNodeList} belongs.
     * <p>
     * <b>Preconditions:</b><br>
     * This iterator must not point to null.
     * 
     * @return the absolute x-coordinate of the contour node this iterator is
     *         pointing to.
     * @throws NoSuchElementException
     *             if this iterator points to <code>null</code>.
     */
    public double getX();

    /**
     * Returns the absolute y-coordinate of the contour node this iterator is
     * pointing to. The origin of the coordinate system is considered to be the
     * top left corner of the {@link Node} that is the root of the tree to which
     * the contour node's {@link ContourNodeList} belongs.
     * <p>
     * <b>Preconditions:</b><br>
     * This iterator must not point to null.
     * 
     * @return the absolute y-coordinate of the contour node this iterator is
     *         pointing to.
     * @throws NoSuchElementException
     *             if this iterator points to <code>null</code>.
     */
    public double getY();

    /**
     * The x-coordinate of the contour node this iterator is pointing to
     * relative to the position of its predecessor in the contour list.
     * <p>
     * <b>Preconditions:</b><br>
     * This iterator must not point to null.
     * 
     * @return the x-coordinate of the contour node this iterator is pointing to
     *         relative to the position of its predecessor in the contour list.
     * @throws NoSuchElementException
     *             if this iterator points to <code>null</code>.
     */
    public double getDx();

    /**
     * The y-coordinate of the contour node this iterator is pointing to
     * relative to the position of its predecessor in the contour list.
     * <p>
     * <b>Preconditions:</b><br>
     * This iterator must not point to null.
     * 
     * @return the y-coordinate of the contour node this iterator is pointing to
     *         relative to the position of its predecessor in the contour list.
     * @throws NoSuchElementException
     *             if this iterator points to <code>null</code>.
     */
    public double getDy();

    /**
     * After a call to <code>increment()</code> this iterator points to the
     * successor of the contour node this iterator was pointing to before or
     * <code>null</code> if this was pointing to the last node in its contour.
     * <p>
     * <b>Preconditions:</b><br>
     * This iterator must not point to null.
     * 
     * @throws NoSuchElementException
     *             if this iterator points to <code>null</code>.
     */
    public void increment();

    /**
     * Returns if this iterator points to the last contour node in its
     * {@link ContourNodeList}.
     * 
     * @return <code>true</code> if this iterator points to the last contour
     *         node in its {@link ContourNodeList}.<br>
     *         <code>false</code> otherwise.
     */
    public boolean isEnd();

    /**
     * Returns if this iterator points to <code>null</code>.
     * 
     * @return <code>true</code> if this iterator points to <code>null</code>. <br>
     *         <code>false</code> otherwise.
     */
    public boolean isLast();

    /**
     * Returns an new iterator pointing to the node in the original {@code
     * BasicContourNodeList} with the greatest y-coordinate less than or equal
     * to the y-coordinate of the contour node this iterator is pointing to.
     * 
     * @return an new iterator pointing to the node in the original
     *         {@link BasicContourNodeList} with the greatest y-coordinate less
     *         than or equal to the y-coordinate of the contour node this
     *         iterator is pointing to.
     */
    public BasicContourNodeList.Iterator getBaseIterator();

    /**
     * Returns a copy of this <code>ContourNodeIterator</code>.
     * 
     * @return a copy of this <code>ContourNodeIterator</code>.
     */
    public ContourNodeIterator clone();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
