// =============================================================================
//
//   VoidCallback.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

/**
 * Classes implementing {@code VoidCallback} employ the Inversion of Control
 * pattern. If the method to be called back should return a value, they can
 * rather implement {@link Callback}.
 * 
 * @param <T>
 *            The type of the parameter passed to {@code #call(Object)}.
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface VoidCallback<T> {
    /**
     * The method to be called by the recipient of this object.
     * 
     * @param t
     *            random parameter. Its semantics must be defined by the class
     *            implementing {@code Callback}.
     */
    public void call(T t);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
