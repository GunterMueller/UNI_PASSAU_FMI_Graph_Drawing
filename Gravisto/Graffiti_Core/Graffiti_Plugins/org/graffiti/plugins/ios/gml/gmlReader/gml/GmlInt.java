// =============================================================================
//
//   GmlInt.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlInt.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlInt</code> represents an integer value occuring in a GML
 * declaration. It might as well represent a boolean value if its value is 0 or
 * 1.
 * 
 * @author ruediger
 */
public class GmlInt extends GmlValue {

    /** The value of the <code>GmlInt</code>. */
    private Integer value;

    /**
     * Constructs a new <code>GmlInt</code>.
     * 
     * @param line
     *            the line in which the integer occured.
     * @param value
     *            the integer's value.
     */
    public GmlInt(int line, Integer value) {
        super(line);
        this.value = value;
    }

    /**
     * Returns <code>true</code> if the object represents a GML integer value.
     * 
     * @return <code>true</code> if the object represents a GML integer value.
     */
    @Override
    public boolean isInt() {
        return true;
    }

    /**
     * Returns the value.
     * 
     * @return the value.
     */
    @Override
    public Object getValue() {
        return value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
