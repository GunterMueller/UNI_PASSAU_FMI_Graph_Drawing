// =============================================================================
//
//   GmlValuable.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlValuable.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * The superclass of all objects to which values can be associated according to
 * the definition of GML.
 * 
 * @author ruediger
 */
public abstract class GmlValuable extends GmlParsableItem {

    /** The value associated with the valuable. */
    protected GmlValue value;

    /**
     * Constructs a new <code>GmlValuable</code>.
     * 
     * @param line
     *            the line the valuable was declared at.
     * @param value
     *            the value that was associated to the valuable.
     */
    public GmlValuable(int line, GmlValue value) {
        super(line);
        this.value = value;
    }

    /**
     * Returns <code>true</code> if the valuable is an edge.
     * 
     * @return <code>true</code> if the valuable is an edge.
     */
    public boolean isEdge() {
        return false;
    }

    /**
     * Returns <code>true</code> if the valuable is a graph.
     * 
     * @return <code>true</code> if the valuable is a graph.
     */
    public boolean isGraph() {
        return false;
    }

    /**
     * Returns <code>true</code> if the valuable is a node.
     * 
     * @return <code>true</code> if the valuable is a node.
     */
    public boolean isNode() {
        return false;
    }

    /**
     * Returns the value associated to the valuable.
     * 
     * @return the value associated to the valuable.
     */
    public GmlValue getValue() {
        return this.value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
