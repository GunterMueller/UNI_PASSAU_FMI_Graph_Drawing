// =============================================================================
//
//   ConcatCollection.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConcatCollection.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A wrapper that encapsulates a series of collections as one collection. All
 * methods directly operate on the underlying collections, such that changes in
 * the underlying collections are reflected immediately. No caching is done.
 * This also means that any operation on the concatenated collection has the
 * same complexity as the sum of the respective complexities of the underlying
 * collections.
 * <p>
 * The {@link #remove(Object)} is supported and removes the given object from
 * one of the underlying collections (if contained in any). The
 * {@link #add(Object) } method, however, is not supported. This is because the
 * underlying collections are allowed to be parameterized with a subtype of T.
 * 
 * @param <T>
 *            The type of the elements contained in the collection. Must be a
 *            supertype of all of the element types of the underlying
 *            collections.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-01-13 11:54:43 +0100 (Fr, 13 Jan 2006)
 *          $
 */
public class ConcatCollection<T> extends AbstractCollection<T> implements
        Collection<T> {
    /** The collections to be concatenated. */
    private Collection<? extends Collection<? extends T>> collections;

    /**
     * Constructs a new collection from the concatenation of the given
     * collections.
     * 
     * @param collections
     *            The collections to be concatenated.
     */
    public ConcatCollection(Collection<? extends T>... collections) {
        this.collections = Arrays.asList(collections);
    }

    /**
     * Constructs a new collection from the concatenation of the given
     * collections.
     * 
     * @param collections
     *            The collections to be concatenated.
     */
    public ConcatCollection(
            Collection<? extends Collection<? extends T>> collections) {
        this.collections = collections;
    }

    /*
     * @see java.util.AbstractCollection#clear()
     */
    @Override
    public void clear() {
        for (Collection<? extends T> collection : collections) {
            collection.clear();
        }
    }

    /*
     * @see java.util.AbstractCollection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        for (Collection<? extends T> collection : collections)
            if (collection.contains(o))
                return true;

        return false;
    }

    /*
     * @see java.util.AbstractCollection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        for (Collection<? extends T> collection : collections)
            if (!collection.isEmpty())
                return false;

        return true;
    }

    /*
     * @see java.util.AbstractCollection#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new ConcatIterator<T>(collections);
    }

    /*
     * @see java.util.AbstractCollection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        for (Collection<? extends T> collection : collections)
            if (collection.remove(0))
                return true;

        return false;
    }

    /*
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        int size = 0;

        for (Collection<? extends T> collection : collections) {
            size += collection.size();
        }

        return size;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
