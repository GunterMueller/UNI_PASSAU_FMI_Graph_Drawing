// =============================================================================
//
//   PermutationGenerator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.math.BigInteger;

/**
 * Systematically generate permutations.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
/*
 * Taken from http://www.merriampark.com/perm.htm "The source code is free for
 * you to use in whatever way you wish."
 */
public class PermutationGenerator {
    private int[] a;

    private BigInteger numLeft;

    private BigInteger total;

    /**
     * Constructs a permutation generator that provides all premutations of a
     * set of n elements.
     */
    /*
     * WARNING: Don't make n too large. n! can be very large, even when n is as
     * small as 20 - 20! = 2,432,902,008,176,640,000 and 21! is too big to fit
     * into a Java long, which is why we use BigInteger instead.
     */
    public PermutationGenerator(int n) {
        if (n < 1)
            throw new IllegalArgumentException(
                    "The minimum number of elements to permute is 1.");
        a = new int[n];
        total = getFactorial(n);
        reset();
    }

    /**
     * Reset
     */
    public void reset() {
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        numLeft = new BigInteger(total.toString());
    }

    /**
     * @return the number of permutations not yet generated
     */
    public BigInteger getNumLeft() {
        return numLeft;
    }

    /**
     * @return the total number of permutations
     */
    public BigInteger getTotal() {
        return total;
    }

    /**
     * Are there more permutations?
     * 
     * @return <code>true</code> if there are more combinations,
     *         <code>false</code> otherwise
     */
    public boolean hasMore() {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    /**
     * Computes the factorial of an integer n.
     * 
     * @param n
     *            a positive integer n
     * @return the factorial of n
     */
    private static BigInteger getFactorial(int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    /**
     * Generates the next permutation (algorithm from Rosen).
     * 
     * @return the next permutation
     */
    public int[] getNext() {

        if (numLeft.equals(total)) {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }

        int temp;

        // Find largest index j with a[j] < a[j+1]
        int j = a.length - 2;
        while (a[j] > a[j + 1]) {
            j--;
        }

        // Find index k such that a[k] is smallest integer
        // greater than a[j] to the right of a[j]
        int k = a.length - 1;
        while (a[j] > a[k]) {
            k--;
        }

        // Interchange a[j] and a[k]
        temp = a[k];
        a[k] = a[j];
        a[j] = temp;

        // Put tail end of permutation after jth position in increasing order
        int r = a.length - 1;
        int s = j + 1;

        while (r > s) {
            temp = a[s];
            a[s] = a[r];
            a[r] = temp;
            r--;
            s++;
        }
        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
