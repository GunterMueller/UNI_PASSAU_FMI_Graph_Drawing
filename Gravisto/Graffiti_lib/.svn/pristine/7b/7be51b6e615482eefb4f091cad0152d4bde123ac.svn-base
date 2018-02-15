// =============================================================================
//
//   ContourNodeList.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.NoSuchElementException;

import org.graffiti.graph.Node;

/**
 * BasicContourNodeList maintains a linked list of contour coordinates. The
 * first {@link ContourNode} always has the coordinate (0.0, 0.0) and is
 * considered as being placed on the top left corner of this contour list's
 * tree's root {@link Node}. If this is a right contour, the coordinate of the
 * second {@code ContourNode} always has the form (x, 0.0) for some x.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class BasicContourNodeList extends ContourNodeList {
    /**
     * The iterator for traversing the contour.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    class Iterator implements ContourNodeIterator, Cloneable {
        /**
         * The ContourNode this iterator is pointing to.
         */
        private ContourNode currentNode;

        /**
         * x-coordinate of the <code>ContourNode</code> this iterator is
         * pointing to relative to the start point of the contour line.
         */
        private double x;

        /**
         * y-coordinate of the <code>ContourNode</code> this iterator is
         * pointing to relative to the start point of the contour line.
         */
        private double y;

        /**
         * {@inheritDoc}
         */
        public double getDx() {
            if (currentNode == null)
                throw new NoSuchElementException();
            return currentNode.getDx();
        }

        /**
         * {@inheritDoc}
         */
        public double getDy() {
            if (currentNode == null)
                throw new NoSuchElementException();
            return currentNode.getDy();
        }

        /**
         * {@inheritDoc}
         */
        public void increment() {
            if (currentNode == null)
                throw new NoSuchElementException();

            currentNode = currentNode.getNext();
            if (currentNode != null) {
                x += currentNode.getDx();
                y += currentNode.getDy();
            }
        }

        /**
         * Creates a new Iterator pointing to the same ContourNode.
         */
        @Override
        public Iterator clone() {
            return new Iterator(this);
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEnd() {
            return currentNode == null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isLast() {
            if (currentNode == null)
                return false;
            return currentNode.getNext() == null;
        }

        /**
         * Initializes the <code>Iterator</code> so that it points to the first
         * ContourNode in the contour line.
         */
        private Iterator() {
            currentNode = first;
            x = currentNode.getDx();
            y = currentNode.getDy();
        }

        /**
         * Initializes the Iterator so that it points to the same ContourNode as
         * <code>other</code>. Must only be called by clone().
         * 
         * @param other
         */
        private Iterator(Iterator other) {
            currentNode = other.currentNode;
            x = other.x;
            y = other.y;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator getBaseIterator() {
            if (currentNode == null)
                return last.clone();

            return clone();
        }

        /**
         * {@inheritDoc}
         */
        public double getX() {
            return x;
        }

        /**
         * {@inheritDoc}
         */
        public double getY() {
            return y;
        }

        /**
         * Connects the <code>ContourNode</code> this Iterator is pointing to
         * with the <code>ContourNode</code> <code>other</code> is pointing to
         * so that the tail of the contour line <code>other</code> belongs to
         * becomes the tail of the contour line of the <code>ContourNode</code>
         * this Iterator is pointing to. <code>connect</code> is used for
         * connecting contour lines of siblings.
         * <p>
         * <b>Preconditions</b><br>
         * {@code currentNode.next == null}<br>
         * {@code currentNode.y < other.getY()}
         * 
         * @param other
         *            the <code>Iterator</code> pointing to the
         *            <code>ContourNode</code> to connect to.
         * @param xOfs
         *            the difference of the x-origins of the affected contour
         *            lines.
         * @param change
         *            is used to enable a revert of this operation.
         */
        public void connect(Iterator other, double xOfs,
                TreeCombinationStack.Change change) {
            double newDy = other.y - y;
            double beta;
            double otherDy = other.currentNode.getDy();
            double otherDx = other.currentNode.getDx();
            if (otherDy == 0) {
                beta = 0;
            } else {
                beta = newDy / otherDy;
            }
            double newX = other.x - beta * otherDx;
            ContourNode newNode = new ContourNode(newX - x + xOfs, 0.0);
            currentNode.setNext(newNode);
            change.setOtherCurrentNode(other.currentNode);
            change.setOtherCurrentNodeDx(other.currentNode.getDx());
            change.setOtherCurrentNodeDy(other.currentNode.getDy());
            other.currentNode.setDx(beta * otherDx);
            other.currentNode.setDy(newDy);
            newNode.setNext(other.currentNode);
            change.setLast(last);
            last = new Iterator(other.getList().last);
            change.setOtherListLast(last);
            change.setOtherListLastX(last.x);
            last.x += xOfs;
        }

        /**
         * Reverts a previous connect operation.
         * 
         * @param change
         *            contains the information necessary for undoing the connect
         *            operation.
         */
        public void revert(TreeCombinationStack.Change change) {
            change.getOtherListLast().x = change.getOtherListLastX();
            last = change.getLast();
            ContourNode otherCurrentNode = change.getOtherCurrentNode();
            otherCurrentNode.setDx(change.getOtherCurrentNodeDx());
            otherCurrentNode.setDy(change.getOtherCurrentNodeDy());
            currentNode.setNext(null);
        }

        /**
         * Connects this contour line to <code>other</code>, so that other
         * becomes part of this contour. <code>connectToLeftContour</code> is
         * called to connect the contour line of a node with the contour line of
         * its children.
         * <p>
         * <b>Preconditions:</b><br>
         * <code>other</code> must contain at least 1 node.
         * 
         * @param other
         *            the left contour that becomes part of this contour line.
         * @param deltaX
         *            supposed x-origin of <code>other</code> in the coordinate
         *            system of this <code>BasicContourNodeList</code>.
         * @param deltaY
         *            supposed y-origin of <code>other</code> in the coordinate
         *            system of this <code>BasicContourNodeList</code>.
         */
        public void connectToLeftContour(BasicContourNodeList other,
                double deltaX, double deltaY) {
            ContourNode node = other.getFirst().currentNode;
            currentNode.setNext(node);
            node.setDx(deltaX - x);
            node.setDy(deltaY - y);
            last = new Iterator(other.getLast()); // last = other.getLast();
            last.x += deltaX;
            last.y += deltaY;
        }

        /**
         * Connects this contour line to <code>other</code>, so that
         * <code>other</code> becomes part of this contour.
         * <code>connectToRightContour</code> is called to connect the contour
         * line of a node with the contour line of its children.
         * <p>
         * <b>Preconditions:</b><br>
         * <code>other</code> must contain at least 2 nodes.
         * 
         * @param other
         *            the right contour that becomes part of this contour line.
         * @param deltaX
         *            supposed x-origin of other in the coordinate system of
         *            this BasicContourNodeList.
         * @param deltaY
         *            supposed y-origin of other in the coordinate system of
         *            this BasicContourNodeList.
         */
        public void connectToRightContour(BasicContourNodeList other,
                double deltaX, double deltaY) {
            ContourNode node = other.getFirst().currentNode.getNext();
            currentNode.setNext(node);
            node.setDx(deltaX - x + node.getDx());
            node.setDy(deltaY - y);
            last = new Iterator(other.getLast());
            last.x += deltaX;
            last.y += deltaY;
        }

        /**
         * Returns the <code>BasicContourNodeList</code> this iterator belongs
         * to.
         * 
         * @return the <code>BasicContourNodeList</code> this iterator belongs
         *         to.
         */
        private BasicContourNodeList getList() {
            return BasicContourNodeList.this;
        }
    }

    /**
     * The first <code>ContourNode</code> in the list.
     */
    private ContourNode first;

    /**
     * Iterator pointing to the last <code>ContourNode</code> in the list.
     */
    private Iterator last;

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicContourNodeList.Iterator getFirst() {
        return new Iterator();
    }

    /**
     * Returns an <code>Iterator</code> pointing to the last node in this list.
     */
    public BasicContourNodeList.Iterator getLast() {
        return last;
    }

    /**
     * Creates an empty BasicContourNodeList.
     */
    public BasicContourNodeList() {
        first = new ContourNode(0.0, 0.0);
        last = new Iterator();
    }

    /**
     * Adds a new ContourNode at the end of this contour list. Iterators
     * previously returned by {@link #getFirst()} or {@link #getLast()} become
     * invalid.
     * 
     * @param dx
     *            x-coordinate relative to ancestor node.
     * @param dy
     *            y-coordinate relative to ancestor node.
     * @see Iterator
     */
    public void addNode(double dx, double dy) {
        ContourNode newNode = new ContourNode(dx, dy);
        last.currentNode.setNext(newNode);
        // last = last.clone(); redundant?
        last.increment();
    }

    /**
     * Moves the second node of this contour to the right by <code>x</code>
     * units. Iterators previously returned by {@link #getFirst()} or
     * {@link #getLast()} become invalid.
     * <p>
     * <b>Preconditions:</b><br>
     * this list contains at least 2 nodes.
     * 
     * @param x
     *            the amount by which the second node is moved rightwards. If
     *            <code>x</code> is negative, the node is moved <code>-x</code>
     *            units to the left.
     */
    public void extendRightContour(double x) {
        ContourNode second = first.getNext();
        second.setDx(second.getDx() + x);
        last.x += x;
    }

    /**
     * Returns a new <code>BasicContourNodeList</code> which corresponds to this
     * list mirrored along the vertical axis at {@code x/2}. This
     * <code>BasicContourNodeList</code> should be used as a right contour. The
     * result can then be considered as a left contour.
     * 
     * @param x
     *            the x-coordinate of the vertical axis the new list is mirrored
     *            at. <code>x</code> must not be negative. It is usually set to
     *            half of the <code>nodeWidth</code> of the {@link Tree} this
     *            <code>BasicContourNodeList</code> belongs to.
     * @return a new <code>BasicContourNodeList</code> which corresponds to this
     *         list mirrored along the vertical axis at {@code x/2}.
     */
    public BasicContourNodeList getLeftContourOfFlippedRightContour(double x) {
        BasicContourNodeList flippedList = new BasicContourNodeList();
        for (ContourNode node = first.getNext().getNext(); node != null; node = node
                .getNext()) {
            flippedList.addNode(-node.getDx(), node.getDy());
        }
        return flippedList;
    }

    /**
     * Returns a new <code>BasicContourNodeList</code> which corresponds to this
     * list mirrored along the vertical axis at {@code x/2}. This
     * <code>BasicContourNodeList</code> should be used as a left contour. The
     * result can then be considered as a right contour.
     * 
     * @param x
     *            the x-coordinate of the vertical axis the new list is mirrored
     *            at. <code>x</code> must not be negative. It is usually set to
     *            half of the <code>nodeWidth</code> of the {@link Tree} this
     *            <code>BasicContourNodeList</code> belongs to.
     * @return a new <code>BasicContourNodeList</code> which corresponds to this
     *         list mirrored along the vertical axis at {@code x/2}.
     */
    public BasicContourNodeList getRightContourOfFlippedLeftContour(double x) {
        BasicContourNodeList flippedList = new BasicContourNodeList();
        flippedList.addNode(x, 0.0);
        for (ContourNode node = first.getNext(); node != null; node = node
                .getNext()) {
            flippedList.addNode(-node.getDx(), node.getDy());
        }
        return flippedList;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
