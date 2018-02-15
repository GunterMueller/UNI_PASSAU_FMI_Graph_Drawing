// =============================================================================
//
//   GmlValue.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlValue.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlValue</code> is the superclass of all values in the GML
 * format.
 * 
 * @author ruediger
 */
public abstract class GmlValue extends GmlParsableItem {

    /**
     * Constructs a new <code>GmlValue</code>.
     * 
     * @param line
     *            the line the value was declared at.
     */
    public GmlValue(int line) {
        super(line);
    }

    /**
     * Returns the value.
     * 
     * @return the value.
     */
    public abstract Object getValue();

    /**
     * Returns <code>true</code> if the value is an integer.
     * 
     * @return <code>true</code> if the value is an integer.
     */
    public boolean isInt() {
        return false;
    }

    /**
     * Returns <code>true</code> if the value is a real.
     * 
     * @return <code>true</code> if the value is a real.
     */
    public boolean isReal() {
        return false;
    }

    /**
     * Returns <code>true</code> if the value is a string.
     * 
     * @return <code>true</code> if the value is a string.
     */
    public boolean isString() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
