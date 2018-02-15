package org.graffiti.util.ext;

import static java.util.Arrays.asList;

import java.util.NoSuchElementException;

/**
 * Extension methods for arrays.
 * 
 * @author Harald Frankenberger
 */
public class Arrays {

    private Arrays() {
    }

    /**
     * Casts the specified object to an array of the given type; this method
     * will <b>not</b> cast <tt>null</tt> to the specified type, but will throw
     * {@link IllegalArgumentException} instead.
     * 
     * @param <T>
     *            the type to cast to
     * @param other
     *            the object to cast
     * @return the specified object, cast to an array of the given type.
     * @throws IllegalArgumentException
     *             if the specified object is <tt>null</tt>.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cast(Object other) {
        ParameterList.checkNotNull(other);
        return (T[]) other;
    }

    /**
     * Returns the concatentation of the elements in this string-array.
     * 
     * @param this_
     *            this array
     * @return the concatentation of the elements in this string-array.
     */
    public static String concat(String... this_) {
        return Strings.append(new StringBuilder(), this_).toString();
    }

    /**
     * Returns <tt>true</tt> if this array equals the given array; i.e. if they
     * are of the same length and its elements are equal and in the same order.
     * 
     * @param this_
     *            this array
     * @param other
     *            the array to be compared with this array for equality
     * @return <tt>true</tt> if this array equals the given array
     */
    public static boolean equals(Object[] this_, Object other) {
        Objects.checkNotNull(this_);
        if (other == null)
            return false;
        if (!Objects.isArray(other))
            return false;
        return asList(this_).equals(asList(Arrays.cast(other)));
    }

    /**
     * Returns the first non-<code>null</code> element in this array; throws
     * {@link NoSuchElementException} if all elements of this array are
     * <code>null</code>.
     * 
     * @param this_
     *            this array
     * @return the first non-<code>null</code> element in this array
     * @throws NoSuchElementException
     *             if all elements of this array are <code>null</code>
     */
    public static Object findNotNull(Object... this_) {
        Object found = findNotNull(this_, null);
        if (found == null)
            throw new NoSuchElementException();
        return found;
    }

    /**
     * Returns the first non-<code>null</code> element in this array or the
     * <code>defaultResult</code> if all elements in this array are
     * <code>null</code>.
     * 
     * @param this_
     *            this array
     * @param defaultResult
     *            the return value if all values in this array are
     *            <code>null</code>
     * @return the first non-<code>null</code> element in this array or the
     *         <code>defaultResult</code> if all elements in this array are
     *         <code>null</code>
     */
    public static Object findNotNull(Object[] this_, Object defaultResult) {
        for (Object each : this_)
            if (each != null)
                return each;
        return defaultResult;
    }

    /**
     * Returns the last element in this array.
     * 
     * @param <T>
     *            the type of this array
     * @param this_
     *            this array
     * @return the last element in this array.
     */
    public static <T> T last(T[] this_) {
        return this_[this_.length - 1];
    }

    /**
     * Returns a string representation of this array.
     * 
     * @param this_
     *            this array
     * @return a string representation of this array.
     */
    public static String toString(Object... this_) {
        return toString(this_, "[", ",", "]");
    }

    /**
     * Returns an array containing all objects in this array converted to their
     * string representation.
     * 
     * @param this_
     *            this array.
     * @return an array containing all objects in this array converted to their
     *         string representation.
     * @see Object#toString()
     */
    public static String[] toStrings(Object... this_) {
        String[] result = new String[this_.length];
        for (int next = 0; next < result.length; next++) {
            result[next] = Objects.toString(this_[next]);
        }
        return result;
    }

    static String concat(String[] strings, String first, String separator,
            String last) {
        StringBuilder result = new StringBuilder();
        result.append(first);
        Strings.append(result, strings, separator);
        result.append(last);
        return result.toString();
    }

    static boolean someNull(Object... this_) {
        for (Object object : this_)
            if (object == null)
                return true;
        return false;
    }

    private static String toString(Object[] this_, String first,
            String separator, String last) {
        return concat(toStrings(this_), first, separator, last);
    }

}
