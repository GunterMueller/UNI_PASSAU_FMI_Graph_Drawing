// =============================================================================
//
//   QuickSort.java
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
 * This class implements a QuickSort algorithm to minimize crossing edges in a
 * graph.
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class QuickSort extends AbstractSortingCrossMinAlgorithm implements
        CrossMinAlgorithm {
    /** The algorithm's name */
    private final String ALGORITHM_NAME = "QuickSort";

    /**
     * This method sorts the given list in ascending crossing number order in
     * <code>QuickSort</code> manner.
     * 
     * @see org.graffiti.plugins.algorithms.sugiyama.crossmin.AbstractSortingCrossMinAlgorithm#minCrossings(java.util.ArrayList)
     * @param list
     *            the list to sort
     */
    @Override
    protected void minCrossings(ArrayList<Node> list) {
        quickSort(list, 0, list.size() - 1);
    }

    /**
     * This method sorts a given list according to the <code>QuickSort</code>
     * algorithm on the basis of the calculated crossing numbers. <br>
     * A pivot element p is selected, then the node list is split in parts of
     * nodes (
     * < p) and nodes (>
     * p). <br>
     * (
     * < p) indicates that it is better to * place those nodes in front of p due to smaller crossing number. (>
     * p) indicates that it's better to place those nodes behind p. <br>
     * Then the method <code>quickSort</code> is called recursively on the
     * partial lists (
     * < p) and (>
     * p).
     * 
     * @param list
     *            the list to sort
     * @param l
     *            index of first node in list
     * @param r
     *            index of last node in list
     */
    private void quickSort(ArrayList<Node> list, int l, int r) {
        // Index of pivot element in list
        int q;

        if (r > l) {
            q = partition(list, l, r);
            quickSort(list, l, q - 1);
            quickSort(list, q + 1, r);
        }
    }

    /**
     * This method rearranges the given list in place. After rearranging the
     * list looks like { (< pivot), (pivot), (> pivot) }. <br>
     * (< pivot) indicates that it's better to place those nodes in front of
     * pivot, due to smaller crossing number. (> pivot) indicates that it's
     * better to place those nodes behind this pivot node.
     * 
     * @param list
     *            the list to rearrange
     * @param l
     *            index of first node in list
     * @param r
     *            index of last node in list
     * @return index of pivot element after list is rearranged
     */
    private int partition(ArrayList<Node> list, int l, int r) {
        // Pivot node
        Node pivot = list.get(r);
        // Temporary node being compared to pivot
        Node tmp1;
        // Temporary node being exchanged with node tmp1 in case it's better to
        // place tmp1 in front of tmp2, resulting with an increased partial list
        // (< pivot)
        Node tmp2;
        // Pointer to last placed element in partial list (< pivot)
        int j = l - 1;

        for (int i = l; i < r; i++) {
            tmp1 = list.get(i);

            if (countCrossings(tmp1, pivot) < countCrossings(pivot, tmp1)) {
                // It's better to place tmp1 in front of pivot
                // Swap with first element in partial list (> pivot)
                j++;
                tmp2 = list.get(j);

                // Swap tmp1, tmp2
                list.set(i, tmp2);
                list.set(j, tmp1);
                // Update XPos
                tmp1.setDouble(SugiyamaConstants.PATH_XPOS, j);
                tmp2.setDouble(SugiyamaConstants.PATH_XPOS, i);
            }
        }
        // Place pivot element
        // Swap pivot with next node at position j+1
        tmp1 = list.get(j + 1);
        list.set(r, tmp1);
        list.set(j + 1, pivot);
        // Update XPos
        tmp1.setDouble(SugiyamaConstants.PATH_XPOS, r);
        pivot.setDouble(SugiyamaConstants.PATH_XPOS, j + 1);

        return j + 1;
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
