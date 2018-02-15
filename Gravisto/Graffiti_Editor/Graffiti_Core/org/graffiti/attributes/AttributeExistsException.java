// =============================================================================
//
//   AttributeExistsException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeExistsException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * The <code>AttributeExistsException</code> will be thrown if a method tries to
 * add an attribute at a location where another attribute already exists.
 * 
 * @version $Revision: 5767 $
 */
public class AttributeExistsException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 3362429836953824423L;

    /**
     * Constructs an <code>AttributeExistsException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message which is saved for later retrieval by the
     *            <code>getMessage()</code> method.
     */
    public AttributeExistsException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
