// =============================================================================
//
//   BooledAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BooledAttribute.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import org.graffiti.attributes.Attribute;

/**
 * Class that encapsulates an Attribute and a boolean value.
 * 
 * @author ph
 */
public class BooledAttribute {

    /** DOCUMENT ME! */
    private Attribute attribute;

    /** DOCUMENT ME! */
    private boolean bool;

    /**
     * Constructor for BooledAttribute.
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param bool
     *            DOCUMENT ME!
     */
    public BooledAttribute(Attribute attr, boolean bool) {
        this.attribute = attr;
        this.bool = bool;
    }

    /**
     * Sets the attribute.
     * 
     * @param attribute
     *            The attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * Returns the attribute.
     * 
     * @return Attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Sets the bool.
     * 
     * @param bool
     *            The bool to set
     */
    public void setBool(boolean bool) {
        this.bool = bool;
    }

    /**
     * Returns the bool.
     * 
     * @return boolean
     */
    public boolean getBool() {
        return bool;
    }

    /**
     * Returns the ID of the contained attribute.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String toString() {
        return this.attribute.getId();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
