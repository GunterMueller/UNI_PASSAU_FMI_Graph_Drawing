// =============================================================================
//   LinkedList.java
// =============================================================================

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/*
 2    * Copyright 7-6 Sun Microsystems, Inc.  All Rights Reserved.
 3    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 4    *
 5    * This code is free software; you can redistribute it and/or modify it
 6    * under the terms of the GNU General Public License version 2 only, as
 7    * published by the Free Software Foundation.  Sun designates this
 8    * particular file as subject to the "Classpath" exception as provided
 9    * by Sun in the LICENSE file that accompanied this code.
 10    *
 11    * This code is distributed in the hope that it will be useful, but WITHOUT
 12    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 13    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 14    * version 2 for more details (a copy is included in the LICENSE file that
 15    * accompanied this code).
 16    *
 17    * You should have received a copy of the GNU General Public License version
 18    * 2 along with this work; if not, write to the Free Software Foundation,
 19    * Inc., 51 Franklin St, Fifth Floor, Boston, MA 10-1 USA.
 20    *
 21    * Please contact Sun Microsystems, Inc., 0 Network Circle, Santa Clara,
 22    * CA 54 USA or visit www.sun.com if you need additional information or
 23    * have any questions.
 24    */

/**
 * 29 * Linked list implementation of the <tt>List</tt> interface. Implements
 * all 30 * optional list operations, and permits all elements (including 31 *
 * <tt>null</tt>). In addition to implementing the <tt>List</tt> interface, 32 *
 * the <tt>LinkedList</tt> class provides uniformly named methods to 33 *
 * <tt>get</tt>, <tt>remove</tt> and <tt>insert</tt> an element at the 34 *
 * beginning and end of the list. These operations allow linked lists to be 35 *
 * used as a stack, { queue}, 36 *
 * <p>
 * 37 * 38 * The class implements the <tt>Deque</tt> interface, providing 39 *
 * first-in-first-out queue operations for <tt>add</tt>, 40 * <tt>poll</tt>,
 * along with other stack and deque operations.
 * <p>
 * 41 * 42 * All of the operations perform as could be expected for a
 * doubly-linked 43 * list. Operations that index into the list will traverse
 * the list from 44 * the beginning or the end, whichever is closer to the
 * specified index.
 * <p>
 * 45 * 46 *
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> 47 * If
 * multiple threads access a linked list concurrently, and at least 48 * one of
 * the threads modifies the list structurally, it <i>must</i> be 49 *
 * synchronized externally. (A structural modification is any operation 50 *
 * that adds or deletes one or more elements; merely setting the value of 51 *
 * an element is not a structural modification.) This is typically 52 *
 * accomplished by synchronizing on some object that naturally 53 * encapsulates
 * the list. 54 * 55 * If no such object exists, the list should be "wrapped"
 * using the 56 * {synchronizedList Collections.synchronizedList} 57 * method.
 * This is best done at creation time, to prevent accidental 58 * unsynchronized
 * access to the list:
 * 
 * <pre>
 * 59    *   List list = Collections.synchronizedList(new LinkedList(...));
 * </pre>
 * 
 * 60 * 61 *
 * <p>
 * The iterators returned by this class's <tt>iterator</tt> and 62 *
 * <tt>listIterator</tt> methods are <i>fail-fast</i>: if the list is 63 *
 * structurally modified at any time after the iterator is created, in 64 * any
 * way except through the Iterator's own <tt>remove</tt> or 65 * <tt>add</tt>
 * methods, the iterator will throw a { 66 * ConcurrentModificationException}.
 * Thus, in the face of concurrent 67 * modification, the iterator fails quickly
 * and cleanly, rather than 68 * risking arbitrary, non-deterministic behavior
 * at an undetermined 69 * time in the future. 70 * 71 *
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed 72 * as
 * it is, generally speaking, impossible to make any hard guarantees in the 73 *
 * presence of unsynchronized concurrent modification. Fail-fast iterators 74 *
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 75 *
 * Therefore, it would be wrong to write a program that depended on this 76 *
 * exception for its correctness: <i>the fail-fast behavior of iterators 77 *
 * should be used only to detect bugs.</i> 78 * 79 *
 * <p>
 * This class is a member of the 80 * <a href="{@docRoot}
 * /../technotes/guides/collections/index.html"> 81 * Java Collections
 * Framework</a>. 82 * 83 * @author Josh Bloch 84 * @see List 85 * @see
 * ArrayList 86 * @see Vector 87 * @since 1.2 88 * @param <E> the type of
 * elements held in this collection 89
 */
