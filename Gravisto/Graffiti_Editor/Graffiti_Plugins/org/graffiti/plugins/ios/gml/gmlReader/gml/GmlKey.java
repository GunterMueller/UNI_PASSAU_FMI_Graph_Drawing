// =============================================================================
//
//   GmlKey.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlKey.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlKey</code> represents a possibly predefined key declaring an
 * attribute in the GML format.
 * 
 * @author ruediger
 */
public class GmlKey extends GmlValuable {

    /** The id representing the key. */
    private String id;

    /**
     * Constructs a new <code>GmlKey</code>.
     * 
     * @param line
     *            the line in which the key was declared.
     * @param id
     *            the id of the key.
     * @param value
     *            the value(s) associated with this key.
     */
    public GmlKey(int line, String id, GmlValue value) {
        super(line, value);
        this.id = id;
    }

    /**
     * Returns the id of this key.
     * 
     * @return the id of this key.
     */
    public String getId() {
        return this.id;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
