// =============================================================================
//
//   DeepCopy.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DeepCopy.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.core;

/**
 * Guarantees a deep copy.
 * 
 * @version $Revision: 5767 $
 */
public interface DeepCopy {
    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    public Object copy();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
