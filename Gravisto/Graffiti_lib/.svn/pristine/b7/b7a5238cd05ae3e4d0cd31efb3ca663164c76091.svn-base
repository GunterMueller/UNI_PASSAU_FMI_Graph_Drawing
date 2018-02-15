// =============================================================================
//
//   FaceAttribute.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.Attribute;

/**
 * Attribute for graph elements to store a face object
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class FaceAttribute extends AbstractAttribute implements Attribute {

    private FaceWithComponents value = null;

    /**
     * Creates a face attribute with the specified id.
     * 
     * @param id
     *            the id of this node attribute.
     */
    public FaceAttribute(String id) {
        super(id);
    }

    /**
     * Creates a face attribute with the specified id and value
     * 
     * @param id
     *            the id of this node attribute.
     * @param value
     *            the face to store
     */
    public FaceAttribute(String id, FaceWithComponents value) {
        super(id);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object copy() {
        throw new UnsupportedOperationException();
        //return new FaceAttribute(this.getId(), this.value.);
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
    protected void doSetValue(Object v) throws IllegalArgumentException {
        value = (FaceWithComponents) v;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
