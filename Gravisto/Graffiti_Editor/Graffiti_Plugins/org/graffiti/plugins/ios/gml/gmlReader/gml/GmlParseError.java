// =============================================================================
//
//   GmlParseError.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlParseError.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Represents an error the parser has detected.
 * 
 * @author ruediger
 */
public class GmlParseError extends GmlParsableItem {

    /** The error message. */
    private String message;

    /**
     * Constructs a new <code>GmlParseError</code> instance.
     * 
     * @param line
     *            the line the error was detected at.
     * @param message
     *            the error message.
     */
    public GmlParseError(int line, String message) {
        super(line);
        this.message = message;
    }

    /**
     * Returns a string representation of the parse error.
     * 
     * @return a string representation of the parse error.
     */
    @Override
    public String toString() {
        return "parse error (line " + this.line + "): " + this.message;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
