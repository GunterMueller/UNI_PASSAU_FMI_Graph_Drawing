// =============================================================================
//
//   InsertionSort.java
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
 * This class implements an InsertionSort algorithm to minimize crossing edges
 * in a graph.
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class InsertionSort extends AbstractSortingCrossMinAlgorithm implements
        CrossMinAlgorithm {
    /** The algorithm's name */
    private final String ALGORITHM_NAME = "InsertionSort";

    /**
     * This method sorts the given list in ascending crossing number order in
     * <code>InsertionSort</code> manner.
     * 
     * @see org.graffiti.plugins.algorithms.sugiyama.crossmin.AbstractSortingCrossMinAlgorithm#minCrossings(java.util.ArrayList)
     * @param list
     *            the list to sort
     */
    @Override
    protected void minCrossings(ArrayList<Node> list) {
        if (list.size() > 1) {
            // Node being inserted into already sorted partial list
            Node key;
            // Temporary node in already sorted partial list compared to key
            Node tmpS;
            // Index of node tmpS
            int j;

            for (int i = 1; i < list.size(); i++) {
                key = list.get(i);
                j = i - 1;
                tmpS = list.get(j);

                // Insert key into sorted sequence (j=i-1...0)
                while (j >= 0
                        && countCrossings(key, tmpS) < countCrossings(tmpS, key)) {
                    // Need place to insert key
                    // Shift node tmpS by one to the right
                    list.set(j + 1, tmpS);
                    // Update XPos
                    tmpS.setDouble(SugiyamaConstants.PATH_XPOS, j + 1);

                    // Decrease to access previous node in partial sorted list
                    j--;

                    // Update node tmpS for next run
                    if (j >= 0) {
                        tmpS = list.get(j);
                    }
                }
                // Insert node key into correct position in list
                list.set(j + 1, key);
                // Update XPos
                key.setDouble(SugiyamaConstants.PATH_XPOS, j + 1);
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
