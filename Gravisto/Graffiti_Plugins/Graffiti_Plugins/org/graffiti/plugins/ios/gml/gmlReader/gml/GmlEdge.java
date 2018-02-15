// =============================================================================
//
//   GmlEdge.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlEdge.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlEdge</code> represents an edge declared in the GML format.
 * 
 * @author ruediger
 */
public class GmlEdge extends GmlValuable {

    /**
     * Constructs a new <code>GmlEdge</code>.
     * 
     * @param line
     *            the line in which the edge was declared.
     * @param value
     *            the value(s) associated with the edge.
     */
    public GmlEdge(int line, GmlValue value) {
        super(line, value);
    }

    /**
     * Returns <code>true</code> if the object is a GmlEdge.
     * 
     * @return <code>true</code> if the object is a GmlEdge.
     */
    @Override
    public boolean isEdge() {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
