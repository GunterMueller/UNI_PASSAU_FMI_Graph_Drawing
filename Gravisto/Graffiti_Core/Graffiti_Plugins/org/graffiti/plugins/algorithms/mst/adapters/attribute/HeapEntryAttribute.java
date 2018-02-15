// =============================================================================
//
//   HeapEntryAttribute.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.mst.adapters.attribute;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.Heap;

/**
 * An attribute storing heap entries.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class HeapEntryAttribute extends AbstractAttribute implements Attribute {

    /**
     * The heap entry encapsulated by this attribute.
     */
    private Heap.Entry<Node, Float> entry = null;

    /**
     * Creates a new heap entry attribute with the specified id.
     * 
     * @param id
     *            the id of this heap entry attribute.
     */
    public HeapEntryAttribute(String id) {
        super(id);
    }

    /**
     * Sets the heap entry of this attribute to the specified value.
     * 
     * @param v
     *            the new heap entry this attribute encapsulates.
     * @throws ClassCastException
     *             if the specified value is not a heap entry.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doSetValue(Object v) throws IllegalArgumentException {
        entry = (Heap.Entry<Node, Float>) v;
    }

    /**
     * Returns the heap entry this attribute encapsulates.
     * 
     * @return the heap entry this attribute encapsulates.
     */
    public Object getValue() {
        return entry;
    }

    /**
     * Sets the value of this attribute to <tt>null</tt>.
     */
    public void setDefaultValue() {
        entry = null;
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>.
     */
    public Object copy() {
        throw new UnsupportedOperationException();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
