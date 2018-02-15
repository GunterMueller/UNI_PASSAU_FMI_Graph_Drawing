//=============================================================================
//
//   BooledAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: BooledAttribute.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import org.graffiti.attributes.Attribute;

/**
 * Class that encapsulates an Attribute and a boolean value.
 */
public class BooledAttribute {

    /** The attribute. */
    private Attribute attribute;

    /** The boolean. */
    private boolean bool;

    /**
     * Creates a new BooledAttribute and sets the instance fields.
     * 
     * @param newAttribute
     *            the attribute
     * @param newBool
     *            the boolean
     */
    public BooledAttribute(Attribute newAttribute, boolean newBool) {
        this.attribute = newAttribute;
        this.bool = newBool;
    }

    /**
     * Returns the attribute.
     * 
     * @return this attribute
     */
    public Attribute getAttribute() {
        return this.attribute;
    }

    /**
     * Sets the attribute.
     * 
     * @param newAttribute
     *            the new attribute
     */
    public void setAttribute(Attribute newAttribute) {
        this.attribute = newAttribute;
    }

    /**
     * Sets the boolean.
     * 
     * @param newBool
     *            the new boolean.
     */
    public void setBool(boolean newBool) {
        this.bool = newBool;
    }

    /**
     * Returns the boolean.
     * 
     * @return this boolean
     */
    public boolean getBool() {
        return this.bool;
    }

    /**
     * Returns the ID of the contained attribute.
     * 
     * @return the ID of the attribute
     */
    @Override
    public String toString() {
        return this.attribute.getId();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
