package org.graffiti.util.ext;

/**
 * Extension methods for {@link String}s.
 * 
 * @author Harald Frankenberger
 */
public class Strings {

    /**
     * The empty string.
     */
    public static final String EMPTY_STRING = "";

    private static final String NO_SEPARATOR = "";

    private Strings() {
    }

    /**
     * Appends the specified suffixes to this string.
     * 
     * @param this_
     *            this string
     * @param suffixes
     *            the strings to append to this string.
     * @return a new string consisting of this string appended by the specified
     *         string.
     */
    public static String append(String this_, String... suffixes) {
        return append(new StringBuilder(this_), suffixes).toString();
    }

    /**
     * Capitalizes the first character of this string.
     * 
     * @param this_
     *            this string
     * @return a new instance of this string with its first character
     *         capitalized.
     */
    public static String capitalizeFirstCharacter(String this_) {
        if (this_.length() == 0)
            return EMPTY_STRING;
        return capitalize(this_, 0);

    }

    /**
     * Returns the sub-string starting after the first occurrence of the
     * specified character.
     * 
     * @param this_
     *            this string
     * @param c
     *            the character located just before the returned sub-string.
     * @return the sub-string starting just after the first occurrence of the
     *         specified character.
     */
    public static String substringAfter(String this_, char c) {
        return substringFrom(this_, this_.indexOf(c) + 1);
    }

    /**
     * Returns the sub-string ending just before the first occurrence of the
     * specified character.
     * 
     * @param this_
     *            this string
     * @param c
     *            the character that is located just after the returned
     *            sub-string.
     * @return the sub-string ending just before the first occurrence of the
     *         specified character.
     */
    public static String substringBefore(String this_, char c) {
        return substringTo(this_, this_.indexOf(c));
    }

    /**
     * Returns the sub-string from <tt>startIndex</tt> to the end of this string
     * or the empty string if the start index does not lie within the bounds of
     * this string.
     * 
     * @param this_
     *            this string
     * @param startIndex
     *            the start-index of the sub-string to construct.
     * @return the sub-string from <tt>startIndex</tt> to the end of this string
     *         or the empty if the start index does not lie within the bounds of
     *         this string
     */
    public static String substringFrom(String this_, int startIndex) {
        if (startIndex >= this_.length() - 1)
            return EMPTY_STRING;
        return this_.substring(startIndex, this_.length());

    }

    /**
     * Returns the sub-string from 0 to <tt>endIndex</tt> of this string or the
     * empty string if <tt>endIndex <= 0</tt>
     * 
     * @param this_
     *            this string
     * @param endIndex
     *            the end-index (exclusive) of the returned sub-string.
     * @return the sub-string from 0 to <tt>endIndex</tt> of this string
     */

    public static String substringTo(String this_, int endIndex) {
        if (endIndex <= 0)
            return EMPTY_STRING;
        return this_.substring(0, endIndex);
    }

    static StringBuilder append(StringBuilder this_, String... strings) {
        return append(this_, strings, NO_SEPARATOR);
    }

    static StringBuilder append(StringBuilder this_, String[] suffixes,
            String separator) {
        for (int next = 0; next < suffixes.length; next++) {
            append(this_, suffixes[next]);
            if (!isLastIndexIn(next, suffixes)) {
                append(this_, separator);
            }
        }
        return this_;
    }

    private static void append(StringBuilder this_, String string) {
        this_.append(string);
    }

    private static String capitalize(String string, int index) {
        return capitalize(string, index, index + 1);
    }

    private static String capitalize(String string, int startIndex, int endIndex) {
        final String capitalizedSubstring = string.substring(startIndex,
                endIndex).toUpperCase();
        final StringBuilder result = new StringBuilder(string).replace(
                startIndex, endIndex, capitalizedSubstring);
        return result.toString();
    }

    private static boolean isLastIndexIn(int this_, Object[] array) {
        return this_ == array.length - 1;
    }

}
