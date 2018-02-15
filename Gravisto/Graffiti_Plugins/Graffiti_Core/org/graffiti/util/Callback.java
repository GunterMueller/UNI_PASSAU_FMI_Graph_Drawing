// =============================================================================
//
//   Callback.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

/**
 * Classes implementing {@code Callback} employ the Inversion of Control
 * pattern. If the method to be called back should not return a value, they can
 * rather implement {@link VoidCallback}.
 * 
 * @param <S>
 *            The return type of {@code #call(Object)}.
 * @param <T>
 *            The type of the parameter passed to {@code #call(Object)}.
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface Callback<S, T> {
    /**
     * The method to be called by the recipient of this object.
     * 
     * @param t
     *            random parameter. Its semantics must be defined by the class
     *            implementing {@code Callback}.
     * @return a random return value. Its semantics must be defined by the class
     *         implementing {@code Callback}.
     */
    public S call(T t);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
