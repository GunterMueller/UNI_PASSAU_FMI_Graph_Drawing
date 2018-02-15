package org.graffiti.util.ext;

/**
 * Extension interface for sets.
 * 
 * @author Harald Frankenberger
 */
public interface Set<E> extends java.util.Set<E> {
    /**
     * Adds the specified element to this set if it is not already present and
     * if the specified condition holds.
     * 
     * @param condition
     *            the condition that must hold, so that the element is added to
     *            this set.
     * @param element
     *            the element to be added to this set.
     * @return <tt>true</tt> if this set did not already contain the specified
     *         element and <tt>condition</tt> was true.
     */
    boolean addIf(boolean condition, E element);
}
