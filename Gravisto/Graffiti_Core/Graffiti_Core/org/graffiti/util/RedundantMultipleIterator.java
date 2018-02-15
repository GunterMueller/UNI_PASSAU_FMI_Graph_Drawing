// =============================================================================
//
//   RedundantMultipleIterator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RedundantMultipleIterator.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class <code>MultipleIterator</code> encapsulates a number of instances
 * implementing the <code>java.util.Iterator</code> interface. It is possible to
 * iterate over all the iterators one after the other.
 * 
 * @version $Revision: 5767 $
 */
public class RedundantMultipleIterator<T> implements Iterator<T> {

    /** The iterators to iterate over. */
    private Iterator<T>[] iters;

    /** Points to the current iterator. */
    private int current;

    /**
     * Constructs a new <code>MultipleIterator</code> instance.
     * 
     * @param iters
     *            the iterators over which to iterate.
     */
    public RedundantMultipleIterator(Iterator<T>[] iters) {
        current = 0;
        this.iters = iters;
    }

    /**
     * Constructs a new <code>MultipleIterator</code> instance.
     * 
     * @param itr
     *            the iterator over which to iterate.
     */
    @SuppressWarnings("unchecked")
    public RedundantMultipleIterator(Iterator<T> itr) {
        iters = new Iterator[1];
        iters[0] = itr;
    }

    /**
     * Constructs a new <code>MultipleIterator</code> instance.
     * 
     * @param itr1
     *            the first iterator over which to iterate.
     * @param itr2
     *            the second iterator over which to iterate.
     */
    @SuppressWarnings("unchecked")
    public RedundantMultipleIterator(Iterator<T> itr1, Iterator<T> itr2) {
        iters = new Iterator[2];
        iters[0] = itr1;
        iters[1] = itr2;
    }

    /**
     * Constructs a new <code>MultipleIterator</code> instance.
     * 
     * @param itr1
     *            the first iterator over which to iterate.
     * @param itr2
     *            the second iterator over which to iterate.
     * @param itr3
     *            the third iterator over which to iterate.
     */
    @SuppressWarnings("unchecked")
    public RedundantMultipleIterator(Iterator<T> itr1, Iterator<T> itr2,
            Iterator<T> itr3) {
        iters = new Iterator[3];
        iters[0] = itr1;
        iters[1] = itr2;
        iters[2] = itr3;
    }

    /**
     * Returns <code>true</code> if the iteration has not yet passed each of the
     * iterators, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the iteration has not yet passed each of the
     *         iterators, <code>false</code> otherwise.
     */
    public boolean hasNext() {
        if (iters[current].hasNext())
            return true;
        else {
            while (current < (iters.length - 1)) {
                current++;

                if (iters[current].hasNext())
                    return true;
            }
        }

        return false;
    }

    /**
     * Returns the next element of the iteration. If the end of one iterator has
     * been reached, the iteration will be continued on the next one.
     * 
     * @return the next element of the iteration.
     * 
     * @throws NoSuchElementException
     *             DOCUMENT ME!
     */
    public T next() {
        if (iters[current].hasNext())
            return iters[current].next();
        else {
            while (current < (iters.length - 1)) {
                current++;

                if (iters[current].hasNext())
                    return iters[current].next();
            }
        }

        throw new NoSuchElementException();
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
