// =============================================================================
//
//   GmlStyle.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlStyle.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Represents a style declaration in the GML format.
 * 
 * @author ruediger
 */
public abstract class GmlStyle extends GmlParsableItem {

    /** The value(s) assigned to the style. */
    protected GmlValue value;

    /**
     * Constructs a new <code>GmlStyle</code>.
     * 
     * @param line
     *            the line the style was declared in.
     * @param value
     *            the value(s) assigned to the style.
     */
    public GmlStyle(int line, GmlValue value) {
        super(line);
        this.value = value;
    }

    /**
     * Returns the value(s) of the style.
     * 
     * @return the value(s) of the style.
     */
    public GmlValue getValue() {
        return this.value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
