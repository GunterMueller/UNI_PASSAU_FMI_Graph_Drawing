// =============================================================================
//
//   RadixSort.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

/**
 * This class is an implementation of the radix-sort-algorithm. It is used to
 * sort an array of nodes depending on their number in the lexicographic order.
 * 
 * @author struckmeier
 */

public class RadixSort {

    /**
     * This method is used to call the sorting algorithm.
     * 
     * @param source
     * @param digits
     * @return an array sorted depending on the lexicographic order of the
     *         nodes.
     */
    public LexBFSNode[] Radix(LexBFSNode source[], int digits) {
        LexBFSNode temp[] = new LexBFSNode[source.length];
        LexBFSNode result[] = new LexBFSNode[source.length];

        for (int i = 0; i < digits; i++) {
            result = counting_sort_by_radix(i, source, temp);
        }

        return result;
    }

    /**
     * this method is used to get the byte value of a node.
     * 
     * @param val
     * @param bytenum
     * @return the byte value of a node.
     */
    private int GETBYTE(int val, int bytenum) {
        return (val >> (8 * bytenum)) & 0xff;
    }

    /**
     * This method is used to sort nodes depending on their number in the
     * lexicographic ordering.
     * 
     * @param int_byte
     * @param source
     * @param dest
     * @return a sorted array.
     */
    private LexBFSNode[] counting_sort_by_radix(int int_byte,
            LexBFSNode[] source, LexBFSNode[] dest) {
        final int k = 256;
        int count[] = new int[k];
        int i;
        int source_i;

        for (i = 0; i < source.length; i++) {
            source_i = GETBYTE(source[i].getNumber(), int_byte);
            count[source_i]++;
        }

        for (i = 1; i < k; i++) {
            count[i] += count[i - 1];
        }

        for (i = source.length - 1; i >= 0; i--) {
            source_i = GETBYTE(source[i].getNumber(), int_byte);
            dest[count[source_i] - 1] = source[i];
            count[source_i]--;
        }
        return dest;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
