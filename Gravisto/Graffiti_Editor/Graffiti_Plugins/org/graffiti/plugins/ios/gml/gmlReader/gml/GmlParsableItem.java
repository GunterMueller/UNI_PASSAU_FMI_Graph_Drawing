// =============================================================================
//
//   GmlParsableItem.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlParsableItem.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlParsableItem</code> is the superclass of any GML item read by
 * the parser.
 * 
 * @author ruediger
 */
public abstract class GmlParsableItem {

    /** The line the item was declared in. */
    protected int line;

    /**
     * Constructs a new <code>GmlParsableItem</code>.
     * 
     * @param line
     *            the line the item was declared in.
     */
    public GmlParsableItem(int line) {
        this.line = line;
    }

    /**
     * Returns the line the item was declared in.
     * 
     * @return the line the item was declared in.
     */
    public int getLine() {
        return this.line;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
