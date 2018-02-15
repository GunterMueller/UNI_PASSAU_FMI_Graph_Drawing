// =============================================================================
//
//   GmlReal.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlReal.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlReal</code> represents a real value occuring in a GML
 * declaration.
 * 
 * @author ruediger
 */
public class GmlReal extends GmlValue {

    /** The value of the <code>GmlReal</code>. */
    private Double value;

    /**
     * Constructs a new <code>GmlReal</code>.
     * 
     * @param line
     *            the line in which the real occured.
     * @param value
     *            the real's value.
     */
    public GmlReal(int line, Double value) {
        super(line);
        this.value = value;
    }

    /**
     * Returns <code>true</code> if the object represents a GML real value.
     * 
     * @return <code>true</code> if the object represents a GML real value.
     */
    @Override
    public boolean isReal() {
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
