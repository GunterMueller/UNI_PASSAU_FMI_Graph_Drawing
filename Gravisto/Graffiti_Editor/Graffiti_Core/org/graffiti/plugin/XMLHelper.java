// =============================================================================
//
//   XMLHelper.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: XMLHelper.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin;

/**
 * Contains some (static) auxiliary methods for writing XML.
 */
public class XMLHelper {

    /** Indicates whether or not indent XML elements. */
    public static boolean useIndentation = false;

    /**
     * Returns a string used to separate XML elements for better readability.
     * 
     * @return XML element delimiter string
     */
    public static String getDelimiter() {
        // return GeneralUtils.getNewLineDelimiter();
        return "";
    }

    /**
     * Returns a String containing <code>n</code> spaces (or the empty String if
     * <code>useIndentation</code> is set to <code>false</code>).
     * 
     * @param n
     *            number of spaces
     * 
     * @return DOCUMENT ME!
     */
    public static String spc(int n) {
        if (useIndentation) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < n; i++) {
                sb.append(" ");
            }

            return sb.toString();
        } else
            return "";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
