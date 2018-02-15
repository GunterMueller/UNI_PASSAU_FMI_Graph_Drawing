// =============================================================================
//
//   GmlToGraffitiException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlToGraffitiException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

/**
 * A <code>GmlToGraffitiException</code> is thrown whenever a fatal error occurs
 * during the transformation from the GML representation to the graffiti
 * representation. The message field contains a message indicating the reason
 * for the exception.
 * 
 * @author ruediger
 */
public class GmlToGraffitiException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1565867783455530866L;

    /**
     * Constructs a new <code>GmlToGraffitiException</code>.
     * 
     * @param message
     *            the message indicating the reason why the exception was
     *            thrown.
     */
    public GmlToGraffitiException(String message) {
        super(message);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
