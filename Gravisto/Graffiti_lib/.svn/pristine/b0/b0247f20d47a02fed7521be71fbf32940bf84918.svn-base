package org.graffiti.plugins.algorithms.chebyshev;

import java.util.Comparator;

class IdComparator implements Comparator<AuxNode> {
    private static IdComparator comparator = new IdComparator();

    public static IdComparator get() {
        return comparator;
    }

    public int compare(AuxNode n1, AuxNode n2) {
        int id1 = n1.getId();
        int id2 = n2.getId();
        if (id1 < id2)
            return -1;
        else if (id1 > id2)
            return 1;
        else
            return 0;
    }
}
