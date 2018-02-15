// =============================================================================
//
//   MutuallyReferable.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

/**
 * Classes implementing {@code MutuallyReferable} allow for the creation of
 * references that influence the garbage collector but have no further semantic
 * relevance.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface MutuallyReferable {
    /**
     * Adds a reference to the specified object. The reference is only used to
     * influence the garbage collector.
     * 
     * @param o
     *            the object to reference.
     */
    public void addReference(Object o);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
