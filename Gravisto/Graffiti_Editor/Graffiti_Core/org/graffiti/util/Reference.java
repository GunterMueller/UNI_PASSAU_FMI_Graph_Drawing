// =============================================================================
//
//   Reference.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

/**
 * Holds a reference to an object of type <code>T</code>.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Reference<T> {
    /**
     * The referenced object.
     */
    private T t;

    /**
     * Returns the referenced object.
     * 
     * @return the referenced object.
     */
    public T get() {
        return t;
    }

    /**
     * Sets the referenced object.
     * 
     * @param t
     *            the referenced object. May be <code>null</code>.
     */
    public void set(T t) {
        this.t = t;
    }

    /**
     * Creates a new <code>Reference</code> initially referencing
     * <code>null</code>.
     */
    public Reference() {
        //
    }

    /**
     * Creates a new <code>Reference</code> initially referencing <code>t</code>
     * .
     * 
     * @param t
     *            the referenced object.
     */
    public Reference(T t) {
        this.t = t;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
