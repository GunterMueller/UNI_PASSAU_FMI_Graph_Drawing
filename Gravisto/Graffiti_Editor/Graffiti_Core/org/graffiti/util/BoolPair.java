// =============================================================================
//
//   BoolPair.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BoolPair.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * Encapsulates two boolean values.
 * 
 * @author Paul
 * @version $Revision: 5767 $
 */
public class BoolPair {

    /** DOCUMENT ME! */
    private boolean bool1;

    /** DOCUMENT ME! */
    private boolean bool2;

    /**
     * Creates a new BoolPair object.
     * 
     * @param bool1
     *            DOCUMENT ME!
     * @param bool2
     *            DOCUMENT ME!
     */
    public BoolPair(boolean bool1, boolean bool2) {
        this.bool1 = bool1;
        this.bool2 = bool2;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean getFst() {
        return bool1;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean getSnd() {
        return bool2;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
