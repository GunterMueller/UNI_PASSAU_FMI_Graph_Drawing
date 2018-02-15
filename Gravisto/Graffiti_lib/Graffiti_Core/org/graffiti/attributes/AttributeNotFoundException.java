// =============================================================================
//
//   AttributeNotFoundException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeNotFoundException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * The <code>AttributeNotFoundException</code> will be thrown if a method tries
 * to access a nonexistent attribute.
 * 
 * @version $Revision: 5767 $
 */
public class AttributeNotFoundException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4378388964705221358L;

    /**
     * Constructs an <code>AttributeNotFoundException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message which is saved for later retrieval by the
     *            <code>getMessage()</code> method.
     */
    public AttributeNotFoundException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
