// =============================================================================
//
//   GmlNodeStyle.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlNodeStyle.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlNodeStyle</code> represents a node style declared in the GML
 * file.
 * 
 * @author ruediger
 */
public class GmlNodeStyle extends GmlStyle {

    /**
     * Constructs a new <code>GmlNodeStyle</code>.
     * 
     * @param line
     *            the line the node style was declared in.
     * @param value
     *            the value(s) the node style is associated with.
     */
    public GmlNodeStyle(int line, GmlValue value) {
        super(line, value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
