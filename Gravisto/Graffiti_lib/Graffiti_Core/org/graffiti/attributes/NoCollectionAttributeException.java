// =============================================================================
//
//   NoCollectionAttributeException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NoCollectionAttributeException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * The <code>NoCollectionAttributeException</code> will be thrown if a method
 * tries to add an attribute to an attribute which is no
 * <code>CollectionAttribute</code>.
 * 
 * @version $Revision: 5767 $
 */
public class NoCollectionAttributeException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 248160779981673909L;

    /**
     * Constructs a <code>NoCollectionAttributeException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            The detail message which is saved for later retrieval by the
     *            <code>getMessage()</code> method.
     */
    NoCollectionAttributeException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
