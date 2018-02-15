// =============================================================================
//
//   ShortAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShortAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a short
 * 
 * @version $Revision: 5767 $
 */
public class ShortAttribute extends AbstractAttribute {
    /** The value of this attribute */
    private short value;

    /**
     * Constructs a new instance of a <code>ShortAttribute</code>.
     * 
     * @param id
     *            the id of the attribute
     */
    public ShortAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>ShortAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public ShortAttribute(String id, short value) {
        super(id);
        this.value = value;
    }

    /**
     * Constructs a new instance of a <code>ShortAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public ShortAttribute(String id, Short value) {
        super(id);
        this.value = value.shortValue();
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = 0;
    }

    /**
     * Set the value of this Object.
     * 
     * @param value
     *            the new value for this object.
     */
    public void setShort(short value) {
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
    public short getShort() {
        return value;
    }

    /**
     * Returns the value of the attribute wrapped in an <code>Short</code>
     * object.
     * 
     * @return the value of the attribute wrapped in an <code>Short</code>
     *         object.
     */
    public Object getValue() {
        return new Short(value);
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new ShortAttribute(this.getId(), this.value);
    }

    /**
     * Sets the value of the attribute. The <code>ListenerManager</code> is
     * informed by the method <code>setValue()</code>.
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
            value = ((Short) o).shortValue();
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
