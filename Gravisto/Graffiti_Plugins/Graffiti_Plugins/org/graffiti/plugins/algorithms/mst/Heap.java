package org.graffiti.plugins.algorithms.mst;

import java.util.Collection;
import java.util.Comparator;

/**
 * An ordered collection that allows clients to change the key of an element.
 * <p>
 * 
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 * @param <E>
 *            type of elements to be stored in this heap.
 * @param <K>
 *            type of keys to be associated with elements.
 */
public interface Heap<E, K> extends Collection<E> {

    /**
     * Returns the comparator of this heap or <tt>null</tt> if this heap uses
     * the natural ordering of its elements.
     * 
     * @return the comparator of this heap or <tt>null</tt> if this heap uses
     *         the natural ordering of its elements.
     */
    Comparator<? super K> comparator();

    /**
     * Returns the peek (i.e. smallest) element of this heap according to the
     * order specified by this heap's comparator or the natural order of its
     * elements if this heap's comparator is <tt>null</tt>.
     * 
     * @see #comparator()
     * 
     * @return the peek (i.e. smallest) element of this heap according to the
     *         order specified by this heap's comparator or the natural order of
     *         its elements if this heap's comparator is <tt>null</tt>.
     */
    E getPeek();

    /**
     * Adds the specified element to this heap setting its key to <tt>null</tt>.
     * 
     * @throws NullPointerException
     *             if this class does not support <tt>null</tt> keys.
     * 
     */
    boolean add(E element);

    /**
     * Adds the specified element with the specified key to this heap; returns a
     * new heap entry to enable clients to modify the element's key.
     * 
     * @param element
     *            the element to be added to this heap
     * @param key
     *            the key to be associated with the specified element
     * @return a new heap entry to enable clients to modify the element's key.
     */
    Entry<E, K> add(E element, K key);

    /**
     * Removes the peek (i.e. smallest) element from this heap.
     * 
     * @return the peek (i.e. smallest) element of this heap.
     */
    E removePeek();

    /**
     * Returns a collection view of the entries of this heap. The returned
     * collection supports <tt>remove</tt> but does not support <tt>add</tt>.
     * 
     * @return a collection view of the entries of this heap.
     */
    Collection<Entry<E, K>> entries();

    /**
     * A heap entry.
     * 
     * @author Harald Frankenberger
     * 
     * @version $Revision$ $Date$
     * @param <E>
     *            The type of elements to be stored in this heap.
     * @param <K>
     *            The type of keys to be associated with the elements of this
     *            heap.
     */
    interface Entry<E, K> {

        /**
         * Sets the key of this entry to the specified value.
         * 
         * @param key
         */
        void setKey(K key);

        /**
         * Returns the key of this entry.
         * 
         * @return the key of this entry.
         */
        K getKey();

        /**
         * Returns the element of this entry.
         * 
         * @return the element of this entry.
         */
        E getElement();

        /**
         * Returns <tt>true</tt> if this entry is equal to the specified value.
         * 
         * @param o
         *            the object this entry is to compared with for equality
         * @return <tt>true</tt> if this entry is equal to the specified value.
         */
        boolean equals(Object o);

        /**
         * Returns a hash code for this entry.
         * 
         * @return a hash code for this entry.
         */
        int hashCode();

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
