// =============================================================================
//
//   ShapeNotFoundException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShapeNotFoundException.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

/**
 * DOCUMENT ME!
 * 
 * @author schoeffl To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class ShapeNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5984889433963582264L;

    /**
     * Constructs a ShapeNotFoundException.
     * 
     * @param msg
     *            the message to set.
     */
    public ShapeNotFoundException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
