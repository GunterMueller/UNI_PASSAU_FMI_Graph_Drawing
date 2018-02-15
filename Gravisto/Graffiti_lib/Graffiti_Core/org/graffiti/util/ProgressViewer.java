// =============================================================================
//
//   ProgressViewer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ProgressViewer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * A class that displays in some sense progress made while a long running task.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-01-03 09:42:05 +0100 (Di, 03 Jan 2006)
 *          $
 */
public interface ProgressViewer {

    /**
     * Sets the progress viewer's maximum value to n. By default, the maximum
     * value is 100.
     * 
     * @param n
     *            the new maximum
     */
    void setMaximum(int n);

    /**
     * Sets the value of the progress text. By default, this string is null. If
     * you have provided a custom progress string and want to revert to the
     * built-in behavior, set the string back to null.
     * 
     * @param text
     *            the value of the progress string
     */
    void setText(String text);

    /**
     * Sets the progress viewer's current value to n.
     * 
     * @param n
     *            the new value
     */
    void setValue(int n);

    /**
     * Returns the progress viewer's current value. The value is always between
     * the zero and the maximum values, inclusive. By default, the value is
     * initialized to zero.
     * 
     * @return DOCUMENT ME!
     */
    int getValue();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
