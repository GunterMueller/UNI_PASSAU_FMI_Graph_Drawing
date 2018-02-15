// =============================================================================
//
//   GmlEdgeStyle.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlEdgeStyle.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Represents an edge style as declared in the GML format.
 * 
 * @author ruediger
 */
public class GmlEdgeStyle extends GmlStyle {

    /**
     * Constructs a new <code>GmlEdgeStyle</code>.
     * 
     * @param line
     *            the line in which the edge style was declared.
     * @param value
     *            the value(s) associated with this edge style.
     */
    public GmlEdgeStyle(int line, GmlValue value) {
        super(line, value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
