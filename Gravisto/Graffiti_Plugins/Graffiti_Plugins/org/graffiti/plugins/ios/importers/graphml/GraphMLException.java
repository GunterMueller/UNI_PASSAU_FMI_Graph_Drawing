// =============================================================================
//
//   GraphMLException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml;

import java.io.IOException;

/**
 * This exception is thrown when errors occur during parsing.
 * 
 * @author ruediger
 */
public class GraphMLException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 3402057563881951712L;

    /**
     * Constructs a new <code>GraphMLException</code> from a given
     * <code>Throwable</code>.
     * 
     * @param cause
     *            the <code>Throwable</code> that caused the exception to be
     *            thrown.
     */
    public GraphMLException(Throwable cause) {
        super(cause.getMessage());
        this.initCause(cause);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
