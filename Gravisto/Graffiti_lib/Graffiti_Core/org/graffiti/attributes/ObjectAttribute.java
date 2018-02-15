// =============================================================================
//
//   ObjectAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================

package org.graffiti.attributes;

import org.graffiti.core.DeepCopy;
import org.graffiti.event.AttributeEvent;

/**
 * Contains an object. The object has to implement to interface
 * <code>DeepCopy</code> to support the <code>copy</code> method.
 * 
 * Note: Use this class to store short-lived information for algorithms only.
 * Delete the <code>ObjectAttribute</code> after the algorithm finishes. Saving
 * this attribute will not work.
 * 
 * @version $Revision: 5767 $
 */
public class ObjectAttribute extends AbstractAttribute {
    /** The value of this attribute. */
    private DeepCopy value;

    /**
     * Constructs a new instance of an <code>ObjectAttribute</code>.
     * 
     * @param id
     *            the id of the attribute
     */
    public ObjectAttribute(String id) {
        super(id);
    }

    /**
     * Constructs a new instance of an <code>ObjectAttribute</code> with the
     * given value.
     * 
     * @param id
     *            the id of the attribute
     * @param value
     *            the value of the attribute
     */
    public ObjectAttribute(String id, DeepCopy value) {
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
    public void setObject(DeepCopy value) {
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
    public DeepCopy getObject() {
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
        return value.copy();
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

        try {
            value = (DeepCopy) o;
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        return getStandardXML(value.toString());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
