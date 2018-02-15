package org.graffiti.util.ext;

/**
 * Extension methods for {@link Object}s.
 * 
 * @author Harald Frankenberger
 */
public class Objects {

    private Objects() {
    }

    /**
     * Returns <code>true</code> if this object is equal to the given object;
     * this implementation will handle <code>null</code> correctly. That is:
     * <code>equals(null,null)</code> will return <code>true</code> whereas
     * <code>equals(foo,null)</code> will return <code>foo.equals(null)</code>.
     * 
     * @param this_
     *            this object
     * @param other
     *            the object to check for equality with this object
     * @return <code>true</code> if this object is equal to the given object.
     */
    public static boolean equals(Object this_, Object other) {
        return this_ == null ? other == null : this_.equals(other);
    }

    /**
     * Checks whether this object is not <code>null</code>; throws
     * {@link NullPointerException} otherwise.
     * 
     * @param this_
     *            this object.
     * @throws NullPointerException
     *             if this object is <code>null</code>
     */
    public static void checkNotNull(Object this_) {
        if (this_ == null)
            throw new NullPointerException();
    }

    /**
     * Returns <code>true</code> if this object is an array.
     * 
     * @param this_
     *            this object
     * @return <code>true</code> if this object is an array
     */
    static boolean isArray(Object this_) {
        return this_.getClass().isArray();
    }

    /**
     * Returns a string representation of this object. This method will return
     * the string literal "null" if this object is null.
     * 
     * @param this_
     *            this object
     * @return a string representation of this object
     */
    public static String toString(Object this_) {
        return this_ == null ? "null" : this_.toString();
    }
}
