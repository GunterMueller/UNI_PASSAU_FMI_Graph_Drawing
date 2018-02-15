// =============================================================================
//
//   GmlList.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlList.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.gml;

/**
 * Class <code>GmlList</code> represents a list as declared in the GML manual.
 * The empty list is represented as a <code>null</code> value.
 * 
 * @author ruediger
 */
public class GmlList extends GmlValue {

    /** The tail of the list. */
    private GmlList tail;

    /** The head of the List. */
    private GmlValuable head;

    /**
     * Constructs a new <code>GmlList</code> from a given head and a given tail.
     * 
     * @param line
     *            the line in which the list was declared.
     * @param valuable
     *            the head of the list.
     * @param list
     *            the tail of the list.
     */
    public GmlList(int line, GmlValuable valuable, GmlList list) {
        super(line);
        this.head = valuable;
        this.tail = list;
    }

    /**
     * Returns the head of the list.
     * 
     * @return the head of the list.
     */
    public GmlValuable getHead() {
        return this.head;
    }

    /**
     * Returns the tail of the list, <code>null</code> if the tail is the empty
     * list.
     * 
     * @return the tail of the list, <code>null</code> if the tail is the empty
     *         list.
     */
    public GmlList getTail() {
        return this.tail;
    }

    /**
     * Returns the value of the list.
     * 
     * @return the value of the list.
     */
    @Override
    public Object getValue() {
        return this;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
