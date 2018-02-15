// =============================================================================
//
//   Pair.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Pair.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.planarity.faces;

/**
 * Class representing a pair of two values of the same type <code>T</code>.
 * 
 * @param <T>
 *            Type of the two values.
 */
public class Pair<T> {

    /** First value of the pair. */
    private T fst;

    /** Second value of the pair. */
    private T snd;

    /**
     * Create a new instance of the pair.
     * 
     * @param fst
     *            First value.
     * @param snd
     *            Second value.
     */
    public Pair(T fst, T snd) {
        this.fst = fst;
        this.snd = snd;
    }

    /**
     * Check whether <code>o</code> contains the same values as the current
     * instance.
     * 
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Pair<?> other = (Pair<?>) o;
        return this.fst.equals(other.fst) && this.snd.equals(other.snd);
    }

    /**
     * Calculate a hashCode for the pair of values.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return fst.hashCode() + snd.hashCode();
    }
}
