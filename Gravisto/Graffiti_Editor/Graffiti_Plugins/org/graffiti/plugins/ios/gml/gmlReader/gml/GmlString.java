// =============================================================================
//
//   GmlString.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlString.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlString</code> represents a string value occuring in a GML
 * declaration.
 * 
 * @author ruediger
 */
public class GmlString extends GmlValue {

    /** The value of the <code>GmlString</code>. */
    private String value;

    /**
     * Constructs a new <code>GmlString</code>.
     * 
     * @param line
     *            the line in which the string occured.
     * @param value
     *            the string's value.
     */
    public GmlString(int line, String value) {
        super(line);
        this.value = value;
    }

    /**
     * Returns <code>true</code> if the object represents a GML string value.
     * 
     * @return <code>true</code> if the object represents a GML string value.
     */
    @Override
    public boolean isString() {
        return true;
    }

    /**
     * Returns the value.
     * 
     * @return the value.
     */
    @Override
    public Object getValue() {
        return this.value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
