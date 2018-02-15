// =============================================================================
//
//   LexTopoComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class implements a Comparator in order to compare two <code>Nodes</code>
 * according to their <code>predecessor list</code>.
 * <p>
 * The <code>predecessor list</code> is a list of integers. Each integer value
 * corresponds to the CoffmanGraham number <code>cgNum</code> which is assigned
 * by <code>CoffmanGraham</code>. <br>
 * The lexicographical order is as follows: <br>
 * <b><code>list1 &lt list2:</code></b> <br>
 * 1. if <code>list1</code> is empty and <code>list2</code> is not empty. <br>
 * 2. <code>list1</code> and <code>list2</code> are not empty and
 * <code>max(list1) &lt
 * max(list2)</code> <br>
 * 3. <code>list1</code> and <code>list2</code> are not empty and
 * <code>max(list1) =
 * max(list2)</code> and
 * <code>(list1 \ {max(list1)}) &lt (list2 \ {max(list2)})</code>
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class LexTopoComparator implements Comparator<ArrayList<Integer>> {
    /*
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        // Check if lists are equal, also in order
        if (l1.equals(l2))
            return 0;

        // Lists Not equal
        // Check if one of both lists is empty
        if (l1.isEmpty() && !l2.isEmpty())
            return -1;
        else if (!l1.isEmpty() && l2.isEmpty())
            return 1;
        else if (l1.isEmpty() && l2.isEmpty())
            return 0;

        // None of both lists is empty and they are not "equal" (also in order)
        // Create new list to be able to manipulate it temporary
        // -> remove max if equal
        ArrayList<Integer> tmpPreds1 = new ArrayList<Integer>(l1);
        ArrayList<Integer> tmpPreds2 = new ArrayList<Integer>(l2);

        // Get max value and its position in list
        int[] max1 = getMax(tmpPreds1);
        int[] max2 = getMax(tmpPreds2);

        // Remove max in both predLists until they are
        // not equal anymore or one list is empty
        while (max1[0] == max2[0]) {
            tmpPreds1.remove(max1[1]);
            tmpPreds2.remove(max2[1]);

            if (!tmpPreds1.isEmpty() && !tmpPreds2.isEmpty()) {
                max1 = getMax(tmpPreds1);
                max2 = getMax(tmpPreds2);
            } else {
                // One or both lists is/are empty
                // Check if one or both predList(s) is/are
                // empty and decide on the basis of that
                if (tmpPreds1.isEmpty() && tmpPreds2.isEmpty())
                    return 0;
                else if (tmpPreds1.isEmpty() && !tmpPreds2.isEmpty())
                    return -1;
                else if (!tmpPreds1.isEmpty() && tmpPreds2.isEmpty())
                    return 1;
            }
        }

        // Both lists are not empty and the max is not equal
        int max1Num = max1[0];
        int max2Num = max2[0];

        if (max1Num > max2Num)
            return 1;
        else if (max1Num < max2Num)
            return -1;
        else if (max1Num == max2Num)
            return 0;

        // Shouldn't be reached. But for return reasons
        return 0;
    }

    /**
     * Returns an array containing the max. number and its index in this list.
     * At array index "0" is the maxNumber and at array index "1" is the index
     * within this list.
     * 
     * @param list
     *            non-empty list to locate max. number
     * @return integer array with maxNumber at A[0] and its index at A[1].
     */
    private int[] getMax(ArrayList<Integer> list) {
        int max = -1;
        int maxIndex = -1;
        int[] result = { max, maxIndex };

        for (int i = 0; i <= list.size() - 1; i++) {
            int tmp = list.get(i);
            if (tmp > max) {
                max = tmp;
                maxIndex = i;
            }
        }
        result[0] = max;
        result[1] = maxIndex;
        return result;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
