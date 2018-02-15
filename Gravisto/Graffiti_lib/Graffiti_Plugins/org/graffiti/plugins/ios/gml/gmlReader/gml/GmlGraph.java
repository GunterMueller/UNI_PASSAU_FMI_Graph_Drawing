// =============================================================================
//
//   GmlGraph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlGraph.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlGraph</code> represents a graph as declared in the GML format.
 * 
 * @author ruediger
 */
public class GmlGraph extends GmlValuable {

    /**
     * Constructs a new <code>GmlGraph</code>.
     * 
     * @param line
     *            the line in which the graph was declared.
     * @param value
     *            the value(s) associated with this graph.
     */
    public GmlGraph(int line, GmlValue value) {
        super(line, value);
    }

    /**
     * Returns <code>true</code> if the object represents a graph.
     * 
     * @return <code>true</code> if the object represents a graph.
     */
    @Override
    public boolean isGraph() {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
