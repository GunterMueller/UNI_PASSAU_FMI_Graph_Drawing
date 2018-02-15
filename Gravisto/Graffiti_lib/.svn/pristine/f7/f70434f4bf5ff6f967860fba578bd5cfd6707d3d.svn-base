// =============================================================================
//
//   ArrayComparator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.util.Comparator;

/**
 * Serves to compare int arrays lexicographically. Also provides methods to
 * compare matrices lecicographically.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class IntArrayComparator implements Comparator<int[]> {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(int[] a1, int[] a2) {
        if (a1.length != a2.length)
            // should not happen
            throw new ArrayIndexOutOfBoundsException(
                    "The int arrays that you want to compare should have the same length!");
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] < a2[i])
                return -1;
            else if (a1[i] > a2[i])
                return 1;
        }
        return 0;
    }

    /**
     * Checks the inner arrays (let's define them as rows) for equality.
     * 
     * @param m1
     *            first matrix
     * @param m2
     *            second matrix
     * @return true if both matrices have the same entries, false otherwise
     */
    public boolean equals(int[][] m1, int[][] m2) {
        if (m1.length != m2.length)
            return false;
        for (int i = 0; i < m1.length; i++) {
            if (compare(m1[i], m2[i]) != 0)
                return false;
        }
        return true;
    }

    /**
     * Compares the rows of the matrix (the inner arrays) from first to last and
     * from top to bottom.
     * <p>
     * A matrix is equal to another matrix if all the entries are equal.
     * <p>
     * It is greater than another matrix if the first row that is not equal in
     * the other matrix is greater than that in the other matrix. A row is
     * greater than another if the first entry from top to button, that
     * distinguishes the two rows, is greater.
     * <p>
     * Respective rules apply for smaller.
     * 
     * @param m1
     *            first matrix
     * @param m2
     *            second matrix
     * @return 0 if matrices are equal, 1 if m1 < m2, -1 if m1 > m2
     */
    public int compare(int[][] m1, int[][] m2) {
        if (m1.length != m2.length)
            // should not happen
            throw new ArrayIndexOutOfBoundsException(
                    "The int arrays that you want to compare should have the same length!");
        for (int i = 0; i < m1.length; i++) {
            if (compare(m1[i], m2[i]) < 0)
                return -1;
            else if (compare(m1[i], m2[i]) > 0)
                return 1;
        }
        return 0;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