public class Liste<E> extends AbstractSequentialList<E> implements List<E>,
        Deque<E>, Cloneable, java.io.Serializable {
    private transient Entry<E> header = new Entry<E>(null, null, null);

    private transient int size = 0;

    /**
     * Constructs an empty list.
     */
    public Liste() {
        header.next = header.previous = header;
    }

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator.
     * 
     * @param c
     *            the collection whose elements are to be placed into this list
     * @throws NullPointerException
     *             if the specified collection is null
     */
    public Liste(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * Returns the first element in this list.
     * 
     * @return the first element in this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E getFirst() {
        if (size == 0)
            throw new NoSuchElementException();

        return header.next.element;
    }

    /**
     * Returns the last element in this list.
     * 
     * @return the last element in this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E getLast() {
        if (size == 0)
            throw new NoSuchElementException();

        return header.previous.element;
    }

    /**
     * Removes and returns the first element from this list.
     * 
     * @return the first element from this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E removeFirst() {
        return remove(header.next);
    }

    /**
     * Removes and returns the last element from this list.
     * 
     * @return the last element from this list
     * @throws NoSuchElementException
     *             if this list is empty
     */
    public E removeLast() {
        return remove(header.previous);
    }

    /**
     * Inserts the specified element at the beginning of this list.
     * 
     * @param e
     *            the element to add
     */
    public void addFirst(E e) {
        addBefore(e, header.next);
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * <p>
     * This method is equivalent to {@link #add}.
     * 
     * @param e
     *            the element to add
     */
    public void addLast(E e) {
        addBefore(e, header);
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 
     * @param o
     *            element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * <p>
     * This method is equivalent to {@link #addLast}.
     * 
     * @param e
     *            element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        addBefore(e, header);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If this list does not contain the element, it is
     * unchanged. More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists). Returns <tt>true</tt> if this list contained
     * the specified element (or equivalently, if this list changed as a result
     * of the call).
     * 
     * @param o
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (Entry<E> e = header.next; e != header; e = e.next) {
                if (e.element == null) {
                    remove(e);
                    return true;
                }
            }
        } else {
            for (Entry<E> e = header.next; e != header; e = e.next) {
                if (o.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator. The behavior of this operation is undefined if the
     * specified collection is modified while the operation is in progress.
     * (Note that this will occur if the specified collection is this list, and
     * it's nonempty.)
     * 
     * @param c
     *            collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException
     *             if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * Inserts all of the elements in the specified collection into this list,
     * starting at the specified position. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (increases
     * their indices). The new elements will appear in the list in the order
     * that they are returned by the specified collection's iterator.
     * 
     * @param index
     *            index at which to insert the first element from the specified
     *            collection
     * @param c
     *            collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     * @throws NullPointerException
     *             if the specified collection is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + size);
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;
        modCount++;

        Entry<E> successor = (index == size ? header : entry(index));
        Entry<E> predecessor = successor.previous;
        for (int i = 0; i < numNew; i++) {
            Entry<E> e = new Entry<E>((E) a[i], successor, predecessor);
            predecessor.next = e;
            predecessor = e;
        }
        successor.previous = predecessor;

        size += numNew;
        return true;
    }

    /**
     * Removes all of the elements from this list.
     */
    @Override
    public void clear() {
        Entry<E> e = header.next;
        while (e != header) {
            Entry<E> next = e.next;
            e.next = e.previous = null;
            e.element = null;
            e = next;
        }
        header.next = header.previous = header;
        size = 0;
        modCount++;
    }

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public E get(int index) {
        return entry(index).element;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * 
     * @param index
     *            index of the element to replace
     * @param element
     *            element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        Entry<E> e = entry(index);
        E oldVal = e.element;
        e.element = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     * 
     * @param index
     *            index at which the specified element is to be inserted
     * @param element
     *            element to be inserted
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        addBefore(element, (index == size ? header : entry(index)));
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     * 
     * @param index
     *            the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        return remove(entry(index));
    }

    /**
     * Returns the indexed entry.
     */
    private Entry<E> entry(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                    + size);
        Entry<E> e = header;
        if (index < (size >> 1)) {
            for (int i = 0; i <= index; i++) {
                e = e.next;
            }
        } else {
            for (int i = size; i > index; i--) {
                e = e.previous;
            }
        }
        return e;
    }

    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param o
     *            element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    @Override
    @SuppressWarnings("unchecked")
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Entry e = header.next; e != header; e = e.next) {
                if (e.element == null)
                    return index;
                index++;
            }
        } else {
            for (Entry e = header.next; e != header; e = e.next) {
                if (o.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally,
     * returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param o
     *            element to search for
     * @return the index of the last occurrence of the specified element in this
     *         list, or -1 if this list does not contain the element
     */
    @Override
    @SuppressWarnings("unchecked")
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (e.element == null)
                    return index;
            }
        } else {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (o.equals(e.element))
                    return index;
            }
        }
        return -1;
    }

    // Queue operations.

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     * 
     * @return the head of this list, or <tt>null</tt> if this list is empty
     * @since 1.5
     */
    public E peek() {
        if (size == 0)
            return null;
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     * 
     * @return the head of this list
     * @throws NoSuchElementException
     *             if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list
     * 
     * @return the head of this list, or <tt>null</tt> if this list is empty
     * @since 1.5
     */
    public E poll() {
        if (size == 0)
            return null;
        return removeFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     * 
     * @return the head of this list
     * @throws NoSuchElementException
     *             if this list is empty
     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     * 
     * @param e
     *            the element to add
     * @return <tt>true</tt>
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations
    /**
     * Inserts the specified element at the front of this list.
     * 
     * @param e
     *            the element to insert
     * @return <tt>true</tt> (as specified by {@link Deque#offerFirst})
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     * 
     * @param e
     *            the element to insert
     * @return <tt>true</tt> (as specified by {@link Deque#offerLast})
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list, or
     * returns <tt>null</tt> if this list is empty.
     * 
     * @return the first element of this list, or <tt>null</tt> if this list is
     *         empty
     * @since 1.6
     */
    public E peekFirst() {
        if (size == 0)
            return null;
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the last element of this list, or returns
     * <tt>null</tt> if this list is empty.
     * 
     * @return the last element of this list, or <tt>null</tt> if this list is
     *         empty
     * @since 1.6
     */
    public E peekLast() {
        if (size == 0)
            return null;
        return getLast();
    }

    /**
     * Retrieves and removes the first element of this list, or returns
     * <tt>null</tt> if this list is empty.
     * 
     * @return the first element of this list, or <tt>null</tt> if this list is
     *         empty
     * @since 1.6
     */
    public E pollFirst() {
        if (size == 0)
            return null;
        return removeFirst();
    }

    /**
     * Retrieves and removes the last element of this list, or returns
     * <tt>null</tt> if this list is empty.
     * 
     * @return the last element of this list, or <tt>null</tt> if this list is
     *         empty
     * @since 1.6
     */
    public E pollLast() {
        if (size == 0)
            return null;
        return removeLast();
    }

    /**
     * Pushes an element onto the stack represented by this list. In other
     * words, inserts the element at the front of this list.
     * 
     * <p>
     * This method is equivalent to {@link #addFirst}.
     * 
     * @param e
     *            the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list. In other words,
     * removes and returns the first element of this list.
     * 
     * <p>
     * This method is equivalent to {@link #removeFirst()}.
     * 
     * @return the element at the front of this list (which is the top of the
     *         stack represented by this list)
     * @throws NoSuchElementException
     *             if this list is empty
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * Removes the first occurrence of the specified element in this list (when
     * traversing the list from head to tail). If the list does not contain the
     * element, it is unchanged.
     * 
     * @param o
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this list (when
     * traversing the list from head to tail). If the list does not contain the
     * element, it is unchanged.
     * 
     * @param o
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Entry<E> e = header.previous; e != header; e = e.previous) {
                if (e.element == null) {
                    remove(e);
                    return true;
                }
            }
        } else {
            for (Entry<E> e = header.previous; e != header; e = e.previous) {
                if (o.equals(e.element)) {
                    remove(e);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list. Obeys the
     * general contract of <tt>List.listIterator(int)</tt>.
     * <p>
     * 
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own <tt>remove</tt> or <tt>add</tt> methods,
     * the list-iterator will throw a <tt>ConcurrentModificationException</tt>.
     * Thus, in the face of concurrent modification, the iterator fails quickly
     * and cleanly, rather than risking arbitrary, non-deterministic behavior at
     * an undetermined time in the future.
     * 
     * @param index
     *            index of the first element to be returned from the
     *            list-iterator (by a call to <tt>next</tt>)
     * @return a ListIterator of the elements in this list (in proper sequence),
     *         starting at the specified position in the list
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     * @see List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Entry<E> lastReturned = header;

        private Entry<E> next;

        private int nextIndex;

        private int expectedModCount = modCount;

        ListItr(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("Index: " + index
                        + ", Size: " + size);
            if (index < (size >> 1)) {
                next = header.next;
                for (nextIndex = 0; nextIndex < index; nextIndex++) {
                    next = next.next;
                }
            } else {
                next = header;
                for (nextIndex = size; nextIndex > index; nextIndex--) {
                    next = next.previous;
                }
            }
        }

        public boolean hasNext() {
            return nextIndex != size;
        }

        public E next() {
            checkForComodification();
            if (nextIndex == size)
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.element;
        }

        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        public E previous() {
            if (nextIndex == 0)
                throw new NoSuchElementException();

            lastReturned = next = next.previous;
            nextIndex--;
            checkForComodification();
            return lastReturned.element;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            Entry<E> lastNext = lastReturned.next;
            try {
                Liste.this.remove(lastReturned);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException();
            }
            if (next == lastReturned) {
                next = lastNext;
            } else {
                nextIndex--;
            }
            lastReturned = header;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == header)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.element = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = header;
            addBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    public static class Entry<E> {
        E element;

        Entry<E> next;

        Entry<E> previous;

        int location;

        Entry(E element, Entry<E> next, Entry<E> previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
            this.location = 0;
        }
    }

    private Entry<E> addBefore(E e, Entry<E> entry) {
        Entry<E> newEntry = new Entry<E>(e, entry, entry.previous);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        size++;
        modCount++;
        newEntry.location = newEntry.previous.location + 1;

        for (Entry<E> it = newEntry.next; it != header; it = it.next) {
            it.location++;
        }

        return newEntry;
    }

    private E remove(Entry<E> e) {
        if (e == header)
            throw new NoSuchElementException();
        if (size > 1) {
            for (Entry<E> it = e.next; it != header; it = it.next) {
                it.location--;
            }
        }

        E result = e.element;
        e.previous.next = e.next;
        e.next.previous = e.previous;
        e.next = e.previous = null;
        e.element = null;
        size--;
        modCount++;
        return result;
    }

    /**
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /** Adapter to provide descending iterators via ListItr.previous */
    @SuppressWarnings("unchecked")
    private class DescendingIterator implements Iterator {
        final ListItr itr = new ListItr(size());

        public boolean hasNext() {
            return itr.hasPrevious();
        }

        public E next() {
            return itr.previous();
        }

        public void remove() {
            itr.remove();
        }
    }

    /**
     * Returns a shallow copy of this <tt>LinkedList</tt>. (The elements
     * themselves are not cloned.)
     * 
     * @return a shallow copy of this <tt>LinkedList</tt> instance
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        Liste<E> clone = null;
        try {
            clone = (Liste<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }

        // Put clone into "virgin" state
        clone.header = new Entry<E>(null, null, null);
        clone.header.next = clone.header.previous = clone.header;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Entry<E> e = header.next; e != header; e = e.next) {
            clone.add(e.element);
        }

        return clone;
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * 
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new
     * array). The caller is thus free to modify the returned array.
     * 
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     * 
     * @return an array containing all of the elements in this list in proper
     *         sequence
     */
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Entry<E> e = header.next; e != header; e = e.next) {
            result[i++] = e.element;
        }
        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array. If the list fits in the specified
     * array, it is returned therein. Otherwise, a new array is allocated with
     * the runtime type of the specified array and the size of this list.
     * 
     * <p>
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the list is set to <tt>null</tt>. (This
     * is useful in determining the length of the list <i>only</i> if the caller
     * knows that the list does not contain any null elements.)
     * 
     * <p>
     * Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs. Further, this method allows
     * precise control over the runtime type of the output array, and may, under
     * certain circumstances, be used to save allocation costs.
     * 
     * <p>
     * Suppose <tt>x</tt> is a list known to contain only strings. The following
     * code can be used to dump the list into a newly allocated array of
     * <tt>String</tt>:
     * 
     * <pre>
     * String[] y = x.toArray(new String[0]);
     * </pre>
     * 
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     * 
     * @param a
     *            the array into which the elements of the list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException
     *             if the runtime type of the specified array is not a supertype
     *             of the runtime type of every element in this list
     * @throws NullPointerException
     *             if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                    .getComponentType(), size);
        }
        int i = 0;
        Object[] result = a;
        for (Entry<E> e = header.next; e != header; e = e.next) {
            result[i++] = e.element;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * Save the state of this <tt>LinkedList</tt> instance to a stream (that is,
     * serialize it).
     * 
     * @serialData The size of the list (the number of elements it contains) is
     *             emitted (int), followed by all of its elements (each an
     *             Object) in the proper order.
     */
    @SuppressWarnings("unchecked")
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Entry e = header.next; e != header; e = e.next) {
            s.writeObject(e.element);
        }
    }

    /**
     * Reconstitute this <tt>LinkedList</tt> instance from a stream (that is
     * deserialize it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Initialize header
        header = new Entry<E>(null, null, null);
        header.next = header.previous = header;

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++) {
            addBefore((E) s.readObject(), header);
        }
    }

    public Entry<E> getPointerToLastElem() {
        return header.previous;
    }

    public Entry<E> getPointerToFirstElem() {
        return header.next;
    }

    public Liste<E> cut(Entry<E> e) {
        Liste<E> ergebnis = new Liste<E>();
        if (e.next != header) {

            ergebnis.size = this.size - e.location;
            this.size = e.location;
            Entry<E> anfangNeue = e.next;
            Entry<E> endeNeue = header.previous;

            ergebnis.header.next = anfangNeue;
            anfangNeue.previous = ergebnis.header;

            endeNeue.next = ergebnis.header; // Letzte
            ergebnis.header.previous = endeNeue;

            // e ist der letzte
            header.previous = e;
            e.next = header;

            for (Entry<E> it = ergebnis.header.next; it != ergebnis.header; it = it.next) {
                it.location = it.location - e.location;
            }
        }
        return ergebnis;

    }

    public Liste<E> cutBefore(Entry<E> e) {
        Liste<E> ergebnis = new Liste<E>();
        ergebnis.size = this.size - e.location + 1;
        this.size = e.location - 1;
        Entry<E> anfangNeue = e;
        Entry<E> endeNeue = header.previous;

        header.previous = e.previous;
        e.previous.next = header;

        ergebnis.header.next = anfangNeue;
        anfangNeue.previous = ergebnis.header;

        endeNeue.next = ergebnis.header; // Letzte
        ergebnis.header.previous = endeNeue;
        int sub = e.location;
        for (Entry<E> it = ergebnis.header.next; it != ergebnis.header; it = it.next) {
            it.location = it.location - sub + 1;
            // e.previous ist der letzte
        }

        return ergebnis;
    }

    /*
     * F�gt "liste" am Ende von "this" an, das objekt liste bleibt erhalten
     */
    public void addListe(Liste<E> liste) {
        if (!liste.isEmpty()) {
            this.header.previous.next = liste.header.next;

            liste.header.next.previous = this.header.previous;

            liste.header.previous.next = this.header;

            this.header.previous = liste.header.previous;

            for (Entry<E> it = liste.header.next; it != header; it = it.next) {
                it.location = it.location + this.size;
            }

            this.size += liste.size;
        }

    }

    /**
     * @param location
     */
    public Entry<E> getListElem(int location) {
        return entry(location);
    }

}
