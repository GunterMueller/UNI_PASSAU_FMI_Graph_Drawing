package org.graffiti.plugins.tools.math;

import java.math.BigInteger;

/**
 * A finite set of numbered elements. The elements are contiguously numbered
 * from 0 to {@code getSize() - 1}.
 * 
 * @author Andreas Glei&szlig;ner
 */
public interface FiniteSet<T> extends Iterable<T> {
    /**
     * Returns the element with the specified number.
     * 
     * @param number
     *            the number of the element to return, which must be greater
     *            than or equal to 0 and less than {@link #getSize()}.
     */
    public T get(BigInteger number);

    /**
     * Returns the size of the set.
     * 
     * @return the size of the set.
     */
    public BigInteger getSize();
}
