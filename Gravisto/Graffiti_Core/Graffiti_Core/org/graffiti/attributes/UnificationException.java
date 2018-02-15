// =============================================================================
//
//   UnificationException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UnificationException.java 5767 2010-05-07 18:42:02Z gleissner $

/*
 * $Id: UnificationException.java 5767 2010-05-07 18:42:02Z gleissner $
 */

package org.graffiti.attributes;

/**
 * Thrown in the context of unification failures (e.g.: during the merge of two
 * collection attributes in an <code>addAttributeConsumer</code> call).
 * 
 * @version $Revision: 5767 $
 */
public class UnificationException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -4506724938819746758L;

    /**
     * Constructor for UnificationException.
     * 
     * @param arg0
     */
    public UnificationException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor for UnificationException.
     * 
     * @param arg0
     */
    public UnificationException(Throwable arg0) {
        super(arg0);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
