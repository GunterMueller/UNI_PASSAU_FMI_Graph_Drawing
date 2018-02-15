// =============================================================================
//
//   Parameter.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.ext;

/**
 * Extension methods for {@link Object}s that represent actual parameters of
 * method invocations.
 * 
 * @author Harald Frankenberger
 */
public class Parameter {

    private Parameter() {
    }

    /**
     * Converts this parameter to its type.
     * 
     * @param this_
     *            this parameter
     * @return the type of this parameter
     */
    public static Class<?> toType(Object this_) {
        return this_ == null ? Object.class : this_.getClass();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
