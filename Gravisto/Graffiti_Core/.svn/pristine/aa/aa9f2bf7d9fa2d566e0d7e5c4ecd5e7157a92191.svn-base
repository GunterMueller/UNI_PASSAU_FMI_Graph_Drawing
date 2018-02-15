// =============================================================================
//
//   LexicalPairComparator.java
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
public class LexicalPairComparator<S extends Comparable<S>, T extends Comparable<T>>
        implements Comparator<Pair<S, T>> {

    /**
     * {@inheritDoc}
     */
    public int compare(Pair<S, T> arg0, Pair<S, T> arg1) {
        S first0 = arg0.getFirst();
        S first1 = arg1.getFirst();
        if (first0 == null) {
            if (first1 != null)
                return -1;
        } else {
            if (first1 == null)
                return 1;
            else {
                int value = first0.compareTo(first1);
                if (value != 0)
                    return value;
            }
        }
        T second0 = arg0.getSecond();
        T second1 = arg1.getSecond();
        if (second0 == null) {
            if (second1 == null)
                return 0;
            else
                return -1;
        } else {
            if (second1 == null)
                return 1;
            else
                return second0.compareTo(second1);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
