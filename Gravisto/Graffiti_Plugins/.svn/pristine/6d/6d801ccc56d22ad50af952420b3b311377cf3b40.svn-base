// =============================================================================
//
//   BubbleSort.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * This class implements a BubbleSort algorithm to minimize crossing edges in a
 * graph.
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class BubbleSort extends AbstractSortingCrossMinAlgorithm implements
        CrossMinAlgorithm {
    /** The algorithm's name */
    private final String ALGORITHM_NAME = "BubbleSort";

    /**
     * This method sorts the given list in ascending crossing number order in
     * <code>BubbleSort</code> manner.
     * 
     * @see org.graffiti.plugins.algorithms.sugiyama.crossmin.AbstractSortingCrossMinAlgorithm#minCrossings(java.util.ArrayList)
     * @param list
     *            the list to sort
     */
    @Override
    protected void minCrossings(ArrayList<Node> list) {
        Node u;
        Node v;

        for (int i = 1; i < list.size(); i++) {
            for (int j = 0; j < list.size() - i; j++) {
                u = list.get(j);
                v = list.get(j + 1);
                if (countCrossings(v, u) < countCrossings(u, v)) {
                    // Swap position in list
                    list.set(j, v);
                    list.set(j + 1, u);
                    // Update XPos
                    v.setDouble(SugiyamaConstants.PATH_XPOS, j);
                    u.setDouble(SugiyamaConstants.PATH_XPOS, j + 1);
                }
            }
        }
    }

    /**
     * Accessor for the name of this algorithm
     * 
     * @return returns the name of this algorithm
     */
    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
