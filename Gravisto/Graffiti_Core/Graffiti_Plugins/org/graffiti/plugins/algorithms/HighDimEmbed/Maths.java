// =============================================================================
//
//   Maths.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   This Code was imported from the JAMA-Package.
//
//   http://math.nist.gov/javanumerics/jama/
//
// =============================================================================
// $Id: Maths.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.HighDimEmbed;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class Maths {

    /**
     * sqrt(a^2 + b^2) without under/overflow.
     * 
     * @param a
     *            DOCUMENT ME!
     * @param b
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static double hypot(double a, double b) {
        double r;

        if (Math.abs(a) > Math.abs(b)) {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + (r * r));
        } else if (b != 0) {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + (r * r));
        } else {
            r = 0.0;
        }

        return r;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
