// =============================================================================
//
//   MultipleIterator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultipleIterator.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class <code>UniqueMultipleIterator</code> encapsulates a number of instances
 * implementing the <code>java.util.Iterator</code> interface. It is possible to
 * iterate over all the iterators one after the other.
 * 
 * @version $Revision: 5767 $
 */
public class MultipleIterator<T> implements Iterator<T> {
    /** The iterator that has only unique elements. */
    private Iterator<T> uniqueIterator;

    /** The set used for duplicate removal. */
    private Set<T> set = new LinkedHashSet<T>();

    /**
     * Constructs a new <code>UniqueMultipleIterator</code> instance.
     * 
     * @param iters
     *            the iterators over which to iterate.
     */
    public MultipleIterator(Collection<Iterator<T>> iters) {
        for (Iterator<T> iterator : iters) {
            while (iterator.hasNext()) {
                set.add(iterator.next());
            }
        }

        this.uniqueIterator = set.iterator();
    }

    /**
     * Constructs a new <code>UniqueMultipleIterator</code> instance.
     * 
     * @param itr
     *            the iterator over which to iterate.
     */
    public MultipleIterator(Iterator<T> itr) {
        while (itr.hasNext()) {
            set.add(itr.next());
        }

        this.uniqueIterator = set.iterator();
    }

    /**
     * Constructs a new <code>UniqueMultipleIterator</code> instance.
     * 
     * @param itr1
     *            the first iterator over which to iterate.
     * @param itr2
     *            the second iterator over which to iterate.
     */
    public MultipleIterator(Iterator<T> itr1, Iterator<T> itr2) {
        while (itr1.hasNext()) {
            set.add(itr1.next());
        }

        while (itr2.hasNext()) {
            set.add(itr2.next());
        }

        this.uniqueIterator = set.iterator();
    }

    /**
     * Constructs a new <code>UniqueMultipleIterator</code> instance.
     * 
     * @param itr1
     *            the first iterator over which to iterate.
     * @param itr2
     *            the second iterator over which to iterate.
     * @param itr3
     *            the third iterator over which to iterate.
     */
    public MultipleIterator(Iterator<T> itr1, Iterator<T> itr2, Iterator<T> itr3) {
        while (itr1.hasNext()) {
            set.add(itr1.next());
        }

        while (itr2.hasNext()) {
            set.add(itr2.next());
        }

        while (itr3.hasNext()) {
            set.add(itr3.next());
        }

        this.uniqueIterator = set.iterator();
    }

    /**
     * Returns <code>true</code> if the iteration has not yet passed each of the
     * iterators, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the iteration has not yet passed each of the
     *         iterators, <code>false</code> otherwise.
     */
    public boolean hasNext() {
        return this.uniqueIterator.hasNext();
    }

    /**
     * Returns the next element of the iteration. If the end of one iterator has
     * been reached, the iteration will be continued on the next one.
     * 
     * @return the next element of the iteration.
     */
    public T next() {
        return this.uniqueIterator.next();
    }

    /**
     * The method <code>remove()</code> of the interface
     * <code>java.util.Iterator</code> will not be supported in this
     * implementation.
     * 
     * @exception UnsupportedOperationException
     *                if the method is called.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Removing is not supported "
                + "on MultipleIterators.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
