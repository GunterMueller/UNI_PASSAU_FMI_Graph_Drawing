// =============================================================================
//
//   UnsatisfiedConstraintException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UnsatisfiedConstraintException.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

/**
 * An <code>UnsatisfiedConstraintException</code> is thrown when a constraint to
 * a graph is not satisfied.
 * 
 * @see java.lang.Exception
 */
public class UnsatisfiedConstraintException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3955734766797671546L;

    /**
     * Constructs a new <code>UnsatisfiedConstraintException</code> with
     * <code>null</code> as its detail message.
     */
    public UnsatisfiedConstraintException() {
        super();
    }

    /**
     * Constructs a new <code>UnsatisfiedConstraintException</code> with the
     * specifiecd detail message.
     * 
     * @param msg
     *            DOCUMENT ME!
     */
    public UnsatisfiedConstraintException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
