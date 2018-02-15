// =============================================================================
//
//   CombinationGenerator.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.math.BigInteger;

/**
 * Systematically generate combinations.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
/*
 * Taken from http://www.merriampark.com/comb.htm "The source code is free for
 * you to use in whatever way you wish." Thank you!
 */
public class CombinationGenerator {
    private int[] a;

    private int n;

    private int r;

    private BigInteger numLeft;

    private BigInteger total;

    /**
     * Constructs a combination generator that provides methods to get, from a
     * set with n elements, all subsets with r elements.
     * 
     * @param n
     * @param r
     */
    public CombinationGenerator(int n, int r) {
        if (r > n)
            throw new IllegalArgumentException();
        if (n < 1)
            throw new IllegalArgumentException();
        this.n = n;
        this.r = r;
        a = new int[r];
        BigInteger nFact = getFactorial(n);
        BigInteger rFact = getFactorial(r);
        BigInteger nminusrFact = getFactorial(n - r);
        total = nFact.divide(rFact.multiply(nminusrFact));
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
     * @return the number of combinations not yet generated
     */
    public BigInteger getNumLeft() {
        return numLeft;
    }

    /**
     * @return <code>true</code> if there are more combinations,
     *         <code>false</code> otherwise
     */
    public boolean hasMore() {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }

    /**
     * @return the total number of combinations
     */
    public BigInteger getTotal() {
        return total;
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
     * Generates the next combination (algorithm from Rosen).
     * 
     * @return the next combination
     */
    public int[] getNext() {

        if (numLeft.equals(total)) {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }

        int i = r - 1;
        while (a[i] == n - r + i) {
            i--;
        }
        a[i] = a[i] + 1;
        for (int j = i + 1; j < r; j++) {
            a[j] = a[i] + j - i;
        }

        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
