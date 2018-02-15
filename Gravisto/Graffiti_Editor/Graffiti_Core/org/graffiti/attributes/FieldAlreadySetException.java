// =============================================================================
//
//   FieldAlreadySetException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FieldAlreadySetException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * The <code>FieldAlreadySetException</code> will be thrown if
 * <code>setAttributable()</code> of <code>setParent()</code> is invoked on an
 * attribute where theses fields are not <tt>null</tt> anymore.
 * 
 * @version $Revision: 5767 $
 */
public class FieldAlreadySetException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 2963639038743420158L;

    /**
     * Constructs an <code>FieldAlreadySetException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            The detail message which is saved for later retrieval by the
     *            <code>getMessage()</code> method.
     */
    public FieldAlreadySetException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
