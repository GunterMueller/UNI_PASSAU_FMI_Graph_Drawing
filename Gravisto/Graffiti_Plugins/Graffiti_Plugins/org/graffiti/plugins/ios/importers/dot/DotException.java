// =============================================================================
//
//   GraphMLException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DotException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.dot;

import java.io.IOException;

/**
 * This exception is thrown when errors occur during parsing.
 * 
 * @author keilhaue
 */
public class DotException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 7434180982109414697L;

    /**
     * Constructs a new <code>DotException</code> from a given
     * <code>Throwable</code>.
     * 
     * @param cause
     *            the <code>Throwable</code> that caused the exception to be
     *            thrown.
     */
    public DotException(Throwable cause) {
        super(cause.getMessage());
        this.initCause(cause);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
