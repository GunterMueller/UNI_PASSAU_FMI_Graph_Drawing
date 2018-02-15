// =============================================================================
//
//   ReverseComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

import java.util.Comparator;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
/**
 * {@code ReverseComparator<T>} imposes an ordering which is the reversed
 * ordering imposed by the {@code Comparator<T>} object that is passed on
 * construction.
 */
public class ReverseComparator<T> implements Comparator<T> {
    /**
     * The comparator that imposes the reversed ordering this comparator
     * imposes.
     */
    private Comparator<T> comparator;

    /**
     * Creates a new {@code ReverseComparator<T>}.
     * 
     * @param comparator
     *            the comparator that imposes the reversed ordering this
     *            comparator imposes.
     */
    public ReverseComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Compares two objects.
     * 
     * @param t1
     *            the first object being compared.
     * @param t2
     *            the second object being compared.
     */
    public int compare(T t1, T t2) {
        return comparator.compare(t2, t1);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
