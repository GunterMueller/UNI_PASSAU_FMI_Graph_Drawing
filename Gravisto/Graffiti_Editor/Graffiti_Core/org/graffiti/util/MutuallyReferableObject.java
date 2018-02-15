// =============================================================================
//
//   MutuallyReferableObject.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

/**
 * A class implementing to reference mechanism of {@code MutuallyReferable}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MutuallyReferableObject implements MutuallyReferable {
    /**
     * Simple node to create a linked list of meaningless references.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private static class ReferenceEntry {
        /**
         * The previous reference in the linked list.
         */
        @SuppressWarnings("unused")
        private ReferenceEntry previous;

        /**
         * Meaningless reference.
         */
        @SuppressWarnings("unused")
        private Object o;

        /**
         * Constructs a {@code} ReferenceEntry}.
         * 
         * @param previous
         *            the previously constructed node.
         * @param o
         *            the object to refer to.
         */
        public ReferenceEntry(ReferenceEntry previous, Object o) {
            this.previous = previous;
            this.o = o;
        }
    }

    /**
     * The previously constructed node.
     */
    private ReferenceEntry lastReferenceEntry;

    /**
     * {@inheritDoc}
     */
    public void addReference(Object o) {
        lastReferenceEntry = new ReferenceEntry(lastReferenceEntry, o);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
