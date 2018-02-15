// =============================================================================
//
//   ListAnchor.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultiLinkedList.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.AbstractSequentialList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An doubly linked list implementation of the {@link List} interface that
 * performs the {@link #remove(Object)} operation in constant time. For this to
 * be possible, the contained elements must implement the {@link MultiLinkable}
 * interface, i. e. they must provide storage for the linking information.
 * 
 * It is also supported that the same element is contained in more than one
 * {@link MultiLinkedList}s. Because of that the access to the link nodes is
 * indexed.
 * 
 * @param <Element>
 *            The type of the elements contained in the list.
 * @param <LinkType>
 *            The class (typically an enum) that is used to specify which list
 *            to use if the element is contained in more than one
 *            {@link MultiLinkedList}.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2009-11-14 17:30:13 +0100 (Sa, 14 Nov 2009)
 *          $
 */
public class MultiLinkedList<Element extends MultiLinkable<Element, LinkType>, LinkType>
        extends AbstractSequentialList<Element> {
    /**
     * Pointers to the first and last element in the list. From another point of
     * view: The end node in the circular implementation of the list
     */
    private MultiLinkNode<Element> anchor;

    /** The number of elements currently contained in the list */
    private int size;

    /**
     * The identifier with which the link nodes are indexed in the
     * {@link MultiLinkable#getLinkNode(Object)} and
     * {@link MultiLinkable#setLinkNode(Object, MultiLinkNode)} methods
     */
    private LinkType linkType;

    /**
     * Creates a new empty list with the given identifier for the link nodes.
     * 
     * @param linkType
     *            The identifier with which the link nodes are indexed in the
     *            {@link MultiLinkable#getLinkNode(Object)} and
     *            {@link MultiLinkable#setLinkNode(Object, MultiLinkNode)}
     *            methods
     */
    public MultiLinkedList(LinkType linkType) {
        this.linkType = linkType;

        clear();
    }

    /*
     * @see java.util.AbstractList#clear()
     */
    @Override
    public void clear() {
        // In an empty list the link structure consists of a single empty node
        // that points to itself in both directions.

        anchor = new MultiLinkNode<Element>();
        anchor.next = anchor;
        anchor.prev = anchor;

        size = 0;
        modCount++;
    }

    /*
     * @see java.util.AbstractSequentialList#listIterator(int)
     */
    @Override
    public ListIterator<Element> listIterator(int index) {
        return new Iter(index);
    }

    /*
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object element) {
        if (element == null)
            throw new NullPointerException("null values are not supported");

        // Because of erasure we cannot check for MultLinkable<Element>, so
        // the following must do

        if (!(element instanceof MultiLinkable<?, ?>))
            return false;

        @SuppressWarnings("unchecked")
        Element e = (Element) element;

        MultiLinkNode<Element> link = e.getLinkNode(linkType);

        // element not part of this list
        if (link == null)
            return false;

        link.next.prev = link.prev;
        link.prev.next = link.next;

        --size;
        modCount++;

        // element removed
        return true;
    }

    /*
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Iterator type for the containing list implementation.
     * 
     * @author Michael Forster
     * @version $Revision: 5767 $ $Date: 2009-11-14 17:30:13 +0100 (Sa, 14 Nov
     *          2009) $
     */
    private class Iter implements ListIterator<Element> {
        /** @see #nextIndex() */
        private int nextIndex;

        /** The link node of the {@code previous()} element */
        private MultiLinkNode<Element> linkNode;

        /**
         * The element that was returned by the last call to {@code next()}
         * resp. {@code previous()}
         */
        private Element previousReturned;

        /**
         * Used to check for concurrent modifications which will invalidate this
         * iterator.
         */
        private int expectedModCount = modCount;

        /**
         * Creates a new iterator over the containing list.
         * 
         * @param index
         *            Index of the start position.
         */
        Iter(int index) {
            // first check index value
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException();

            this.linkNode = anchor;

            if (index <= size / 2) {
                this.nextIndex = 0;
                for (int i = 0; i < index; ++i) {
                    next();
                }
            } else {
                // previous is called once more for the anchor element
                // therefor we need size + 1 instead of just size
                this.nextIndex = size + 1;
                for (int i = 0; i < size + 1 - index; ++i) {
                    previous();
                }
            }
        }

        /*
         * @see java.util.ListIterator#add(java.lang.Object)
         */
        public void add(Element element) {
            if (element == null)
                throw new IllegalArgumentException(
                        "null values are not supported");

            checkForComodification();
            MultiLinkNode<Element> newLink = new MultiLinkNode<Element>();

            newLink.content = element;
            element.setLinkNode(linkType, newLink);

            newLink.next = linkNode.next;
            newLink.prev = linkNode;
            linkNode.next.prev = newLink;
            linkNode.next = newLink;

            linkNode = newLink;
            ++nextIndex;

            ++size;
            expectedModCount = ++modCount;
            previousReturned = null;
        }

        /*
         * @see java.util.ListIterator#hasNext()
         */
        public boolean hasNext() {
            return nextIndex != size;
        }

        /*
         * @see java.util.ListIterator#hasPrevious()
         */
        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        /*
         * @see java.util.ListIterator#next()
         */
        public Element next() {
            checkForComodification();
            if (nextIndex >= size)
                throw new NoSuchElementException();

            previousReturned = linkNode.next.content;

            linkNode = linkNode.next;
            nextIndex++;

            return previousReturned;
        }

        /*
         * @see java.util.ListIterator#nextIndex()
         */
        public int nextIndex() {
            return nextIndex;
        }

        /*
         * @see java.util.ListIterator#previous()
         */
        public Element previous() {
            checkForComodification();
            if (nextIndex <= 0)
                throw new NoSuchElementException();

            linkNode = linkNode.prev;
            nextIndex--;

            previousReturned = linkNode.next.content;

            return previousReturned;
        }

        /*
         * @see java.util.ListIterator#previousIndex()
         */
        public int previousIndex() {
            return nextIndex - 1;
        }

        /*
         * @see java.util.ListIterator#remove()
         */
        public void remove() {
            checkForComodification();
            if (previousReturned == null)
                throw new IllegalStateException(
                        "add() or remove() already called.");

            MultiLinkNode<Element> previousNode = previousReturned
                    .getLinkNode(linkType);

            if (previousNode != linkNode.next) {
                nextIndex--;
            }

            if (previousNode == linkNode) {
                linkNode = linkNode.prev;
            }

            previousNode.next.prev = previousNode.prev;
            previousNode.prev.next = previousNode.next;

            size--;
            expectedModCount = ++modCount;

            previousReturned = null;
        }

        /*
         * @see java.util.ListIterator#set(java.lang.Object)
         */
        public void set(Element element) {
            if (element == null)
                throw new IllegalArgumentException(
                        "null values are not supported");

            checkForComodification();

            if (previousReturned == null)
                throw new IllegalStateException(
                        "add() or remove() already called.");

            MultiLinkNode<Element> previousNode = previousReturned
                    .getLinkNode(linkType);

            previousNode.content = element;
            element.setLinkNode(linkType, previousNode);
        }

        /**
         * Checks for any modification which will invalidate the iterator.
         * 
         * @throws ConcurrentModificationException
         *             if the iterator was invalidated.
         */
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
