// =============================================================================
//
//   WeakHashSet.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A {@code Set} implementation with weak elements. An element in a {@code
 * WeakHashSet} will automatically be removed when it is no longer in ordinary
 * use. The set is backed by a {@link WeakHashMap} , so all warnings on using
 * {@code WeakHashMap} apply to {@code WeakHashSet} as well.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see WeakHashSet
 */
public class WeakHashSet<E> implements Set<E> {
    /**
     * Dummy object to easily distinguish nonexistent from garbage collected
     * mappings.
     */
    private static final Object PRESENT = new Object();

    /**
     * The map backing this set.
     */
    private WeakHashMap<E, Object> map;

    /**
     * Constructs a new, empty set; the backing {@code WeakHashMap} instance has
     * default initial capacity (16) and load factor (0.75).
     */
    public WeakHashSet() {
        map = new WeakHashMap<E, Object>();
    }

    /**
     * Constructs a new set containing the elements in the specified collection.
     * 
     * @param c
     *            the collection whose elements are to be placed into this set.
     * @throws NullPointerException
     *             if the specified collection is null.
     */
    public WeakHashSet(Collection<E> c) {
        map = new WeakHashMap<E, Object>();

        addAll(c);
    }

    /**
     * Constructs a new, empty set; the backing {@code WeakHashMap} instance has
     * the specified initial capacity and default load factor, which is 0.75.
     * 
     * @param initialCapacity
     *            the initial capacity of the hash table.
     */
    public WeakHashSet(int initialCapacity) {
        map = new WeakHashMap<E, Object>(initialCapacity);
    }

    /**
     * Constructs a new, empty set; the backing {@code WeakHashMap} instance has
     * the specified initial capacity and the specified load factor.
     * 
     * @param initialCapacity
     *            the initial capacity of the hash map.
     * @param loadFactor
     *            the load factor of the hash map.
     * @throws IllegalArgumentException
     *             if the initial capacity is less than zero, or if the load
     *             factor is nonpositive.
     */
    public WeakHashSet(int initialCapacity, float loadFactor) {
        map = new WeakHashMap<E, Object>(initialCapacity, loadFactor);
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            changed |= add(e);
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {
        return map.keySet().containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {
        return map.keySet().remove(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c) {
        return map.keySet().removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c) {
        return map.keySet().retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
