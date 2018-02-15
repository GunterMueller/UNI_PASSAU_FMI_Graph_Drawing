// =============================================================================
//
//   SubtreeComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.sorter;

import java.util.Comparator;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.CostFunction;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutComposition;

/**
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class SubtreeComparator implements Comparator<LayoutComposition> {

    private CostFunction cost = null;

    public SubtreeComparator(CostFunction cost) {
        this.cost = cost;
    }

    /*
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(LayoutComposition arg0, LayoutComposition arg1) {
        Node firstRoot = arg0.getRoot();
        if (firstRoot == null)
            return -1;

        Node secondRoot = arg1.getRoot();
        if (secondRoot == null)
            return 1;

        double firstCost = this.cost.costOf(arg0);
        double secondCost = this.cost.costOf(arg1);

        if (Util.isHelperNode(firstRoot)) {
            if (Util.isHelperNode(secondRoot)) {
                if (firstCost < secondCost)
                    return -1;
                else if (firstCost > secondCost)
                    return 1;
                else
                    return 0;
            } else
                return -1;
        } else {
            if (Util.isHelperNode(secondRoot))
                return 1;
            else {
                if (firstCost < secondCost)
                    return -1;
                else if (firstCost > secondCost)
                    return 1;
                else
                    return 0;
            }
        }

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
