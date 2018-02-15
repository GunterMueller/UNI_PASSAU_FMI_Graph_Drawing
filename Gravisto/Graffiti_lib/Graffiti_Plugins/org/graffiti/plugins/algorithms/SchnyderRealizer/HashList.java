package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class implements a ring buffer of elements of type <code>T</code> Each
 * element is stored in a <code>HashListNode</code>. As all
 * <code>HashListNode</code>s are saved in a <code>HashMap</code> with the
 * wrapped element as the key it is possible to remove an element without
 * running through the list.
 * 
 * @author hofmeier
 */
public class HashList<T extends Object> {

    /**
     * The elements saved in the ring buffer are the keys, the
     * <code>HashListNode</code>s are the values.
     */
    private HashMap<T, HashListNode> nodes = new HashMap<T, HashListNode>();

    /** Marks the "beginning" of the ring buffer */
    private HashListNode dummy;

    /**
     * Creates a new ring buffer
     */
    public HashList() {
        this.dummy = new HashListNode(null);
        this.dummy.next = dummy;
        this.dummy.pre = dummy;
    }

    /**
     * Appends a new element to the end of the ring buffer, that is the positon
     * before the <code>dummy</code>. Does not do anything if the ring buffer
     * already contains the element.
     * 
     * @param n
     *            the element to append.
     */
    public void append(T n) {
        if (!this.contains(n)) {
            HashListNode s = new HashListNode(n);
            s.next = this.dummy;
            s.pre = this.dummy.pre;
            this.dummy.pre.next = s;
            this.dummy.pre = s;
            this.nodes.put(n, s);
        }
    }

    /**
     * Appends all elements contained in a given <code>HashList</code> to this
     * <code>HashList</code>. The order of the elements of the given ring buffer
     * will be reversed in this ring buffer.
     * 
     * @param hl
     *            the given list, whose elements will be appended to this list.
     */
    public void append(HashList<T> hl) {
        if (!hl.isEmpty()) {
            this.append(hl.getFirst());
            T element = hl.getNextNeighbor(hl.getFirst());
            while (element != hl.getFirst()) {
                this.append(element);
                element = hl.getNextNeighbor(element);
            }
        }
    }

    /**
     * Adds a new element to the ring buffer at the position after a given
     * element.
     * 
     * @param pre
     *            the element to add after.
     * @param toInsert
     *            the element to add.
     */
    public void addAfter(T pre, T toInsert) {
        HashListNode s = new HashListNode(toInsert);
        HashListNode preNode = this.nodes.get(pre);
        assert (preNode != null) : "The node to insert after is not in the list";
        if (!this.contains(toInsert)) {
            s.next = preNode.next;
            s.pre = preNode;
            preNode.next.pre = s;
            preNode.next = s;
            this.nodes.put(toInsert, s);
        }
    }

    /**
     * Adds a new element to the ring buffer at the position before a given
     * element.
     * 
     * @param next
     *            the element to add before.
     * @param toInsert
     *            the element to add.
     */
    public void addBefore(T next, T toInsert) {
        HashListNode s = new HashListNode(toInsert);
        HashListNode nextNode = this.nodes.get(next);
        assert (nextNode != null) : "The node to insert before is not in the list";
        if (!this.contains(toInsert)) {
            s.next = nextNode;
            s.pre = nextNode.pre;
            nextNode.pre.next = s;
            nextNode.pre = s;
            this.nodes.put(toInsert, s);
        }
    }

    /**
     * Removes an element from the ring buffer.
     * 
     * @param n
     *            the element to remove.
     * @return true if the element was removed, false if the ring buffer did not
     *         contain the element.
     */
    public boolean remove(T n) {
        if (this.contains(n)) {
            HashListNode s = this.nodes.remove(n);
            s.next.pre = s.pre;
            s.pre.next = s.next;
            return true;
        }
        return false;
    }

    /**
     * Returns an iterator over all elements of the ring buffer.
     * 
     * @return an iterator over all elements of the ring buffer.
     */
    public Iterator<T> iterator() {
        LinkedList<T> list = new LinkedList<T>();
        HashListNode current = this.dummy.next;
        while (current != this.dummy) {
            list.addLast(current.wrapped);
            current = current.next;
        }
        return list.iterator();
    }

    /**
     * Returns the element saved after a given element.
     * 
     * @param n
     *            the given element.
     * @return the element at the position after the given element.
     */
    public T getNextNeighbor(T n) {
        assert (this.contains(n)) : "List does not contain this element";
        if (this.nodes.get(n).next == this.dummy)
            return dummy.next.wrapped;
        return this.nodes.get(n).next.wrapped;
    }

    /**
     * Returns the element saved before a given element.
     * 
     * @param n
     *            the given element.
     * @return the element at the position before the given element.
     */
    public T getPredecessor(T n) {
        assert (this.contains(n)) : "List does not contain this element";
        if (this.nodes.get(n).pre == this.dummy)
            return dummy.pre.wrapped;
        return this.nodes.get(n).pre.wrapped;
    }

    /**
     * Checks if the ring buffer contains a given element.
     * 
     * @param n
     *            the given element.
     * @return true if the ring buffer contains the element, false if not.
     */
    public boolean contains(T n) {
        return (this.nodes.get(n) != null);
    }

    /**
     * Returns the first element of the ring buffer, that is the element after
     * the dummy node.
     * 
     * @return the first element.
     */
    public T getFirst() {
        assert (!this.isEmpty()) : "No elements in the list";
        return this.dummy.next.wrapped;
    }

    /**
     * Returns the last element of the ring buffer, that is the element before
     * the dummy node.
     * 
     * @return the last element.
     */
    public T getLast() {
        assert (!this.isEmpty()) : "No elements in the list";
        return this.dummy.pre.wrapped;
    }

    /**
     * Checks if the ring buffer is empty.
     * 
     * @return true if the ring buffer is empty, false if not.
     */
    public boolean isEmpty() {
        return this.dummy.next == this.dummy;
    }

    /**
     * Creates a shallow copy of this <code>HashList</code>.
     */
    @Override
    public HashList<T> clone() {
        HashList<T> clone = new HashList<T>();
        Iterator<T> it = this.iterator();
        while (it.hasNext()) {
            T element = it.next();
            clone.append(element);
        }
        return clone;
    }

    /**
     * Returns the size of this ring buffer.
     * 
     * @return the size of this ring buffer.
     */
    public int size() {
        return this.nodes.size();
    }

    /**
     * Class represents a node in the <code>HashList</code> which has references
     * to his prdecessor and his successor.
     * 
     * @author hofmeier
     */
    private class HashListNode {

        /** The predecessor in the ring buffer */
        private HashListNode pre;

        /** The successor in the ring buffer */
        private HashListNode next;

        /** The element wrapped in this element */
        private T wrapped;

        /**
         * Creates a new list node
         * 
         * @param w
         *            the element to wrap in here
         */
        public HashListNode(T w) {
            this.wrapped = w;
        }
    }
}
