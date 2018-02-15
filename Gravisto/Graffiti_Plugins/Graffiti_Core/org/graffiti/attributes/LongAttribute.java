// =============================================================================
//
//   LongAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LongAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a long
 * 
 * @version $Revision: 5767 $
 */
public class LongAttribute extends AbstractAttribute {
    /** The value of this attribute */
    private long value;

    /**
     * Constructs a new instance of a <code>LongAttribute</code>.
     * 
     * @param id
     *            the id of the attribute
     */
    public LongAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>LongAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public LongAttribute(String id, long value) {
        super(id);
        this.value = value;
    }

    /**
     * Constructs a new instance of a <code>LongAttribute</code> with the given
     * value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the attribute.
     */
    public LongAttribute(String id, Long value) {
        super(id);
        this.value = value.longValue();
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
    public void setLong(long value) {
        // setValue(new Long(value));
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
    public long getLong() {
        return value;
    }

    /**
     * Returns the value of the attribute wrapped in an <code>Long</code>
     * object.
     * 
     * @return the value of the attribute wrapped in an <code>Long</code>
     *         object.
     */
    public Object getValue() {
        return new Long(value);
    }

    /**
     * Returns a deep copy of this instance.
     * 
     * @return a deep copy of this instance.
     */
    public Object copy() {
        return new LongAttribute(this.getId(), this.value);
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
            value = ((Long) o).longValue();
        } catch (ClassCastException cce) {
            try {
                value = ((Integer) o).intValue();
            } catch (ClassCastException cce2) {
                throw new IllegalArgumentException("Invalid value type.");
            }
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
