// =============================================================================
//
//   EditComponentNotFoundException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditComponentNotFoundException.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor;

/**
 * Thrown if no EditComponent could be found.
 */
public class EditComponentNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3292699801893305791L;

    /**
     * Constructor for AttributeComponentNotFoundException.
     * 
     * @param message
     */
    public EditComponentNotFoundException(String message) {
        super(message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
