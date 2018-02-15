// =============================================================================
//
//   GmlNode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlNode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlNode</code> represents a node declared in the GML format.
 * 
 * @author ruediger
 */
public class GmlNode extends GmlValuable {

    /**
     * Constructs a new <code>GmlNode</code>.
     * 
     * @param line
     *            the line the node was declared in.
     * @param value
     *            the value(s) associated with the node.
     */
    public GmlNode(int line, GmlValue value) {
        super(line, value);
    }

    /**
     * Returns <code>true</code> if the object represents a node.
     * 
     * @return <code>true</code> if the object represents a node.
     */
    @Override
    public boolean isNode() {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
