// =============================================================================
//
//   FloatAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FloatAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a float
 * 
 * @version $Revision: 5767 $
 */
public class FloatAttribute extends AbstractAttribute {
    /** The value of this attribute */
    private float value;

    /**
     * Constructs a new instance of a <code>FloatAttribute</code>.
     * 
     * @param id
     *            the id of the attribute.
     */
    public FloatAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>FloatAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public FloatAttribute(String id, float value) {
        super(id);
        this.value = value;
    }

    /**
     * Constructs a new instance of a <code>FloatAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public FloatAttribute(String id, Float value) {
        super(id);
        this.value = value.floatValue();
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = 0f;
    }

    /**
     * Set the value of this Object.
     * 
     * @param value
     *            the new value for this object.
     */
    public void setFloat(float value) {
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
    public float getFloat() {
        return value;
    }

    /**
     * Returns the value of the attribute wrapped in an <code>Float</code>
     * object.
     * 
     * @return the value of the attribute wrapped in an <code>Float</code>
     *         object.
     */
    public Object getValue() {
        return new Float(value);
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new FloatAttribute(this.getId(), this.value);
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
            value = ((Float) o).floatValue();
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
