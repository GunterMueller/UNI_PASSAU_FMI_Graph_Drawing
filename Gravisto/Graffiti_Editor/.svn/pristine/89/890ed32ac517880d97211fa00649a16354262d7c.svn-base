// =============================================================================
//
//   Parameters.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.ext;

/**
 * Extension methods for arrays representing parameter lists.
 * 
 * @author Harald Frankenberger
 */
public class ParameterList {

    private ParameterList() {
    }

    /**
     * Checks that this parameter list contains no <code>null</code> values;
     * throws {@link IllegalArgumentException} otherwise.
     * 
     * @param this_
     *            this parameter list
     */
    public static void checkNotNull(Object... this_) {
        Objects.checkNotNull(this_);
        for (Object each : this_)
            if (each == null)
                throw new IllegalArgumentException(Arrays.toString(this_));
    }

    /**
     * Converts this list of actual parameters to the list of parameter types.
     * 
     * @param this_
     *            this parameter list
     * @return the list of parameter types for this parameter list
     */
    public static Class<?>[] toTypes(Object... this_) {
        Objects.checkNotNull(this_);
        Class<?>[] result = new Class<?>[this_.length];
        for (int i = 0; i < this_.length; i++) {
            result[i] = Parameter.toType(this_[i]);
        }
        return result;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
