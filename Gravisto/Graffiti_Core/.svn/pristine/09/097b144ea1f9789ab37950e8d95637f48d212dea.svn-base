// =============================================================================
//
//   TranslatedContourNodeList.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.util.NoSuchElementException;

import org.graffiti.util.Reference;

/**
 * A <code>ContourNodeList</code>, which is constructed from another
 * <code>ContourNodeList</code> in order to achieve vertical distance
 * constraints of tree layouts. This does not permanently hold a linked list of
 * contour nodes but rather build and drop the nodes on the fly when the
 * iterators returned by this <code>ContourNodeList</code> are accessed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TranslatedContourNodeList extends ContourNodeList {
    /**
     * A contour node used internally by <code>TranslatedContourNodeList</code>.
     * The contour nodes are built and dropped on the fly when the iterators
     * returned by the <code>TranslatedContourNodeList</code> are accessed.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class Node {
        /**
         * x-coordinate of this <code>Node</code> in the coordinate systen of
         * the <code>baseList</code> contour line.
         */
        private double x;

        /**
         * y-coordinate of this <code>Node</code> in the coordinate systen of
         * the <code>baseList</code> contour line.
         */
        private double y;

        /**
         * The next <code>Node</code> in this contour line.
         */
        private Node next;

        /**
         * Points to the last contour node of the <code>baseList</code> contour
         * line with the greatest y-coordinate less than or equal to {@link #y}.
         */
        private ContourNodeIterator base;

        /**
         * Returns if this <code>Node</code> will not be changed anymore in any
         * field except for {@link #next}.
         */
        private boolean isFinished;

        /**
         * Creates a new <code>Node</code>.
         * 
         * @param x
         *            See {@link #x}.
         * @param y
         *            See {@link #y}.
         * @param base
         *            See {@link #base}.
         */
        private Node(double x, double y, ContourNodeIterator base) {
            this.x = x;
            this.y = y;
            this.base = base;
            isFinished = false;
        }
    }

    /**
     * The <code>ContourNodeIterator</code> returned by this
     * <code>ContourNodeList</code>.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class Iterator implements ContourNodeIterator {
        /**
         * The <code>Node</code> this iterator is currently pointing to.
         * <code>currentNode</code> must always be a finished <code>Node</code>.
         * 
         * @see Node#isFinished
         */
        private Node currentNode;

        /**
         * The <code>Node</code> in this list that last become safe from changes
         * as the vertical distance from unprocessed contour nodes to that
         * contour node is greater than
         * {@link TranslatedContourNodeList#minimalVerticalDistance} so that the
         * node cannot be affected by further processing steps. Older finished
         * contour nodes are dropped.
         * 
         * @see Node#isFinished
         * @see #finishNextNode()
         */
        private Reference<Node> lastFinishedNode;

        /**
         * Points to the last used node of the <code>baseList</code> used in a
         * processing step.
         * 
         * @see #finishNextNode()
         */
        private ContourNodeIterator lastUsedNode;

        /**
         * {@inheritDoc}
         */
        public double getX() {
            if (currentNode == null)
                throw new NoSuchElementException();
            return currentNode.x;
        }

        /**
         * {@inheritDoc}
         */
        public double getY() {
            if (currentNode == null)
                throw new NoSuchElementException();
            return currentNode.y;
        }

        /**
         * Copies this iterator. The copy and this iterator share the
         * {@link #lastUsedNode} iterator, the {@link #lastFinishedNode}
         * reference and hence the linked list of temporarily constructed
         * {@link Node}s. Both iterators may be used to advance in the
         * processing.
         * 
         * @return a copy of this iterator.
         */
        @Override
        public Iterator clone() {
            return new Iterator(this);
        }

        /**
         * After a call to <code>increment</code>, this iterator points to the
         * next contour node. If that contour node has not been finished yet,
         * {@link #finishNextNode()} is called.
         */
        public void increment() {
            if (currentNode == null)
                throw new NoSuchElementException();
            Node next = currentNode.next;
            if (next == null || !next.isFinished) {
                finishNextNode();
            }
            currentNode = currentNode.next;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEnd() {
            return currentNode == null;
        }

        /**
         * Creates a new iterator that points to the first contour node in the
         * list.
         */
        public Iterator() {
            lastFinishedNode = new Reference<Node>();
            lastUsedNode = baseList.getFirst();
            Node newNode = new Node(0.0, 0.0, lastUsedNode);
            newNode.isFinished = true;
            lastFinishedNode.set(newNode);
            this.currentNode = newNode;
        }

        /**
         * Used by {@link #clone()} to create a copy of <code>other</code>.
         * 
         * @param other
         *            the iterator being copied.
         */
        private Iterator(Iterator other) {
            currentNode = other.currentNode;
            lastFinishedNode = other.lastFinishedNode;
            lastUsedNode = other.lastUsedNode;
        }

        /**
         * {@inheritDoc}
         */
        public BasicContourNodeList.Iterator getBaseIterator() {
            if (currentNode == null)
                return lastFinishedNode.get().base.getBaseIterator();
            return currentNode.base.getBaseIterator();
        }

        /**
         * Updates after a processing step by {@link #processLine()} the
         * {@link Node#isFinished} flags. All contour nodes except the last one
         * that are finished are dropped out of the list.
         * 
         * @return <code>true</code> if at least on more contour node was
         *         finished by <code>processLine</code>;<br>
         *         <code>false</code> otherwise.
         * 
         */
        private boolean updateFinishedFlags() {
            if (lastUsedNode.isLast()) {
                Node node;
                for (node = lastFinishedNode.get(); node.next != null; node = node.next) {
                    node.next.isFinished = true;
                }
                lastFinishedNode.set(node);
                return true;
            }
            Node nextToFinish = lastFinishedNode.get().next;
            Node lastFinished = null;
            while (nextToFinish != null
                    && nextToFinish.y < lastUsedNode.getY()
                            - minimalVerticalDistance) {
                nextToFinish.isFinished = true;
                lastFinished = nextToFinish;
                nextToFinish = nextToFinish.next;
            }
            if (lastFinished != null) {
                lastFinishedNode.set(lastFinished);
                return true;
            }
            return false;
        }

        /**
         * Uses the next contour node of the <code>baseList</code> contour list
         * to construct this contour list. {@link #lastUsedNode} is incremented.
         */
        private void processLine() {
            if (lastUsedNode.isLast())
                return;
            ContourNodeIterator base1 = lastUsedNode.getBaseIterator();
            double x1 = lastUsedNode.getX();
            double y1 = lastUsedNode.getY();
            lastUsedNode.increment();
            ContourNodeIterator base2 = lastUsedNode.getBaseIterator();
            double x2 = lastUsedNode.getX();
            double y2 = lastUsedNode.getY();
            double dx = lastUsedNode.getDx();
            double dy = lastUsedNode.getDy();
            if (dy == 0) {
                double maxX = Math.max(x1, x2);
                addLine(maxX, y1 - minimalVerticalDistance, maxX, y1, null);
                addLine(maxX, y1, maxX, y1 + minimalVerticalDistance, base2);
            } else {
                if (dx < 0) {
                    addLine(x1, y1 - minimalVerticalDistance, x1, y1, null);
                    if (minimalVerticalDistance < dy) {
                        addLine(x1, y1, x1, y1 + minimalVerticalDistance, base1);
                        double intX = x2 - minimalVerticalDistance / dy * dx;
                        addLine(x1, y1 + minimalVerticalDistance, intX, y2,
                                base1);
                        addLine(intX, y2, x2, y2 + minimalVerticalDistance,
                                base2);

                    } else if (minimalVerticalDistance > dy) {
                        addLine(x1, y1, x1, y2, base1);
                        addLine(x1, y2, x1, y1 + minimalVerticalDistance, base2);
                        addLine(x1, y1 + minimalVerticalDistance, x2, y2
                                + minimalVerticalDistance, base2);
                    } else {
                        addLine(x1, y1, x1, y2, base1);
                        addLine(x1, y2, x2, y2 + minimalVerticalDistance, base2);
                    }
                } else if (dx == 0) {
                    addLine(x1, y1 - minimalVerticalDistance, x1, y1, null);
                    addLine(x1, y1, x2, y2, base1);
                    addLine(x2, y2, x2, y2 + minimalVerticalDistance, base2);
                } else {
                    if (minimalVerticalDistance < dy) {
                        double intX = x1 + minimalVerticalDistance / dy * dx;
                        addLine(x1, y1 - minimalVerticalDistance, intX, y1,
                                null);
                        addLine(intX, y1, x2, y2 - minimalVerticalDistance,
                                base1);
                        addLine(x2, y2 - minimalVerticalDistance, x2, y2, base1);
                    } else if (minimalVerticalDistance > dy) {
                        addLine(x1, y1 - minimalVerticalDistance, x2, y2
                                - minimalVerticalDistance, null);
                        addLine(x2, y2 - minimalVerticalDistance, x2, y1, null);
                        addLine(x2, y1, x2, y2, base1);
                    } else {
                        addLine(x1, y1 - minimalVerticalDistance, x2, y1, null);
                        addLine(x2, y1, x2, y2, base1);
                    }
                    addLine(x2, y2, x2, y2 + minimalVerticalDistance, base2);
                }
            }
        }

        /**
         * Sets the extended contour line to the (in terms of x-coordinate)
         * maximum of the current translated contour line and the line defined
         * by (<code>x1</code>, <code>y1</code>) (<code>x2</code>,
         * <code>y2</code>).
         * 
         * @param x1
         *            the x-coordinate of the start point of the line to add.
         * @param y1
         *            the y-coordinate of the start point of the line to add.
         * @param x2
         *            the x-coordinate of the end point of the line to add.
         * @param y2
         *            the y-coordinate of the end point of the line to add.
         * @param newBase
         *            points to the original contour line's contour node that is
         *            the responsible for the shape of the translated contour
         *            line where the added line has points with greater
         *            x-coordinates than the previous shape of the translated
         *            contour line.
         */
        private void addLine(double x1, double y1, double x2, double y2,
                ContourNodeIterator newBase) {
            if (y2 < 0)
                return;
            if (y1 < 0) {
                if (y2 == 0)
                    return;
                x1 = x2 - (x2 - x1) * y2 / (y2 - y1);
                y1 = 0;
            }
            Node node = lastFinishedNode.get();
            while (node.next != null && node.next.y < y1) {
                node = node.next;
            }
            if (node.next == null) {
                if (node.y != y1) {
                    node.next = new Node(node.x, y1, node.base);
                    node = node.next;
                }
                if (node.x != x1) {
                    node.next = new Node(x1, y1, node.base);
                    node = node.next;
                }
                node.next = new Node(x2, y2, newBase);
                return;
            }
            double qdxdy = (x2 - x1) / (y2 - y1);
            if (node.next.y != y1) {
                double intX = (node.next.x - node.x) * (y1 - node.y)
                        / (node.next.y - node.y);
                Node newNode = new Node(intX, y1, node.base);
                newNode.next = node.next;
                node.next = newNode;
            }
            Node dummy = node.next;
            Node newestNode = addNode(node, Math.max(x1, node.next.x), y1,
                    newBase == null ? node.next.base : newBase);
            node = dummy;
            double lx1;
            double lx2 = x1;
            while (node.next != null) {
                lx1 = x1 + (x2 - x1) * (node.y - y1) / (y2 - y1);
                lx2 = x1 + (x2 - x1) * (node.next.y - y1) / (y2 - y1);
                if ((lx1 < node.x && lx2 > node.next.x)
                        || (lx1 > node.x && lx2 < node.next.x)) {
                    double intY;
                    if (node.y != node.next.y) {
                        intY = node.y
                                + (lx1 - node.x)
                                / ((node.next.x - node.x)
                                        / (node.next.y - node.y) - qdxdy);
                    } else {
                        intY = node.y;
                    }
                    if (intY > y2) {
                        break;
                    }
                    double intX = x1 + (intY - y1) * qdxdy;
                    newestNode = addNode(newestNode, intX, intY,
                            newBase == null ? node.next.base : newBase);
                }
                if (node.next.y > y2) {
                    break;
                }
                newestNode = addNode(newestNode, Math.max(lx2, node.next.x),
                        node.next.y, newBase == null ? node.next.base : newBase);
                node = node.next;
            }
            if (node.next == null) {
                newestNode = addNode(newestNode, lx2, node.y,
                        newBase == null ? node.base : newBase);
                newestNode = addNode(newestNode, x2, y2,
                        newBase == null ? node.base : newBase);
            } else {
                double intX = node.x + (y2 - node.y) * (node.next.x - node.x)
                        / (node.next.y - node.y);
                newestNode = addNode(newestNode, intX, y2,
                        newBase == null ? node.next.base : newBase);
                newestNode.next = node.next;
            }
        }

        /**
         * Creates a new <code>Node</code>, adds it as the succedessor of
         * <code>node</code> and returns it. If <code>node</code> has the
         * coordinates (<code>x</code>, <code>y</code>) no new node is created
         * and <code>node</code> is returned instead.
         * 
         * @param node
         *            the node after that the new node is added.
         * @param x
         *            the x-coordinate of the new node.
         * @param y
         *            the y-coordinate of the new node.
         * @param base
         *            points to the node in the original contour node list that
         *            is responsible for the addition of <code>node</code>.
         * @return a new <code>Node</code> if <code>node</code> does not have
         *         the coordinates (<code>x</code>, <code>y</code>);
         *         <code>node</code> otherwise.
         */
        private Node addNode(Node node, double x, double y,
                ContourNodeIterator base) {
            if (node.x == x && node.y == y) {
                node.base = base;
                return node;
            }
            node.next = new Node(x, y, base);
            return node.next;
        }

        /**
         * Repeatedly processes the next contour nodes of the
         * <code>baseList</code> contour until at least one more translated
         * contour node has been finished.
         * 
         */
        private void finishNextNode() {
            do {
                processLine();
            } while (!updateFinishedFlags());
        }

        /**
         * Throws an <code>UnsupportedOperationException</code>.
         * 
         * @throws UnsupportedOperationException
         */
        public double getDx() {
            throw new UnsupportedOperationException("Will not be implemented.");
        }

        /**
         * Throws an <code>UnsupportedOperationException</code>.
         * 
         * @throws UnsupportedOperationException
         */
        public double getDy() {
            throw new UnsupportedOperationException("Will not be implemented.");
        }

        /**
         * Throws an <code>UnsupportedOperationException</code>.
         * 
         * @throws UnsupportedOperationException
         */
        public boolean isLast() {
            throw new UnsupportedOperationException("Will not be implemented.");
        }
    }

    /**
     * See {@link ReingoldTilfordAlgorithm#getMinimalVerticalDistance()}.
     * 
     * @see ReingoldTilfordAlgorithm#minimalHorizontalDistance
     */
    private double minimalVerticalDistance;

    /**
     * The ContourNodeList from which this ContourNodeList is constructed.
     */
    private ContourNodeList baseList;

    /**
     * Creates a new <code>TranslatedContourNodeList</code> based on
     * <code>list</code>.
     * 
     * @param minimalVerticalDistance
     *            See
     *            {@link ReingoldTilfordAlgorithm#getMinimalVerticalDistance()}.
     * @param list
     *            the contour <code>list</code> the new contour ist based on.
     */
    TranslatedContourNodeList(double minimalVerticalDistance,
            ContourNodeList list) {
        this.minimalVerticalDistance = minimalVerticalDistance;
        this.baseList = list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContourNodeIterator getFirst() {
        return new Iterator();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
