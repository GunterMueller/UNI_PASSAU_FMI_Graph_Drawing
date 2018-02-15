package org.graffiti.util.ext;

/**
 * Extension class for hash-sets.
 * 
 * @author Harald Frankenberger
 */
public class HashSet<E> extends java.util.HashSet<E> implements Set<E> {
    /**
     * 
     */
    private static final long serialVersionUID = -8529973602844065235L;

    /**
     * Default constructor.
     */
    public HashSet() {
        super();
    }

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
    public boolean addIf(boolean condition, E element) {
        if (condition)
            return add(element);
        return false;
    }
}
