// =============================================================================
//
//   Pair.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Pair.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * Encapsulates two values.
 * 
 * @author Paul
 * @author Andreas Glei&szlig;ner
 * @version $Revision: 5767 $
 */
public class Pair<S, T> {
    public static <U, V> Pair<U, V> create(U first, V second) {
        return new Pair<U, V>(first, second);
    }

    /**
     * The first object.
     */
    private S first;

    /**
     * The second object.
     */
    private T second;

    /**
     * Creates a new Pair object.
     */
    public Pair() {
    }

    /**
     * Creates a new Pair object.
     * 
     * @param first
     *            the first object.
     * @param second
     *            the second object.
     */
    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first object.
     * 
     * @return the first object.
     */
    public S getFirst() {
        return first;
    }

    /**
     * Returns the first object.
     * 
     * @return the first object.
     */
    public S getFst() {
        return first;
    }

    /**
     * Sets the first object.
     * 
     * @param first
     *            the first object.
     */
    public void setFirst(S first) {
        this.first = first;
    }

    /**
     * Returns the second object.
     * 
     * @return the second object.
     */
    public T getSecond() {
        return second;
    }

    /**
     * Returns the second object.
     * 
     * @return the second object.
     */
    public T getSnd() {
        return second;
    }

    /**
     * Sets the second object.
     * 
     * @param second
     *            the second object.
     */
    public void setSecond(T second) {
        this.second = second;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code true} if <code>obj</code> is a <code>Pair</code> and both
     *         pairs equal in {@link #first} and {@link #second}; <br> {@code false}
     *         otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>))
            return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        if (first == null) {
            if (pair.first != null)
                return false;
        } else {
            if (!first.equals(pair.first))
                return false;
        }
        if (second == null) {
            if (pair.second != null)
                return false;
        } else {
            if (!second.equals(pair.second))
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int h1 = first == null ? 0 : first.hashCode();
        int h2 = second == null ? 0 : second.hashCode();
        return h1 ^ h2;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
