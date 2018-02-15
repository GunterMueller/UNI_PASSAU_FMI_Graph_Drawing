// =============================================================================
//
//   ParentAttribute.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.mst.adapters.attribute;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;

/**
 * An attribute storing nodes.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class NodeAttribute extends AbstractAttribute implements Attribute {

    /**
     * The node encapsulated by this attribute.
     */
    private Node value = null;

    /**
     * Creates a node attribute with the specified id.
     * 
     * @param id
     *            the id of this node attribute.
     */
    public NodeAttribute(String id) {
        super(id);
    }

    /**
     * Sets this attribute's value to the specified value.
     * 
     * @see org.graffiti.graph.Node
     * 
     * @param v
     *            the new value of this attribute
     * @throws ClassCastException
     *             if the specified value is not an instance of <tt>Node</tt>.
     */
    @Override
    protected void doSetValue(Object v) throws IllegalArgumentException {
        value = (Node) v;
    }

    /**
     * Returns the value of this attribute; i.e. the node it encapsulates.
     * 
     * @return the node this attribute encapsulates.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets this attribute's value to <tt>null</tt>.
     */
    public void setDefaultValue() {
        value = null;
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
