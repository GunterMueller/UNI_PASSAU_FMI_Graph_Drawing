// =============================================================================
//
//   StringSplitter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringSplitter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * Provides a method to split a String into several Strings according to a
 * certain String.
 * 
 * @version $Revision: 5767 $
 */
public class StringSplitter {

    /**
     * Splits String <code>toSplit</code> using <code>delim</code> as delimeter.
     * Fills the returned String array with the results. If <code>delim</code>
     * does not appear in <code>toSplit</code>, the whole String is returned as
     * first entry of the returned String array.
     * 
     * @param toSplit
     *            the String that is split.
     * @param delim
     *            the String that is used as delimiter.
     * 
     * @return the parts of the String in a String array.
     */
    public static String[] split(String toSplit, String delim) {
        // count how many entries the array is going to have
        int count = 0;
        String restString = toSplit;
        int nextSplitPos = restString.indexOf(delim);

        while (nextSplitPos != -1) {
            count++;
            restString = restString.substring(nextSplitPos + delim.length());
            nextSplitPos = restString.indexOf(delim);
        }

        if (count == 0)
            return new String[] { toSplit };
        else {
            String[] splitted = new String[count + 1];
            count = 0;

            nextSplitPos = toSplit.indexOf(delim);

            while (nextSplitPos != -1) {
                splitted[count++] = toSplit.substring(0, nextSplitPos);
                toSplit = toSplit.substring(nextSplitPos + delim.length());
                nextSplitPos = toSplit.indexOf(delim);
            }

            splitted[count] = toSplit;

            return splitted;
        }
    }

    /**
     * Splits String <code>toSplit</code> using <code>delim</code> as delimeter.
     * Splits into a maximum of <code>limit</code> Strings. Fills the returned
     * String array with the results. If <code>delim</code> does not appear in
     * <code>toSplit</code>, the whole String is returned as first entry of the
     * returned String array.
     * 
     * @param toSplit
     *            the String that is split.
     * @param delim
     *            the String that is used as delimiter.
     * @param limit
     *            the maximum number of resulting Strings
     * 
     * @return the parts of the String in a String array.
     */
    public static String[] split(String toSplit, String delim, int limit) {
        if (limit == 0)
            return split(toSplit, delim);
        else if (limit == 1)
            return new String[] { toSplit };
        else {
            assert limit > 1;

            // count how many entries the array is going to have
            int count = 0;
            String restString = toSplit;
            int nextSplitPos = restString.indexOf(delim);

            while ((nextSplitPos != -1) && (count < (limit - 1))) {
                count++;
                restString = restString
                        .substring(nextSplitPos + delim.length());
                nextSplitPos = restString.indexOf(delim);
            }

            String[] splitted = new String[count + 1];
            count = 0;

            nextSplitPos = toSplit.indexOf(delim);

            while ((nextSplitPos != -1) && (count < (limit - 1))) {
                splitted[count++] = toSplit.substring(0, nextSplitPos);
                toSplit = toSplit.substring(nextSplitPos + delim.length());
                nextSplitPos = toSplit.indexOf(delim);
            }

            splitted[count] = toSplit;

            return splitted;
        }
    }

    /**
     * Splits String <code>toSplit</code> using <code>delim</code> as delimeter.
     * Splits into a maximum of 2 Strings. Fills the returned String array with
     * the results. If <code>delim</code> does not appear in
     * <code>toSplit</code>, the whole String is returned as first entry of the
     * returned String array. This method is faster than a call to
     * <code>split(toSplit, delim, 2)</code>, but otherwise has the same result.
     * 
     * @param toSplit
     *            the String that is split.
     * @param delim
     *            the String that is used as delimiter.
     * 
     * @return the parts of the String in a String array.
     */
    public static String[] splitInTwo(String toSplit, String delim) {
        int splitPos = toSplit.indexOf(delim);

        if (splitPos == -1)
            return new String[] { toSplit };
        else
            return new String[] { toSplit.substring(0, splitPos),
                    toSplit.substring(splitPos + delim.length()) };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
