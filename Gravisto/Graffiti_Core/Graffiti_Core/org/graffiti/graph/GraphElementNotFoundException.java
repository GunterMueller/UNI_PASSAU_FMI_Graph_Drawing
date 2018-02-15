// =============================================================================
//
//   GraphElementNotFoundException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementNotFoundException.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

/**
 * The <code>GraphElementNotFoundException</code> will be thrown if a method
 * tries to deal with a <code>GraphElement</code> which cannot be found in the
 * <code>Graph</code>.
 * 
 * @version $Revision: 5767 $
 */
public class GraphElementNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2530160011271799940L;

    /**
     * Constructs a new <code>GraphElementNotFoundException</code> with the
     * specified detail message.
     * 
     * @param msg
     *            the error message.
     */
    public GraphElementNotFoundException(String msg) {
        super(msg);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
