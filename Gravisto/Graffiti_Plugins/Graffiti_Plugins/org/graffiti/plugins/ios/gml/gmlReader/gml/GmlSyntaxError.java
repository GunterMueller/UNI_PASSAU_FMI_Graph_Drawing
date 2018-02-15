// =============================================================================
//
//   GmlSyntaxError.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlSyntaxError.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Represents a syntax error detected by the parser.
 * 
 * @author ruediger
 */
public class GmlSyntaxError extends GmlParseError {

    /** The symbol which could not be read. */
    private Object symbol;

    /**
     * Constructs a new <code>GmlSyntaxError</code>.
     * 
     * @param line
     *            the line the syntax error was detected at.
     * @param symbol
     *            the symbol that caused the syntax error.
     */
    public GmlSyntaxError(int line, Object symbol) {
        super(line, "syntax error");
        this.symbol = symbol;
    }

    /**
     * Returns a String representation of the syntax error.
     * 
     * @return a String representation of the syntax error.
     */
    @Override
    public String toString() {
        return "syntax error (line " + this.line + "): " + "unknown token "
                + this.symbol + ".";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
