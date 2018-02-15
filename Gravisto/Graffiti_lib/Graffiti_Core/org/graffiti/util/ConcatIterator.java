// =============================================================================
//
//   ConcatIterator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConcatIterator.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A wrapper for concatenating multiple Iterators into a single one. Iterating a
 * ConcatIterator is equivalent to iterating the given iterators one after the
 * other. The iteration is efficient in the sense that the iteration of the
 * underlying iterators does not occur until it is needed, i. e., no caching is
 * done. The overhead in comparison to direct iteration of the underlying
 * iterators is negligible.
 * <p>
 * The {@link #remove()} method is implemented and removes the current element
 * from the underlying iterator (provided it supports removal).
 * 
 * @param <T>
 *            The type of the elements iterated over. Must be a supertype of all
 *            of the element types of the underlying iterator.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-01-13 11:54:43 +0100 (Fr, 13 Jan 2006)
 *          $
 */
public class ConcatIterator<T> implements Iterator<T> {
    /** The iterators to be concatenated. */
    private List<Iterator<? extends T>> iterators;

    /** Current position in the list of iterators. */
    private Iterator<Iterator<? extends T>> global;

    /** Current position in the current iterator. */
    private Iterator<? extends T> local;

    /**
     * Constructs a new empty ConcatIterator.
     */
    public ConcatIterator() {
        this.iterators = Collections.emptyList();
    }

    /**
     * Constructs a new ConcatIterator from the concatenation of the given
     * iterators.
     * 
     * @param iterators
     *            The iterators to be concatenated.
     */
    public ConcatIterator(Iterator<? extends T>... iterators) {
        this.iterators = Arrays.asList(iterators);
    }

    /**
     * Constructs a new ConcatIterator from the concatenation of the given
     * iterables.
     * 
     * @param iterables
     *            The iterables to be concatenated.
     */
    public ConcatIterator(Iterable<? extends T>... iterables) {
        iterators = new ArrayList<Iterator<? extends T>>(iterables.length);

        for (Iterable<? extends T> iterable : iterables) {
            iterators.add(iterable.iterator());
        }
    }

    /**
     * Constructs a new ConcatIterator from the concatenation of the given
     * iterables.
     * 
     * @param iterables
     *            The iterables to be concatenated.
     */
    public ConcatIterator(Collection<? extends Iterable<? extends T>> iterables) {
        iterators = new ArrayList<Iterator<? extends T>>(iterables.size());

        for (Iterable<? extends T> iterable : iterables) {
            iterators.add(iterable.iterator());
        }
    }

    /*
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        advance();

        // As we just called advance(), we have a next element if and only if
        // the local iterator has a next element

        return local != null && local.hasNext();
    }

    /*
     * @see java.util.Iterator#next()
     */
    public T next() {
        advance();

        // The local iterator is only null if the global iteration is empty.
        // In that case we do not have any elements

        if (local == null)
            throw new NoSuchElementException(
                    "next() must not be called if hasNext() returned false;");

        return local.next();
    }

    /*
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        // If global or local is null, a previous call th next() must have
        // thrown an exception

        if (!(global != null) || local == null)
            throw new IllegalStateException(
                    "remove() must not be called before next();");

        local.remove();
    }

    /**
     * Skip to the iterator that contains the next element. If necessary,
     * initialize the iteration first.
     */
    private void advance() {
        // Initialization on the first call of advance(): The global and
        // local iteration are started.

        if (global == null) {
            global = iterators.iterator();

            if (global.hasNext()) {
                local = global.next();
            }
        }

        // If we reached the end of an underlying iterator, we iteratively try
        // the next one until we find a new element or reach the end of the
        // global iteration

        while (local != null && !local.hasNext() && global.hasNext()) {
            local = global.next();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
