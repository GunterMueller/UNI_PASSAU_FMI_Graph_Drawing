// =============================================================================
//
//   UpperBoundComparator.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.cores;

import java.util.Comparator;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class UpperBoundComparator implements Comparator<Interval> {
    private static UpperBoundComparator comparator = new UpperBoundComparator();

    public static UpperBoundComparator get() {
        return comparator;
    }

    public int compare(Interval interval1, Interval interval2) {
        if (interval1 == null) {
            if (interval2 == null)
                return 0;
            else
                return 1;
        } else if (interval2 == null)
            return -1;
        int x1 = interval1.getUpper();
        int x2 = interval2.getUpper();
        if (x1 < x2)
            return -1;
        if (x1 > x2)
            return 1;
        x1 = interval1.getLower();
        x2 = interval2.getLower();
        if (x1 < x2)
            return -1;
        if (x1 > x2)
            return 1;
        x1 = interval1.getNode().getTieBreaker();
        x2 = interval2.getNode().getTieBreaker();
        if (x1 < x2)
            return -1;
        if (x1 > x2)
            return 1;
        return 0;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
