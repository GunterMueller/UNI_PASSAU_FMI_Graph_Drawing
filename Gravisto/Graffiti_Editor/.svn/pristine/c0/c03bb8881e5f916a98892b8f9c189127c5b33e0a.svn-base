package org.graffiti.plugins.algorithms.chebyshev;

import java.util.Comparator;

public class XComparator implements Comparator<AuxNode> {
    private static XComparator comparator = new XComparator();

    public static XComparator get() {
        return comparator;
    }

    public int compare(AuxNode n1, AuxNode n2) {
        int x1 = n1.getX();
        int x2 = n2.getX();
        if (x1 < x2)
            return -1;
        else if (x1 > x2)
            return 1;
        x1 = n1.getTieBreaker();
        x2 = n2.getTieBreaker();
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
