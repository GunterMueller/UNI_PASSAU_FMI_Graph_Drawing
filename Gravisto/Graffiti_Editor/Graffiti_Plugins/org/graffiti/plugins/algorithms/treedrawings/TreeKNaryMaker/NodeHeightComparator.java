// =============================================================================
//
//   NodeHeightComparator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeHeightComparator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker;

import java.util.Comparator;

/**
 * Comparator that compares NodeHeight objects.
 * 
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class NodeHeightComparator implements Comparator<Object> {

    /*
     * A Node n1 can come before another Node n2 if the height of n1 is >= the
     * height of n2
     * 
     * @see java.util.Comparator#compare(T, T)
     */
    public int compare(Object arg0, Object arg1) {
        NodeHeight n1 = (NodeHeight) arg0;
        NodeHeight n2 = (NodeHeight) arg1;

        if (n1.getHeight() < n2.getHeight())
            return -1;
        else
            return 1;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
