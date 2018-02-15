/*
 * HashAttribute.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Aug 11, 2005
 *
 */

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.HashMap;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.event.AttributeEvent;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class HashAttribute extends AbstractAttribute {

    /** The value of this attribute. */
    private HashMap<?, ?> value;

    /**
     * Constructs a new instance of an <code>IntegerAttribute</code>.
     * 
     * @param id
     *            the id of the attribute
     */
    public HashAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>IntegerAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public HashAttribute(String id, HashMap<?, ?> value) {
        super(id);
        this.value = value;
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = null;
    }

    /**
     * Sets the value of this object. The <code>ListenerManager</code> is
     * informed by the method <code>setValue()</code>.
     * 
     * @param value
     *            The new value of this object.
     */
    public void setHash(HashMap<?, ?> value) {
        AttributeEvent ae = new AttributeEvent(this);
        callPreAttributeChanged(ae);
        this.value = value;
        callPostAttributeChanged(ae);
    }

    /**
     * Returns the value of this object.
     * 
     * @return The value of this object.
     */
    public HashMap<?, ?> getHash() {
        return value;
    }

    /**
     * Returns the value of the attribute wrapped in an <code>Integer</code>
     * object.
     * 
     * @return The value of the attribute wrapped in an <code>Integer</code>
     *         object.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new HashAttribute(this.getId(), this.value);
    }

    /**
     * Sets the value of the attribute. The <code>ListenerManager</code> is
     * informed by the method <code>setValue()</code>.
     * 
     * @param o
     *            The new value of the attribute.
     * 
     * @exception IllegalArgumentException
     *                if the parameter has not the appropriate class for this
     *                attribute.
     */
    @Override
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            value = (HashMap<?, ?>) o;
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(String.valueOf(value));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
