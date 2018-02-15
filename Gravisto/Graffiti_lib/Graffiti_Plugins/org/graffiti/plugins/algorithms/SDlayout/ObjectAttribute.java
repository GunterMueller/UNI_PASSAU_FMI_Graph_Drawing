// =============================================================================
//
//   ObjectAttribute.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.SDlayout;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.IllegalIdException;

/**
 * This attribute can encapsulate any type. The difference to the class in
 * Graffiti_Core : The encapsulated element can be modified.
 * 
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class ObjectAttribute extends AbstractAttribute {

    /**
     * attribute for the object, which should be encapsulated
     */
    private Object value;

    /**
     * The constructor creates a instance of an ObjectAttribute
     * 
     * @param id
     *            : the id for the attribute
     * @throws IllegalIdException
     *             : from AbstractAtribute
     */
    public ObjectAttribute(String id) throws IllegalIdException {
        super(id);
        value = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValue() {
        value = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object copy() {
        return new Object();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(Object v) throws IllegalArgumentException {
        value = v;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
