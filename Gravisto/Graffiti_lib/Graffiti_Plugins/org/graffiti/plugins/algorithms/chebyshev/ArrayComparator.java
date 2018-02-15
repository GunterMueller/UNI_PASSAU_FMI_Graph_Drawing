// =============================================================================
//
//   ArrayXComparator.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import java.util.Comparator;

/**
 * Compares to nodes using an array.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ArrayComparator<T extends Comparable<T>> implements
        Comparator<AuxNode> {
    private T[] array;

    public ArrayComparator(T[] array) {
        this.array = array;
    }

    public int compare(AuxNode n1, AuxNode n2) {
        int c = array[n1.getLocalId()].compareTo(array[n2.getLocalId()]);
        if (c != 0)
            return c;

        int x1 = n1.getTieBreaker();
        int x2 = n2.getTieBreaker();
        if (x1 < x2)
            return -1;
        else if (x1 > x2)
            return 1;
        else
            return 0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
