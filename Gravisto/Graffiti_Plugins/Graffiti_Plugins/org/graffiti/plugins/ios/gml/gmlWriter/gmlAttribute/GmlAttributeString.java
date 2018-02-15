// =============================================================================
//
//   GmlAttributeString.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlAttributeString.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute;

/**
 * This class provides an implementation of the <code>GmlAttributeValue</code>
 * interface for writing Strings.
 * 
 * @author ruediger
 */
public class GmlAttributeString implements GmlAttributeValue {

    /** The attribute value represented as a String. */
    private String value;

    /**
     * Constructs a new <code>GmlAttributeString</code> for a given value.
     * 
     * @param value
     *            the String representation of the value.
     */
    public GmlAttributeString(String value) {
        this.value = value;
    }

    /*
     * 
     */
    public String getString() {
        return this.value.trim();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
