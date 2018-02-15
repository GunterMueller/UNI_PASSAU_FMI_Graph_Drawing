// =============================================================================
//
//   DoubleAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DoubleAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a double
 * 
 * @version $Revision: 5767 $
 */
public class DoubleAttribute extends AbstractAttribute {
    /** The value of this attribute */
    private double value;

    /**
     * Constructs a new instance of a <code>DoubleAttribute</code>.
     * 
     * @param id
     *            the id of the attribute
     */
    public DoubleAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>DoubleAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public DoubleAttribute(String id, double value) {
        super(id);
        this.value = value;
    }

    /**
     * Constructs a new instance of a <code>DoubleAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public DoubleAttribute(String id, Double value) {
        super(id);
        this.value = value.doubleValue();
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = 0.0;
    }

    /**
     * Set the value of this Object.
     * 
     * @param value
     *            the new value for this object.
     */
    public void setDouble(double value) {
        AttributeEvent ae = new AttributeEvent(this);
        callPreAttributeChanged(ae);
        this.value = value;
        callPostAttributeChanged(ae);
    }

    /**
     * Returns the value of this attribute wrapped in an <code>Double</code>
     * object.
     * 
     * @return the value of this attribute wrapped in an <code>Double</code>
     *         object.
     */
    public double getDouble() {
        return value;
    }

    /**
     * Returns the value of the attribute.
     * 
     * @return the value of the attribute.
     */
    public Object getValue() {
        return new Double(value);
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new DoubleAttribute(this.getId(), this.value);
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
            value = ((Double) o).doubleValue();
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
