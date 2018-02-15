// =============================================================================
//
//   ParserException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ParserException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.IOException;

/**
 * ParserException will be thrown whenever an error occurs while reading in a
 * graph.
 * 
 * @see IOException
 */
public class ParserException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 3839416019887418417L;

    /**
     * Constructs a new <code>ParserException</code>.
     * 
     * @param message
     *            the message for this exception.
     */
    public ParserException(String message) {
        super(message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
