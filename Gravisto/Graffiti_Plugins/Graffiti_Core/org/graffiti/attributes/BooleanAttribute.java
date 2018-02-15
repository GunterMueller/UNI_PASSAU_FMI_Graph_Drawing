// =============================================================================
//
//   BooleanAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BooleanAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a boolean value.
 */
public class BooleanAttribute extends AbstractAttribute {
    /** The value of this Attribute */
    private boolean value;

    /**
     * Constructs a new instance of a <code>BooleanAttribute</code>.
     * 
     * @param id
     *            the id of the attribute.
     */
    public BooleanAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>BooleanAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public BooleanAttribute(String id, boolean value) {
        super(id);
        this.value = value;
    }

    /**
     * Constructs a new instance of a <code>BooleanAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public BooleanAttribute(String id, Boolean value) {
        super(id);
        this.value = value.booleanValue();
    }

    /**
     * Sets the value of this object.
     * 
     * @param value
     *            the new value of this object.
     */
    public void setBoolean(boolean value) {
        AttributeEvent ae = new AttributeEvent(this);
        callPreAttributeChanged(ae);
        this.value = value;
        callPostAttributeChanged(ae);
    }

    /**
     * Returns the value of this object.
     * 
     * @return the value of this object.
     */
    public boolean getBoolean() {
        return value;
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = true;
    }

    /**
     * Returns the value of the attribute wrapped in an <code>Boolean</code>
     * object.
     * 
     * @return the value of the attribute wrapped in an <code>Boolean</code>
     *         object.
     */
    public Object getValue() {
        return new Boolean(value);
    }

    /**
     * Returns a deep copy of this instance. Parent won't be set because the
     * context may be different.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new BooleanAttribute(this.getId(), this.value);
    }

    /**
     * Sets the value of the attribute. The <code>ListenerManager</code> is
     * informed implicitly by the method <code>setValue()</code>.
     * 
     * @param o
     *            the new value of the attribute.
     * 
     * @exception IllegalArgumentException
     *                if the parameter has not the appropriate class for this
     *                attribute.
     */
    @Override
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            value = ((Boolean) o).booleanValue();
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(value ? "true" : "false");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
