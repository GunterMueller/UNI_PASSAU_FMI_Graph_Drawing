// =============================================================================
//
//   StringAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.AttributeEvent;

/**
 * Contains a String.
 * 
 * @version $Revision: 5767 $
 */
public class StringAttribute extends AbstractAttribute {
    /** The value of this <code>StringAttribute</code>. */
    private String value;

    /**
     * Constructs a new instance of a <code>StringAttribute</code>.
     * 
     * @param id
     *            the id of the <code>Attribute</code>.
     */
    public StringAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of a <code>StringAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute.
     * @param value
     *            the value of the <code>Attribute</code>.
     */
    public StringAttribute(String id, String value) {
        super(id);
        this.value = value;
    }

    /**
     * @see org.graffiti.attributes.Attribute#setDefaultValue()
     */
    public void setDefaultValue() {
        value = "";
    }

    /**
     * Sets the value of this object. The <code>ListenerManager</code> is
     * informed by the method <code>setValue()</code>.
     * 
     * @param value
     *            the new value of this object.
     */
    public void setString(String value) {
        assert value != null;

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
    public String getString() {
        return value;
    }

    /**
     * Returns the value of this attribute, i.e. contained Sting object.
     * 
     * @return the value of the attribute, i.e. contained String object.
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
        return new StringAttribute(this.getId(), this.value);
    }

    /**
     * @see org.graffiti.attributes.Attribute#toString(int)
     */
    @Override
    public String toString(int n) {
        return getSpaces(n) + getId() + " = \"" + value + "\"";
    }

    /**
     * Sets the value of the <code>Attribute</code>. The
     * <code>ListenerManager</code> is informed by the method
     * <code>setValue()</code>.
     * 
     * @param o
     *            the new value of the attribute.
     * 
     * @exception IllegalArgumentException
     *                if the parameter has not the appropriate class for this
     *                <code>Attribute</code>.
     */
    @Override
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            value = (String) o;
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
